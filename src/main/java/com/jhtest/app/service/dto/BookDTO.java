package com.jhtest.app.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.jhtest.app.domain.Book} entity.
 */
public class BookDTO implements Serializable {
    
    private Long id;

    @NotNull
    @Size(min = 5, max = 13)
    private String isbn;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Size(min = 4, max = 50)
    private String publishYear;

    @NotNull
    private Integer copies;

    @Lob
    private byte[] cover;

    private String coverContentType;

    private Long publisherId;

    private String publisherName;
    private Set<AuthorDTO> authors = new HashSet<>();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return coverContentType;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public Set<AuthorDTO> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorDTO> authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BookDTO bookDTO = (BookDTO) o;
        if (bookDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bookDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BookDTO{" +
            "id=" + getId() +
            ", isbn='" + getIsbn() + "'" +
            ", name='" + getName() + "'" +
            ", publishYear='" + getPublishYear() + "'" +
            ", copies=" + getCopies() +
            ", cover='" + getCover() + "'" +
            ", publisherId=" + getPublisherId() +
            ", publisherName='" + getPublisherName() + "'" +
            ", authors='" + getAuthors() + "'" +
            "}";
    }
}
