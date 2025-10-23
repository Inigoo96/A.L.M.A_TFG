package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoMeta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarMetaRequestDTO {
    @NotNull(message = "El ID de la meta es obligatorio")
    private Integer idMeta;

    @NotNull(message = "El estado es obligatorio")
    private EstadoMeta estado;

    private String notas;
}