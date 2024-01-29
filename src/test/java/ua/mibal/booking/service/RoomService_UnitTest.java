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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.model.dto.request.RoomDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Room;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.exception.entity.RoomNotFoundException;
import ua.mibal.booking.model.mapper.RoomMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.RoomRepository;

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
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RoomService_UnitTest {

    private RoomService service;

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private RoomMapper roomMapper;

    @Mock
    private Apartment apartment;
    @Mock
    private Room room;
    @Mock
    private RoomDto roomDto;

    @BeforeEach
    void setup() {
        service = new RoomService(roomRepository, apartmentRepository, roomMapper);
    }

    @Test
    public void delete() {
        Long id = 1L;
        when(roomRepository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(roomRepository, times(1)).deleteById(id);
    }

    @Test
    public void delete_should_throw_RoomNotFoundException() {
        Long id = 1L;
        when(roomRepository.existsById(id)).thenReturn(false);

        assertThrows(
                RoomNotFoundException.class,
                () -> service.delete(id)
        );

        verify(roomRepository, never()).deleteById(id);
    }

    @Test
    public void create() {
        when(apartment.getId()).thenReturn(1L);
        when(apartmentRepository.existsById(1L)).thenReturn(true);
        when(roomMapper.toEntity(roomDto)).thenReturn(room);
        when(apartmentRepository.getReferenceById(1L)).thenReturn(apartment);

        service.create(apartment.getId(), roomDto);

        verify(room, times(1)).setApartment(apartment);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    public void create_should_throw_ApartmentNotFoundException() {
        when(apartment.getId()).thenReturn(1L);
        when(apartmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                ApartmentNotFoundException.class,
                () -> service.create(apartment.getId(), roomDto)
        );

        verifyNoInteractions(roomMapper, room, roomRepository);
    }
}
