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

package ua.mibal.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.request.CreateCommentDto;
import ua.mibal.booking.model.dto.response.CommentDto;
import ua.mibal.booking.model.entity.ApartmentType;
import ua.mibal.booking.model.entity.Comment;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.mapper.CommentMapper;
import ua.mibal.booking.repository.CommentRepository;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ApartmentService apartmentService;
    private final UserService userService;

    public Page<CommentDto> getCommentsInApartment(Long apartmentId, Pageable pageable) {
        return commentRepository.findByApartmentIdFetchUser(apartmentId, pageable)
                .map(commentMapper::toDto);
    }

    @Transactional
    public void addCommentByAuthenticationToApartment(CreateCommentDto createCommentDto,
                                                      Authentication authentication,
                                                      Long apartmentId) {
        ApartmentType apartmentType = apartmentService.getOne(apartmentId);
        User user = userService.getOneByEmail(authentication.getName());
        Comment comment = new Comment(
                user,
                apartmentType,
                createCommentDto.rate(),
                createCommentDto.body()
        );
        commentRepository.save(comment);
    }
}
