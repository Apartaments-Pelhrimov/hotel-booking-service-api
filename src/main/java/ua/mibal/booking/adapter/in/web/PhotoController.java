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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ua.mibal.booking.adapter.in.web.model.PhotoResponse;
import ua.mibal.booking.application.PhotoService;
import ua.mibal.booking.application.port.photo.storage.model.PhotoResource;
import ua.mibal.booking.config.security.annotation.ManagerAllowed;
import ua.mibal.booking.config.security.annotation.UserAllowed;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PhotoController {
    private final PhotoService photoService;

    @GetMapping("/users/{email}/photo")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable String email) {
        PhotoResource photo = photoService.getUserPhoto(email);
        return PhotoResponse.of(photo);
    }

    @UserAllowed
    @PutMapping("/users/me/photo")
    @ResponseStatus(HttpStatus.CREATED)
    public void changeMyPhoto(@RequestParam("file") MultipartFile file,
                              Authentication authentication) {
        photoService.changeUserPhoto(authentication.getName(), file);
    }

    @UserAllowed
    @DeleteMapping("/users/me/photo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyPhoto(Authentication authentication) {
        photoService.deleteUserPhoto(authentication.getName());
    }

    @GetMapping("/apartments/{apartmentId}/photos/{photoOrderIndex}")
    public ResponseEntity<byte[]> getApartmentPhoto(@PathVariable Long apartmentId,
                                                    @PathVariable Integer photoOrderIndex) {
        PhotoResource photo = photoService.getApartmentPhoto(apartmentId, photoOrderIndex);
        return PhotoResponse.of(photo);
    }

    @ManagerAllowed
    @PostMapping("/apartments/{id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public void createApartmentPhoto(@PathVariable Long id,
                                     @RequestParam("file") MultipartFile file) {
        photoService.createApartmentPhoto(id, file);
    }

    @ManagerAllowed
    @DeleteMapping("/apartments/{apartmentId}/photos/{photoOrderIndex}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApartmentPhoto(@PathVariable Long apartmentId,
                                     @PathVariable Integer photoOrderIndex) {
        photoService.deleteApartmentPhoto(apartmentId, photoOrderIndex);
    }
}
