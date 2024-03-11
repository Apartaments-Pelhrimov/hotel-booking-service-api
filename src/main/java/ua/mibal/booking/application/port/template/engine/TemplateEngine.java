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

package ua.mibal.booking.application.port.template.engine;

import java.util.Locale;
import java.util.Map;

/**
 * The {@code TemplateEngine} interface defines the contract for template processing in a localized
 * context.
 * Implementations of this interface are responsible for processing a template with dynamic data
 * provided in the form of a context, and producing the final processed output as a {@code String}.
 *
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public interface TemplateEngine {

    /**
     * Processes the specified template with the given locale and context, producing the final
     * processed output.
     *
     * <p>The context parameter, represented as {@code Map<String, Object> context}, is expected
     * to be a mapping of variable names to corresponding values.
     * The variables in the template are identified by their names, and the context provides the
     * values to be substituted for these variables during template processing.
     *
     * @param template The template name to be processed.
     * @param locale   The locale specifying template language.
     * @param context  The context containing variable name and value to be used during template
     *                 processing.
     * @return The processed template as a {@code String}.
     * @throws TemplateEngineException on any error
     */
    String process(String template, Locale locale, Map<String, Object> context);
}
