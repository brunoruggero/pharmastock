package br.com.rrstecnologia.pharmastock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PharmaAlreadyRegisteredException extends Exception{

    public PharmaAlreadyRegisteredException(String pharmaName){
        super(String.format("Pharma with name %s already registered in the system.", pharmaName));
    }
}
