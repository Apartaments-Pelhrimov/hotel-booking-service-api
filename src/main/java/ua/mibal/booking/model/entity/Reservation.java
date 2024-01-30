/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.mibal.booking.model.entity.embeddable.Rejection;
import ua.mibal.booking.model.entity.embeddable.ReservationDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ua.mibal.booking.model.entity.Reservation.State.PROCESSED;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "reservations_user_id_idx", columnList = "user_id"),
        @Index(name = "reservations_apartment_instance_id_idx", columnList = "apartment_instance_id")
})
public class Reservation implements Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "reservations_user_id_fk")
    )
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "apartment_instance_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "reservations_apartment_instance_id_fk")
    )
    private ApartmentInstance apartmentInstance;

    @ElementCollection
    @CollectionTable(
            name = "reservation_rejections",
            joinColumns = @JoinColumn(
                    name = "reservation_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "reservation_rejections_reservation_id_fk")
            ),
            indexes = @Index(
                    name = "reservation_rejections_reservation_id_idx",
                    columnList = "reservation_id"
            ))
    @OrderColumn
    @Setter(AccessLevel.PRIVATE)
    private List<Rejection> rejections = new ArrayList<>();

    @Embedded
    private ReservationDetails details;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    private Reservation(User user,
                        ApartmentInstance apartmentInstance,
                        ReservationDetails details) {
        this.user = user;
        this.apartmentInstance = apartmentInstance;
        this.details = details;
        this.dateTime = LocalDateTime.now();
        this.state = PROCESSED;
    }

    public static Reservation of(User user,
                                 ApartmentInstance apartmentInstance,
                                 ReservationDetails details) {
        return new Reservation(user, apartmentInstance, details);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void reject(User user, String reason) {
        this.state = State.REJECTED;
        this.rejections.add(Rejection.of(user, reason));
    }

    @Override
    public LocalDateTime getStart() {
        return details.getReservedFrom();
    }

    @Override
    public LocalDateTime getEnd() {
        return details.getReservedTo();
    }

    @Override
    public String getEventName() {
        return apartmentInstance.getName() + " reservation";
    }

    public boolean isNotRejected() {
        return state != State.REJECTED;
    }

    public enum State {
        PROCESSED, REJECTED
    }
}
