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

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.mibal.booking.application.port.template.engine.TemplateEngineException;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@SpringBootTest(classes = {
        ThymeleafTemplateEngineAdapter.class,
        ThymeleafEngineConfig.class,
})
@DisplayNameGeneration(ReplaceUnderscores.class)
class ThymeleafTemplateEngineAdapter_IntegrationTest {

    @Autowired
    private ThymeleafTemplateEngineAdapter templateEngine;

    @Test
    void process() {
        String actual = templateEngine.process("test.html", ENGLISH, Map.of(
                "user", new TestUser("Mykhailo", "email@gmail.com")
        ));

        assertEquals("""
                <!--
                  ~ Copyright (c) 2024. Mykhailo Balakhon mailto:9mohapx9@gmail.com
                  ~
                  ~ Licensed under the Apache License, Version 2.0 (the "License");
                  ~ you may not use this file except in compliance with the License.
                  ~ You may obtain a copy of the License at
                  ~
                  ~     http://www.apache.org/licenses/LICENSE-2.0
                  ~
                  ~ Unless required by applicable law or agreed to in writing, software
                  ~ distributed under the License is distributed on an "AS IS" BASIS,
                  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                  ~ See the License for the specific language governing permissions and
                  ~ limitations under the License.
                  -->
                                
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"
                          name="viewport">
                    <meta content="ie=edge" http-equiv="X-UA-Compatible">
                    <title>Document</title>
                </head>
                <body>
                                        
                <h1>
                    Hello, <span>Mykhailo</span>
                </h1>
                                        
                <h1>
                    Hello, <span>email@gmail.com</span>
                </h1>
                                        
                </body>
                </html>
                """, actual);
    }

    @Test
    void process_should_throw_TemplateEngineException_if_context_not_exists() {
        assertThrows(
                TemplateEngineException.class,
                () -> templateEngine.process("test.html", ENGLISH, emptyMap())
        );
    }

    @Test
    void process_should_throw_TemplateEngineException_if_template_not_found() {
        assertThrows(
                TemplateEngineException.class,
                () -> templateEngine.process("not-existing-file.html", ENGLISH, emptyMap())
        );
    }

    private record TestUser(String firstName, String email) {
    }
}
