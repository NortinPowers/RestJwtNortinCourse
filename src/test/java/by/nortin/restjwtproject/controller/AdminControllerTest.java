package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.ResponseUtils.CHANGE_ROLE_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.ENTITY_NOT_FOUND_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.getExceptionResponse;
import static by.nortin.restjwtproject.utils.ResponseUtils.getMapperWithTimeModule;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.nortin.restjwtproject.dto.ExceptionResponse;
import by.nortin.restjwtproject.dto.UserDto;
import by.nortin.restjwtproject.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;
    private String url;
    private ExceptionResponse exceptionResponse;
    private final ObjectMapper mapper;

    {
        exceptionResponse = new ExceptionResponse(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "AccessDeniedException"
        );
        mapper = getMapperWithTimeModule();
    }

    @Nested
    class TestSetAdmin {

        private final Long id;

        {
            url = "/admin/set/{id}";
            id = 1L;
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Verification of access by anonymous user")
        void test_setAdmin_anonymous_denied() throws Exception {
            mockMvc.perform(patch(url, id))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("error").value("To get access you need token"));
            verifyNoInteractions(adminService);
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        void test_setAdmin_roleUser_denied() throws Exception {
            mockMvc.perform(patch(url, id))
                    .andExpectAll(
                            status().isForbidden(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verifyNoInteractions(adminService);
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void test_setAdmin_roleAdmin_Alloyed() throws Exception {
            doNothing().when(adminService).setAdmin(id);

            mockMvc.perform(patch(url, id))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("message").value(String.format(CHANGE_ROLE_MESSAGE, "user")));
            verify(adminService, atLeastOnce()).setAdmin(id);
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void test_setAdmin_roleAdmin_dBError() throws Exception {
            DataSourceLookupFailureException exception = new DataSourceLookupFailureException("not matter");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE,
                    exception
            );

            doThrow(exception).when(adminService).setAdmin(id);

            mockMvc.perform(patch(url, id))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(adminService, atLeastOnce()).setAdmin(id);
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void test_setAdmin_roleAdmin_entityNotFound() throws Exception {
            EntityNotFoundException exception = new EntityNotFoundException("not matter");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.NOT_FOUND,
                    ENTITY_NOT_FOUND_EXCEPTION_MESSAGE,
                    exception
            );

            doThrow(exception).when(adminService).setAdmin(id);

            mockMvc.perform(patch(url, id))
                    .andExpectAll(
                            status().isNotFound(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(adminService, atLeastOnce()).setAdmin(id);
        }
    }

    @Nested
    class TestGetAllUsers {

        {
            url = "/admin/users";
        }

        @Test
        @WithAnonymousUser
        void test_getAllUsers_anonymous_denied() throws Exception {
            mockMvc.perform(get(url))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("error").value("To get access you need token"));
            verifyNoInteractions(adminService);
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        void test_getAllUsers_roleUser_denied() throws Exception {
            mockMvc.perform(get(url))
                    .andExpectAll(
                            status().isForbidden(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verifyNoInteractions(adminService);
        }

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")
        void test_getAllUsers_roleAdmin_Alloyed() throws Exception {
            UserDto firstUser = new UserDto();
            firstUser.setId(1L);
            firstUser.setUsername("First");
            firstUser.setRole("ROLE_USER");
            UserDto secondUser = new UserDto();
            secondUser.setId(2L);
            secondUser.setUsername("Second");
            secondUser.setRole("ROLE_ADMIN");
            List<UserDto> users = List.of(firstUser, secondUser);

            when(adminService.getAllUsers()).thenReturn(users);

            mockMvc.perform(get(url))
                    .andExpectAll(
                            status().isOk(),
                            content().json(mapper.writeValueAsString(users)));
            verify(adminService, atLeastOnce()).getAllUsers();
        }
    }
}
