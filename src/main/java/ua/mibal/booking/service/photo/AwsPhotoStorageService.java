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
import ua.mibal.booking.config.properties.AwsProps.AwsBucketProps;
import ua.mibal.booking.model.entity.ApartmentType;
import ua.mibal.booking.model.entity.embeddable.Photo;
import ua.mibal.booking.repository.ApartmentTypeRepository;
import ua.mibal.booking.repository.UserRepository;
import ua.mibal.booking.service.util.AwsUrlUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static ua.mibal.booking.service.util.FileNameUtils.getPhotoExtension;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class AwsPhotoStorageService implements PhotoStorageService {
    private final UserRepository userRepository;
    private final ApartmentTypeRepository apartmentTypeRepository;
    private final AwsStorage awsStorage;
    private final AwsBucketProps awsBucketProps;

    @Transactional
    @Override
    public String setUserPhoto(String email, MultipartFile photo) {
        String encodedLink = perform(aws -> aws.uploadImage(
                awsBucketProps.usersFolder(),
                email,
                photo.getBytes(),
                getPhotoExtension(photo.getOriginalFilename())
        ));
        String link = URLDecoder.decode(encodedLink, StandardCharsets.UTF_8);
        userRepository.updateUserPhotoByEmail(new Photo(link), email);
        return link;
    }

    @Transactional
    @Override
    public void deleteUserPhoto(String email) {
        perform(aws -> aws.delete(awsBucketProps.usersFolder(), email));
        userRepository.deleteUserPhotoByEmail(email);
    }

    @Transactional
    @Override
    public String saveApartmentTypePhoto(Long id, MultipartFile photo) {
        ApartmentType apartmentType = getApartmentTypeById(id);
        String fileName = photo.getOriginalFilename();
        String encodedLink = perform(aws -> aws.uploadImage(
                awsBucketProps.apartmentsFolder(),
                fileName,
                photo.getBytes(),
                getPhotoExtension(fileName)
        ));
        String link = URLDecoder.decode(encodedLink, StandardCharsets.UTF_8);
        apartmentType.addPhoto(new Photo(link));
        return link;
    }

    @Transactional
    @Override
    public void deleteApartmentTypePhoto(Long id, String link) {
        ApartmentType apartmentType = getApartmentTypeById(id);
        if (!apartmentType.deletePhoto(new Photo(link)))
            throw new IllegalArgumentException(
                    "Apartment with id=" + id + " doesn't contain photo='" + link + "'");
        String name = AwsUrlUtils.getFileName(link);
        perform(aws -> aws.delete(awsBucketProps.apartmentsFolder(), name));
    }

    private ApartmentType getApartmentTypeById(Long id) {
        return apartmentTypeRepository.findByIdFetchPhotos(id)
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
