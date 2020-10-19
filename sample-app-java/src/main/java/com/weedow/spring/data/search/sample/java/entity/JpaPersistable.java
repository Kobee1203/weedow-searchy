package com.weedow.spring.data.search.sample.java.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class JpaPersistable<ID extends Serializable> implements Persistable<ID> {

    @Id
    @GeneratedValue
    private ID id;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdOn;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedOn;

    @PrePersist
    public void prePersist() {
        createdOn = OffsetDateTime.now();
        updatedOn = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedOn = OffsetDateTime.now();
    }

    @Override
    public ID getId() {
        return id;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public OffsetDateTime getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Must be [Transient] in order to ensure that no JPA provider complains because of a missing setter.
     *
     * @see org.springframework.data.domain.Persistable#isNew
     */
    @Transient
    @Override
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JpaPersistable<?> that = (JpaPersistable<?>) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
