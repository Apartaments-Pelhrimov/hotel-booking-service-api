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

package ua.mibal.booking.application.port.jpa;

import org.junit.jupiter.api.Test;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.id.ApartmentId;
import ua.mibal.test.annotation.UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class FakeInMemoryApartmentRepositoryTest {

    private final FakeInMemoryApartmentRepository repository = new FakeInMemoryApartmentRepository();

    private Apartment apartment;
    private Apartment found;
    private boolean exists;

    @Test
    void save() {
        givenApartment(new ApartmentId("1L"));

        whenSave();

        thenApartmentIsSaved(new ApartmentId("1L"));
    }

    @Test
    void existsById() {
        givenSavedApartment(new ApartmentId("1L"));

        whenExistsById(new ApartmentId("1L"));

        thenExistsIs(true);
    }

    @Test
    void getReferenceById() {
        givenSavedApartment(new ApartmentId("1L"));

        whenGetReferenceById(new ApartmentId("1L"));

        thenApartmentsEquals();
    }

    @Test
    void deleteAll() {
        givenSavedApartment(new ApartmentId("1L"));

        whenDeleteAll();

        thenApartmentIsDeleted(new ApartmentId("1L"));
    }

    private void givenApartment(ApartmentId id) {
        apartment = new Apartment();
        apartment.setId(id);
    }

    private void givenSavedApartment(ApartmentId id) {
        givenApartment(id);
        whenSave();
    }

    private void whenSave() {
        repository.save(apartment);
    }

    private void whenExistsById(ApartmentId id) {
        exists = repository.existsById(id);
    }

    private void whenGetReferenceById(ApartmentId id) {
        found = repository.getReferenceById(id);
    }

    private void thenApartmentIsSaved(ApartmentId id) {
        assertTrue(repository.existsById(id));
    }

    private void whenDeleteAll() {
        repository.deleteAll();
    }

    private void thenApartmentIsDeleted(ApartmentId id) {
        assertFalse(repository.existsById(id));
    }

    private void thenExistsIs(boolean expected) {
        assertEquals(expected, exists);
    }

    private void thenApartmentsEquals() {
        assertEquals(apartment, found);
    }
}
