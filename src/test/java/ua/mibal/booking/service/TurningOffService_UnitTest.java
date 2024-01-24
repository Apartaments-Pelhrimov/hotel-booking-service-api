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

package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.request.TurnOffDto;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;
import ua.mibal.booking.model.exception.IllegalTurningOffTimeException;
import ua.mibal.booking.model.mapper.TurningOffTimeMapper;
import ua.mibal.booking.repository.HotelTurningOffRepository;
import ua.mibal.booking.repository.ReservationRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TurningOffService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TurningOffService_UnitTest {

    @Autowired
    private TurningOffService service;

    @MockBean
    private TurningOffTimeMapper turningOffTimeMapper;
    @MockBean
    private ReservationRepository reservationRepository;
    @MockBean
    private ApartmentInstanceService apartmentInstanceService;
    @MockBean
    private HotelTurningOffRepository hotelTurningOffRepository;

    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private HotelTurningOffTime hotelTurningOffTime;
    @Mock
    private TurningOffTime turningOffTime;

    @Spy
    private TurnOffDto turnOffDto =
            new TurnOffDto(now(), now().plusDays(1), "event");

    @Test
    void turnOffApartmentInstance() {
        Long instanceId = 1L;

        when(apartmentInstanceService.getOneFetchReservations(instanceId))
                .thenReturn(apartmentInstance);
        when(apartmentInstance.hasReservationsAt(turnOffDto.from(), turnOffDto.to()))
                .thenReturn(false);
        when(turningOffTimeMapper.apartmentfromDto(turnOffDto))
                .thenReturn(turningOffTime);

        service.turnOffApartmentInstance(instanceId, turnOffDto);

        verify(apartmentInstance, times(1))
                .addTurningOffTime(turningOffTime);
    }

    @Test
    void turnOffApartmentInstance_should_throw_IllegalTurningOffTimeException() {
        Long instanceId = 1L;

        when(apartmentInstanceService.getOneFetchReservations(instanceId))
                .thenReturn(apartmentInstance);
        when(apartmentInstance.hasReservationsAt(turnOffDto.from(), turnOffDto.to()))
                .thenReturn(true);

        assertThrows(IllegalTurningOffTimeException.class,
                () -> service.turnOffApartmentInstance(instanceId, turnOffDto));
    }

    @Test
    void turnOffHotel() {
        when(reservationRepository.existsReservationThatIntersectsRange(
                turnOffDto.from(), turnOffDto.to()))
                .thenReturn(false);
        when(turningOffTimeMapper.hotelFromDto(turnOffDto))
                .thenReturn(hotelTurningOffTime);

        service.turnOffHotel(turnOffDto);

        verify(hotelTurningOffRepository, times(1))
                .save(hotelTurningOffTime);
    }

    @Test
    void turnOffHotel_should_throw_IllegalTurningOffTimeException() {
        when(reservationRepository.existsReservationThatIntersectsRange(
                turnOffDto.from(), turnOffDto.to()))
                .thenReturn(true);

        assertThrows(IllegalTurningOffTimeException.class,
                () -> service.turnOffHotel(turnOffDto));
    }

    @Test
    void getForHotelForNow() {
        when(hotelTurningOffRepository.findFromNow())
                .thenReturn(List.of(hotelTurningOffTime, hotelTurningOffTime));

        var actual = service.getForHotelForNow();

        assertEquals(List.of(hotelTurningOffTime, hotelTurningOffTime), actual);
    }
}
