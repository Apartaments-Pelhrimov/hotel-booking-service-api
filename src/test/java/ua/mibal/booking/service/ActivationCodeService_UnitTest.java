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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.entity.ActivationCodeNotFoundException;
import ua.mibal.booking.repository.ActivationCodeRepository;
import ua.mibal.booking.service.security.CodeGenerationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ActivationCodeService_UnitTest {

    private ActivationCodeService service;

    @Mock
    private ActivationCodeRepository activationCodeRepository;
    @Mock
    private CodeGenerationService codeGenerationService;

    @Mock
    private User user;
    @Mock
    private ActivationCode activationCode;

    @BeforeEach
    void setup() {
        service = new ActivationCodeService(activationCodeRepository, codeGenerationService);
    }

    @Test
    void generateAndSaveCodeFor_should_generate_and_save() {
        String code = "code";

        when(codeGenerationService.generateCode())
                .thenReturn(code);

        service.generateAndSaveCodeFor(user);

        verify(activationCodeRepository, times(1))
                .save(new ActivationCode(any(), user, code));
    }

    @Test
    void getOneByCode() {
        String code = "code";
        when(activationCodeRepository.findByCode(code))
                .thenReturn(Optional.of(activationCode));

        var actual = service.getOneByCode(code);

        assertEquals(activationCode, actual);
    }

    @Test
    void getOneByCode_should_throw_ActivationCodeNotFoundException() {
        String code = "code";
        when(activationCodeRepository.findByCode(code))
                .thenReturn(Optional.empty());

        assertThrows(ActivationCodeNotFoundException.class,
                () -> service.getOneByCode(code));
    }
}
