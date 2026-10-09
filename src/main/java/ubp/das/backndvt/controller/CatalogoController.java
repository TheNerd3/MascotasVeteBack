package ubp.das.backndvt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import ubp.das.backndvt.dto.ValorCatalogoResponse;
import ubp.das.backndvt.service.CatalogoService;

/**
 * RF06/RF13 - Catalogos de especie y raza, para los combos del
 * formulario de alta de mascota/publicacion. Endpoints publicos (son
 * listados de referencia, no datos de negocio).
 */
@RestController
@RequestMapping("/catalogos")
@Tag(name = "6. Catalogos", description = "Valores de especie y raza para formularios")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/especies")
    @SecurityRequirements
    @Operation(summary = "Listar especies", description = "Valores de dominio del rasgo Especie.")
    @ApiResponse(responseCode = "200", description = "Listado de especies")
    public ResponseEntity<List<ValorCatalogoResponse>> listarEspecies() {
        return ResponseEntity.ok(catalogoService.listarEspecies());
    }

    @GetMapping("/razas")
    @SecurityRequirements
    @Operation(
            summary = "Listar razas",
            description = "Valores de dominio del rasgo Raza. No esta filtrado por especie: el modelo no "
                    + "relaciona razas con especies, asi que devuelve el catalogo completo.")
    @ApiResponse(responseCode = "200", description = "Listado de razas")
    public ResponseEntity<List<ValorCatalogoResponse>> listarRazas() {
        return ResponseEntity.ok(catalogoService.listarRazas());
    }
}
