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

package ua.mibal.booking.model.mapper.linker;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.hateoas.Link;
import ua.mibal.booking.controller.PhotoController;
import ua.mibal.booking.model.entity.User;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class UserPhotoLinker {

    public String toLink(User user) {
        if (user.getPhoto().isEmpty()) {
            return null;
        }
        var getPhotoMethod = methodOn(PhotoController.class)
                .getUserPhoto(user.getEmail());
        Link photoLink = linkTo(getPhotoMethod).withSelfRel();
        return photoLink.getHref();
    }
}
