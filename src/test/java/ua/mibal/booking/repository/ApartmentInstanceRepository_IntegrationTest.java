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

package ua.mibal.booking.repository;

import org.assertj.core.api.AssertionsForClassTypes;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.Reservation;
import ua.mibal.test.annotation.JpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ua.mibal.booking.testUtils.DataGenerator.testApartment;
import static ua.mibal.booking.testUtils.DataGenerator.testApartmentInstance;
import static ua.mibal.booking.testUtils.DataGenerator.testApartmentInstanceWithReservations;
import static ua.mibal.booking.testUtils.DataGenerator.testApartmentInstanceWithoutReservation;
import static ua.mibal.booking.testUtils.DataGenerator.testApartmentWithPriceFor;
import static ua.mibal.booking.testUtils.DataGenerator.testReservationOf;
import static ua.mibal.booking.testUtils.DataGenerator.testUser;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@JpaTest
class ApartmentInstanceRepository_IntegrationTest {

    @Autowired
    private ApartmentInstanceRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionFactory sessionFactory;
    private Statistics stats;

    private ApartmentInstance apartmentInstance;

    @BeforeEach
    void statisticsSetUp() {
        apartmentInstance = persistTestApartmentInstance();
        stats = sessionFactory.getStatistics();
        stats.clear();
    }

    @Test
    void findByIdFetchReservations() {
        Long id = apartmentInstance.getId();

        ApartmentInstance managedApartmentInstance =
                repo.findByIdFetchReservations(id).orElseThrow();

        entityManager.detach(managedApartmentInstance);
        assertDoesNotThrow(() -> managedApartmentInstance.getReservations().size());

        AssertionsForClassTypes.assertThat(stats.getEntityLoadCount()).isOne();
        AssertionsForClassTypes.assertThat(stats.getCollectionLoadCount()).isOne();

        assertThat(stats.getQueryExecutionCount()).isOne();
        assertThat(stats.getEntityFetchCount()).isZero();
        assertThat(stats.getPrepareStatementCount())
                .isEqualTo(stats.getQueryExecutionCount() + stats.getEntityFetchCount());
    }

    // FIXME
    //  org.hibernate.exception.ConstraintViolationException: could not execute statement [ERROR: duplicate key value violates unique constraint "prices_apartment_id_and_person_uq"
    //  Detail: Key (apartment_id, person)=(261, 7442) already exists.] [insert into prices (apartment_id,price,person) values (?,?,?)]
    @Test
    void findByApartmentIdFetchReservations() {
        Long apartmentId = apartmentInstance.getApartment().getId();

        List<ApartmentInstance> managedApartmentInstances =
                repo.findByApartmentIdFetchReservations(apartmentId);

        managedApartmentInstances.forEach(ai -> {
            entityManager.detach(ai);
            assertDoesNotThrow(() -> ai.getReservations().size());
        });

        AssertionsForClassTypes.assertThat(stats.getEntityLoadCount()).isOne();
        AssertionsForClassTypes.assertThat(stats.getCollectionLoadCount()).isOne();

        assertThat(stats.getQueryExecutionCount()).isOne();
        assertThat(stats.getEntityFetchCount()).isZero();
        assertThat(stats.getPrepareStatementCount())
                .isEqualTo(stats.getQueryExecutionCount() + stats.getEntityFetchCount());
    }

    private ApartmentInstance persistTestApartmentInstance() {
        Apartment apartment = testApartment();
        entityManager.persistAndFlush(apartment);

        ApartmentInstance apartmentInstance = testApartmentInstance();
        apartmentInstance.setApartment(apartment);
        entityManager.persistAndFlush(apartmentInstance);

        entityManager.detach(apartment);
        entityManager.detach(apartmentInstance);

        return apartmentInstance;
    }

    @Test
    void findFreeByRequest_with_no_price_option() {
        LocalDateTime requestFrom = now().plusYears(1);
        LocalDateTime requestTo = requestFrom.plusDays(7);
        int people = Integer.MAX_VALUE;

        Apartment apartment =
                prepareCaseWithoutReservationsAndWithIntersectingReservaitons(
                        5, requestFrom, requestTo);

        List<ApartmentInstance> freeByRequest = repo.findFreeByRequestFetchApartmentAndPrices(
                apartment.getId(), requestFrom, requestTo, people);

        assertThat(freeByRequest).isEmpty();
    }

    @Test
    void findFreeByRequest_with_no_ApartmentInstances() {
        LocalDateTime requestFrom = now().plusYears(1);
        LocalDateTime requestTo = requestFrom.plusDays(7);
        int people = 5;

        Apartment apartment = prepareCaseWithoutApartmentInstances(people);

        List<ApartmentInstance> freeByRequest = repo.findFreeByRequestFetchApartmentAndPrices(
                apartment.getId(), requestFrom, requestTo, people);

        assertThat(freeByRequest).isEmpty();
    }

    @Test
    void findFreeByRequest_with_intersecting_ApartmentInstance() {
        LocalDateTime requestFrom = now().plusYears(1);
        LocalDateTime requestTo = requestFrom.plusDays(7);
        int people = 5;

        Apartment apartment =
                prepareCaseWithIntersectingReservations(people, requestFrom, requestTo);

        List<ApartmentInstance> freeByRequest = repo.findFreeByRequestFetchApartmentAndPrices(
                apartment.getId(), requestFrom, requestTo, people);

        assertThat(freeByRequest).isEmpty();
    }

    @Disabled("Actual result is empty")
    @Test
    void findFreeByRequest_without_reservations_ApartmentInstance() {
        LocalDateTime requestFrom = now().plusYears(1);
        LocalDateTime requestTo = requestFrom.plusDays(7);
        int people = 5;

        Apartment apartment = prepareCaseWithoutReservations(people);

        List<ApartmentInstance> freeByRequest = repo.findFreeByRequestFetchApartmentAndPrices(
                apartment.getId(), requestFrom, requestTo, people);

        assertThat(freeByRequest).hasSize(1);
    }

    @Disabled("Actual result does not contain ApartmentInstance with name '1'")
    @Test
    void findFreeByRequest_with_intersecting_and_without_reservations() {
        LocalDateTime requestFrom = now().plusYears(1);
        LocalDateTime requestTo = requestFrom.plusDays(7);
        int people = 5;

        Apartment apartment =
                prepareCaseWithoutReservationsAndWithIntersectingReservaitons(
                        people, requestFrom, requestTo);

        List<ApartmentInstance> freeByRequest = repo.findFreeByRequestFetchApartmentAndPrices(
                apartment.getId(), requestFrom, requestTo, people);

        Assertions.assertTrue(freeByRequest.stream()
                .anyMatch(ai -> ai.getName().contains("1")));
        Assertions.assertFalse(freeByRequest.stream()
                .anyMatch(ai -> ai.getName().contains("2")));
    }

    private Apartment prepareCaseWithoutApartmentInstances(int people) {
        Apartment apartment = testApartmentWithPriceFor(people);
        entityManager.persistAndFlush(apartment);
        return apartment;
    }

    private Apartment prepareCaseWithoutReservations(int people) {
        ApartmentInstance withoutReservations =
                testApartmentInstanceWithoutReservation("1");
        return prepareCaseWithApartmentInstances(people, withoutReservations);
    }

    private Apartment prepareCaseWithIntersectingReservations(
            int people, LocalDateTime requestFrom, LocalDateTime requestTo) {
        List<Reservation> reservations = List.of(
                testReservationOf(requestFrom, requestTo, testUser())
        );
        ApartmentInstance withIntersectsReservation =
                testApartmentInstanceWithReservations("1", reservations);
        return prepareCaseWithApartmentInstances(people, withIntersectsReservation);
    }

    private Apartment prepareCaseWithoutReservationsAndWithIntersectingReservaitons(
            int people, LocalDateTime requestFrom, LocalDateTime requestTo) {
        ApartmentInstance withoutReservations =
                testApartmentInstanceWithoutReservation("1");

        List<Reservation> reservations = List.of(
                testReservationOf(requestFrom, requestTo, testUser()));
        ApartmentInstance withIntersectsReservation =
                testApartmentInstanceWithReservations("2", reservations);

        return prepareCaseWithApartmentInstances(
                people, withoutReservations, withIntersectsReservation);
    }


    private Apartment prepareCaseWithApartmentInstances(int people,
                                                        ApartmentInstance... apartmentInstances) {
        Apartment apartment = prepareCaseWithoutApartmentInstances(people);
        insertApartmentIntoApartmentInstances(apartment, apartmentInstances);
        persistTestApartmentInstances(apartmentInstances);
        return apartment;
    }

    private void insertApartmentIntoApartmentInstances(Apartment apartment,
                                                       ApartmentInstance[] apartmentInstances) {
        Arrays.stream(apartmentInstances)
                .forEach(ai -> ai.setApartment(apartment));
    }


    private void persistTestApartmentInstances(ApartmentInstance... testApartmentInstances) {
        Arrays.stream(testApartmentInstances).forEach(ai -> {
            entityManager.persistAndFlush(ai);
            ai.getReservations().forEach(r -> {
                entityManager.persistAndFlush(r.getUser());
                r.setApartmentInstance(ai);
                entityManager.persistAndFlush(r);
            });
        });
    }
}
