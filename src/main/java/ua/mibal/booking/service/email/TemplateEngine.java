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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Component
public class TemplateEngine {

    public String insertIntoTemplate(String template, Map<String, Object> vars) {
        List<InsertPlace> insertPlaces = getInsertPlacesInTemplate(template);
        for (InsertPlace insertPlace : insertPlaces) {
            Object var = vars.get(insertPlace.getName());
            if (insertPlace.isComplex()) {
                Object fieldValue = getFieldValue(var, insertPlace);
                template = insert(fieldValue, insertPlace, template);
            } else {
                template = insert(var, insertPlace, template);
            }
        }
        return template;
    }

    private Object getFieldValue(Object var, InsertPlace insertPlace) {
        try {
            String fieldName = insertPlace.getFieldName().get();
            Field field = var.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object result = field.get(var);
            field.setAccessible(false);
            return result;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(
                    "Exception while retrieving value from field name=" + insertPlace.fieldName +
                    " from object by class='" + var.getClass(),
                    e
            );
        }
    }

    private List<InsertPlace> getInsertPlacesInTemplate(String template) {
        return getTokens(template)
                .stream()
                .map(this::tokenToInsertPlace)
                .toList();
    }

    private List<String> getTokens(String template) {
        Pattern pattern = Pattern.compile("\\$\\{(.*?)}");
        Matcher matcher = pattern.matcher(template);

        List<String> tokens = new ArrayList<>();
        if (matcher.find()) {
            do {
                tokens.add(matcher.group(1));
            } while (matcher.find(matcher.end()));
        }
        return tokens;
    }

    private String insert(Object value,
                          InsertPlace insertPlace,
                          String template) {
        return template.replace(insertPlace.getToken(), value.toString());
    }

    private InsertPlace tokenToInsertPlace(String token) {
        if (token.contains(".")) {
            String[] tokenParts = token.split("\\.");
            validateTokenParts(tokenParts, token);
            String varName = tokenParts[0];
            String fieldName = tokenParts[1];
            return new InsertPlace(varName, fieldName, token);
        } else {
            return new InsertPlace(token, null, token);
        }
    }

    private void validateTokenParts(String[] tokenParts, String token) {
        if (tokenParts.length != 2) {
            throw new IllegalArgumentException(
                    "Illegal placeholder '" + token + "'. " +
                    "Token must consists of ${name.field}, 'field' is optional."
            );
        }
    }

    @Getter
    @AllArgsConstructor
    private static class InsertPlace {

        private String name;
        private String fieldName;
        private String token;

        public Optional<String> getFieldName() {
            return Optional.ofNullable(fieldName);
        }

        public String getToken() {
            return "${" + token + "}";
        }

        public boolean isComplex() {
            return fieldName != null;
        }
    }
}
