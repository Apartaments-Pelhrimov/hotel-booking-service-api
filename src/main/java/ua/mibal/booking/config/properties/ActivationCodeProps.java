package ua.mibal.booking.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@ConfigurationProperties("activation-code")
public record ActivationCodeProps(
        int length
) {
}
