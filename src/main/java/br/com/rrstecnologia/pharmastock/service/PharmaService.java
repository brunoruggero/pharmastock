package br.com.rrstecnologia.pharmastock.service;

import br.com.rrstecnologia.pharmastock.dto.PharmaDTO;
import br.com.rrstecnologia.pharmastock.entity.Pharma;
import br.com.rrstecnologia.pharmastock.exception.PharmaAlreadyRegisteredException;
import br.com.rrstecnologia.pharmastock.exception.PharmaNotFoundException;
import br.com.rrstecnologia.pharmastock.exception.PharmaStockExceededException;
import br.com.rrstecnologia.pharmastock.mapper.PharmaMapper;
import br.com.rrstecnologia.pharmastock.repository.PharmaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PharmaService {

    private final PharmaRepository pharmaRepository;
    private final PharmaMapper pharmaMapper = PharmaMapper.INSTANCE;

    public PharmaDTO createPharma(PharmaDTO pharmaDTO) throws PharmaAlreadyRegisteredException {
        verifyIfIsAlreadyResgistered(pharmaDTO.getName());
        Pharma pharma = pharmaMapper.toModel(pharmaDTO);
        Pharma savedPharma = pharmaRepository.save(pharma);
        return pharmaMapper.toDTO(savedPharma);
    }

    public PharmaDTO findByName(String name) throws PharmaNotFoundException{
        Pharma foundPharma = pharmaRepository.findByName(name)
                .orElseThrow(() -> new PharmaNotFoundException(name));
        return pharmaMapper.toDTO(foundPharma);
    }

    public List<PharmaDTO> listAll(){
        return pharmaRepository.findAll()
                .stream()
                .map(pharmaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws PharmaNotFoundException{
        verifyIfExists(id);
        pharmaRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyResgistered(String name) throws PharmaAlreadyRegisteredException{
        Optional<Pharma> optSavedPharma = pharmaRepository.findByName(name);
        if(optSavedPharma.isPresent()){
            throw new PharmaAlreadyRegisteredException(name);
        }
    }

    private Pharma verifyIfExists(Long id) throws PharmaNotFoundException{
        return pharmaRepository.findById(id)
                .orElseThrow(() -> new PharmaNotFoundException(id));
    }

    public PharmaDTO increment(Long id, int quantityToIncrement) throws PharmaNotFoundException, PharmaStockExceededException{
        Pharma pharmaToIncrementStock = verifyIfExists(id);
        int pharmaStockAfterIncrement = quantityToIncrement + pharmaToIncrementStock.getQuantity();
        if(pharmaStockAfterIncrement <= pharmaToIncrementStock.getMax()){
            pharmaToIncrementStock.setQuantity(pharmaStockAfterIncrement);
            Pharma incrementPharmaStock = pharmaRepository.save(pharmaToIncrementStock);
            return pharmaMapper.toDTO(incrementPharmaStock);
        }
        throw new PharmaStockExceededException(id, quantityToIncrement);
    }

    public  PharmaDTO decrement(Long id, int quantityToDecrement) throws PharmaNotFoundException, PharmaStockExceededException{
        Pharma pharmaToDecrementStock = verifyIfExists(id);
        int pharmaStockAfterDecrement = quantityToDecrement + pharmaToDecrementStock.getQuantity();
        if(pharmaStockAfterDecrement <= pharmaToDecrementStock.getMax()){
            pharmaToDecrementStock.setQuantity(pharmaStockAfterDecrement);
            Pharma decrementPharmaStock = pharmaRepository.save(pharmaToDecrementStock);
            return pharmaMapper.toDTO(decrementPharmaStock);
        }
        throw new PharmaStockExceededException(id, quantityToDecrement);
    }
}
