package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "atributos_sistema")
public class AtributoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_atributo")
    private Integer codAtributo;

    @Column(name = "desc_atributo", nullable = false, length = 150)
    private String descAtributo;

    @Column(name = "tipo_dato", nullable = false, length = 30)
    private String tipoDato;

    @Column(name = "observ_atributo", length = 300)
    private String observAtributo;

    public Integer getCodAtributo() {
        return codAtributo;
    }

    public void setCodAtributo(Integer codAtributo) {
        this.codAtributo = codAtributo;
    }

    public String getDescAtributo() {
        return descAtributo;
    }

    public void setDescAtributo(String descAtributo) {
        this.descAtributo = descAtributo;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getObservAtributo() {
        return observAtributo;
    }

    public void setObservAtributo(String observAtributo) {
        this.observAtributo = observAtributo;
    }
}
