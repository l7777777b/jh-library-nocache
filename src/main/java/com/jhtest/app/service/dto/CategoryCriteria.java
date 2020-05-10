package com.jhtest.app.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.jhtest.app.domain.enumeration.CategoryStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.jhtest.app.domain.Category} entity. This class is used
 * in {@link com.jhtest.app.web.rest.CategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CategoryCriteria implements Serializable, Criteria {
    /**
     * Class for filtering CategoryStatus
     */
    public static class CategoryStatusFilter extends Filter<CategoryStatus> {

        public CategoryStatusFilter() {
        }

        public CategoryStatusFilter(CategoryStatusFilter filter) {
            super(filter);
        }

        @Override
        public CategoryStatusFilter copy() {
            return new CategoryStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private IntegerFilter sortOrder;

    private LocalDateFilter dateAdded;

    private LocalDateFilter dateModified;

    private CategoryStatusFilter status;

    private LongFilter parentId;

    private LongFilter productId;

    public CategoryCriteria() {
    }

    public CategoryCriteria(CategoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.sortOrder = other.sortOrder == null ? null : other.sortOrder.copy();
        this.dateAdded = other.dateAdded == null ? null : other.dateAdded.copy();
        this.dateModified = other.dateModified == null ? null : other.dateModified.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.parentId = other.parentId == null ? null : other.parentId.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
    }

    @Override
    public CategoryCriteria copy() {
        return new CategoryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public IntegerFilter getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(IntegerFilter sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateFilter getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateFilter dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDateFilter getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateFilter dateModified) {
        this.dateModified = dateModified;
    }

    public CategoryStatusFilter getStatus() {
        return status;
    }

    public void setStatus(CategoryStatusFilter status) {
        this.status = status;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CategoryCriteria that = (CategoryCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(sortOrder, that.sortOrder) &&
            Objects.equals(dateAdded, that.dateAdded) &&
            Objects.equals(dateModified, that.dateModified) &&
            Objects.equals(status, that.status) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        description,
        sortOrder,
        dateAdded,
        dateModified,
        status,
        parentId,
        productId
        );
    }

    @Override
    public String toString() {
        return "CategoryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (sortOrder != null ? "sortOrder=" + sortOrder + ", " : "") +
                (dateAdded != null ? "dateAdded=" + dateAdded + ", " : "") +
                (dateModified != null ? "dateModified=" + dateModified + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (parentId != null ? "parentId=" + parentId + ", " : "") +
                (productId != null ? "productId=" + productId + ", " : "") +
            "}";
    }

}
