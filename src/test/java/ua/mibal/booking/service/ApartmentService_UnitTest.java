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
import ua.mibal.booking.model.dto.response.ApartmentTypeCardDto;
import ua.mibal.booking.model.dto.response.ApartmentTypeDto;
import ua.mibal.booking.model.entity.ApartmentType;
import ua.mibal.booking.model.exception.entity.ApartmentNotFoundException;
import ua.mibal.booking.model.mapper.ApartmentMapper;
import ua.mibal.booking.repository.ApartmentTypeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">9mohapx9@gmail.com</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApartmentService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentService_UnitTest {

    @Autowired
    private ApartmentService service;

    @MockBean
    private ApartmentTypeRepository apartmentTypeRepository;
    @MockBean
    private ApartmentMapper apartmentMapper;

    @Mock
    private ApartmentType apartmentType;
    @Mock
    private ApartmentTypeDto apartmentTypeDto;
    @Mock
    private ApartmentTypeCardDto apartmentTypeCardDto;

    @Test
    void getOneDto() {
        when(apartmentTypeRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.of(apartmentType));
        when(apartmentMapper.toDto(apartmentType))
                .thenReturn(apartmentTypeDto);

        ApartmentTypeDto actual = assertDoesNotThrow(
                () -> service.getOneDto(1L)
        );

        assertEquals(apartmentTypeDto, actual);
    }

    @Test
    void getOneDto_should_throw_ApartmentNotFoundException() {
        when(apartmentTypeRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.empty());

        ApartmentNotFoundException e = assertThrows(
                ApartmentNotFoundException.class,
                () -> service.getOneDto(1L)
        );

        assertEquals(
                new ApartmentNotFoundException(1L).getMessage(),
                e.getMessage()
        );
        verifyNoInteractions(apartmentMapper);

    }

    @Test
    void getOne() {
        when(apartmentTypeRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.of(apartmentType));

        ApartmentType actual = assertDoesNotThrow(
                () -> service.getOne(1L)
        );

        assertEquals(apartmentType, actual);
    }

    @Test
    void getOne_should_throw_ApartmentNotFoundException() {
        when(apartmentTypeRepository.findByIdFetchPhotos(1L))
                .thenReturn(Optional.empty());

        ApartmentNotFoundException e = assertThrows(
                ApartmentNotFoundException.class,
                () -> service.getOne(1L)
        );

        assertEquals(
                new ApartmentNotFoundException(1L).getMessage(),
                e.getMessage()
        );
        verifyNoInteractions(apartmentMapper);
    }

    @Test
    void getAll() {
        when(apartmentTypeRepository.findAllFetchPhotos())
                .thenReturn(List.of(apartmentType, apartmentType));
        when(apartmentMapper.toCardDto(apartmentType))
                .thenReturn(apartmentTypeCardDto);

        List<ApartmentTypeCardDto> actual = service.getAll();

        assertEquals(
                List.of(apartmentTypeCardDto, apartmentTypeCardDto),
                actual
        );
    }
}
