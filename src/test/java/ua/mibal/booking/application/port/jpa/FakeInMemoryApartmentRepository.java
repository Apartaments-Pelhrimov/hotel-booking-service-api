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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.domain.Apartment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class FakeInMemoryApartmentRepository implements ApartmentRepository {

    private final Map<Long, Apartment> apartments = new HashMap<>();

    @Override
    public Optional<Apartment> findByIdFetchPhotos(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Apartment> findAllFetchPhotos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Apartment> findByIdFetchPrices(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Apartment> findByIdFetchInstances(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Apartment> findByIdFetchPhotosRooms(Long id) {
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
    public Optional<Apartment> findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Apartment getReferenceById(Long id) {
        return apartments.get(id);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsById(Long id) {
        return apartments.containsKey(id);
    }

    @Override
    public Page<Apartment> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Apartment apartment) {
        throw new UnsupportedOperationException();
    }

    public void deleteAll() {
        apartments.clear();
    }
}
