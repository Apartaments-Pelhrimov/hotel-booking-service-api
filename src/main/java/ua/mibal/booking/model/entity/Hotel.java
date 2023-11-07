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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.exception.IllegalRoleException;
import ua.mibal.booking.model.embeddable.HotelOptions;
import ua.mibal.booking.model.embeddable.Location;
import ua.mibal.booking.model.embeddable.Photo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;
import static ua.mibal.booking.model.embeddable.Role.GLOBAL_MANAGER;
import static ua.mibal.booking.model.embeddable.Role.LOCAL_MANAGER;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer stars;

    @Embedded
    private Location location;

    private String description;

    private String rules;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Money money;

    @Embedded
    private HotelOptions hotelOptions = HotelOptions.DEFAULT;

    @ElementCollection
    @CollectionTable(
            name = "hotel_photos",
            joinColumns = @JoinColumn(
                    name = "hotel_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "hotel_photos_hotel_id_fk")
            ),
            indexes = @Index(
                    name = "hotel_photos_hotel_id_idx",
                    columnList = "hotel_id"
            ))
    @Setter(PRIVATE)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    @Setter(PRIVATE)
    private List<Apartment> apartments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "hotel_managers",
            joinColumns = @JoinColumn(
                    name = "hotel_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "hotel_managers_hotel_id_fk")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "hotel_managers_user_id_fk")
            ),
            indexes = {
                    @Index(name = "hotel_managers_hotel_id_idx", columnList = "hotel_id"),
                    @Index(name = "hotel_managers_user_id_idx", columnList = "user_id")
            })
    @Setter(PRIVATE)
    private Set<User> managers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public void deletePhoto(Photo photo) {
        this.photos.remove(photo);
    }

    public void addApartment(Apartment apartment) {
        apartment.setHotel(this);
        this.apartments.add(apartment);
    }

    public void deleteApartment(Apartment apartment) {
        if (this.apartments.contains(apartment)) {
            this.apartments.remove(apartment);
            apartment.setHotel(null);
        }
    }

    public void addManager(User user) {
        if (user.is(GLOBAL_MANAGER)) return;
        if (!user.is(LOCAL_MANAGER)) throw new IllegalRoleException(user);
        this.managers.add(user);
        user.getHotels().add(this);
    }

    public void removeManager(User user) {
        if (this.managers.contains(user)) {
            this.managers.remove(user);
            user.getHotels().remove(this);
        }
    }

    public enum Money {
        USD, EUR, UAH, CZK,
    }
}