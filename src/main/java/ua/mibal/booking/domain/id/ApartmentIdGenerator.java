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

package ua.mibal.booking.domain.id;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import ua.mibal.booking.domain.Apartment;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
public class ApartmentIdGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        validateNotNull(object);
        validateIsApartment(object);
        return generateIdFor((Apartment) object);
    }

    private Object generateIdFor(Apartment apartment) {
        String name = apartment.getName();
        validateNotNull(name);
        String idValue = name.replace(" ", "-").toLowerCase();
        return new ApartmentId(idValue);
    }

    private void validateNotNull(Object object) {
        if (object == null) {
            throw new HibernateException("Object passed to generate id is null");
        }
    }

    private void validateIsApartment(Object object) {
        if (!(object instanceof Apartment)) {
            throw new HibernateException(
                    "Object passed to generate id is not instance of " + Apartment.class + object);
        }
    }
}
