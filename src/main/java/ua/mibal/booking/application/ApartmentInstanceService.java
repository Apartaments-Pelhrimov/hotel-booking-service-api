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
import ua.mibal.booking.application.dto.CreateApartmentInstanceForm;
import ua.mibal.booking.application.exception.ApartmentInstanceNotFoundException;
import ua.mibal.booking.application.exception.ApartmentIsNotAvailableForReservation;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.mapper.ApartmentInstanceMapper;
import ua.mibal.booking.application.port.jpa.ApartmentInstanceRepository;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.ReservationRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentInstanceService {
    private final ApartmentInstanceRepository apartmentInstanceRepository;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentInstanceMapper apartmentInstanceMapper;
    private final ReservationSystemManager reservationSystemManager;

    public ApartmentInstance getFreeOneFetchApartmentAndPrices(ReservationRequest request) {
        List<ApartmentInstance> free = getFree(request);
        return selectMostSuitable(free, request);
    }

    public void create(CreateApartmentInstanceForm form) {
        validateApartmentExists(form.getApartmentId());
        ApartmentInstance newInstance = assembleBy(form);
        apartmentInstanceRepository.save(newInstance);
    }

    public void delete(Long id) {
        validateApartmentInstanceExists(id);
        apartmentInstanceRepository.deleteById(id);
    }

    public ApartmentInstance getOneFetchReservations(Long id) {
        return apartmentInstanceRepository.findByIdFetchReservations(id)
                .orElseThrow(() -> new ApartmentInstanceNotFoundException(id));
    }

    private ApartmentInstance assembleBy(CreateApartmentInstanceForm form) {
        ApartmentInstance apartmentInstance = apartmentInstanceMapper.assemble(form);
        Apartment apartmentRef = apartmentRepository.getReferenceById(form.getApartmentId());
        apartmentInstance.setApartment(apartmentRef);
        return apartmentInstance;
    }

    private List<ApartmentInstance> getFree(ReservationRequest request) {
        List<ApartmentInstance> freeLocal = getFreeLocal(request);
        reservationSystemManager.filterForFree(freeLocal, request);
        return freeLocal;
    }

    private List<ApartmentInstance> getFreeLocal(ReservationRequest request) {
        List<ApartmentInstance> freeLocal =
                apartmentInstanceRepository.findFreeByRequestFetchApartmentAndPrices(request);
        return new ArrayList<>(freeLocal);
    }

    private ApartmentInstance selectMostSuitable(List<ApartmentInstance> variants,
                                                 ReservationRequest request) {
        if (variants.isEmpty()) {
            throw new ApartmentIsNotAvailableForReservation();
        }
        if (variants.size() == 1) {
            return variants.get(0);
        }
        // TODO implement logic
        return variants.get(0);
    }

    private void validateApartmentExists(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
    }

    private void validateApartmentInstanceExists(Long id) {
        if (!apartmentInstanceRepository.existsById(id)) {
            throw new ApartmentInstanceNotFoundException(id);
        }
    }
}
