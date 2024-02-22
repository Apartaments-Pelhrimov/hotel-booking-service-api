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
import ua.mibal.booking.application.dto.request.CreateApartmentDto;
import ua.mibal.booking.application.dto.request.UpdateApartmentDto;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.mapper.ApartmentMapper;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
@UnitTest
class ApartmentService_UnitTest {

    private ApartmentService service;

    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private ApartmentMapper apartmentMapper;

    @Mock
    private Apartment apartment;
    @Mock
    private CreateApartmentDto createApartmentDto;
    @Mock
    private UpdateApartmentDto updateApartmentDto;

    @BeforeEach
    void setup() {
        service = new ApartmentService(apartmentRepository, apartmentMapper);
    }

    @Test
    void getAllFetchPhotosBeds() {
        when(apartmentRepository.findAllFetchPhotosRooms())
                .thenReturn(List.of(apartment, apartment));

        List<Apartment> actual = service.getAllFetchPhotosBeds();

        assertThat(actual).containsOnly(apartment, apartment);
    }

    @Test
    void getOneFetchPhotos() {
        Long id = 1L;

        when(apartmentRepository.findByIdFetchPhotos(id))
                .thenReturn(Optional.of(apartment));

        var actual = service.getOneFetchPhotos(id);

        assertEquals(apartment, actual);
    }

    @Test
    void getOneFetchPhotos_should_throw_ApartmentNotFoundException() {
        Long id = 1L;

        when(apartmentRepository.findByIdFetchPhotos(id))
                .thenReturn(Optional.empty());

        assertThrows(ApartmentNotFoundException.class,
                () -> service.getOneFetchPhotos(id));
    }

    @Test
    void getOneFetchInstances() {
        Long id = 1L;

        when(apartmentRepository.findByIdFetchInstances(id))
                .thenReturn(Optional.of(apartment));

        var actual = service.getOneFetchInstances(id);

        assertEquals(apartment, actual);
    }

    @Test
    void getOneFetchInstances_should_throw_ApartmentNotFoundException() {
        Long id = 1L;

        when(apartmentRepository.findByIdFetchInstances(id))
                .thenReturn(Optional.empty());

        assertThrows(ApartmentNotFoundException.class,
                () -> service.getOneFetchInstances(id));
    }

    @Test
    void getOneFetchPrices() {
        Long id = 1L;
        when(apartmentRepository.findByIdFetchPrices(id)).thenReturn(Optional.of(apartment));

        Apartment actual = service.getOneFetchPrices(id);

        assertEquals(apartment, actual);
    }

    @Test
    void getOneFetchPrices_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.findByIdFetchPrices(id)).thenReturn(Optional.empty());

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.getOneFetchPrices(id)
        );
    }

    @Test
    void getOneFetchPhotosBeds() {
        when(apartmentRepository.findByIdFetchPhotosRooms(1L))
                .thenReturn(Optional.of(apartment));

        Apartment actual = assertDoesNotThrow(
                () -> service.getOneFetchPhotosBeds(1L)
        );

        assertEquals(apartment, actual);
    }

    @Test
    void getOneFetchPhotosBeds_should_throw_ApartmentNotFoundException() {
        when(apartmentRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.empty());

        ApartmentNotFoundException e = assertThrows(
                ApartmentNotFoundException.class,
                () -> service.getOneFetchPhotosBeds(1L)
        );

        assertEquals(
                new ApartmentNotFoundException(1L).getMessage(),
                e.getMessage()
        );
        verifyNoInteractions(apartmentMapper);

    }

    @Test
    void create() {
        when(apartmentMapper.toEntity(createApartmentDto)).thenReturn(apartment);

        service.create(createApartmentDto);

        verify(apartmentRepository).save(apartment);
    }

    @Test
    void update() {
        Long id = 1L;
        when(apartmentRepository.findById(id)).thenReturn(Optional.of(apartment));

        service.update(updateApartmentDto, id);

        verify(apartmentMapper, times(1)).update(apartment, updateApartmentDto);
    }

    @Test
    void update_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.update(updateApartmentDto, id)
        );

        verifyNoInteractions(apartmentMapper);
    }

    @Test
    void delete() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(apartmentRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.delete(id)
        );

        verify(apartmentRepository, never()).deleteById(id);
    }
}
