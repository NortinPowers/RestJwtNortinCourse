package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.ResponseUtils.BOOK_NOT_FOUND_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.CREATION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.DELETE_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.ENTITY_NOT_FOUND_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.UPDATE_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.getExceptionResponse;
import static by.nortin.restjwtproject.utils.ResponseUtils.getMapperWithTimeModule;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.nortin.restjwtproject.dto.BookDto;
import by.nortin.restjwtproject.dto.ErrorValidatorResponse;
import by.nortin.restjwtproject.dto.ExceptionResponse;
import by.nortin.restjwtproject.exception.BookNotFoundException;
import by.nortin.restjwtproject.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private final ObjectMapper mapper;

    private String url;
    private ExceptionResponse exceptionResponse;
    private List<String> validationErrors;
    private ErrorValidatorResponse errorValidatorResponse;

    {
        mapper = getMapperWithTimeModule();
    }

    @Nested
    class TestGetAllBooks {

        {
            url = "/book";
        }

        @Test
        @WithAnonymousUser
        void test_getAllBooks_anonymous_denied() throws Exception {
            mockMvc.perform(get(url))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("error").value("To get access you need token"));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
        void test_getAllBooks_authorized_alloyed() throws Exception {
            BookDto firstBook = new BookDto();
            firstBook.setId(1L);
            firstBook.setAuthor("First author");
            firstBook.setTitle("First title");
            BookDto secondBook = new BookDto();
            secondBook.setId(1L);
            secondBook.setAuthor("Second author");
            secondBook.setTitle("Second title");
            List<BookDto> books = List.of(firstBook, secondBook);

            when(bookService.getAllBooks()).thenReturn(books);

            mockMvc.perform(get(url))
                    .andExpectAll(
                            status().isOk(),
                            content().json(mapper.writeValueAsString(books)));
            verify(bookService, atLeastOnce()).getAllBooks();
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
        void test_getAllBooks_dBError() throws Exception {
            DataSourceLookupFailureException exception = new DataSourceLookupFailureException("not matter");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE,
                    exception
            );

            doThrow(exception).when(bookService).getAllBooks();

            mockMvc.perform(get(url))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(bookService, atLeastOnce()).getAllBooks();
        }
    }

    @Nested
    class TestGetBook {

        private final Long id = 1L;

        {
            url = "/book/{id}";
        }

        @Test
        @WithAnonymousUser
        void test_getBook_anonymous_denied() throws Exception {
            mockMvc.perform(get(url, id))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("error").value("To get access you need token"));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
        void test_getBook_authorized_alloyed() throws Exception {
            BookDto firstBook = new BookDto();
            firstBook.setId(1L);
            firstBook.setAuthor("First author");
            firstBook.setTitle("First title");

            when(bookService.getBook(id)).thenReturn(firstBook);

            mockMvc.perform(get(url, id))
                    .andExpectAll(
                            status().isOk(),
                            content().json(mapper.writeValueAsString(firstBook)));
            verify(bookService, atLeastOnce()).getBook(id);
        }

        @Test
        @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
        void test_getBook_entityNotFound() throws Exception {
            EntityNotFoundException exception = new EntityNotFoundException("not matter");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.NOT_FOUND,
                    ENTITY_NOT_FOUND_EXCEPTION_MESSAGE,
                    exception
            );

            doThrow(exception).when(bookService).getBook(any());

            mockMvc.perform(get(url, id))
                    .andExpectAll(
                            status().isNotFound(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(bookService, atLeastOnce()).getBook(id);
        }
    }

    @Nested
    class TestCreate {

        private final BookDto createdBook;

        {
            url = "/book";
            createdBook = new BookDto();
            createdBook.setAuthor("First Author");
            createdBook.setTitle("First Title");
        }

        @Test
        @WithAnonymousUser
        void test_create_anonymous_denied() throws Exception {
            mockMvc.perform(post(url))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("error").value("To get access you need token"));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        void test_create_roleUser_denied() throws Exception {
            AccessDeniedException exception = new AccessDeniedException("Access Denied");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.FORBIDDEN,
                    exception.getMessage(),
                    exception
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(createdBook)))
                    .andExpectAll(
                            status().isForbidden(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_create_roleAdmin_Alloyed() throws Exception {
            doNothing().when(bookService).save(createdBook);

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(createdBook)))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("message").value(String.format(CREATION_MESSAGE, "book")));
            verify(bookService, atLeastOnce()).save(createdBook);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_create_roleAdmin_invalidBody() throws Exception {
            createdBook.setAuthor("Incorrect");
            validationErrors = List.of("Incorrect author`s name");
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(createdBook)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verify(bookService, never()).save(createdBook);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_create_roleAdmin_emptyBody() throws Exception {
            createdBook.setAuthor("");
            createdBook.setTitle("");
            validationErrors = List.of("Incorrect author`s name", "Enter author", "Enter title");
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(createdBook)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verify(bookService, never()).save(createdBook);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_create_roleAdmin_incorrectBodyType() throws Exception {
            String book = "author, title";
            HttpMessageNotReadableException exception = new HttpMessageNotReadableException("not matter", new MockHttpInputMessage("not matter".getBytes()));
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE,
                    exception
            );

            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(book)))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(bookService, never()).save(createdBook);
        }
    }

    @Nested
    class TestUpdate {

        private final BookDto updatedBook;
        private final Long id;

        {
            url = "/book/{id}";
            updatedBook = new BookDto();
            updatedBook.setAuthor("First Author");
            updatedBook.setTitle("First Title");
            id = 1L;
        }

        @Test
        @WithAnonymousUser
        void test_update_anonymous_denied() throws Exception {
            mockMvc.perform(patch(url, id))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("error").value("To get access you need token"));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        void test_update_roleUser_denied() throws Exception {
            AccessDeniedException exception = new AccessDeniedException("Access Denied");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.FORBIDDEN,
                    exception.getMessage(),
                    exception
            );

            mockMvc.perform(patch(url, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updatedBook)))
                    .andExpectAll(
                            status().isForbidden(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_update_roleAdmin_Alloyed() throws Exception {
            doNothing().when(bookService).update(id, updatedBook);

            mockMvc.perform(patch(url, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updatedBook)))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("message").value(String.format(UPDATE_MESSAGE, "book")));
            verify(bookService, atLeastOnce()).update(id, updatedBook);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_update_roleAdmin_bookNotFound() throws Exception {
            BookNotFoundException exception = new BookNotFoundException();
            exceptionResponse = getExceptionResponse(
                    HttpStatus.NOT_FOUND,
                    BOOK_NOT_FOUND_EXCEPTION_MESSAGE,
                    exception
            );

            doThrow(exception).when(bookService).update(id, updatedBook);

            mockMvc.perform(patch(url, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updatedBook)))
                    .andExpectAll(
                            status().isNotFound(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(bookService, atLeastOnce()).update(id, updatedBook);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_update_roleAdmin_invalidBody() throws Exception {
            updatedBook.setAuthor("Incorrect");
            validationErrors = List.of("Incorrect author`s name");
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(patch(url, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updatedBook)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verify(bookService, never()).update(id, updatedBook);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_update_roleAdmin_emptyBody() throws Exception {
            updatedBook.setAuthor("");
            updatedBook.setTitle("");
            validationErrors = List.of("Incorrect author`s name", "Enter author", "Enter title");
            errorValidatorResponse = new ErrorValidatorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationErrors,
                    METHOD_ARGUMENT_NOT_VALID_EXCEPTION_MESSAGE
            );

            mockMvc.perform(patch(url, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updatedBook)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().json(mapper.writeValueAsString(errorValidatorResponse)));
            verify(bookService, never()).update(id, updatedBook);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_create_roleAdmin_incorrectBodyType() throws Exception {
            String book = "author, title";
            HttpMessageNotReadableException exception = new HttpMessageNotReadableException("not matter", new MockHttpInputMessage("not matter".getBytes()));
            exceptionResponse = getExceptionResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE,
                    exception
            );

            mockMvc.perform(patch(url, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(book)))
                    .andExpectAll(
                            status().isInternalServerError(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(bookService, never()).update(eq(id), any());
        }

    }

    @Nested
    class TestDelete {

        private final Long id;

        {
            url = "/book/{id}";
            id = 1L;
        }

        @Test
        @WithAnonymousUser
        void test_delete_anonymous_denied() throws Exception {
            mockMvc.perform(delete(url, id))
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("error").value("To get access you need token"));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        void test_delete_roleUser_denied() throws Exception {
            AccessDeniedException exception = new AccessDeniedException("Access Denied");
            exceptionResponse = getExceptionResponse(
                    HttpStatus.FORBIDDEN,
                    exception.getMessage(),
                    exception
            );

            mockMvc.perform(delete(url, id))
                    .andExpectAll(
                            status().isForbidden(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verifyNoInteractions(bookService);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_delete_roleAdmin_Alloyed() throws Exception {
            doNothing().when(bookService).delete(id);

            mockMvc.perform(delete(url, id))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("message").value(String.format(DELETE_MESSAGE, "book")));
            verify(bookService, atLeastOnce()).delete(id);
        }

        @Test
        @WithMockUser(username = "user", roles = "ADMIN")
        void test_delete_roleAdmin_bookNotFound() throws Exception {
            BookNotFoundException exception = new BookNotFoundException();
            exceptionResponse = getExceptionResponse(
                    HttpStatus.NOT_FOUND,
                    BOOK_NOT_FOUND_EXCEPTION_MESSAGE,
                    exception
            );

            doThrow(exception).when(bookService).delete(id);

            mockMvc.perform(delete(url, id))
                    .andExpectAll(
                            status().isNotFound(),
                            content().json(mapper.writeValueAsString(exceptionResponse)));
            verify(bookService, atLeastOnce()).delete(id);
        }
    }
}
