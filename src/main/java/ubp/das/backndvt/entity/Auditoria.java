package ubp.das.backndvt.entity;

import java.time.LocalDateTime;

/**
 * Representa una fila de la tabla de auditoria generica que llenan los
 * triggers TR_*_Audit (ver script de auditoria de MascotasCordoba).
 * Nunca se debe insertar/actualizar/borrar desde la aplicacion: eso lo
 * hace la base de datos sola via triggers, nunca codigo Java.
 */
public class Auditoria {

    private Long idAuditoria;
    private String nombreTabla;
    private String operacion;
    private LocalDateTime fechaOperacion;
    private String usuarioBd;
    private String datosAnteriores;
    private String datosNuevos;

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public String getNombreTabla() {
        return nombreTabla;
    }

    public void setNombreTabla(String nombreTabla) {
        this.nombreTabla = nombreTabla;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public LocalDateTime getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(LocalDateTime fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public String getUsuarioBd() {
        return usuarioBd;
    }

    public void setUsuarioBd(String usuarioBd) {
        this.usuarioBd = usuarioBd;
    }

    public String getDatosAnteriores() {
        return datosAnteriores;
    }

    public void setDatosAnteriores(String datosAnteriores) {
        this.datosAnteriores = datosAnteriores;
    }

    public String getDatosNuevos() {
        return datosNuevos;
    }

    public void setDatosNuevos(String datosNuevos) {
        this.datosNuevos = datosNuevos;
    }
}
