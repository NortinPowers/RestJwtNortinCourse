package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.Constants.BOOK;
import static by.nortin.restjwtproject.utils.ResponseUtils.CREATION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.DELETE_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.UPDATE_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.getSuccessResponse;

import by.nortin.restjwtproject.dto.BaseResponse;
import by.nortin.restjwtproject.dto.BookDto;
import by.nortin.restjwtproject.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> create(@RequestBody BookDto bookDto) {
        bookService.save(bookDto);
        return ResponseEntity.ok(getSuccessResponse(CREATION_MESSAGE, BOOK));
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> update(@PathVariable("id") Long id, @RequestBody BookDto bookDto) {
        bookService.update(id, bookDto);
        return ResponseEntity.ok(getSuccessResponse(UPDATE_MESSAGE, BOOK));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> delete(@PathVariable("id") Long id) {
        bookService.delete(id);
        return ResponseEntity.ok(getSuccessResponse(DELETE_MESSAGE, BOOK));
    }
}
