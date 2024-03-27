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

package ua.mibal.booking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.mapper.ApartmentMapper;
import ua.mibal.booking.application.model.ChangeApartmentForm;
import ua.mibal.booking.application.model.ChangeApartmentOptionsForm;
import ua.mibal.booking.application.model.CreateApartmentForm;
import ua.mibal.booking.application.model.SearchQuery;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.domain.Apartment;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ApartmentMapper apartmentMapper;

    public List<Apartment> getPropositionsFetchPhotosPricesRoomsBeds() {
        // TODO implement filtering logic
        return apartmentRepository.findAllFetchFetchPhotosPricesRoomsBeds();
    }

    public List<Apartment> getByQueryFetchPhotosPricesRoomsBeds(SearchQuery searchQuery) {
        // TODO
        //  service also defines default values for query
        return apartmentRepository.findAllFetchFetchPhotosPricesRoomsBeds();
    }

    public Apartment getOneFetchPhotos(Long id) {
        return apartmentRepository.findByIdFetchPhotos(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public Apartment getOneFetchInstances(Long id) {
        return apartmentRepository.findByIdFetchInstances(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public Apartment getOneFetchPrices(Long id) {
        return apartmentRepository.findByIdFetchPrices(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public Apartment getOneFetchPhotosBeds(Long id) {
        return apartmentRepository.findByIdFetchPhotosRooms(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public void create(CreateApartmentForm form) {
        Apartment apartment = apartmentMapper.assemble(form);
        apartmentRepository.save(apartment);
    }

    @Transactional
    public void change(Long id, ChangeApartmentForm form) {
        Apartment apartment = getOne(id);
        apartmentMapper.change(apartment, form);
    }

    @Transactional
    public void changeOptions(Long id, ChangeApartmentOptionsForm form) {
        Apartment apartment = getOne(id);
        apartmentMapper.change(apartment.getOptions(), form);
    }

    public void delete(Long id) {
        validateExists(id);
        apartmentRepository.deleteById(id);
    }

    private Apartment getOne(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    private void validateExists(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
    }
}
