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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ua.mibal.booking.application.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.application.exception.ApartmentInstanceNotFoundException;
import ua.mibal.booking.application.exception.ApartmentIsNotAvailableForReservation;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.mapper.ApartmentInstanceMapper;
import ua.mibal.booking.application.port.jpa.ApartmentInstanceRepository;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.ApartmentInstance;
import ua.mibal.booking.domain.ReservationRequest;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.MIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class ApartmentInstanceService_UnitTest {

    private ApartmentInstanceService service;

    @Mock
    private ApartmentInstanceRepository apartmentInstanceRepository;
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private ApartmentInstanceMapper apartmentInstanceMapper;
    @Mock
    private ReservationSystemManager reservationSystemManager;

    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private ApartmentInstance apartmentInstance2;
    @Mock
    private Apartment apartment;
    @Mock
    private CreateApartmentInstanceDto createApartmentInstanceDto;

    @BeforeEach
    void setup() {
        service = new ApartmentInstanceService(apartmentInstanceRepository, apartmentRepository, apartmentInstanceMapper, reservationSystemManager);
    }

    @Test
    void getFreeOne() {
        String userEmail = "userEmail";
        Long id = 1L;
        int people = 1;
        ReservationRequest reservationRequest = new ReservationRequest(MIN, MAX, people, id, userEmail);
        List<ApartmentInstance> apartmentInstances = List.of(apartmentInstance2, apartmentInstance);

        when(apartmentInstanceRepository.findFreeByRequestFetchApartmentAndPrices(reservationRequest))
                .thenReturn(apartmentInstances);
        doAnswer(invocation -> {
            List<ApartmentInstance> apartmentInstancesArg = invocation.getArgument(0);
            apartmentInstancesArg.remove(apartmentInstance2);
            return null;
        }).when(reservationSystemManager).filterForFree(apartmentInstances, reservationRequest);

        ApartmentInstance actual = service
                .getFreeOneFetchApartmentAndPrices(reservationRequest);

        assertEquals(apartmentInstance, actual);
    }

    @Test
    void getFreeOne_should_throw_FreeApartmentsForDateNotFoundException() {
        String userEmail = "userEmail";
        Long id = 1L;
        int people = 1;
        ReservationRequest reservationRequest = new ReservationRequest(MIN, MAX, people, id, userEmail);

        when(apartmentInstanceRepository.findFreeByRequestFetchApartmentAndPrices(reservationRequest))
                .thenReturn(List.of());

        ApartmentIsNotAvailableForReservation e = assertThrows(
                ApartmentIsNotAvailableForReservation.class,
                () -> service.getFreeOneFetchApartmentAndPrices(reservationRequest)
        );

        assertEquals(
                new ApartmentIsNotAvailableForReservation().getMessage(),
                e.getMessage()
        );
    }


    @Test
    void create() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(true);
        when(apartmentInstanceMapper.toEntity(createApartmentInstanceDto)).thenReturn(apartmentInstance);
        when(apartmentRepository.getReferenceById(id)).thenReturn(apartment);

        service.create(id, createApartmentInstanceDto);

        verify(apartmentInstance, times(1)).setApartment(apartment);
        verify(apartmentInstanceRepository, times(1)).save(apartmentInstance);
    }

    @Test
    void create_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.create(id, createApartmentInstanceDto)
        );

        verifyNoInteractions(apartmentInstanceMapper, apartmentInstance, apartmentInstanceRepository);
    }

    @Test
    void delete() {
        Long id = 1L;
        when(apartmentInstanceRepository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(apartmentInstanceRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_should_throw_ApartmentInstanceNotFoundException() {
        Long id = 1L;
        when(apartmentInstanceRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ApartmentInstanceNotFoundException.class,
                () -> service.delete(id)
        );

        verify(apartmentInstanceRepository, never()).deleteById(id);
    }

    @Test
    void getOneFetchReservations() {
        Long id = 1L;

        when(apartmentInstanceRepository.findByIdFetchReservations(id))
                .thenReturn(Optional.of(apartmentInstance));

        var actual = service.getOneFetchReservations(id);

        assertEquals(apartmentInstance, actual);
    }

    @Test
    void getOneFetchReservations_should_throw_ApartmentInstanceNotFoundException() {
        Long id = 1L;

        when(apartmentInstanceRepository.findByIdFetchReservations(id))
                .thenReturn(Optional.empty());

        assertThrows(ApartmentInstanceNotFoundException.class,
                () -> service.getOneFetchReservations(id));
    }
}
