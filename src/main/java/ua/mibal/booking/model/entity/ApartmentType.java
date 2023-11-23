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
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.type.NumericBooleanConverter;
import ua.mibal.booking.model.entity.embeddable.ApartmentOptions;
import ua.mibal.booking.model.entity.embeddable.Bed;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "apartment_types",
        indexes = @Index(name = "apartment_types_hotel_id_idx", columnList = "hotel_id")
)
public class ApartmentType {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer rooms;

    @Column(nullable = false)
    private BigDecimal cost;

    @Embedded
    private ApartmentOptions options = ApartmentOptions.DEFAULT;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean published = false;

    @Column(nullable = false)
    private Integer size;

    @Column
    private Double rating;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "hotel_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "apartment_types_hotel_id_fk"))
    private Hotel hotel;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartment_type_photos",
            joinColumns = @JoinColumn(
                    name = "apartment_type_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartment_type_photos_apartment_type_id_fk")
            ),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = "photoLink"
            ))
    @Setter(PRIVATE)
    private Set<Photo> photos = new HashSet<>();

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartment_type_beds",
            joinColumns = @JoinColumn(
                    name = "apartment_type_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartment_type_beds_apartment_type_id_fk")
            ))
    @Setter(PRIVATE)
    private List<Bed> beds = new ArrayList<>();

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartment_type_turning_off_times",
            joinColumns = @JoinColumn(
                    name = "apartment_type_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartment_type_turning_off_times_apartment_type_id_fk")
            ),
            indexes = @Index(
                    name = "apartment_type_turning_off_times_apartment_type_id_idx",
                    columnList = "apartment_type_id"
            ))
    @Setter(PRIVATE)
    private List<TurningOffTime> turningOffTimes = new ArrayList<>();

    @OneToMany(mappedBy = "apartmentType")
    @Setter(PRIVATE)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "apartmentType")
    @Setter(PRIVATE)
    private List<Comment> comments = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservation.setApartmentType(this);
        this.reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (this.reservations.contains(reservation)) {
            this.reservations.remove(reservation);
            reservation.setApartmentType(null);
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
}
