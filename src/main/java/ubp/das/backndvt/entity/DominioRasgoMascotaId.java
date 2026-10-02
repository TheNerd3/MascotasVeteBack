package ubp.das.backndvt.entity;

import java.io.Serializable;
import java.util.Objects;

public class DominioRasgoMascotaId implements Serializable {

    private Integer codRasgo;
    private Integer nroValorDominio;

    public DominioRasgoMascotaId() {
    }

    public DominioRasgoMascotaId(Integer codRasgo, Integer nroValorDominio) {
        this.codRasgo = codRasgo;
        this.nroValorDominio = nroValorDominio;
    }

    public Integer getCodRasgo() {
        return codRasgo;
    }

    public void setCodRasgo(Integer codRasgo) {
        this.codRasgo = codRasgo;
    }

    public Integer getNroValorDominio() {
        return nroValorDominio;
    }

    public void setNroValorDominio(Integer nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DominioRasgoMascotaId that)) {
            return false;
        }
        return Objects.equals(codRasgo, that.codRasgo)
                && Objects.equals(nroValorDominio, that.nroValorDominio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codRasgo, nroValorDominio);
    }
}
