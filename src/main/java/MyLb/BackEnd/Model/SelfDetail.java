package MyLb.BackEnd.Model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference; // NÉCESSAIRE

@Entity
@Table(name = "self_detail")
public class SelfDetail {

    @Id
    @Column(name = "self_detail_id")
    private String selfDetailId; // ID de type String pour la logique métier

    private String usagePurpose;
    private String cinNumber;
    private String phoneNumber;
    private Integer age;

    // Relation inverse : lien vers l'entité Client
    // Utilise FetchType.LAZY par défaut, ce qui est recommandé pour le côté inverse.
    @OneToOne(mappedBy = "selfDetail")
    @JsonBackReference // Pour la sérialisation JSON (l'ignore)
    private Client client;

    // ----------------------------------------------------------------------
    // Constructeur Sans Argument (Requis par JPA)
    // ----------------------------------------------------------------------
    public SelfDetail() {
        // Constructeur par défaut requis par JPA
    }

    // ----------------------------------------------------------------------
    // Getters et Setters (Importants pour la sérialisation Jackson)
    // ----------------------------------------------------------------------

    public String getSelfDetailId() { return selfDetailId; }
    public void setSelfDetailId(String selfDetailId) { this.selfDetailId = selfDetailId; }

    public String getUsagePurpose() { return usagePurpose; }
    public void setUsagePurpose(String usagePurpose) { this.usagePurpose = usagePurpose; }

    public String getCinNumber() { return cinNumber; }
    public void setCinNumber(String cinNumber) { this.cinNumber = cinNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}