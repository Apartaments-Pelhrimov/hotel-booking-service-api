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

package ua.mibal.booking.adapter.in.web.mapper.linker;

import org.mapstruct.Mapper;
import ua.mibal.booking.adapter.in.web.PhotoController;
import ua.mibal.booking.domain.Photo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = SPRING)
public class PhotoLinker {

    public List<String> getPhotoLinks(List<Photo> photos) {
        return photos.stream()
                .map(this::toLink)
                .toList();
    }

    public String getPhotoLink(Optional<Photo> photo) {
        return photo
                .map(this::toLink)
                .orElse(null);
    }

    private String toLink(Photo photo) {
        try {
            return toLink0(photo);
        } catch (NoSuchMethodException e) {
            // TODO wrap into new Exception class
            throw new RuntimeException(
                    "Exception while trying to get relation link "
                    + "to get photo endpoint by method", e);
        }
    }

    private String toLink0(Photo photo) throws NoSuchMethodException {
        Method getPhotoMethod = PhotoController.class
                .getMethod("getPhoto", String.class);
        return linkTo(getPhotoMethod, photo.getKey())
                .withSelfRel()
                .getHref();
    }
}
