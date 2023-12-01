package ua.mibal.booking.service.security;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserDetailsSecurityService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserDetailsSecurityService_UnitTest {

    @Autowired
    private UserDetailsSecurityService service;

    @MockBean
    private UserRepository userRepository;

    @Mock
    private User user;

    @Test
    void loadUserByUsername() {
        when(userRepository.findByEmail("email"))
                .thenReturn(Optional.of(user));

        UserDetails actual = service.loadUserByUsername("email");

        assertEquals(user, actual);
    }

    @Test
    void loadUserByUsername_should_throw_UsernameNotFoundException() {
        when(userRepository.findByEmail("example@company.com"))
                .thenReturn(Optional.of(user));

        UsernameNotFoundException e = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("example@company.com")
        );

        assertTrue(e.getMessage().contains("example@company.com"));

    }
}
