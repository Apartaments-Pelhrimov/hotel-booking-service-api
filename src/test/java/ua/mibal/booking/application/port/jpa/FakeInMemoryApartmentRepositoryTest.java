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
        givenApartment(1L);

        whenSave();

        thenApartmentIsSaved(1L);
    }

    @Test
    void existsById() {
        givenSavedApartment(1L);

        whenExistsById(1L);

        thenExistsIs(true);
    }

    @Test
    void getReferenceById() {
        givenSavedApartment(1L);

        whenGetReferenceById(1L);

        thenApartmentsEquals();
    }

    @Test
    void deleteAll() {
        givenSavedApartment(1L);

        whenDeleteAll();

        thenApartmentIsDeleted(1L);
    }

    private void givenApartment(long id) {
        apartment = new Apartment();
        apartment.setId(id);
    }

    private void givenSavedApartment(long id) {
        givenApartment(id);
        whenSave();
    }

    private void whenSave() {
        repository.save(apartment);
    }

    private void whenExistsById(long id) {
        exists = repository.existsById(id);
    }

    private void whenGetReferenceById(long id) {
        found = repository.getReferenceById(id);
    }

    private void thenApartmentIsSaved(long id) {
        assertTrue(repository.existsById(id));
    }

    private void whenDeleteAll() {
        repository.deleteAll();
    }

    private void thenApartmentIsDeleted(long id) {
        assertFalse(repository.existsById(id));
    }

    private void thenExistsIs(boolean expected) {
        assertEquals(expected, exists);
    }

    private void thenApartmentsEquals() {
        assertEquals(apartment, found);
    }
}
