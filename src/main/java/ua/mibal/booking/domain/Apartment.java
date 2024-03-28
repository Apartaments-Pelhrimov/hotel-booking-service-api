/*
 * Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import ua.mibal.booking.application.exception.PhotoNotFoundException;
import ua.mibal.booking.application.exception.PriceNotFoundException;
import ua.mibal.booking.domain.id.ApartmentId;
import ua.mibal.booking.domain.id.ApartmentIdGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;
import static ua.mibal.booking.domain.ApartmentOptions.DEFAULT;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "apartments")
public class Apartment {

    @GenericGenerator(name = "ApartmentIdGenerator",
            type = ApartmentIdGenerator.class)
    @GeneratedValue(generator = "ApartmentIdGenerator")
    @EmbeddedId
    private ApartmentId id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private ApartmentOptions options = DEFAULT;

    @Formula("""
                 (select avg(r.rate)
                    from reviews r
                  where r.apartment_id = id)
            """)
    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "class", nullable = false)
    private ApartmentClass apartmentClass;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "prices",
            joinColumns = @JoinColumn(
                    name = "apartment_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "prices_apartment_id_fk")
            ),
            indexes = @Index(
                    name = "prices_apartment_id_idx",
                    columnList = "apartment_id"
            ),
            uniqueConstraints = @UniqueConstraint(
                    name = "prices_apartment_id_and_person_uq",
                    columnNames = {"apartment_id", "person"}
            ))
    @Setter(PRIVATE)
    private List<Price> prices = new LinkedList<>();

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartment_photos",
            joinColumns = @JoinColumn(
                    name = "apartment_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartment_photos_apartment_id_fk")
            ),
            indexes = @Index(
                    name = "apartment_photos_aws_photo_key_idx",
                    columnList = "aws_photo_key",
                    unique = true
            ))
    @Setter(PRIVATE)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Setter(PRIVATE)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Setter(PRIVATE)
    private List<ApartmentInstance> apartmentInstances = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    @Setter(PRIVATE)
    private List<Review> reviews = new ArrayList<>();

    public void addApartmentInstance(ApartmentInstance apartmentInstance) {
        apartmentInstance.setApartment(this);
        this.apartmentInstances.add(apartmentInstance);
    }

    public void removeApartmentInstance(ApartmentInstance apartmentInstance) {
        if (this.apartmentInstances.contains(apartmentInstance)) {
            this.apartmentInstances.remove(apartmentInstance);
            apartmentInstance.setApartment(null);
        }
    }

    public void addReview(Review review) {
        review.setApartment(this);
        this.reviews.add(review);
    }

    public void removeReview(Review review) {
        if (this.reviews.contains(review)) {
            this.reviews.remove(review);
            review.setApartment(null);
        }
    }

    public String getPhotoKey(Integer photoIndex) {
        checkPhotoIndexForBounds(photoIndex);
        Photo photo = photos.get(photoIndex);
        return photo.getKey();
    }

    public void addPhoto(String key) {
        Photo photo = new Photo(key);
        this.photos.add(photo);
    }

    public boolean deletePhoto(String key) {
        return this.photos.removeIf(ph -> Objects.equals(ph.getKey(), key));
    }

    public Price getPriceFor(Integer people) {
        return getPrices().stream()
                .filter(pr -> people.equals(pr.getPerson()))
                .findFirst()
                .orElseThrow(() -> new PriceNotFoundException(people));
    }

    public void putPrice(Price price) {
        prices.remove(price);
        prices.add(price);
    }

    public boolean deletePrice(Integer person) {
        return prices.removeIf(price -> price.getPerson().equals(person));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Apartment that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean hasPhoto(String key) {
        return photos.stream()
                .anyMatch(p -> p.getKey().equals(key));
    }

    private void checkPhotoIndexForBounds(Integer photoIndex) {
        if (photoIndex < 0 || photoIndex >= photos.size()) {
            throw new PhotoNotFoundException();
        }
    }

    public enum ApartmentClass {
        COMFORT, STANDARD
    }
}
