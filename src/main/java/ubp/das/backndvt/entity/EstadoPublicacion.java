package ubp.das.backndvt.entity;

import java.util.EnumSet;
import java.util.Set;

/**
 * RF13 - Estados de una publicacion de adopcion y sus transiciones
 * permitidas. Unico lugar donde vive esta regla (antes estaba
 * repetida como constantes String, un regex en el DTO y un if en el
 * service): Activa y Pausada se alternan entre si o pasan a
 * Finalizada; Finalizada es terminal (no tiene salida).
 */
public enum EstadoPublicacion {

    ACTIVA("Activa"),
    PAUSADA("Pausada"),
    FINALIZADA("Finalizada");

    private final String nombreEnBase;

    EstadoPublicacion(String nombreEnBase) {
        this.nombreEnBase = nombreEnBase;
    }

    public String getNombreEnBase() {
        return nombreEnBase;
    }

    /**
     * Acciones que se le pueden aplicar a una publicacion en este
     * estado (lo que el frontend debe mostrar como accionesDisponibles).
     */
    public Set<Accion> accionesDisponibles() {
        return switch (this) {
            case ACTIVA -> EnumSet.of(Accion.PAUSAR, Accion.FINALIZAR);
            case PAUSADA -> EnumSet.of(Accion.ACTIVAR, Accion.FINALIZAR);
            case FINALIZADA -> EnumSet.noneOf(Accion.class);
        };
    }

    public boolean permite(Accion accion) {
        return accionesDisponibles().contains(accion);
    }

    /**
     * Acciones de negocio que se le pueden pedir a una publicacion
     * (RF13: activar, pausar, finalizar). Cada una sabe a que estado
     * destino lleva.
     */
    public enum Accion {
        ACTIVAR,
        PAUSAR,
        FINALIZAR;

        public EstadoPublicacion getEstadoDestino() {
            return switch (this) {
                case ACTIVAR -> EstadoPublicacion.ACTIVA;
                case PAUSAR -> EstadoPublicacion.PAUSADA;
                case FINALIZAR -> EstadoPublicacion.FINALIZADA;
            };
        }
    }
}
