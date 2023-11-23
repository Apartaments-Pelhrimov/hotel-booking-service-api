package ua.mibal.booking.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.mibal.booking.model.entity.embeddable.Photo;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class PhotoMapper {

    public String toLinkString(Photo photo) {
        return photo.getPhotoLink();
    }
}
