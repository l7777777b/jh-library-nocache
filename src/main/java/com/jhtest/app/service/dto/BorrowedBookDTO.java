package com.jhtest.app.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.jhtest.app.domain.BorrowedBook} entity.
 */
public class BorrowedBookDTO implements Serializable {
    
    private Long id;

    private LocalDate borrowDate;


    private Long bookId;

    private String bookName;

    private Long clientId;

    private String clientEmail;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BorrowedBookDTO borrowedBookDTO = (BorrowedBookDTO) o;
        if (borrowedBookDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), borrowedBookDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BorrowedBookDTO{" +
            "id=" + getId() +
            ", borrowDate='" + getBorrowDate() + "'" +
            ", bookId=" + getBookId() +
            ", bookName='" + getBookName() + "'" +
            ", clientId=" + getClientId() +
            ", clientEmail='" + getClientEmail() + "'" +
            "}";
    }
}
