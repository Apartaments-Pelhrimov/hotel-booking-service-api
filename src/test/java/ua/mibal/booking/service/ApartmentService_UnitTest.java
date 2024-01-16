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
import ua.mibal.booking.model.dto.request.UpdateApartmentDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.repository.ApartmentRepository;

import java.util.List;
import java.util.Optional;

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
    private ApartmentMapper apartmentMapper;

    @Mock
    private Apartment apartment;
    @Mock
    private ApartmentDto apartmentDto;
    @Mock
    private ApartmentCardDto apartmentCardDto;
    @Mock
    private CreateApartmentDto createApartmentDto;
    @Mock
    private UpdateApartmentDto updateApartmentDto;

    @Test
    public void create() {
        when(apartmentMapper.toEntity(createApartmentDto)).thenReturn(apartment);

        service.create(createApartmentDto);

        verify(apartmentRepository).save(apartment);
    }

    @Test
    public void update() {
        Long id = 1L;
        when(apartmentRepository.findById(id)).thenReturn(Optional.of(apartment));

        service.update(updateApartmentDto, id);

        verify(apartmentMapper, times(1)).update(apartment, updateApartmentDto);
    }

    @Test
    public void update_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.update(updateApartmentDto, id)
        );

        verifyNoInteractions(apartmentMapper);
    }

    @Test
    void getOneFetchPhotosBeds() {
        when(apartmentRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.of(apartment));
        when(apartmentMapper.toDto(apartment))
                .thenReturn(apartmentDto);

        ApartmentDto actual = assertDoesNotThrow(
                () -> service.getOneFetchPhotosBeds(1L)
        );

        assertEquals(apartmentDto, actual);
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
    void getAllFetchPhotosBeds() {
        when(apartmentRepository.findAllFetchPhotos())
                .thenReturn(List.of(apartment, apartment));
        when(apartmentMapper.toCardDto(apartment))
                .thenReturn(apartmentCardDto);

        List<ApartmentCardDto> actual = service.getAllFetchPhotosBeds();

        assertEquals(
                List.of(apartmentCardDto, apartmentCardDto),
                actual
        );
    }

    @Test
    public void getOneFetchPrices() {
        Long id = 1L;
        when(apartmentRepository.findByIdFetchPrices(id)).thenReturn(Optional.of(apartment));

        Apartment actual = service.getOneFetchPrices(id);

        assertEquals(apartment, actual);
    }

    @Test
    public void getOneFetchPrices_should_throw_ApartmentNotFoundException() {
        Long id = 1L;
        when(apartmentRepository.findByIdFetchPrices(id)).thenReturn(Optional.empty());

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.getOneFetchPrices(id)
        );
    }
}
