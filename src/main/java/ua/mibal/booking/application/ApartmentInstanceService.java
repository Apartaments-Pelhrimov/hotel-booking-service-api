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
import ua.mibal.booking.application.exception.ApartmentInstanceNotFoundException;
import ua.mibal.booking.application.exception.ApartmentIsNotAvailableForReservation;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.mapper.ApartmentInstanceMapper;
import ua.mibal.booking.application.model.CreateApartmentInstanceForm;
import ua.mibal.booking.application.model.ReservationForm;
import ua.mibal.booking.application.port.jpa.ApartmentInstanceRepository;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.id.ApartmentId;

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

    public ApartmentInstance getFreeOneFetchApartmentAndPrices(ReservationForm form) {
        List<ApartmentInstance> free = getFree(form);
        return selectMostSuitable(free, form);
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

    private List<ApartmentInstance> getFree(ReservationForm form) {
        List<ApartmentInstance> freeLocal = getFreeLocal(form);
        reservationSystemManager.filterForFree(freeLocal, form);
        return freeLocal;
    }

    private List<ApartmentInstance> getFreeLocal(ReservationForm form) {
        List<ApartmentInstance> freeLocal =
                apartmentInstanceRepository.findFreeByRequestFetchApartmentAndPrices(form);
        return new ArrayList<>(freeLocal);
    }

    private ApartmentInstance selectMostSuitable(List<ApartmentInstance> variants,
                                                 ReservationForm form) {
        if (variants.isEmpty()) {
            throw new ApartmentIsNotAvailableForReservation();
        }
        if (variants.size() == 1) {
            return variants.get(0);
        }
        // TODO implement logic
        return variants.get(0);
    }

    private void validateApartmentExists(ApartmentId id) {
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
