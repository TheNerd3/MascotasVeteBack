package ubp.das.backndvt.entity;

import java.io.Serializable;
import java.util.Objects;

public class InformacionSanitariaId implements Serializable {

    private Integer nroRegMunicipal;
    private Integer nroRegistro;

    public InformacionSanitariaId() {
    }

    public InformacionSanitariaId(Integer nroRegMunicipal, Integer nroRegistro) {
        this.nroRegMunicipal = nroRegMunicipal;
        this.nroRegistro = nroRegistro;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InformacionSanitariaId that)) {
            return false;
        }
        return Objects.equals(nroRegMunicipal, that.nroRegMunicipal)
                && Objects.equals(nroRegistro, that.nroRegistro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nroRegMunicipal, nroRegistro);
    }
}
