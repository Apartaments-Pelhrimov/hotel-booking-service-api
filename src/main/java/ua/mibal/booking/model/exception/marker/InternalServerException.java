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

import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public abstract class InternalServerException extends ApiException {

    protected InternalServerException(String message, Throwable cause) {
        super(
                message,
                cause,
                "internal-error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    protected InternalServerException(String message) {
        super(
                message,
                "internal-error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    public Object[] provideArgs() {
        return new Object[0];
    }

    public String getStackTraceMessage() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
