package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.model.entity.ActivationCode;
import ua.mibal.booking.model.entity.User;
import ua.mibal.booking.repository.ActivationCodeRepository;
import ua.mibal.booking.service.security.CodeGenerationService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivationCodeService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ActivationCodeService_UnitTest {

    @Autowired
    private ActivationCodeService service;

    @MockBean
    private ActivationCodeRepository activationCodeRepository;
    @MockBean
    private CodeGenerationService codeGenerationService;

    @Mock
    private User user;
    @Mock
    private ActivationCode activationCode;

    @Test
    void activateByCode_should_activate_user_and_delete_code() {
        when(activationCodeRepository.findByCodeFetchUser("code"))
                .thenReturn(Optional.of(activationCode));
        when(activationCode.getUser())
                .thenReturn(user);

        service.activateByCode("code");

        verify(user, times(1))
                .setEnabled(true);
        verify(activationCodeRepository, times(1))
                .delete(activationCode);
    }

    @Test
    void activateByCode_should_not_activate_user_if_code_not_found() {
        when(activationCodeRepository.findByCodeFetchUser("code"))
                .thenReturn(Optional.empty());

        service.activateByCode("code");

        verify(user, never()).setEnabled(true);
        verify(activationCodeRepository, never()).delete(any());
    }

    @Test
    void changePasswordByCode_should_change_password_and_delete_code() {
        when(activationCodeRepository.findByCodeFetchUser("code"))
                .thenReturn(Optional.of(activationCode));
        when(activationCode.getUser())
                .thenReturn(user);

        service.changePasswordByCode("code", "password");

        verify(user, times(1))
                .setPassword("password");
        verify(activationCodeRepository, times(1))
                .delete(activationCode);
    }

    @Test
    void activateByCode_should_not_change_password_if_code_not_found() {
        when(activationCodeRepository.findByCodeFetchUser("code"))
                .thenReturn(Optional.empty());

        service.changePasswordByCode("code", "password");

        verify(user, never())
                .setPassword(any());
        verify(activationCodeRepository, never())
                .delete(any());
    }

    @Test
    void generateAndSaveCodeForUser_should_generate_and_save() {
        when(codeGenerationService.generateCode())
                .thenReturn("code");

        service.generateAndSaveCodeForUser(user);

        verify(activationCodeRepository, times(1))
                .save(new ActivationCode(any(), user, "code"));
    }
}
