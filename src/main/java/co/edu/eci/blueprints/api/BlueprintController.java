package co.edu.eci.blueprints.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blueprints")
@Tag(name = "Blueprint Controller", description = "Endpoints de ejemplo para demostrar autenticación JWT y scopes")
@SecurityRequirement(name = "Bearer Authentication")
public class BlueprintController {

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    @Operation(
        summary = "Listar blueprints de ejemplo",
        description = "Retorna una lista de blueprints de demostración. Requiere autenticación JWT con scope 'blueprints.read'."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de blueprints obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = List.class),
                examples = @ExampleObject(
                    value = "[{\"id\": \"b1\", \"name\": \"Casa de campo\"}, {\"id\": \"b2\", \"name\": \"Edificio urbano\"}]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Prohibido - Token válido pero sin scope 'blueprints.read'",
            content = @Content(mediaType = "application/json")
        )
    })
    public List<Map<String, String>> list() {
        return List.of(
            Map.of("id", "b1", "name", "Casa de campo"),
            Map.of("id", "b2", "name", "Edificio urbano")
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    @Operation(
        summary = "Crear blueprint de ejemplo",
        description = "Crea un nuevo blueprint de demostración. Requiere autenticación JWT con scope 'blueprints.write'."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Blueprint creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    value = "{\"id\": \"new\", \"name\": \"Mi Blueprint\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Prohibido - Token válido pero sin scope 'blueprints.write'",
            content = @Content(mediaType = "application/json")
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del blueprint a crear",
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Ejemplo de blueprint",
                value = "{\"name\": \"Mi Blueprint\"}"
            )
        )
    )
    public Map<String, String> create(@RequestBody Map<String, String> in) {
        return Map.of("id", "new", "name", in.getOrDefault("name", "nuevo"));
    }
}
