/*
 * Copyright (c) 2023. Mykhailo Balakhon, mailto:9mohapx9@gmail.com
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

package ua.mibal.booking.service.photo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Hotel;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.HotelRepository;
import ua.mibal.booking.repository.UserRepository;
import ua.mibal.booking.service.util.AwsUrlUtils;
import ua.mibal.booking.service.util.FileNameUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class AwsPhotoStorageService implements PhotoStorageService {
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final ApartmentRepository apartmentRepository;
    private final AwsStorage awsStorage;

    @Transactional
    @Override
    public String setUserPhoto(String email, MultipartFile photo) {
        PhotoExtension photoExtension = FileNameUtils.getPhotoExtension(photo.getOriginalFilename());
        String encodedLink = perform(aws -> aws.uploadImage("users/", email, photo.getBytes(), photoExtension));
        String link = URLDecoder.decode(encodedLink, StandardCharsets.UTF_8);
        userRepository.updateUserPhotoByEmail(new Photo(link), email);
        return link;
    }

    @Transactional
    @Override
    public void deleteUserPhoto(String email) {
        perform(aws -> aws.delete("users/", email));
        userRepository.deleteUserPhotoByEmail(email);
    }

    @Transactional
    @Override
    public String saveHotelPhoto(Long id, MultipartFile photo) {
        String fileName = photo.getOriginalFilename();
        PhotoExtension photoExtension = FileNameUtils.getPhotoExtension(fileName);
        Hotel hotel = getHotelById(id);
        String encodedLink = perform(aws -> aws.uploadImage("hotels/", fileName, photo.getBytes(), photoExtension));
        String link = URLDecoder.decode(encodedLink, StandardCharsets.UTF_8);
        hotel.addPhoto(new Photo(link));
        return link;
    }

    @Transactional
    @Override
    public void deleteHotelPhoto(Long id, String link) {
        Hotel hotel = getHotelById(id);
        if (!hotel.deletePhoto(new Photo(link)))
            throw new IllegalArgumentException(
                    "Hotel with id=" + id + " doesn't contain photo='" + link + "'");
        String name = AwsUrlUtils.getFileName(link);
        perform(aws -> aws.delete("hotels/", name));
    }

    @Transactional
    @Override
    public String saveApartmentPhoto(Long id, MultipartFile photo) {
        String fileName = photo.getOriginalFilename();
        PhotoExtension photoExtension = FileNameUtils.getPhotoExtension(fileName);
        Apartment apartment = getApartmentById(id);
        String encodedLink = perform(aws -> aws.uploadImage("apartments/", fileName, photo.getBytes(), photoExtension));
        String link = URLDecoder.decode(encodedLink, StandardCharsets.UTF_8);
        apartment.addPhoto(new Photo(link));
        return link;
    }

    @Transactional
    @Override
    public void deleteApartmentPhoto(Long id, String link) {
        Apartment apartment = getApartmentById(id);
        if (!apartment.deletePhoto(new Photo(link)))
            throw new IllegalArgumentException(
                    "Apartment with id=" + id + " doesn't contain photo='" + link + "'");
        String name = AwsUrlUtils.getFileName(link);
        perform(aws -> aws.delete("apartments/", name));
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findByIdFetchPhotos(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity Hotel by id=" + id + " not found"));
    }

    public Apartment getApartmentById(Long id) {
        return apartmentRepository.findByIdFetchPhotos(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity Apartment by id=" + id + " not found"));
    }

    private <T> T perform(AwsWrapper<T> fn) {
        try {
            return fn.apply(awsStorage);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @FunctionalInterface
    private interface AwsWrapper<T> {

        T apply(AwsStorage awsStorage) throws IOException;
    }
}
