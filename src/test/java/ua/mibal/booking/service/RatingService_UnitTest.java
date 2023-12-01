package ua.mibal.booking.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ua.mibal.booking.repository.RatingRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RatingService.class)
@TestPropertySource(locations = "classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RatingService_UnitTest {

    @Autowired
    private RatingService service;

    @MockBean
    private RatingRepository ratingRepository;

    @Test
    void updateRatings_should_call_RatingsRepository() {
        service.updateRatings();

        verify(ratingRepository, times(1))
                .updateRatings();
    }
}
