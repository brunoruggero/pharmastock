package br.com.rrstecnologia.pharmastock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PharmaNotFoundException extends Exception{

    public PharmaNotFoundException(String pharmaName){
        super(String.format("Pharma with name %s not found in the system.", pharmaName));
    }

    public PharmaNotFoundException(Long id){
        super(String.format("Pharma with id %s not found in the system.", id));
    }
}
