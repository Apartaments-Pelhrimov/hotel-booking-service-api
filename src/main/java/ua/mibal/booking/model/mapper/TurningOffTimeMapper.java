package ua.mibal.booking.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.model.dto.request.TurnOffDto;
import ua.mibal.booking.model.entity.HotelTurningOffTime;
import ua.mibal.booking.model.entity.embeddable.TurningOffTime;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TurningOffTimeMapper {

    TurningOffTime apartmentfromDto(TurnOffDto turnOffDto);

    HotelTurningOffTime hotelFromDto(TurnOffDto turnOffDto);
}
