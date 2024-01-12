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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.request.ChangeApartmentDto;
import ua.mibal.booking.model.dto.request.CreateApartmentDto;
import ua.mibal.booking.model.dto.request.RoomDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Room;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.exception.entity.RoomNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.model.mapper.RoomMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.RoomRepository;

import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final RoomRepository roomRepository;
    private final ApartmentMapper apartmentMapper;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public ApartmentDto getOneDto(Long id) {
        return apartmentRepository.findByIdFetchPhotos(id)
                .map(apartmentMapper::toDto)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public Apartment getOneFetchPhotos(Long id) {
        return apartmentRepository.findByIdFetchPhotos(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    @Transactional(readOnly = true) // for LAZY Apartment.beds fetch
    public List<ApartmentCardDto> getAll() {
        return apartmentRepository.findAllFetchPhotos()
                .stream()
                .map(apartmentMapper::toCardDto)
                .toList();
    }

    public void createApartment(CreateApartmentDto createApartmentDto) {
        var apartment = apartmentMapper.toEntity(createApartmentDto);
        apartmentRepository.save(apartment);
    }

    @Transactional
    public void update(ChangeApartmentDto changeApartmentDto, Long id) {
        Apartment apartment = getOne(id);
        apartmentMapper.update(apartment, changeApartmentDto);
    }

    private Apartment getOne(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public void delete(Long id) {
        validateExists(id);
        apartmentRepository.deleteById(id);
    }

    public void addRoom(Long apartmentId, RoomDto roomDto) {
        validateExists(apartmentId);
        Room room = roomMapper.toEntity(roomDto);
        room.setApartment(apartmentRepository.getReferenceById(apartmentId));
        roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        validateRoomExists(id);
        roomRepository.deleteById(id);
    }

    public Apartment getOneFetchPrices(Long id) {
        return apartmentRepository.findByIdFetchPrices(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    private void validateExists(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
    }

    private void validateRoomExists(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
    }
}
