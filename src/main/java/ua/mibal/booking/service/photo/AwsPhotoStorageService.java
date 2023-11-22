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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Hotel;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.HotelRepository;
import ua.mibal.booking.repository.UserRepository;
import ua.mibal.booking.service.util.AwsUrlUtils;

import java.io.IOException;

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
        User user = userRepository.getReferenceByEmail(email);
        String link = awsSupplier(aws -> aws.upload("users/", email, photo.getBytes()));
        user.setPhoto(new Photo(link));
        return link;
    }

    @Transactional
    @Override
    public void deleteUserPhoto(String email) {
        User user = userRepository.getReferenceByEmail(email);
        awsSupplier(aws -> aws.delete("users/", email));
        user.deletePhoto();
    }

    @Transactional
    @Override
    public String saveHotelPhoto(Long id, MultipartFile photo) {
        Hotel hotel = hotelRepository.getReferenceById(id);
        String name = photo.getName() + "-" + id;
        String link = awsSupplier(aws -> aws.upload("hotels/", name, photo.getBytes()));
        hotel.addPhoto(new Photo(link));
        return link;
    }

    @Transactional
    @Override
    public String saveApartmentPhoto(Long id, MultipartFile photo) {
        Apartment apartment = apartmentRepository.getReferenceById(id);
        String name = AwsUrlUtils.generateName(id, photo.getName());
        String link = awsSupplier(aws -> aws.upload("apartments/", name, photo.getBytes()));
        apartment.addPhoto(new Photo(link));
        return link;
    }

    @Transactional
    @Override
    public void deleteHotelPhoto(Long id, String link) {
        Hotel hotel = hotelRepository.getReferenceById(id);
        if (!hotel.deletePhoto(new Photo(link)))
            throw new IllegalArgumentException(
                    "Hotel with id=" + id + " doesn't contain photo='" + link + "'");
        String name = AwsUrlUtils.getFileName(link);
        awsSupplier(aws -> aws.delete("hotels/", name));
    }

    @Transactional
    @Override
    public void deleteApartmentPhoto(Long id, String link) {
        Apartment apartment = apartmentRepository.getReferenceById(id);
        if (!apartment.deletePhoto(new Photo(link)))
            throw new IllegalArgumentException(
                    "Apartment with id=" + id + " doesn't contain photo='" + link + "'");
        String name = AwsUrlUtils.getFileName(link);
        awsSupplier(aws -> aws.delete("apartments/", name));
    }

    private <T> T awsSupplier(AwsWrapper<T> fn) {
        try {
            return fn.apply(awsStorage);
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface AwsWrapper<T> {
        T apply(AwsStorage awsStorage) throws IOException;
    }
}
