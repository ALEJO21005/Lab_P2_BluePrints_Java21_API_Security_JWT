package co.edu.eci.blueprints.auth;

import co.edu.eci.blueprints.security.InMemoryUserService;
import co.edu.eci.blueprints.security.RsaKeyProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y emisión de tokens JWT")
public class AuthController {

    private final JwtEncoder encoder;
    private final InMemoryUserService userService;
    private final RsaKeyProperties props;

    public AuthController(JwtEncoder encoder, InMemoryUserService userService, RsaKeyProperties props) {
        this.encoder = encoder;
        this.userService = userService;
        this.props = props;
    }

    public record LoginRequest(String username, String password) {}
    public record TokenResponse(String access_token, String token_type, long expires_in) {}

    @PostMapping("/login")
    @Operation(
        summary = "Autenticar usuario y obtener token JWT",
        description = "Autentica un usuario con credenciales y retorna un token Bearer JWT válido con scopes 'blueprints.read' y 'blueprints.write'. " +
                     "Usuarios disponibles: student/student123, assistant/assistant123"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticación exitosa - Token JWT generado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenResponse.class),
                examples = @ExampleObject(
                    name = "Token exitoso",
                    value = """
                    {
                      "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2RlY3Npcy1lY2kvYmx1ZXByaW50cyIsImlhdCI6MTcwOTYwMDAwMCwiZXhwIjoxNzA5NjAzNjAwLCJzdWIiOiJzdHVkZW50Iiwic2NvcGUiOiJibHVlcHJpbnRzLnJlYWQgYmx1ZXByaW50cy53cml0ZSJ9...",
                      "token_type": "Bearer",
                      "expires_in": 300
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de autenticación",
                    value = "{\"error\": \"invalid_credentials\"}"
                )
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Credenciales del usuario",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginRequest.class),
            examples = {
                @ExampleObject(
                    name = "Usuario student",
                    value = "{\"username\": \"student\", \"password\": \"student123\"}"
                ),
                @ExampleObject(
                    name = "Usuario assistant",
                    value = "{\"username\": \"assistant\", \"password\": \"assistant123\"}"
                )
            }
        )
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (!userService.isValid(req.username(), req.password())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }

        Instant now = Instant.now();
        long ttl = props.tokenTtlSeconds() != null ? props.tokenTtlSeconds() : 3600;
        Instant exp = now.plusSeconds(ttl);

        String scope = "blueprints.read blueprints.write";

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.issuer())
                .issuedAt(now)
                .expiresAt(exp)
                .subject(req.username())
                .claim("scope", scope)
                .build();

        JwsHeader jws = JwsHeader.with(() -> "RS256").build();
        String token = this.encoder.encode(JwtEncoderParameters.from(jws, claims)).getTokenValue();

        return ResponseEntity.ok(new TokenResponse(token, "Bearer", ttl));
    }
}
