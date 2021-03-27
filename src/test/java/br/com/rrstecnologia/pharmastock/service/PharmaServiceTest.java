package br.com.rrstecnologia.pharmastock.service;

import br.com.rrstecnologia.pharmastock.builder.PharmaDTOBuilder;
import br.com.rrstecnologia.pharmastock.dto.PharmaDTO;
import br.com.rrstecnologia.pharmastock.entity.Pharma;
import br.com.rrstecnologia.pharmastock.exception.PharmaAlreadyRegisteredException;
import br.com.rrstecnologia.pharmastock.exception.PharmaNotFoundException;
import br.com.rrstecnologia.pharmastock.exception.PharmaStockExceededException;
import br.com.rrstecnologia.pharmastock.mapper.PharmaMapper;
import br.com.rrstecnologia.pharmastock.repository.PharmaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PharmaServiceTest {

    private static final long INVALID_PHARMA_ID = 1L;
    private final PharmaMapper pharmaMapper = PharmaMapper.INSTANCE;
    @Mock
    private PharmaRepository pharmaRepository;
    @InjectMocks
    private PharmaService pharmaService;

    @Test
    void whenNewBeerInformedThenShouldBeCreated() throws PharmaAlreadyRegisteredException {
        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedSavedPharma = pharmaMapper.toModel(pharmaDTO);

        when(pharmaRepository.findByName(pharmaDTO.getName())).thenReturn(Optional.empty());
        when(pharmaRepository.save(expectedSavedPharma)).thenReturn(expectedSavedPharma);

        PharmaDTO createdPharmaDTO = pharmaService.createPharma(pharmaDTO);

        assertThat(createdPharmaDTO.getId(), is(equalTo(pharmaDTO.getId())));
        assertThat(createdPharmaDTO.getName(), is(equalTo(pharmaDTO.getName())));
        assertThat(createdPharmaDTO.getId(), is(equalTo(pharmaDTO.getId())));
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
        PharmaDTO pharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma duplicatedPharma = pharmaMapper.toModel(pharmaDTO);

        when(pharmaRepository.findByName(pharmaDTO.getName())).thenReturn(Optional.of(duplicatedPharma));

        assertThrows(PharmaAlreadyRegisteredException.class, () -> pharmaService.createPharma(pharmaDTO));
    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws PharmaNotFoundException {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedFoundBeer = pharmaMapper.toModel(expectedPharmaDTO);

        when(pharmaRepository.findByName(expectedPharmaDTO.getName())).thenReturn(Optional.of(expectedFoundBeer));

        PharmaDTO foundBeerDTO = pharmaService.findByName(expectedPharmaDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedPharmaDTO)));
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();

        when(pharmaRepository.findByName(expectedPharmaDTO.getName())).thenReturn(Optional.empty());

        assertThrows(PharmaNotFoundException.class, () -> pharmaService.findByName(expectedPharmaDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedFoundBeer = pharmaMapper.toModel(expectedPharmaDTO);

        when(pharmaRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        List<PharmaDTO> foundPharmaDTO = pharmaService.listAll();

        assertThat(foundPharmaDTO, is(not(empty())));
        assertThat(foundPharmaDTO.get(0), is(equalTo(expectedPharmaDTO)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyList() {
        when(pharmaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<PharmaDTO> foundPharmaDTO = pharmaService.listAll();

        assertThat(foundPharmaDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws PharmaNotFoundException {
        PharmaDTO expectedExcludedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedExcludedPharma = pharmaMapper.toModel(expectedExcludedPharmaDTO);

        when(pharmaRepository.findById(expectedExcludedPharmaDTO.getId())).thenReturn(Optional.of(expectedExcludedPharma));
        doNothing().when(pharmaRepository).deleteById(expectedExcludedPharma.getId());

        pharmaService.deleteById(expectedExcludedPharmaDTO.getId());

        verify(pharmaRepository, times(1)).findById(expectedExcludedPharmaDTO.getId());
        verify(pharmaRepository, times(1)).deleteById(expectedExcludedPharmaDTO.getId());
    }

    @Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {
        when(pharmaRepository.findById(INVALID_PHARMA_ID)).thenReturn(Optional.empty());

        assertThrows(PharmaNotFoundException.class, () -> pharmaService.deleteById(INVALID_PHARMA_ID));
    }

    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws PharmaNotFoundException, PharmaStockExceededException {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedPharma = pharmaMapper.toModel(expectedPharmaDTO);

        when(pharmaRepository.findById(expectedPharmaDTO.getId())).thenReturn(Optional.of(expectedPharma));
        when(pharmaRepository.save(expectedPharma)).thenReturn(expectedPharma);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedPharmaDTO.getQuantity() + quantityToIncrement;
        PharmaDTO incrementedBeerDTO = pharmaService.increment(expectedPharmaDTO.getId(), quantityToIncrement);

        assertThat(incrementedBeerDTO.getQuantity(), is(equalTo(expectedQuantityAfterIncrement)));
        assertThat(expectedPharmaDTO.getMax(), is(greaterThan(expectedQuantityAfterIncrement)));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedPharma = pharmaMapper.toModel(expectedPharmaDTO);

        when(pharmaRepository.findById(expectedPharmaDTO.getId())).thenReturn(Optional.of(expectedPharma));

        int quantityToIncrement = 80;
        assertThrows(PharmaStockExceededException.class, () -> pharmaService.increment(expectedPharmaDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(pharmaRepository.findById(INVALID_PHARMA_ID)).thenReturn(Optional.empty());

        assertThrows(PharmaNotFoundException.class, () -> pharmaService.increment(INVALID_PHARMA_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenDecrementBeerStock() throws PharmaNotFoundException, PharmaStockExceededException {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedPharma = pharmaMapper.toModel(expectedPharmaDTO);

        when(pharmaRepository.findById(expectedPharmaDTO.getId())).thenReturn(Optional.of(expectedPharma));
        when(pharmaRepository.save(expectedPharma)).thenReturn(expectedPharma);

        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = expectedPharmaDTO.getQuantity() - quantityToDecrement;
        PharmaDTO incrementedBeerDTO = pharmaService.decrement(expectedPharmaDTO.getId(), quantityToDecrement);

        assertThat(incrementedBeerDTO.getQuantity(), is(equalTo(expectedQuantityAfterDecrement)));
        assertThat(expectedQuantityAfterDecrement, is(greaterThan(0)));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws PharmaNotFoundException, PharmaStockExceededException {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedPharma = pharmaMapper.toModel(expectedPharmaDTO);

        when(pharmaRepository.findById(expectedPharmaDTO.getId())).thenReturn(Optional.of(expectedPharma));
        when(pharmaRepository.save(expectedPharma)).thenReturn(expectedPharma);

        int quantityToDecrement = 10;
        int expectedQuantityAfterDecrement = expectedPharmaDTO.getQuantity() - quantityToDecrement;
        PharmaDTO incrementedBeerDTO = pharmaService.decrement(expectedPharmaDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, is(equalTo(0)));
        assertThat(expectedQuantityAfterDecrement, is(equalTo(incrementedBeerDTO.getQuantity())));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        PharmaDTO expectedPharmaDTO = PharmaDTOBuilder.builder().build().toPharmaDTO();
        Pharma expectedPharma = pharmaMapper.toModel(expectedPharmaDTO);

        when(pharmaRepository.findById(expectedPharmaDTO.getId())).thenReturn(Optional.of(expectedPharma));

        int quantityToDecrement = 80;
        assertThrows(PharmaStockExceededException.class, () -> pharmaService.decrement(expectedPharmaDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;

        when(pharmaRepository.findById(INVALID_PHARMA_ID)).thenReturn(Optional.empty());

        assertThrows(PharmaNotFoundException.class, () -> pharmaService.decrement(INVALID_PHARMA_ID, quantityToDecrement));
    }
}
