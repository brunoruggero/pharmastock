package br.com.rrstecnologia.pharmastock.builder;

import br.com.rrstecnologia.pharmastock.dto.PharmaDTO;
import br.com.rrstecnologia.pharmastock.enums.PharmaType;
import lombok.Builder;

@Builder
public class PharmaDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Loratadina";

    @Builder.Default
    private String brand = "EMS";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private PharmaType type = PharmaType.EMS;

    public PharmaDTO toPharmaDTO() {
        return new PharmaDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
