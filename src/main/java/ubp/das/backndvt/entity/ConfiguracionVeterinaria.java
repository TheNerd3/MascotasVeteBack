package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Nota: el nombre de tabla "configuracion_veterinatarias" (con ese typo)
 * viene tal cual del script de creacion de la base MascotasCordoba. Se
 * respeta a proposito para que el mapeo JPA coincida con el esquema real.
 */
@Entity
@Table(name = "configuracion_veterinatarias")
@IdClass(ConfiguracionVeterinariaId.class)
public class ConfiguracionVeterinaria {

    @Id
    @Column(name = "id_veterinaria")
    private Integer idVeterinaria;

    @Id
    @Column(name = "cod_atributo")
    private Integer codAtributo;

    @Column(name = "valor", length = 300)
    private String valor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_veterinaria", insertable = false, updatable = false)
    private Veterinaria veterinaria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cod_atributo", insertable = false, updatable = false)
    private AtributoSistema atributo;

    public Integer getIdVeterinaria() {
        return idVeterinaria;
    }

    public void setIdVeterinaria(Integer idVeterinaria) {
        this.idVeterinaria = idVeterinaria;
    }

    public Integer getCodAtributo() {
        return codAtributo;
    }

    public void setCodAtributo(Integer codAtributo) {
        this.codAtributo = codAtributo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Veterinaria getVeterinaria() {
        return veterinaria;
    }

    public void setVeterinaria(Veterinaria veterinaria) {
        this.veterinaria = veterinaria;
    }

    public AtributoSistema getAtributo() {
        return atributo;
    }

    public void setAtributo(AtributoSistema atributo) {
        this.atributo = atributo;
    }
}
