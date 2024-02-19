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
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.HotelTurningOffTime;
import ua.mibal.booking.domain.TurningOffTime;
import ua.mibal.booking.model.dto.request.TurnOffDto;
import ua.mibal.booking.model.exception.IllegalTurningOffTimeException;
import ua.mibal.booking.model.mapper.TurningOffTimeMapper;
import ua.mibal.booking.repository.HotelTurningOffRepository;
import ua.mibal.booking.repository.ReservationRepository;

import java.util.List;

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
    public void turnOffApartmentInstance(Long instanceId, TurnOffDto turnOffDto) {
        ApartmentInstance instance =
                apartmentInstanceService.getOneFetchReservations(instanceId);
        validateToTurnOffApartmentInstance(instance, turnOffDto);
        TurningOffTime turningOffTime =
                turningOffTimeMapper.apartmentfromDto(turnOffDto);
        instance.addTurningOffTime(turningOffTime);
    }

    public void turnOffHotel(TurnOffDto turnOffDto) {
        validateToTurnOffHotel(turnOffDto);
        HotelTurningOffTime turningOffTime =
                turningOffTimeMapper.hotelFromDto(turnOffDto);
        hotelTurningOffRepository.save(turningOffTime);
    }

    public List<HotelTurningOffTime> getForHotelForNow() {
        return hotelTurningOffRepository.findFromNow();
    }

    private void validateToTurnOffApartmentInstance(ApartmentInstance instance,
                                                    TurnOffDto turnOffDto) {
        if (instance.hasReservationsAt(turnOffDto.from(), turnOffDto.to())) {
            throw new IllegalTurningOffTimeException();
        }
    }

    private void validateToTurnOffHotel(TurnOffDto turnOffDto) {
        if (reservationRepository.existsReservationThatIntersectsRange(
                turnOffDto.from(), turnOffDto.to())) {
            throw new IllegalTurningOffTimeException();
        }
    }
}
