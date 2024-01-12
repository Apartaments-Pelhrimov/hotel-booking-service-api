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
import ua.mibal.booking.model.dto.request.CreateApartmentInstanceDto;
import ua.mibal.booking.model.dto.request.PriceDto;
import ua.mibal.booking.model.dto.request.RoomDto;
import ua.mibal.booking.model.dto.response.ApartmentCardDto;
import ua.mibal.booking.model.dto.response.ApartmentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.ApartmentInstance;
import ua.mibal.booking.model.entity.Room;
import ua.mibal.booking.model.entity.embeddable.Price;
import ua.mibal.booking.model.exception.ApartmentIsNotAvialableForReservation;
import ua.mibal.booking.model.exception.entity.ApartmentInstanceNotFoundException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.exception.entity.PriceNotFoundException;
import ua.mibal.booking.model.exception.entity.RoomNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.model.mapper.PriceMapper;
import ua.mibal.booking.model.mapper.RoomMapper;
import ua.mibal.booking.model.request.ReservationFormRequest;
import ua.mibal.booking.repository.ApartmentInstanceRepository;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.RoomRepository;
import ua.mibal.booking.service.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ApartmentInstanceRepository apartmentInstanceRepository;
    private final RoomRepository roomRepository;
    private final ApartmentMapper apartmentMapper;
    private final RoomMapper roomMapper;
    private final PriceMapper priceMapper;
    private final DateTimeUtils dateTimeUtils;
    private final BookingComReservationService bookingComReservationService;

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

    public ApartmentInstance getFreeApartmentInstanceByApartmentId(Long apartmentId, ReservationFormRequest request) {
        LocalDateTime from = dateTimeUtils.reserveFrom(request.from());
        LocalDateTime to = dateTimeUtils.reserveTo(request.to());
        List<ApartmentInstance> apartments = apartmentRepository
                .findFreeApartmentInstanceByApartmentIdAndDates(apartmentId, from, to, request.people())
                .stream()
                .filter(in -> bookingComReservationService.isFree(in, from, to))
                .toList();
        return selectMostSuitableApartmentInstance(apartments, apartmentId, from, to);
    }

    private ApartmentInstance selectMostSuitableApartmentInstance(List<ApartmentInstance> apartments,
                                                                  Long apartmentId,
                                                                  LocalDateTime from,
                                                                  LocalDateTime to) {
        if (apartments.isEmpty()) throw new ApartmentIsNotAvialableForReservation(from, to, apartmentId);
        if (apartments.size() == 1) return apartments.get(0);
        // TODO implement logic
        return apartments.get(0);
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

    public void addInstance(Long apartmentId, CreateApartmentInstanceDto createApartmentInstanceDto) {
        validateExists(apartmentId);
        ApartmentInstance instance = apartmentMapper.toInstance(createApartmentInstanceDto);
        instance.setApartment(apartmentRepository.getReferenceById(apartmentId));
        apartmentInstanceRepository.save(instance);
    }

    public void deleteInstance(Long id) {
        validateInstanceExists(id);
        apartmentInstanceRepository.deleteById(id);
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

    @Transactional
    public void addPrice(Long id, PriceDto priceDto) {
        Price price = priceMapper.toEntity(priceDto);
        Apartment apartment = getOneFetchPrices(id);
        apartment.addPrice(price);
    }

    @Transactional
    public void deletePrice(Long apartmentId, Integer person) {
        Apartment apartment = getOneFetchPrices(apartmentId);
        if (!apartment.deletePrice(person)) {
            throw new PriceNotFoundException(apartmentId, person);
        }
    }

    private Apartment getOneFetchPrices(Long id) {
        return apartmentRepository.findByIdFetchPrices(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    private void validateExists(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
    }

    private void validateInstanceExists(Long id) {
        if (!apartmentInstanceRepository.existsById(id)) {
            throw new ApartmentInstanceNotFoundException(id);
        }
    }

    private void validateRoomExists(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
    }

    public List<PriceDto> getPrices(Long apartmentId) {
        return getOneFetchPrices(apartmentId)
                .getPrices().stream()
                .map(priceMapper::toDto)
                .toList();
    }
}
