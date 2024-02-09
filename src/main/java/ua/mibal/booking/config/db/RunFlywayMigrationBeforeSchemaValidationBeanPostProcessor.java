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

package ua.mibal.booking.config.db;

import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// TODO
/**
 * Custom {@link BeanPostProcessor} that used to start Flyway migration
 * just after {@link Flyway} bean initialization.
 *
 * {@link BeanPostProcessor} used only in {@code .jar} packaging because
 * Spring Boot calls at startup schema validation before than
 * {@link Flyway} migration
 *
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Profile("heroku")
@Component
public class RunFlywayMigrationBeforeSchemaValidationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean,
                                                 String beanName) throws BeansException {
        if (bean instanceof Flyway flyway) {
            flyway.migrate();
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
