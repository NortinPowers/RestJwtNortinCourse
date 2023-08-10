package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.Constants.PASSWORD_NOT_BLANK;
import static by.nortin.restjwtproject.utils.Constants.PASSWORD_NOT_MATCHING;
import static by.nortin.restjwtproject.utils.Constants.USERNAME_NOT_BLANK;
import static by.nortin.restjwtproject.utils.ResponseUtils.BAD_CREDENTIALS_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.CREATION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.getExceptionResponse;
import static by.nortin.restjwtproject.utils.ResponseUtils.getMapperWithTimeModule;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.nortin.restjwtproject.dto.ErrorValidatorResponse;
import by.nortin.restjwtproject.dto.ExceptionResponse;
import by.nortin.restjwtproject.dto.JwtRequest;
import by.nortin.restjwtproject.dto.JwtResponse;
import by.nortin.restjwtproject.dto.UserRegistrationDto;
import by.nortin.restjwtproject.service.AuthService;
import by.nortin.restjwtproject.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;
    private final ObjectMapper mapper;
    private String url;
    private List<String> validationErrors;
    private ErrorValidatorResponse errorValidatorResponse;
    private ExceptionResponse exceptionResponse;

    {
        mapper = getMapperWithTimeModule();
    }

    @Nested
    class TestCreateAuthToken {

        private final JwtRequest request;

        {
            url = "/auth";
            request = new JwtRequest();
            request.setUsername("user");
            request.setPassword("password");
        }

        @Test
        void test_createAuthToken_success() throws Exception {
            String token = "header.payload.signature";
            JwtResponse response = new JwtResponse(token);

            when(authService.getToken(any())).thenReturn(token);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpectAll(
                            status().isOk(),
                            content().json(mapper.writeValueAsString(response)));
            verify(authService, atLeastOnce()).getToken(any());
        }

        @Test
        void test_createAuthToken_emptyBody() throws Exception {
            validationErrors = List.of(USERNAME_NOT_BLANK, PASSWORD_NOT_BLANK);
            request.setPassword("");
            request.setUsername("");
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verifyNoInteractions(authService);
        }

        @Test
        void test_createAuthToken_semiEmptyBody() throws Exception {
            validationErrors = List.of(PASSWORD_NOT_BLANK);
            request.setPassword("");
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verifyNoInteractions(authService);
        }

        @Test
        void test_createAuthToken_incorrectBody() throws Exception {
            IncorrectJwtRequest incorrectJwtRequest = new IncorrectJwtRequest("user", "password");
            validationErrors = List.of(USERNAME_NOT_BLANK);
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(incorrectJwtRequest)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verifyNoInteractions(authService);
        }

        @Test
        void test_createAuthToken_badCredentials() throws Exception {
            BadCredentialsException exception = new BadCredentialsException("not matter");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.UNAUTHORIZED,
                    BAD_CREDENTIALS_EXCEPTION_MESSAGE,
                    exception
            );

            when(authService.getToken(any())).thenThrow(exception);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpectAll(
                            status().isUnauthorized(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(authService, atLeastOnce()).getToken(any());
        }

        @Test
        void test_createAuthToken_dBError() throws Exception {
            DataSourceLookupFailureException exception = new DataSourceLookupFailureException("not matter");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE,
                    exception
            );

            when(authService.getToken(any())).thenThrow(exception);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(authService, atLeastOnce()).getToken(any());
        }

        @Test
        void test_createAuthToken_incorrectBodyType() throws Exception {
            String incorrectRequest = "user, password";
            HttpMessageNotReadableException exception = new HttpMessageNotReadableException("not matter", new MockHttpInputMessage("not matter".getBytes()));
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE,
                    exception
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(incorrectRequest)))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verifyNoInteractions(authService);
        }

        record IncorrectJwtRequest(String name, String password) {

        }
    }

    @Nested
    class TestCreateNewUser {

        private final UserRegistrationDto userRegistrationDto;

        {
            userRegistrationDto = new UserRegistrationDto();
            userRegistrationDto.setUsername("user");
            userRegistrationDto.setPassword("password");
            userRegistrationDto.setVerifyPassword("password");
            url = "/auth/registration";
        }

        @Test
        void test_createNewUser_success() throws Exception {
            doNothing().when(userService).save(any());

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userRegistrationDto)))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("message").value(String.format(CREATION_MESSAGE, "user")));
            verify(userService, atLeastOnce()).save(any());
        }

        @Test
        void test_createNewUser_dBError() throws Exception {
            DataSourceLookupFailureException exception = new DataSourceLookupFailureException("not matter");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE,
                    exception
            );

            doThrow(exception).when(userService).save(any());

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userRegistrationDto)))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(userService, atLeastOnce()).save(any());
        }

        @Test
        void test_createNewUser_incorrectBody() throws Exception {
            IncorrectUserRegistrationDto incorrectUserRegistrationDto = new IncorrectUserRegistrationDto("test", "pass", "pass");
            validationErrors = List.of(USERNAME_NOT_BLANK);
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(incorrectUserRegistrationDto)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verify(userService, never()).save(any());
        }

        @Test
        void test_createNewUser_passwordNotMatching() throws Exception {
            userRegistrationDto.setVerifyPassword("qwerty");
            validationErrors = List.of(PASSWORD_NOT_MATCHING);
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userRegistrationDto)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verify(userService, never()).save(any());
        }

        @Test
        void test_createNewUser_incorrectBodyType() throws Exception {
            String incorrectRequest = "user, password, password";
            HttpMessageNotReadableException exception = new HttpMessageNotReadableException("not matter", new MockHttpInputMessage("not matter".getBytes()));
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE,
                    exception
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(incorrectRequest)))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verifyNoInteractions(userService);
        }

        record IncorrectUserRegistrationDto(String name, String password, String verifyPassword) {
        }
    }
}
