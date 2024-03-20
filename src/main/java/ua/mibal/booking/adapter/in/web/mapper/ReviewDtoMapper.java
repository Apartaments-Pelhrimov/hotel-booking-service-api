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

package ua.mibal.booking.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ua.mibal.booking.adapter.in.web.mapper.linker.PhotoLinker;
import ua.mibal.booking.adapter.in.web.model.ReviewDto;
import ua.mibal.booking.domain.Review;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = SPRING, uses = {
        PhotoLinker.class, UserDtoMapper.class})
public interface ReviewDtoMapper {

    default Page<ReviewDto> toDtos(Page<Review> reviews) {
        return reviews.map(this::toDto);
    }

    ReviewDto toDto(Review review);
}
