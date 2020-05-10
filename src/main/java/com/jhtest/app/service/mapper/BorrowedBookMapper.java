package com.jhtest.app.service.mapper;


import com.jhtest.app.domain.*;
import com.jhtest.app.service.dto.BorrowedBookDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link BorrowedBook} and its DTO {@link BorrowedBookDTO}.
 */
@Mapper(componentModel = "spring", uses = {BookMapper.class, ClientMapper.class})
public interface BorrowedBookMapper extends EntityMapper<BorrowedBookDTO, BorrowedBook> {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.name", target = "bookName")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.email", target = "clientEmail")
    BorrowedBookDTO toDto(BorrowedBook borrowedBook);

    @Mapping(source = "bookId", target = "book")
    @Mapping(source = "clientId", target = "client")
    BorrowedBook toEntity(BorrowedBookDTO borrowedBookDTO);

    default BorrowedBook fromId(Long id) {
        if (id == null) {
            return null;
        }
        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setId(id);
        return borrowedBook;
    }
}
