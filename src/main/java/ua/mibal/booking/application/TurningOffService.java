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
import ua.mibal.booking.application.exception.IllegalTurningOffTimeException;
import ua.mibal.booking.application.mapper.TurningOffTimeMapper;
import ua.mibal.booking.application.model.TurnOffForm;
import ua.mibal.booking.application.port.jpa.HotelTurningOffRepository;
import ua.mibal.booking.application.port.jpa.ReservationRepository;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.HotelTurningOffTime;
import ua.mibal.booking.domain.TurningOffTime;

import java.util.List;

import static java.time.LocalDateTime.now;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class TurningOffService {
    private final TurningOffTimeMapper turningOffTimeMapper;
    private final ReservationRepository reservationRepository;
    private final ApartmentInstanceService apartmentInstanceService;
    private final HotelTurningOffRepository hotelTurningOffRepository;

    @Transactional
    public void turnOffApartmentInstance(Long instanceId, TurnOffForm form) {
        ApartmentInstance instance =
                apartmentInstanceService.getOneFetchReservations(instanceId);
        validateToTurnOffApartmentInstance(instance, form);
        TurningOffTime turningOffTime =
                turningOffTimeMapper.assembleForApartmentInstance(form);
        instance.addTurningOffTime(turningOffTime);
    }

    public void turnOffHotel(TurnOffForm form) {
        validateToTurnOffHotel(form);
        HotelTurningOffTime turningOffTime =
                turningOffTimeMapper.assembleForHotel(form);
        hotelTurningOffRepository.save(turningOffTime);
    }

    public List<HotelTurningOffTime> getForHotelForNow() {
        return hotelTurningOffRepository.findActualFor(now());
    }

    private void validateToTurnOffApartmentInstance(ApartmentInstance instance,
                                                    TurnOffForm turnOffForm) {
        if (instance.hasReservationsAt(turnOffForm.from(), turnOffForm.to())) {
            throw new IllegalTurningOffTimeException();
        }
    }

    private void validateToTurnOffHotel(TurnOffForm turnOffForm) {
        if (reservationRepository.existsReservationThatIntersectsRange(
                turnOffForm.from(), turnOffForm.to())) {
            throw new IllegalTurningOffTimeException();
        }
    }
}
