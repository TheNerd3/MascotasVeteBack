package ubp.das.backndvt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rasgos_mascotas")
public class RasgoMascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_rasgo")
    private Integer codRasgo;

    @Column(name = "nom_rasgo", nullable = false, length = 100)
    private String nomRasgo;

    public Integer getCodRasgo() {
        return codRasgo;
    }

    public void setCodRasgo(Integer codRasgo) {
        this.codRasgo = codRasgo;
    }

    public String getNomRasgo() {
        return nomRasgo;
    }

    public void setNomRasgo(String nomRasgo) {
        this.nomRasgo = nomRasgo;
    }
}
