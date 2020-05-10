package com.jhtest.app.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.jhtest.app.domain.Address} entity. This class is used
 * in {@link com.jhtest.app.web.rest.AddressResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /addresses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AddressCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter address1;

    private StringFilter address2;

    private StringFilter city;

    private StringFilter postcode;

    private StringFilter country;

    private LongFilter customerId;

    public AddressCriteria() {
    }

    public AddressCriteria(AddressCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.address1 = other.address1 == null ? null : other.address1.copy();
        this.address2 = other.address2 == null ? null : other.address2.copy();
        this.city = other.city == null ? null : other.city.copy();
        this.postcode = other.postcode == null ? null : other.postcode.copy();
        this.country = other.country == null ? null : other.country.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
    }

    @Override
    public AddressCriteria copy() {
        return new AddressCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAddress1() {
        return address1;
    }

    public void setAddress1(StringFilter address1) {
        this.address1 = address1;
    }

    public StringFilter getAddress2() {
        return address2;
    }

    public void setAddress2(StringFilter address2) {
        this.address2 = address2;
    }

    public StringFilter getCity() {
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getPostcode() {
        return postcode;
    }

    public void setPostcode(StringFilter postcode) {
        this.postcode = postcode;
    }

    public StringFilter getCountry() {
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
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
        final AddressCriteria that = (AddressCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(address1, that.address1) &&
            Objects.equals(address2, that.address2) &&
            Objects.equals(city, that.city) &&
            Objects.equals(postcode, that.postcode) &&
            Objects.equals(country, that.country) &&
            Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        address1,
        address2,
        city,
        postcode,
        country,
        customerId
        );
    }

    @Override
    public String toString() {
        return "AddressCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (address1 != null ? "address1=" + address1 + ", " : "") +
                (address2 != null ? "address2=" + address2 + ", " : "") +
                (city != null ? "city=" + city + ", " : "") +
                (postcode != null ? "postcode=" + postcode + ", " : "") +
                (country != null ? "country=" + country + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
