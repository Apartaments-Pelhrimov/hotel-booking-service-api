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

package ua.mibal.booking.service.email.component;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.exception.ClasspathFileReaderException;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClasspathFileReader.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClasspathFileReader_UnitTest {

    @Autowired
    private ClasspathFileReader reader;

    @Test
    void read() {
        String actual = reader.read("classpathFileReaderTestFile");

        assertEquals("""
                BEGIN:VCALENDAR
                PRODID:-//Ben Fortuna//iCal4j 1.0//EN
                VERSION:2.0
                CALSCALE:GREGORIAN
                BEGIN:VEVENT
                DTSTAMP:20231208T093933Z
                DTSTART;TZID=Australia/Sydney:20240101T000000
                DTEND;TZID=Australia/Sydney:20240101T000000
                SUMMARY:New Year
                END:VEVENT
                BEGIN:VEVENT
                DTSTAMP:20231208T093933Z
                DTSTART:20231225T000000Z
                DTEND;TZID=Australia/Sydney:20240101T000000
                SUMMARY:Christmas holidays
                END:VEVENT
                BEGIN:VEVENT
                DTSTAMP:20231208T093933Z
                DTSTART;TZID=Australia/Sydney:20040918T000000
                DTEND;TZID=Australia/Sydney:20040918T000000
                SUMMARY:My Birthday
                END:VEVENT
                END:VCALENDAR
                """, actual);
    }

    @Test
    void read_should_throw_FileNotFoundException() {
        ClasspathFileReaderException e = assertThrows(
                ClasspathFileReaderException.class,
                () -> reader.read("not_existing_file")
        );

        assertEquals(FileNotFoundException.class, e.getCause().getClass());
    }
}
