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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Component
public class ClasspathFileReader {

    public String read(String path) {
        try {
            URL fileUrl = getUrl(path);
            return Files.readString(Paths.get(fileUrl.toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Exception while reading file", e);
        }
    }

    private URL getUrl(String path) throws FileNotFoundException {
        return Optional.ofNullable(getClass().getClassLoader().getResource(path))
                .orElseThrow(() -> new FileNotFoundException(
                        "File with name '" + path + "' not found in classpath"));
    }
}
