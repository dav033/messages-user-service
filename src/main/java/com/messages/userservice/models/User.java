package com.messages.userservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specify the strategy for ID generation
    private Integer id;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "profile_image", nullable = true)
    private String profile_image;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "temporal", nullable = true)
    private Boolean temporal;

    @Column(name = "rooms", nullable = false)
    private String rooms;

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public Boolean getTemporal() {
        return temporal;
    }

    public void setTemporal(Boolean temporal) {
        this.temporal = temporal;
    }

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

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
