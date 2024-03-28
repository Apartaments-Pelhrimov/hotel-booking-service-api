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

package ua.mibal.booking.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.mibal.booking.adapter.in.web.mapper.PhotoMapper;
import ua.mibal.booking.adapter.in.web.model.PhotoResponse;
import ua.mibal.booking.adapter.in.web.security.annotation.ManagerAllowed;
import ua.mibal.booking.adapter.in.web.security.annotation.UserAllowed;
import ua.mibal.booking.application.PhotoService;
import ua.mibal.booking.application.port.photo.storage.model.PhotoResource;
import ua.mibal.booking.domain.id.ApartmentId;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PhotoController {
    private final PhotoService photoService;
    private final PhotoMapper photoMapper;

    @GetMapping("/photos/{key}")
    public PhotoResponse getPhoto(@PathVariable String key) {
        PhotoResource photo = photoService.getPhotoBy(key);
        return photoMapper.toResponse(photo);
    }

    @UserAllowed
    @PutMapping("/users/me/photo")
    @ResponseStatus(CREATED)
    public void changeMyPhoto(@RequestParam("file") MultipartFile file,
                              Authentication authentication) {
        photoService.changeUserPhoto(authentication.getName(), file);
    }

    @UserAllowed
    @DeleteMapping("/users/me/photo")
    @ResponseStatus(NO_CONTENT)
    public void deleteMyPhoto(Authentication authentication) {
        photoService.deleteUserPhoto(authentication.getName());
    }

    @ManagerAllowed
    @PostMapping("/apartments/{apartmentId}/photos")
    @ResponseStatus(CREATED)
    public void createApartmentPhoto(@PathVariable String apartmentId,
                                     @RequestParam("file") MultipartFile file) {
        photoService.createApartmentPhoto(new ApartmentId(apartmentId), file);
    }

    @ManagerAllowed
    @DeleteMapping("/apartments/{apartmentId}/photos/{key}")
    @ResponseStatus(NO_CONTENT)
    public void deleteApartmentPhoto(@PathVariable String apartmentId,
                                     @PathVariable String key) {
        photoService.deleteApartmentPhoto(new ApartmentId(apartmentId), key);
    }
}
