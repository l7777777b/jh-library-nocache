package com.jhtest.app.service.mapper;


import com.jhtest.app.domain.*;
import com.jhtest.app.service.dto.BookDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring", uses = {PublisherMapper.class, AuthorMapper.class})
public interface BookMapper extends EntityMapper<BookDTO, Book> {

    @Mapping(source = "publisher.id", target = "publisherId")
    @Mapping(source = "publisher.name", target = "publisherName")
    BookDTO toDto(Book book);

    @Mapping(source = "publisherId", target = "publisher")
    @Mapping(target = "removeAuthor", ignore = true)
    Book toEntity(BookDTO bookDTO);

    default Book fromId(Long id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
