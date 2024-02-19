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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ua.mibal.booking.application.port.jpa.ApartmentRepository;
import ua.mibal.booking.application.port.jpa.CommentRepository;
import ua.mibal.booking.application.port.jpa.UserRepository;
import ua.mibal.booking.domain.Apartment;
import ua.mibal.booking.domain.Comment;
import ua.mibal.booking.domain.User;
import ua.mibal.booking.model.dto.request.CreateCommentDto;
import ua.mibal.booking.model.dto.response.CommentDto;
import ua.mibal.booking.model.exception.UserHasNoAccessToCommentException;
import ua.mibal.booking.model.exception.UserHasNoAccessToCommentsException;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.CommentMapper;
import ua.mibal.test.annotation.UnitTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@UnitTest
class CommentService_UnitTest {

    private CommentService service;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private Comment comment;
    @Mock
    private CommentDto commentDto;
    @Mock
    private CreateCommentDto createCommentDto;
    @Mock
    private Apartment apartment;
    @Mock
    private User user;

    @BeforeEach
    void setup() {
        service = new CommentService(commentRepository, commentMapper, userRepository, apartmentRepository);
    }

    @Test
    void getAllByApartment() {
        Long apartmentId = 1L;
        Pageable pageable = Pageable.ofSize(5);

        Page<Comment> commentPage = new PageImpl<>(List.of(comment, comment));
        Page<CommentDto> expectedCommentDtoPage = new PageImpl<>(List.of(commentDto, commentDto));

        when(commentRepository.findByApartmentIdFetchUser(apartmentId, pageable))
                .thenReturn(commentPage);
        when(commentMapper.toDto(comment))
                .thenReturn(commentDto);

        var actual = service.getAllByApartment(apartmentId, pageable);

        assertEquals(expectedCommentDtoPage, actual);
    }

    @Test
    void create() {
        String email = "email";
        Long apartmentId = 1L;

        when(apartmentRepository.existsById(apartmentId))
                .thenReturn(true);
        when(userRepository.userHasReservationWithApartment(email, apartmentId))
                .thenReturn(true);

        when(commentMapper.toEntity(createCommentDto))
                .thenReturn(comment);
        when(apartmentRepository.getReferenceById(apartmentId))
                .thenReturn(apartment);
        when(userRepository.getReferenceByEmail(email))
                .thenReturn(user);

        service.create(createCommentDto, email, apartmentId);

        verify(comment, times(1)).setApartment(apartment);
        verify(comment, times(1)).setUser(user);
    }

    @Test
    void create_should_throw_ApartmentNotFoundException() {
        String email = "email";
        Long apartmentId = 1L;

        when(apartmentRepository.existsById(apartmentId))
                .thenReturn(false);

        assertThrows(ApartmentNotFoundException.class,
                () -> service.create(createCommentDto, email, apartmentId));

        verifyNoInteractions(comment);
    }

    @Test
    void create_should_throw_UserHasNoAccessToCommentsException() {
        String email = "email";
        Long apartmentId = 1L;

        when(apartmentRepository.existsById(apartmentId))
                .thenReturn(true);
        when(userRepository.userHasReservationWithApartment(email, apartmentId))
                .thenReturn(false);

        assertThrows(UserHasNoAccessToCommentsException.class,
                () -> service.create(createCommentDto, email, apartmentId));

        verifyNoInteractions(comment);
    }

    @ParameterizedTest
    @CsvSource({
            "1,          email1",
            "2,          email2",
            "1010101001, email3",
    })
    void delete(Long id, String email) {
        when(userRepository.userHasComment(email, id)).thenReturn(true);

        service.delete(id, email);

        verify(commentRepository, times(1))
                .deleteById(id);

    }

    @ParameterizedTest
    @CsvSource({
            "1,          email1",
            "2,          email2",
            "1010101001, email3",
    })
    void delete_should_throw_UserHasNoAccessToComment(Long id, String email) {
        when(userRepository.userHasComment(email, id)).thenReturn(false);

        assertThrows(
                UserHasNoAccessToCommentException.class,
                () -> service.delete(id, email)
        );
        verify(commentRepository, never()).deleteById(id);
    }
}
