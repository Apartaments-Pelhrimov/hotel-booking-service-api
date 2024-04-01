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

import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.id.ApartmentId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class FakeInMemoryApartmentRepository implements ApartmentRepository {

    private final Map<ApartmentId, Apartment> apartments = new HashMap<>();

    @Override
    public Optional<Apartment> findByIdFetchPhotos(ApartmentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Apartment> findAllFetchPhotos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Apartment> findByIdFetchPrices(ApartmentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Apartment> findByIdFetchInstances(ApartmentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Apartment> findByIdFetchPhotosPricesRoomsBeds(ApartmentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Apartment> findAllFetchFetchPhotosPricesRoomsBeds() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Apartment save(Apartment apartment) {
        return apartments.put(apartment.getId(), apartment);
    }

    @Override
    public Optional<Apartment> findById(ApartmentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Apartment getReferenceById(ApartmentId id) {
        return apartments.get(id);
    }

    @Override
    public void deleteById(ApartmentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsById(ApartmentId id) {
        return apartments.containsKey(id);
    }

    public void deleteAll() {
        apartments.clear();
    }
}