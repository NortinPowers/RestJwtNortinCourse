package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.Constants.PASSWORD_NOT_BLANK;
import static by.nortin.restjwtproject.utils.Constants.USERNAME_NOT_BLANK;
import static by.nortin.restjwtproject.utils.ResponseUtils.BAD_CREDENTIALS_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.getExceptionResponse;
import static by.nortin.restjwtproject.utils.ResponseUtils.getMapperWithTimeModule;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.nortin.restjwtproject.dto.ErrorValidatorResponse;
import by.nortin.restjwtproject.dto.ExceptionResponse;
import by.nortin.restjwtproject.dto.JwtRequest;
import by.nortin.restjwtproject.dto.JwtResponse;
import by.nortin.restjwtproject.service.AuthService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    private final ObjectMapper mapper;
    private String url;
    private List<String> validationErrors;
    private ErrorValidatorResponse errorValidatorResponse;
    private ExceptionResponse exceptionResponse;

    {
        mapper = getMapperWithTimeModule();
    }
    /*
      @PostMapping
    public ResponseEntity<JwtResponse> createAuthToken(@RequestBody @Valid JwtRequest request) throws BadCredentialsException {
        String token = authService.getToken(request);
        return ResponseEntity.ok(new JwtResponse(token));
    }
     */

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

            mockMvc.perform(MockMvcRequestBuilders.post(url)
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

            mockMvc.perform(MockMvcRequestBuilders.post(url)
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

            mockMvc.perform(MockMvcRequestBuilders.post(url)
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

            mockMvc.perform(MockMvcRequestBuilders.post(url)
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

            mockMvc.perform(MockMvcRequestBuilders.post(url)
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

            mockMvc.perform(MockMvcRequestBuilders.post(url)
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

            mockMvc.perform(MockMvcRequestBuilders.post(url)
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

    @Test
    void createNewUser() {
    }
}
