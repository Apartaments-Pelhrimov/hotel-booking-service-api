/*
 * Copyright (c) 2023. Mykhailo Balakhon mailto:9mohapx9@gmail.com
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.dto.request.CreateCommentDto;
import ua.mibal.booking.model.dto.response.CommentDto;
import ua.mibal.booking.model.entity.Apartment;
import ua.mibal.booking.model.entity.Comment;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.CommentMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.CommentRepository;
import ua.mibal.booking.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;

    public Page<CommentDto> getCommentsInApartment(Long apartmentId, Pageable pageable) {
        return commentRepository.findByApartmentIdFetchUser(apartmentId, pageable)
                .map(commentMapper::toDto);
    }

    @Transactional
    public void addCommentToApartment(CreateCommentDto createCommentDto,
                                      String userEmail,
                                      Long apartmentId) {
        validate(apartmentId, userEmail);
        Apartment apartmentReference = apartmentRepository.getReferenceById(apartmentId);
        User userReference = userRepository.getReferenceByEmail(userEmail);
        Comment comment = Comment.builder()
                .apartment(apartmentReference)
                .user(userReference)
                .creationDateTime(LocalDateTime.now())
                .rate(createCommentDto.rate())
                .body(createCommentDto.body())
                .build();
        commentRepository.save(comment);
        commentRepository.refreshRatingForApartment(apartmentId);
    }

    public void deleteComment(Long id, String email) {
        validateUserHasComment(id, email);
        commentRepository.deleteById(id);
    }

    private void validateUserHasComment(Long commentId, String email) {
        if (!userRepository.userHasComment(email, commentId)) {
            throw new IllegalArgumentException("User with email='" + email + "' can not " +
                                               "delete Comment with id=" + commentId);
        }
    }

    private void validate(Long apartmentId, String userEmail) {
        if (!apartmentRepository.existsById(apartmentId))
            throw new ApartmentNotFoundException(apartmentId);
        if (!userRepository.userHasReservationWithApartment(userEmail, apartmentId))
            throw new IllegalArgumentException("User with email='" + userEmail + "' does not " +
                                               "have access to comments for Apartment with id=" + apartmentId);
    }
}
