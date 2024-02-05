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
import ua.mibal.booking.model.entity.Apartment;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class ApartmentPhotoLinker {

    public List<String> toLinks(Apartment apartment) {
        List<String> links = new ArrayList<>();
        for (int i = 0; i < apartment.getPhotos().size(); i++) {
            String link = toLink(apartment.getId(), i);
            links.add(link);
        }
        return links;
    }

    public String toLink(Long id, Integer index) {
        var getPhotoMethod = methodOn(PhotoController.class)
                .getApartmentPhoto(id, index);
        Link photoLink = linkTo(getPhotoMethod).withSelfRel();
        return photoLink.getHref();
    }
}
