package ubp.das.backndvt.entity;

import java.util.Arrays;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Traduce entre el enum EstadoPublicacion y el texto que guarda
 * estado_publicacion en la base ("Activa", "Pausada", "Finalizada"),
 * que no coincide con los nombres de las constantes Java (ACTIVA,
 * PAUSADA, FINALIZADA).
 */
@Converter(autoApply = true)
public class EstadoPublicacionConverter implements AttributeConverter<EstadoPublicacion, String> {

    @Override
    public String convertToDatabaseColumn(EstadoPublicacion estado) {
        return estado == null ? null : estado.getNombreEnBase();
    }

    @Override
    public EstadoPublicacion convertToEntityAttribute(String nombreEnBase) {
        if (nombreEnBase == null) {
            return null;
        }
        return Arrays.stream(EstadoPublicacion.values())
                .filter(estado -> estado.getNombreEnBase().equals(nombreEnBase))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado de publicacion desconocido: " + nombreEnBase));
    }
}
