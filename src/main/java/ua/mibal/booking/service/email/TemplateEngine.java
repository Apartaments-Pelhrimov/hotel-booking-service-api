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
import ua.mibal.booking.model.exception.TemplateEngineException;

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

    private final static String FIND_TOKEN_REGEX = "\\$\\{(.*?)}";
    private final static String TOKEN_BODY_AND_FIELD_DIVIDER_REGEX = "\\.";
    private final static String TOKEN_BODY_AND_FIELD_DIVIDER = ".";
    private final static int TOKEN_BODY_INDEX = 0;
    private final static int TOKEN_FIELD_INDEX = 1;

    public String insertIntoTemplate(String template, Map<String, Object> varsToInsert) {
        List<InsertPlace> insertPlaces = getInsertPlaces(template);
        for (InsertPlace insertPlace : insertPlaces) {
            Object insertValue = varsToInsert.get(insertPlace.getBody());
            template = insertPlace.insertInto(template, insertValue);
        }
        return template;
    }

    private List<InsertPlace> getInsertPlaces(String template) {
        List<String> tokens = getTokens(template);
        return getInsertPlacesFromTokens(tokens);
    }

    private List<InsertPlace> getInsertPlacesFromTokens(List<String> tokens) {
        return tokens.stream()
                .map(this::tokenToInsertPlace)
                .toList();
    }

    /**
     * Method parses given String template into tokens.
     * <p>
     * For example:
     * <p>
     * {@code "Hello, ${user.name}. Welcome to ${app.name}. Today is ${year}"}
     * <p>
     * Tokens in this example: {@code "${user.name}"}, {@code "${app.name}"}
     * and {@code "${year}"}
     * <p>
     * Tokens can be complex and regular (see {@link InsertPlace} for more
     * info);
     *
     * @param template
     * @return tokens
     */
    private List<String> getTokens(String template) {
        Pattern tokenPattern = Pattern.compile(FIND_TOKEN_REGEX);
        Matcher tokenMatcher = tokenPattern.matcher(template);

        List<String> tokens = new ArrayList<>();
        if (tokenMatcher.find()) {
            do {
                String token = tokenMatcher.group(1);
                tokens.add(token);
            } while (tokenMatcher.find(tokenMatcher.end()));
        }
        return tokens;
    }

    private InsertPlace tokenToInsertPlace(String token) {
        if (!isComplex(token)) {
            return new InsertPlace(token, null, token);
        }
        String[] tokenComponents = token.split(TOKEN_BODY_AND_FIELD_DIVIDER_REGEX);
        validateTokenPartsLength(tokenComponents, token);
        String tokenBody = tokenComponents[TOKEN_BODY_INDEX];
        String tokenField = tokenComponents[TOKEN_FIELD_INDEX];
        return new InsertPlace(tokenBody, tokenField, token);
    }

    private boolean isComplex(String token) {
        return token.contains(TOKEN_BODY_AND_FIELD_DIVIDER);
    }

    private void validateTokenPartsLength(String[] tokenParts, String token) {
        if (tokenParts.length != 2) {
            throw new TemplateEngineException(
                    "Illegal placeholder '%s'. Token must consists of " +
                    "'${obj.field}' or '${obj}' ('field' is optional).", token
            );
        }
    }

    /**
     * This is representation of placeholder found at template.
     * <p>
     * There are 2 types of {@link InsertPlace}s: regular and complex.
     * <p>
     * <p>
     * Example of complex:
     * <p>
     * {@code "Hello, ${user.name}."} or {@code "Today will be
     * ${weather.forecast}"}
     * <p>
     * <p>
     * Example of regular:
     * <p>
     * {@code "Click on the ${link} if you agree"}
     * <p>
     *
     * @author Mykhailo Balakhon
     * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
     */
    @AllArgsConstructor
    private static class InsertPlace {
        @Getter
        private String body;
        private String field;
        private String token;

        public String insertInto(String template, Object objectToInsert) {
            if (this.isComplex()) {
                Object fieldValue = getFieldValue(objectToInsert);
                return template.replace(getToken(), fieldValue.toString());
            }
            return template.replace(getToken(), objectToInsert.toString());
        }

        /**
         * @return {@code true} if {@link InsertPlace} has
         * {@link InsertPlace#field},
         * {@code false} - if it has no
         * {@link InsertPlace#field}
         */
        private boolean isComplex() {
            return field != null;
        }

        private Optional<String> getField() {
            return Optional.ofNullable(field);
        }

        private String getToken() {
            return "${" + token + "}";
        }

        private Object getFieldValueFor(Object obj)
                throws NoSuchFieldException, IllegalAccessException {
            if (!this.isComplex()) {
                throw new TemplateEngineException(
                        "Exception while trying to retrieve field value from " +
                        "class=%s when InsertPlace is not complex (has no field)",
                        obj.getClass()
                );
            }
            String fieldName = this.getField().get();
            Field field = obj.getClass().getDeclaredField(fieldName);
            if (field.canAccess(obj)) {
                return field.get(obj);
            }
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            field.setAccessible(false);
            return fieldValue;
        }

        private Object getFieldValue(Object obj) {
            try {
                return getFieldValueFor(obj);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new TemplateEngineException(
                        "Exception while retrieving value from field name=%s " +
                        "from object by class='%s'",
                        e, field, obj.getClass()
                );
            }
        }
    }
}
