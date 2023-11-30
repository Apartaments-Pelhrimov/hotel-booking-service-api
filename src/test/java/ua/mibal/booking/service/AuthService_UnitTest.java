package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.dto.auth.AuthResponseDto;
import ua.mibal.booking.model.dto.auth.RegistrationDto;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.model.exception.EmailAlreadyExistsException;
import ua.mibal.booking.model.mapper.UserMapper;
import ua.mibal.booking.service.security.TokenService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthService.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:application.yaml")
class AuthService_UnitTest {

    private final static User testUser = new User() {{
        setFirstName("test");
        setLastName("test");
        setEmail("test");
    }};

    @MockBean
    private TokenService tokenService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private ActivationCodeService activationCodeService;
    @MockBean
    private EmailSendingService emailSendingService;

    @Autowired
    private AuthService authService;

    @Test
    void token_should_return_correct_AuthDto() {
        AuthResponseDto expectedAuthDto = generateAuthResponseDtoWithToken("test_token");
        when(tokenService.generateToken(testUser.getEmail(), testUser.getAuthorities()))
                .thenReturn("test_token");
        when(userMapper.toAuthResponse(testUser, "test_token"))
                .thenReturn(expectedAuthDto);

        assertEquals(expectedAuthDto, authService.token(testUser));
        verify(tokenService, times(1))
                .generateToken(testUser.getEmail(), testUser.getAuthorities());
        verify(userMapper, times(1))
                .toAuthResponse(testUser, "test_token");
    }

    private AuthResponseDto generateAuthResponseDtoWithToken(String token) {
        return new AuthResponseDto(
                testUser.getFirstName(),
                testUser.getLastName(),
                token
        );
    }

    @Test
    void register_should_throw_EmailAlreadyExistsException_if_email_exists() {
        String existingEmail = "existing_email";
        when(userService.isExistsByEmail(existingEmail))
                .thenReturn(true);

        EmailAlreadyExistsException e = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(registrationDtoWithEmail(existingEmail))
        );
        assertEquals(
                new EmailAlreadyExistsException(existingEmail).getMessage(),
                e.getMessage()
        );
    }

    private RegistrationDto registrationDtoWithEmail(String email) {
        return new RegistrationDto(
                "test",
                "test",
                "test",
                email,
                "test"
        );
    }

    @Test
    void register_should_delegate_user_saving_to_UserService() {
        RegistrationDto registrationDto = registrationDtoWithEmail("email");
        when(userService.isExistsByEmail(registrationDto.email()))
                .thenReturn(false);
        when(passwordEncoder.encode(registrationDto.password()))
                .thenReturn(registrationDto.password());
        when(userService.save(registrationDto, registrationDto.password()))
                .thenReturn(testUser);

        authService.register(registrationDto);
        verify(userService, times(1))
                .save(registrationDto, registrationDto.password());
    }

    @Test
    void register_should_delegate_ActivationCode_saving_and_email_sending() {
        RegistrationDto registrationDto = registrationDtoWithEmail("email");
        when(userService.isExistsByEmail(registrationDto.email()))
                .thenReturn(false);
        when(passwordEncoder.encode(registrationDto.password()))
                .thenReturn(registrationDto.password());
        when(userService.save(registrationDto, registrationDto.password()))
                .thenReturn(testUser);
        ActivationCode expectedActivationCode = new ActivationCode();
        when(activationCodeService.generateAndSaveCodeForUser(testUser))
                .thenReturn(expectedActivationCode);

        authService.register(registrationDto);
        verify(activationCodeService, times(1))
                .generateAndSaveCodeForUser(testUser);
        verify(emailSendingService, times(1))
                .sendActivationCode(testUser, expectedActivationCode);
    }

    @Test
    void activate_should_delegate_activation_to_ActivationCodeService() {
        String activationCode = "CODE";

        authService.activate(activationCode);
        verify(activationCodeService, times(1))
                .activateByCode(activationCode);
    }

    @Test
    void restore() {
    }

    @Test
    void newPassword() {
    }
}
