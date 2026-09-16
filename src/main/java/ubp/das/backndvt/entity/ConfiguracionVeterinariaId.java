package ubp.das.backndvt.entity;

import java.io.Serializable;
import java.util.Objects;

public class ConfiguracionVeterinariaId implements Serializable {

    private Integer idVeterinaria;
    private Integer codAtributo;

    public ConfiguracionVeterinariaId() {
    }

    public ConfiguracionVeterinariaId(Integer idVeterinaria, Integer codAtributo) {
        this.idVeterinaria = idVeterinaria;
        this.codAtributo = codAtributo;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfiguracionVeterinariaId that)) {
            return false;
        }
        return Objects.equals(idVeterinaria, that.idVeterinaria)
                && Objects.equals(codAtributo, that.codAtributo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVeterinaria, codAtributo);
    }
}
