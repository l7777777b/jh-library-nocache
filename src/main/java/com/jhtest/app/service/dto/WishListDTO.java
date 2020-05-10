package com.jhtest.app.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.jhtest.app.domain.WishList} entity.
 */
public class WishListDTO implements Serializable {
    
    private Long id;

    @NotNull
    private String title;

    private Boolean restricted;


    private Long customerId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WishListDTO wishListDTO = (WishListDTO) o;
        if (wishListDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), wishListDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "WishListDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", restricted='" + isRestricted() + "'" +
            ", customerId=" + getCustomerId() +
            "}";
    }
}
