package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "profesionales_veterinarias")
@IdClass(ProfesionalVeterinariaId.class)
public class ProfesionalVeterinaria {

    @Id
    @Column(name = "id_veterinaria")
    private Integer idVeterinaria;

    @Id
    @Column(name = "id_profesional")
    private Integer idProfesional;

    @Column(name = "baja", nullable = false)
    private Boolean baja = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_veterinaria", insertable = false, updatable = false)
    private Veterinaria veterinaria;

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

    public Boolean getBaja() {
        return baja;
    }

    public void setBaja(Boolean baja) {
        this.baja = baja;
    }

    public Veterinaria getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(Veterinaria veterinaria) {
        this.veterinaria = veterinaria;
    }

}
