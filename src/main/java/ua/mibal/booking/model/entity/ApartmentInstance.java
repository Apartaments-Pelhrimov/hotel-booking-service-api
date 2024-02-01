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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.time.LocalDateTime.now;
import static lombok.AccessLevel.PRIVATE;
import static ua.mibal.booking.service.util.CollectionUtils.union;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
@Entity
@Table(
        name = "apartment_instances", indexes = {
        @Index(name = "apartment_instances_apartment_id_idx", columnList = "apartment_id")
})
public class ApartmentInstance {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "booking_ical_url")
    private String bookingICalUrl;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "apartment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "apartment_instances_apartment_id_fk")
    )
    private Apartment apartment;

    @ElementCollection
    @BatchSize(size = 100)
    @CollectionTable(
            name = "apartment_instances_turning_off_times",
            joinColumns = @JoinColumn(
                    name = "apartment_instance_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "apartment_instances_turning_off_times_apartment_instance_id_fk")
            ),
            indexes = @Index(
                    name = "apartment_instances_turning_off_times_apartment_instance_id_idx",
                    columnList = "apartment_instance_id"
            ))
    @Setter(PRIVATE)
    private List<TurningOffTime> turningOffTimes = new ArrayList<>();

    @OneToMany(mappedBy = "apartmentInstance")
    @Setter(PRIVATE)
    private List<Reservation> reservations = new ArrayList<>();

    public Optional<String> getBookingICalUrl() {
        return Optional.ofNullable(bookingICalUrl);
    }

    public void addReservation(Reservation reservation) {
        reservation.setApartmentInstance(this);
        this.reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (this.reservations.contains(reservation)) {
            this.reservations.remove(reservation);
            reservation.setApartmentInstance(null);
        }
    }

    public void addTurningOffTime(TurningOffTime turningOffTime) {
        turningOffTime.setApartmentInstance(this);
        this.turningOffTimes.add(turningOffTime);
    }

    public List<Event> getNotRejectedEventsForNow() {
        Predicate<Event> isForNow =
                event -> event.getEnd().isAfter(now());
        return getAllNotRejectedEvents()
                .stream()
                .filter(isForNow)
                .toList();
    }

    public boolean hasReservationsAt(LocalDateTime start, LocalDateTime end) {
        Predicate<Reservation> intersectsWithRange =
                r -> r.isNotRejected() &&
                     r.getDetails().getTo().isAfter(start) &&
                     r.getDetails().getFrom().isBefore(end);
        return getReservations().stream()
                .anyMatch(intersectsWithRange);
    }

    private List<Event> getAllNotRejectedEvents() {
        List<Reservation> reservations = getNotRejectedReservations();
        return union(reservations, turningOffTimes);
    }

    private List<Reservation> getNotRejectedReservations() {
        return reservations.stream()
                .filter(Reservation::isNotRejected)
                .toList();
    }
}
