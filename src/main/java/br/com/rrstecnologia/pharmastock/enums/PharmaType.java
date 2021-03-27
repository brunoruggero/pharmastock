package br.com.rrstecnologia.pharmastock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PharmaType {

    EMS("EMS"),
    HYPER("HYPER"),
    ACHE("ACHE"),
    EURO("EURO");

    private final String description;

}
