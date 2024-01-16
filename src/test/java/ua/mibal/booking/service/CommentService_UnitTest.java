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

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.exception.UserHasNoAccessToComment;
import ua.mibal.booking.model.mapper.CommentMapper;
import ua.mibal.booking.repository.ApartmentRepository;
import ua.mibal.booking.repository.CommentRepository;
import ua.mibal.booking.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommentService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentService_UnitTest {

    @Autowired
    private CommentService service;

    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private CommentMapper commentMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ApartmentRepository apartmentRepository;


    @Test
    void getCommentsInApartment() {
    }

    @Test
    void addCommentToApartment() {
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
                UserHasNoAccessToComment.class,
                () -> service.delete(id, email)
        );
        verify(commentRepository, never()).deleteById(id);
    }
}
