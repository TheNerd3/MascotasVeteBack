package ubp.das.backndvt.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/**
 * Entidad de solo lectura sobre la tabla de auditoria generica que llenan
 * los triggers TR_*_Audit (ver script de auditoria de MascotasCordoba).
 * Nunca se debe insertar/actualizar/borrar desde la aplicacion: eso lo
 * hace la base de datos sola via triggers.
 */
@Entity
@Immutable
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @Column(name = "nombre_tabla", nullable = false, length = 100)
    private String nombreTabla;

    @Column(name = "operacion", nullable = false, length = 10)
    private String operacion;

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDateTime fechaOperacion;

    @Column(name = "usuario_bd", nullable = false, length = 128)
    private String usuarioBd;

    @Lob
    @Column(name = "datos_anteriores")
    private String datosAnteriores;

    @Lob
    @Column(name = "datos_nuevos")
    private String datosNuevos;

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public String getNombreTabla() {
        return nombreTabla;
    }

    public String getOperacion() {
        return operacion;
    }

    public LocalDateTime getFechaOperacion() {
        return fechaOperacion;
    }

    public String getUsuarioBd() {
        return usuarioBd;
    }

    public String getDatosAnteriores() {
        return datosAnteriores;
    }

    public String getDatosNuevos() {
        return datosNuevos;
    }
}
