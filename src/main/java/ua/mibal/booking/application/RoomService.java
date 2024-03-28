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
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.exception.RoomNotFoundException;
import ua.mibal.booking.application.mapper.RoomMapper;
import ua.mibal.booking.application.model.CreateRoomForm;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.application.port.jpa.RoomRepository;
import ua.mibal.booking.domain.Room;
import ua.mibal.booking.domain.id.ApartmentId;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final ApartmentRepository apartmentRepository;
    private final RoomMapper roomMapper;

    public void create(ApartmentId apartmentId, CreateRoomForm form) {
        validateApartmentExists(apartmentId);
        Room room = roomMapper.assemble(form);
        room.setApartment(apartmentRepository.getReferenceById(apartmentId));
        roomRepository.save(room);
    }

    public void delete(Long id) {
        validateRoomExists(id);
        roomRepository.deleteById(id);
    }

    private void validateApartmentExists(ApartmentId id) {
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
