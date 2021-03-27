package br.com.rrstecnologia.pharmastock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PharmaStockExceededException extends Exception{

    public PharmaStockExceededException(Long id, int quantityToIncrement){
        super(String.format("Pharma with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}
