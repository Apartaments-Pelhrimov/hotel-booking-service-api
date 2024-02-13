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

package ua.mibal.booking.model.exception.marker;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Getter
public abstract class ApiException extends RuntimeException {

    protected final String code;
    protected final HttpStatus httpStatus;

    public ApiException(String code,
                        HttpStatus httpStatus) {
        super();
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public ApiException(String message,
                        String code,
                        HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public ApiException(String message,
                        Throwable cause,
                        String code,
                        HttpStatus httpStatus) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getLocalizedMessage(MessageSource errorMessageSource,
                                      Locale locale) {
        return errorMessageSource.getMessage(code, provideArgs(), locale);
    }

    public abstract Object[] provideArgs();
}
