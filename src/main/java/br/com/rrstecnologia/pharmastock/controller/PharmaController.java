package br.com.rrstecnologia.pharmastock.controller;


import br.com.rrstecnologia.pharmastock.dto.PharmaDTO;
import br.com.rrstecnologia.pharmastock.dto.QuantityDTO;
import br.com.rrstecnologia.pharmastock.exception.PharmaAlreadyRegisteredException;
import br.com.rrstecnologia.pharmastock.exception.PharmaNotFoundException;
import br.com.rrstecnologia.pharmastock.exception.PharmaStockExceededException;
import br.com.rrstecnologia.pharmastock.service.PharmaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmas")
@AllArgsConstructor(onConstructor = @__(@Autowired))
//@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class PharmaController implements PharmaControllerDocs{

    private final PharmaService pharmaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PharmaDTO createPharma(@RequestBody @Valid PharmaDTO pharmaDTO) throws PharmaAlreadyRegisteredException{
        return pharmaService.createPharma(pharmaDTO);
    }

    @GetMapping("/{name}")
    public PharmaDTO findByName(@PathVariable String name) throws PharmaNotFoundException{
        return pharmaService.findByName(name);
    }

    @GetMapping
    public List<PharmaDTO> listPharms() { return pharmaService.listAll(); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws PharmaNotFoundException{
        pharmaService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public PharmaDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws PharmaNotFoundException, PharmaStockExceededException{
        return pharmaService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public PharmaDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws PharmaNotFoundException, PharmaStockExceededException{
        return pharmaService.decrement(id, quantityDTO.getQuantity());
    }



}
