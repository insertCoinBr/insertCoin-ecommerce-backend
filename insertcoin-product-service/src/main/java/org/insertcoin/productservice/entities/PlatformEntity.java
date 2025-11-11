package org.insertcoin.productservice.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "platform")
public class PlatformEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    private String name;

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

