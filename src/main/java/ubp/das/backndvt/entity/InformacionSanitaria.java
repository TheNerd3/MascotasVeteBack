package ubp.das.backndvt.entity;

import java.time.LocalDate;

public class InformacionSanitaria {

    private Integer nroRegMunicipal;
    private Integer nroRegistro;
    private LocalDate fechaAtencion;
    private Integer codTipoAtencion;
    private String detalleAtencion;
    private LocalDate fechaVencimiento;
    private Integer idVeterinaria;
    private Integer idProfesional;
    private TipoAtencionSanitaria tipoAtencion;
    private ProfesionalVeterinaria profesionalVeterinaria;

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public Integer getNroRegistro() {
        return nroRegistro;
    }

    public void setNroRegistro(Integer nroRegistro) {
        this.nroRegistro = nroRegistro;
    }

    public LocalDate getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDate fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public Integer getCodTipoAtencion() {
        return codTipoAtencion;
    }

    public void setCodTipoAtencion(Integer codTipoAtencion) {
        this.codTipoAtencion = codTipoAtencion;
    }

    public String getDetalleAtencion() {
        return detalleAtencion;
    }

    public void setDetalleAtencion(String detalleAtencion) {
        this.detalleAtencion = detalleAtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public Integer getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(Integer idProfesional) {
        this.idProfesional = idProfesional;
    }

    public TipoAtencionSanitaria getTipoAtencion() {
        return tipoAtencion;
    }

    public void setTipoAtencion(TipoAtencionSanitaria tipoAtencion) {
        this.tipoAtencion = tipoAtencion;
    }

    public ProfesionalVeterinaria getProfesionalVeterinaria() {
        return profesionalVeterinaria;
    }

    public void setProfesionalVeterinaria(ProfesionalVeterinaria profesionalVeterinaria) {
        this.profesionalVeterinaria = profesionalVeterinaria;
    }
}
