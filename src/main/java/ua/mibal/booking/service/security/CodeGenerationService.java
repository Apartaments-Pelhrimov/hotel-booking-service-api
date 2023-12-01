package ua.mibal.booking.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RequiredArgsConstructor
@Service
public class CodeGenerationService {

    private final static int CODE_LENGTH = 30;
    private final static String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                "abcdefghijklmnopqrstuvwxyz" +
                                                "0123456789" +
                                                "-._~:/?#[]@!$&'()*+,;=";

    public String generateCode() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        int length = ALLOWED_CHARS.length();
        int count = 0;
        while (count++ != CODE_LENGTH) {
            int index = random.nextInt(length);
            char val = ALLOWED_CHARS.charAt(index);
            stringBuilder.append(val);
        }
        return stringBuilder.toString();
    }
}
