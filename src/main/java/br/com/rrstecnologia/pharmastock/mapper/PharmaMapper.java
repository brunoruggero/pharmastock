package br.com.rrstecnologia.pharmastock.mapper;

import br.com.rrstecnologia.pharmastock.dto.PharmaDTO;
import br.com.rrstecnologia.pharmastock.entity.Pharma;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PharmaMapper {

    PharmaMapper INSTANCE = Mappers.getMapper(PharmaMapper.class);

    Pharma toModel(PharmaDTO pharmaDTO);

    PharmaDTO toDTO(Pharma pharma);
}
