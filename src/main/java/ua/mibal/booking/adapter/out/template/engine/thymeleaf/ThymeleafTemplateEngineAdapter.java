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

package ua.mibal.booking.adapter.out.template.engine.thymeleaf;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import ua.mibal.booking.application.port.template.engine.TemplateEngine;
import ua.mibal.booking.application.port.template.engine.TemplateEngineException;

import java.util.Locale;
import java.util.Map;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Component
public class ThymeleafTemplateEngineAdapter implements TemplateEngine {
    private final ITemplateEngine templateEngine;

    @Override
    public String process(String template, Locale locale, Map<String, Object> vars) {
        try {
            return processTemplate(template, locale, vars);
        } catch (org.thymeleaf.exceptions.TemplateEngineException e) {
            throw new TemplateEngineException(
                    "Exception while processing '%s' thymeleaf template with vars: %s"
                            .formatted(template, vars), e
            );
        }
    }

    private String processTemplate(String template, Locale locale, Map<String, Object> vars) {
        return templateEngine.process(template, new Context(locale, vars));
    }
}
