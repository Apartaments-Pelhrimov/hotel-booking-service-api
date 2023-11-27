package ua.mibal.booking.model.dto.request;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
public record ChangeNotificationSettingsDto(
        Boolean receiveOrderEmails,
        Boolean receiveNewsEmails
) {
}
