package by.nortin.restjwtproject.mapper;

import by.nortin.restjwtproject.domain.Book;
import by.nortin.restjwtproject.dto.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BaseMapper.class)
public interface BookMapper {

    BookDto convertToDto(Book book);

    Book convertToDomain(BookDto bookDto);
}
