package ua.mibal.booking.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import ua.mibal.booking.model.entity.User;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User getReferenceByEmail(String email) {
        Session session = entityManager.unwrap(Session.class);
        return session.bySimpleNaturalId(User.class)
                .getReference(email);
    }
}
