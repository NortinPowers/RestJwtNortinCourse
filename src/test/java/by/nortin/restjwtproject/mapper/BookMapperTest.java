package by.nortin.restjwtproject.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import by.nortin.restjwtproject.domain.Book;
import by.nortin.restjwtproject.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    private final BookDto bookDto;
    private final Book book;

    {
        Long id = 1L;
        String author = "Stephen King";
        String title = "The Dark Tower";
        book = new Book();
        book.setId(id);
        book.setAuthor(author);
        book.setTitle(title);
        bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setAuthor(author);
        bookDto.setTitle(title);
    }

    @Test
    void convertToDto() {
        assertEquals(bookDto, bookMapper.convertToDto(book));
    }

    @Test
    void convertToDomain() {
        assertEquals(book, bookMapper.convertToDomain(bookDto));
    }
}
