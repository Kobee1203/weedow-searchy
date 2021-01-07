package com.weedow.spring.data.search.sample.java.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
public class Feature extends JpaPersistable<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "metadata_mapping",
            joinColumns = {@JoinColumn(referencedColumnName = "id", name = "feature_id")}
    )
    @MapKeyColumn(name = "metadata_name")
    @Column(name = "value")
    private Map<String, String> metadata;

    public String getName() {
        return name;
    }

    public Feature setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Feature setDescription(String description) {
        this.description = description;
        return this;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public Feature setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }
}