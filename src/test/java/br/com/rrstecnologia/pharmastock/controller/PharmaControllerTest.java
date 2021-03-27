package br.com.rrstecnologia.pharmastock.controller;

import br.com.rrstecnologia.pharmastock.builder.PharmaDTOBuilder;
import br.com.rrstecnologia.pharmastock.dto.PharmaDTO;
import br.com.rrstecnologia.pharmastock.dto.QuantityDTO;
import br.com.rrstecnologia.pharmastock.exception.PharmaNotFoundException;
import br.com.rrstecnologia.pharmastock.exception.PharmaStockExceededException;
import br.com.rrstecnologia.pharmastock.service.PharmaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static br.com.rrstecnologia.pharmastock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PharmaControllerTest {

    private static final String PHARMA_API_URL_PATH = "/api/v1/pharmas";
    private static final long VALID_PHARMA_ID = 1L;
    private static final long INVALID_PHARMA_ID = 2l;
    private static final String PHARMA_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String PHARMA_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private PharmaService pharmaService;

    @InjectMocks
    private PharmaController pharmaController;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(pharmaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();

        when(pharmaService.createPharma(pharmaDTO)).thenReturn(pharmaDTO);

        mockMvc.perform(post(PHARMA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pharmaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(pharmaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(pharmaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(pharmaDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithouRequiredFieldThenAnErrorIsReturned() throws Exception {
        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        pharmaDTO.setName(null);

        mockMvc.perform(post(PHARMA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pharmaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();

        when(pharmaService.findByName(pharmaDTO.getName())).thenReturn(pharmaDTO);

        mockMvc.perform(get(PHARMA_API_URL_PATH + "/" + pharmaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(pharmaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(pharmaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(pharmaDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithNotRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();

        when(pharmaService.findByName(pharmaDTO.getName())).thenThrow(PharmaNotFoundException.class);

        mockMvc.perform(get(PHARMA_API_URL_PATH + "/" + pharmaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();

        when(pharmaService.listAll()).thenReturn(Collections.singletonList(pharmaDTO));

        mockMvc.perform(get(PHARMA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(pharmaDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(pharmaDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(pharmaDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
        when(pharmaService.listAll()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(get(PHARMA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        doNothing().when(pharmaService).deleteById(VALID_PHARMA_ID);

        mockMvc.perform(delete(PHARMA_API_URL_PATH + "/" + VALID_PHARMA_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(pharmaService, times(1)).deleteById(VALID_PHARMA_ID);
    }

    @Test
    void whenDELETEIsCalledWithoutValidIdThenNotFoundStatusIsReturned() throws Exception {
        doThrow(PharmaNotFoundException.class).when(pharmaService).deleteById(INVALID_PHARMA_ID);

        mockMvc.perform(delete(PHARMA_API_URL_PATH + "/" + INVALID_PHARMA_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        pharmaDTO.setQuantity(pharmaDTO.getQuantity() + quantityDTO.getQuantity());

        when(pharmaService.increment(VALID_PHARMA_ID, quantityDTO.getQuantity())).thenReturn(pharmaDTO);

        mockMvc.perform(patch(PHARMA_API_URL_PATH + "/" + VALID_PHARMA_ID + PHARMA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(pharmaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(pharmaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(pharmaDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(pharmaDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        pharmaDTO.setQuantity(pharmaDTO.getQuantity() + quantityDTO.getQuantity());

        when(pharmaService.increment(VALID_PHARMA_ID, quantityDTO.getQuantity())).thenThrow(PharmaStockExceededException.class);

        mockMvc.perform(patch(PHARMA_API_URL_PATH + "/" + VALID_PHARMA_ID + PHARMA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        when(pharmaService.increment(INVALID_PHARMA_ID, quantityDTO.getQuantity())).thenThrow(PharmaNotFoundException.class);
        mockMvc.perform(patch(PHARMA_API_URL_PATH + "/" + INVALID_PHARMA_ID + PHARMA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        pharmaDTO.setQuantity(pharmaDTO.getQuantity() + quantityDTO.getQuantity());

        when(pharmaService.decrement(VALID_PHARMA_ID, quantityDTO.getQuantity())).thenReturn(pharmaDTO);

        mockMvc.perform(patch(PHARMA_API_URL_PATH + "/" + VALID_PHARMA_ID + PHARMA_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(pharmaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(pharmaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(pharmaDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(pharmaDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(60)
                .build();

        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        pharmaDTO.setQuantity(pharmaDTO.getQuantity() + quantityDTO.getQuantity());

        when(pharmaService.decrement(VALID_PHARMA_ID, quantityDTO.getQuantity())).thenThrow(PharmaStockExceededException.class);

        mockMvc.perform(patch(PHARMA_API_URL_PATH + "/" + VALID_PHARMA_ID + PHARMA_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBeerIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        when(pharmaService.decrement(INVALID_PHARMA_ID, quantityDTO.getQuantity())).thenThrow(PharmaNotFoundException.class);
        mockMvc.perform(patch(PHARMA_API_URL_PATH + "/" + INVALID_PHARMA_ID + PHARMA_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

}
