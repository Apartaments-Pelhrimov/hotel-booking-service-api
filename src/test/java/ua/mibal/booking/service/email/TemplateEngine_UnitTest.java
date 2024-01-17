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

package ua.mibal.booking.service.email;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TemplateEngine.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TemplateEngine_UnitTest {

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void insertIntoTemplate_simple() {
        String template = "Though our outer self is wasting away, our " +
                          "inner self is being renewed ${how_often}";

        String actual = templateEngine.insertIntoTemplate(template, Map.of(
                "how_often", "day by day"
        ));

        assertEquals(
                "Though our outer self is wasting away, our " +
                "inner self is being renewed day by day",
                actual
        );
    }

    @Test
    void insertIntoTemplate_field() {
        String template = "For ${who} ${who.action} the world," +
                          "that he gave his only Son," +
                          "that whoever believes in him" +
                          "should not perish but have eternal life";

        String actual = templateEngine.insertIntoTemplate(template, Map.of(
                "who", new TestObject()
        ));

        assertEquals(
                "For God so loved the world," +
                "that he gave his only Son," +
                "that whoever believes in him" +
                "should not perish but have eternal life",
                actual
        );
    }

    @Test
    void insertIntoTemplate_field_should_throw() {
        String template = "Therefore, since through God’s ${mercy.expiration} " +
                          "we have this ministry, we do not lose heart";

        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> templateEngine.insertIntoTemplate(template, Map.of(
                        "mercy", new TestObject()
                ))
        );

        assertEquals(NoSuchFieldException.class, e.getCause().getClass());
    }

    public static class TestObject {
        private final String action = "so loved";

        @Override
        public String toString() {
            return "God";
        }
    }
}
