package ubp.das.backndvt.entity;

import java.io.Serializable;
import java.util.Objects;

public class CaracteristicaMascotaId implements Serializable {

    private Integer nroRegMunicipal;
    private Integer codRasgo;
    private Integer nroCaracteristica;

    public CaracteristicaMascotaId() {
    }

    public CaracteristicaMascotaId(Integer nroRegMunicipal, Integer codRasgo, Integer nroCaracteristica) {
        this.nroRegMunicipal = nroRegMunicipal;
        this.codRasgo = codRasgo;
        this.nroCaracteristica = nroCaracteristica;
    }

    public Integer getNroRegMunicipal() {
        return nroRegMunicipal;
    }

    public void setNroRegMunicipal(Integer nroRegMunicipal) {
        this.nroRegMunicipal = nroRegMunicipal;
    }

    public Integer getCodRasgo() {
        return codRasgo;
    }

    public void setCodRasgo(Integer codRasgo) {
        this.codRasgo = codRasgo;
    }

    public Integer getNroCaracteristica() {
        return nroCaracteristica;
    }

    public void setNroCaracteristica(Integer nroCaracteristica) {
        this.nroCaracteristica = nroCaracteristica;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CaracteristicaMascotaId that)) {
            return false;
        }
        return Objects.equals(nroRegMunicipal, that.nroRegMunicipal)
                && Objects.equals(codRasgo, that.codRasgo)
                && Objects.equals(nroCaracteristica, that.nroCaracteristica);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nroRegMunicipal, codRasgo, nroCaracteristica);
    }
}
