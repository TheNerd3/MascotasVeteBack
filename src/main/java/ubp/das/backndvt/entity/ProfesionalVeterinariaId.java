package ubp.das.backndvt.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProfesionalVeterinariaId implements Serializable {

    private Integer idVeterinaria;
    private Integer idProfesional;

    public ProfesionalVeterinariaId() {
    }

    public ProfesionalVeterinariaId(Integer idVeterinaria, Integer idProfesional) {
        this.idVeterinaria = idVeterinaria;
        this.idProfesional = idProfesional;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfesionalVeterinariaId that)) {
            return false;
        }
        return Objects.equals(idVeterinaria, that.idVeterinaria)
                && Objects.equals(idProfesional, that.idProfesional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVeterinaria, idProfesional);
    }
}
