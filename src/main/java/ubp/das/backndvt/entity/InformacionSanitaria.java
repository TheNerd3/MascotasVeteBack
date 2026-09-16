package ubp.das.backndvt.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Nota: nro_registro es un correlativo por mascota generado de forma
 * atomica en la base con el stored procedure
 * sp_InsertarInformacionSanitaria (contador en mascotas.ultimo_nro_atencion).
 * No usar @GeneratedValue aca: el numero se calcula del lado de la base.
 */
@Entity
@Table(name = "informacion_sanitaria")
@IdClass(InformacionSanitariaId.class)
public class InformacionSanitaria {

    @Id
    @Column(name = "nro_reg_municipal")
    private Integer nroRegMunicipal;

    @Id
    @Column(name = "nro_registro")
    private Integer nroRegistro;

    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;

    @Column(name = "cod_tipo_atencion", nullable = false)
    private Integer codTipoAtencion;

    @Lob
    @Column(name = "detalle_atencion")
    private String detalleAtencion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "id_veterinaria", nullable = false)
    private Integer idVeterinaria;

    @Column(name = "id_profesional", nullable = false)
    private Integer idProfesional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nro_reg_municipal", insertable = false, updatable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cod_tipo_atencion", insertable = false, updatable = false)
    private TipoAtencionSanitaria tipoAtencion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "id_veterinaria", referencedColumnName = "id_veterinaria", insertable = false, updatable = false),
            @JoinColumn(name = "id_profesional", referencedColumnName = "id_profesional", insertable = false, updatable = false)
    })
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

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
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
