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

import org.springframework.stereotype.Component;
import ua.mibal.booking.model.exception.ClasspathFileReaderException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Component
public class ClasspathFileReader {

    public String read(String path) {
        try (InputStream fileStream = getInputStreamBy(path)) {
            return new String(fileStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ClasspathFileReaderException(
                    "Exception while reading file " + path, e);
        }
    }

    private InputStream getInputStreamBy(String path) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream fileStream = classLoader.getResourceAsStream(path);
        if (fileStream == null) {
            throw new FileNotFoundException(path + " not found in classpath");
        }
        return fileStream;
    }
}
