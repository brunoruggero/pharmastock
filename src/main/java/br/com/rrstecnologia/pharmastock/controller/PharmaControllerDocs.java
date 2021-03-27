package br.com.rrstecnologia.pharmastock.controller;

import br.com.rrstecnologia.pharmastock.dto.PharmaDTO;
import br.com.rrstecnologia.pharmastock.dto.QuantityDTO;
import br.com.rrstecnologia.pharmastock.exception.PharmaAlreadyRegisteredException;
import br.com.rrstecnologia.pharmastock.exception.PharmaNotFoundException;
import br.com.rrstecnologia.pharmastock.exception.PharmaStockExceededException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api("Manges pharma stock")
public interface PharmaControllerDocs {

    @ApiOperation(value = "Pharma creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success pharma creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    PharmaDTO createPharma(PharmaDTO beerDTO) throws PharmaAlreadyRegisteredException;

    @ApiOperation(value = "Returns pharma found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success pharma found in the system"),
            @ApiResponse(code = 404, message = "Pharma with given name not found.")
    })
    PharmaDTO findByName(@PathVariable String name) throws PharmaNotFoundException;

    @ApiOperation(value = "Returns a list of all pharmas registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all pharmas registered in the system"),
    })
    List<PharmaDTO> listPharms();

    @ApiOperation(value = "Delete a pharma found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success pharmas deleted in the system"),
            @ApiResponse(code = 404, message = "Pharma with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws PharmaNotFoundException;

    @ApiOperation(value = "Increment pharma by a given id quantity in a stock")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success pharma incremented in stock"),
            @ApiResponse(code = 400, message = "Pharma not successfully increment in stock"),
            @ApiResponse(code = 404, message = "Pharma with given id not found.")
    })
    PharmaDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws PharmaNotFoundException, PharmaStockExceededException;

    @ApiOperation(value = "Decrement pharma by a given id quantity in a stock")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success pharma decremented in stock"),
            @ApiResponse(code = 400, message = "Pharma not successfully increment in stock"),
            @ApiResponse(code = 404, message = "Pharma with given id not found.")
    })
    PharmaDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws PharmaNotFoundException, PharmaStockExceededException;
}
