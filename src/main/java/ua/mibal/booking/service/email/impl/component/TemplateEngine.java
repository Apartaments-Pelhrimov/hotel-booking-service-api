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

package ua.mibal.booking.service.email.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class TemplateEngine {
    private final org.thymeleaf.TemplateEngine thymeleafTemplateEngine;

    // TODO
    //  implement localization
    //  rename components
    //  tests

    public String generate(String template, Object... args) {
        Context context = new Context(getLocale());
        for (Object arg : args) {
            context.setVariable(arg.getClass().getSimpleName(), arg);
        }
        return thymeleafTemplateEngine.process(template, context);
    }
}
