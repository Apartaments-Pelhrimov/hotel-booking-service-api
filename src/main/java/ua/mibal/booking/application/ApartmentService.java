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
import ua.mibal.booking.application.dto.request.CreateApartmentDto;
import ua.mibal.booking.application.dto.request.UpdateApartmentDto;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.mapper.ApartmentMapper;
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

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public List<Apartment> getAllFetchPhotosBeds() {
        return apartmentRepository.findAllFetchPhotosRoomsBeds();
    }

    public Apartment getOne(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
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
        return apartmentRepository.findByIdFetchPhotosRoomsBeds(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public void create(CreateApartmentDto createApartmentDto) {
        Apartment apartment = apartmentMapper.toEntity(createApartmentDto);
        apartmentRepository.save(apartment);
    }

    @Transactional
    public void update(UpdateApartmentDto updateApartmentDto, Long id) {
        Apartment apartment = getOne(id);
        apartmentMapper.update(apartment, updateApartmentDto);
    }

    public void delete(Long id) {
        validateExists(id);
        apartmentRepository.deleteById(id);
    }

    private void validateExists(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
    }
}
