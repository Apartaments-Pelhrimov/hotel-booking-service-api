/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.mibal.booking.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.type.NumericBooleanConverter;
import ua.mibal.booking.model.entity.embeddable.ApartmentOptions;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.model.entity.embeddable.Price;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "apartment_types")
public class ApartmentType {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "prices",
            joinColumns = @JoinColumn(
                    name = "apartment_type_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "prices_apartment_type_id_fk")
            ),
            indexes = @Index(
                    name = "prices_apartment_type_id_idx",
                    columnList = "apartment_type_id"
            ),
            uniqueConstraints = @UniqueConstraint(
                    name = "prices_apartment_type_id_and_person_uq",
                    columnNames = {"apartment_type_id", "person"}
            ))
    private List<Price> prices = new LinkedList<>();

    @Embedded
    private ApartmentOptions options = ApartmentOptions.DEFAULT;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean published = false;

    @Column
    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApartmentClass apartmentClass;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartment_type_photos",
            joinColumns = @JoinColumn(
                    name = "apartment_type_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartment_type_photos_apartment_type_id_fk")
            ),
            indexes = @Index(
                    name = "apartment_type_photos_photo_link_idx",
                    columnList = "photo_link",
                    unique = true
            ))
    @OrderColumn
    @Setter(PRIVATE)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "apartmentType")
    @Setter(PRIVATE)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "apartmentType")
    @Setter(PRIVATE)
    private List<Apartment> apartments = new ArrayList<>();

    @OneToMany(mappedBy = "apartmentType")
    @Setter(PRIVATE)
    private List<Comment> comments = new ArrayList<>();

    public void addApartment(Apartment apartment) {
        apartment.setApartmentType(this);
        this.apartments.add(apartment);
    }

    public void removeApartment(Apartment apartment) {
        if (this.apartments.contains(apartment)) {
            this.apartments.remove(apartment);
            apartment.setApartmentType(null);
        }
    }

    public void addComment(Comment comment) {
        comment.setApartmentType(this);
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        if (this.comments.contains(comment)) {
            this.comments.remove(comment);
            comment.setApartmentType(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApartmentType that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public boolean deletePhoto(Photo photo) {
        return this.photos.remove(photo);
    }

    public enum ApartmentClass {
        COMFORT, STANDARD
    }
}
