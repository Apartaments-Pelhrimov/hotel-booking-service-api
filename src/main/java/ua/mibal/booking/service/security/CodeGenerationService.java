package ua.mibal.booking.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mibal.booking.config.properties.ActivationCodeProps;

import java.util.Random;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class CodeGenerationService {
    private final static String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                "abcdefghijklmnopqrstuvwxyz" +
                                                "0123456789" + "0123456789" +
                                                "0123456789" + "0123456789";

    private final ActivationCodeProps activationCodeProps;

    public String generateCode() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        int length = ALLOWED_CHARS.length();
        int count = 0;
        while (count++ != activationCodeProps.length()) {
            int index = random.nextInt(length);
            char val = ALLOWED_CHARS.charAt(index);
            stringBuilder.append(val);
        }
        return stringBuilder.toString();
    }
}
