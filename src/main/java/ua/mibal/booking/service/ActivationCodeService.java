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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.entity.ActivationCodeNotFoundException;
import ua.mibal.booking.repository.ActivationCodeRepository;
import ua.mibal.booking.service.security.CodeGenerationService;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class ActivationCodeService {
    private final ActivationCodeRepository activationCodeRepository;
    private final CodeGenerationService codeGenerationService;

    @Transactional
    public ActivationCode generateAndSaveCodeFor(User user) {
        ActivationCode activationCode = generateActivationCodeFor(user);
        return activationCodeRepository.save(activationCode);
    }

    public ActivationCode getOneByCode(String code) {
        return activationCodeRepository.findByCode(code)
                .orElseThrow(() -> new ActivationCodeNotFoundException(code));
    }

    private ActivationCode generateActivationCodeFor(User user) {
        String code = codeGenerationService.generateCode();
        return ActivationCode.builder()
                .user(user)
                .code(code)
                .build();
    }
}
