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

package ua.mibal.booking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.application.dto.CreateCommentForm;
import ua.mibal.booking.application.exception.ApartmentNotFoundException;
import ua.mibal.booking.application.exception.UserHasNoAccessToCommentException;
import ua.mibal.booking.application.exception.UserHasNoAccessToCommentsException;
import ua.mibal.booking.application.mapper.CommentMapper;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.application.port.jpa.CommentRepository;
import ua.mibal.booking.application.port.jpa.UserRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Comment;
import ua.mibal.booking.domain.User;

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

    public Page<Comment> getAllByApartment(Long apartmentId, Pageable pageable) {
        return commentRepository.findByApartmentIdFetchUser(apartmentId, pageable);
    }

    @Transactional
    public void create(CreateCommentForm form) {
        validateUserHasReservationWithThisApartment(form);
        Comment newComment = assembleCommentBy(form);
        commentRepository.save(newComment);
    }

    public void delete(Long id, String userEmail) {
        validateUserHasComment(userEmail, id);
        commentRepository.deleteById(id);
    }

    private Comment assembleCommentBy(CreateCommentForm form) {
        Comment newComment = commentMapper.assemble(form);
        Apartment apartmentRef = apartmentRepository.getReferenceById(form.getApartmentId());
        User userRef = userRepository.getReferenceByEmail(form.getUserEmail());
        newComment.setApartment(apartmentRef);
        newComment.setUser(userRef);
        return newComment;
    }

    private void validateUserHasReservationWithThisApartment(CreateCommentForm form) {
        Long apartmentId = form.getApartmentId();
        String userEmail = form.getUserEmail();
        if (!apartmentRepository.existsById(apartmentId)) {
            throw new ApartmentNotFoundException(apartmentId);
        }
        if (!userRepository.userHasReservationWithApartment(userEmail, apartmentId)) {
            throw new UserHasNoAccessToCommentsException();
        }
    }

    private void validateUserHasComment(String userEmail, Long commentId) {
        if (!userRepository.userHasComment(userEmail, commentId)) {
            throw new UserHasNoAccessToCommentException();
        }
    }
}
