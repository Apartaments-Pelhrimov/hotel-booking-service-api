/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.request.CreateApartmentDto;
import ua.mibal.booking.model.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.model.dto.request.RoomDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Room;
import ua.mibal.booking.model.exception.ApartmentIsNotAvialableForReservation;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.model.mapper.RoomMapper;
import ua.mibal.booking.model.request.ReservationFormRequest;
import ua.mibal.booking.repository.ApartmentInstanceRepository;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.RoomRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.MIN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApartmentService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentService_UnitTest {

    @Autowired
    private ApartmentService service;

    @MockBean
    private ApartmentRepository apartmentRepository;
    @MockBean
    private ApartmentInstanceRepository apartmentInstanceRepository;
    @MockBean
    private RoomRepository roomRepository;
    @MockBean
    private ApartmentMapper apartmentMapper;
    @MockBean
    private RoomMapper roomMapper;
    @MockBean
    private DateTimeUtils dateTimeUtils;
    @MockBean
    private BookingComReservationService bookingComReservationService;

    @Mock
    private Apartment apartment;
    @Mock
    private ApartmentDto apartmentDto;
    @Mock
    private ApartmentCardDto apartmentCardDto;
    @Mock
    private ApartmentInstance apartmentInstance;
    @Mock
    private LocalDate dateFrom;
    @Mock
    private LocalDate dateTo;
    @Mock
    private CreateApartmentDto createApartmentDto;
    @Mock
    private CreateApartmentInstanceDto createApartmentInstanceDto;
    @Mock
    private Room room;
    @Mock
    private RoomDto roomDto;

    @Test
    void getOneDto() {
        when(apartmentRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.of(apartment));
        when(apartmentMapper.toDto(apartment))
                .thenReturn(apartmentDto);

        ApartmentDto actual = assertDoesNotThrow(
                () -> service.getOneDto(1L)
        );

        assertEquals(apartmentDto, actual);
    }

    @Test
    void getOneDto_should_throw_ApartmentNotFoundException() {
        when(apartmentRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.empty());

        ApartmentNotFoundException e = assertThrows(
                ApartmentNotFoundException.class,
                () -> service.getOneDto(1L)
        );

        assertEquals(
                new ApartmentNotFoundException(1L).getMessage(),
                e.getMessage()
        );
        verifyNoInteractions(apartmentMapper);

    }

    @Test
    void getOne() {
        when(apartmentRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.of(apartment));

        Apartment actual = assertDoesNotThrow(
                () -> service.getOne(1L)
        );

        assertEquals(apartment, actual);
    }

    @Test
    void getOne_should_throw_ApartmentNotFoundException() {
        when(apartmentRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.empty());

        ApartmentNotFoundException e = assertThrows(
                ApartmentNotFoundException.class,
                () -> service.getOne(1L)
        );

        assertEquals(
                new ApartmentNotFoundException(1L).getMessage(),
                e.getMessage()
        );
        verifyNoInteractions(apartmentMapper);
    }

    @Test
    void getAll() {
        when(apartmentRepository.findAllFetchPhotos())
                .thenReturn(List.of(apartment, apartment));
        when(apartmentMapper.toCardDto(apartment))
                .thenReturn(apartmentCardDto);

        List<ApartmentCardDto> actual = service.getAll();

        assertEquals(
                List.of(apartmentCardDto, apartmentCardDto),
                actual
        );
    }

    @Test
    public void getFreeApartmentInstanceByApartmentId() {
        when(dateTimeUtils.reserveFrom(dateFrom)).thenReturn(MIN);
        when(dateTimeUtils.reserveTo(dateTo)).thenReturn(MAX);
        when(apartmentRepository.findFreeApartmentInstanceByApartmentIdAndDates(1L, MIN, MAX, 1))
                .thenReturn(List.of(apartmentInstance));
        when(bookingComReservationService.isFree(apartmentInstance, MIN, MAX))
                .thenReturn(true);
        // TODO check selecting logic

        ApartmentInstance actual = service
                .getFreeApartmentInstanceByApartmentId(1L, new ReservationFormRequest(dateFrom, dateTo, 1));

        assertEquals(apartmentInstance, actual);
    }

    @Test
    public void getFreeApartmentInstanceByApartmentId_should_throw_FreeApartmentsForDateNotFoundException() {
        when(dateTimeUtils.reserveFrom(dateFrom)).thenReturn(MIN);
        when(dateTimeUtils.reserveTo(dateTo)).thenReturn(MAX);
        when(apartmentRepository.findFreeApartmentInstanceByApartmentIdAndDates(1L, MIN, MAX, 1))
                .thenReturn(List.of());

        ApartmentIsNotAvialableForReservation e = assertThrows(
                ApartmentIsNotAvialableForReservation.class,
                () -> service.getFreeApartmentInstanceByApartmentId(1L, new ReservationFormRequest(dateFrom, dateTo, 1))
        );

        assertEquals(
                new ApartmentIsNotAvialableForReservation(MIN, MAX, 1L).getMessage(),
                e.getMessage()
        );
    }

    @Test
    public void createApartment() {
        when(apartmentMapper.toEntity(createApartmentDto)).thenReturn(apartment);

        service.createApartment(createApartmentDto);

        verify(apartmentRepository).save(apartment);
    }

    @Test
    public void delete() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(apartmentRepository, times(1)).deleteById(id);
    }

    @Test
    public void delete_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.delete(id)
        );

        verify(apartmentRepository, never()).deleteById(id);
    }

    @Test
    public void addInstance() {
        when(apartment.getId()).thenReturn(1L);
        when(apartmentRepository.existsById(1L)).thenReturn(true);
        when(apartmentMapper.toInstance(createApartmentInstanceDto)).thenReturn(apartmentInstance);
        when(apartmentRepository.getReferenceById(1L)).thenReturn(apartment);

        service.addInstance(apartment.getId(), createApartmentInstanceDto);

        verify(apartmentInstance, times(1)).setApartment(apartment);
        verify(apartmentInstanceRepository, times(1)).save(apartmentInstance);
    }

    @Test
    public void addInstance_should_throw_ApartmentNotFoundException() {
        when(apartment.getId()).thenReturn(1L);
        when(apartmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.addInstance(apartment.getId(), createApartmentInstanceDto)
        );

        verifyNoInteractions(apartmentMapper, apartmentInstance, apartmentInstanceRepository);
    }

    @Test
    public void deleteInstance() {
        Long id = 1L;
        when(apartmentInstanceRepository.existsById(id)).thenReturn(true);

        service.deleteInstance(id);

        verify(apartmentInstanceRepository, times(1)).deleteById(id);
    }

    @Test
    public void deleteInstance_should_throw_ApartmentInstanceNotFoundException() {
        Long id = 1L;
        when(apartmentInstanceRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ApartmentInstanceNotFoundException.class,
                () -> service.deleteInstance(id)
        );

        verify(apartmentInstanceRepository, never()).deleteById(id);
    }

    @Test
    public void addRoom() {
        when(apartment.getId()).thenReturn(1L);
        when(apartmentRepository.existsById(1L)).thenReturn(true);
        when(roomMapper.toEntity(roomDto)).thenReturn(room);
        when(apartmentRepository.getReferenceById(1L)).thenReturn(apartment);

        service.addRoom(apartment.getId(), roomDto);

        verify(room, times(1)).setApartment(apartment);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    public void addRoom_should_throw_ApartmentNotFoundException() {
        when(apartment.getId()).thenReturn(1L);
        when(apartmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.addRoom(apartment.getId(), roomDto)
        );

        verifyNoInteractions(roomMapper, room, roomRepository);
    }
}
