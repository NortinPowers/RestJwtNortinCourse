package by.nortin.restjwtproject.service.impl;

import static by.nortin.restjwtproject.utils.ObjectUtils.getIgnoredProperties;

import by.nortin.restjwtproject.domain.Book;
import by.nortin.restjwtproject.dto.BookDto;
import by.nortin.restjwtproject.exception.BookNotFoundException;
import by.nortin.restjwtproject.mapper.BookMapper;
import by.nortin.restjwtproject.repository.BookRepository;
import by.nortin.restjwtproject.service.BookService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(bookMapper::convertToDto)
                .toList();
    }

    @Override
    public BookDto getBook(Long id) {
        return bookMapper.convertToDto(bookRepository.getReferenceById(id));
    }

    @Override
    public void save(BookDto bookDto) {
        bookRepository.save(bookMapper.convertToDomain(bookDto));
    }

    @Override
    public void update(Long id, BookDto bookDto) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            Book updatedBook = bookMapper.convertToDomain(bookDto);
            BeanUtils.copyProperties(updatedBook, book, getIgnoredProperties(updatedBook, "id"));
        } else {
            throw new BookNotFoundException();
        }

    }

    @Override
    public void delete(Long id) {
        if (bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException();
        }
    }
}
