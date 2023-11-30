package ua.mibal.booking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.mibal.booking.service.ApartmentService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Mykhailo Balakhon
 * @link <a href="mailto:9mohapx9@gmail.com">email</a>
 */
@WebMvcTest(ApartmentController.class)
@TestPropertySource("classpath:application.yaml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApartmentController_UnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @MockBean
    private ApartmentService apartmentService;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @CsvSource({"1-1", "value", "superman2004"})
    void getOne_should_throw_exception_if_id_path_variable_is_illegal(String id) throws Exception {
        mvc.perform(get("/api/apartments/{id}", id))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(apartmentService);
    }

    @ParameterizedTest
    @CsvSource({"1", "1000000", "" + Long.MAX_VALUE, "" + Long.MIN_VALUE})
    void getOne_should_handle_id_correct(Long id) throws Exception {
        mvc.perform(get("/api/apartments/{id}", id))
                .andExpect(status().isOk());

        verify(apartmentService, times(1))
                .getOneDto(id);
    }

    @Test
    void getAll_should_delegate_to_ApartmentService() throws Exception {
        mvc.perform(get("/api/apartments"))
                .andExpect(status().isOk());

        verify(apartmentService, times(1))
                .getAll();
    }
}
