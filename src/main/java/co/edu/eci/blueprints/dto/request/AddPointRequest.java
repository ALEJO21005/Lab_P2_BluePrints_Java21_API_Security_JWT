package co.edu.eci.blueprints.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddPointRequest(
        @NotNull(message = "La coordenada X no puede ser nula")
        Integer x,
        
        @NotNull(message = "La coordenada Y no puede ser nula")
        Integer y
) { }
