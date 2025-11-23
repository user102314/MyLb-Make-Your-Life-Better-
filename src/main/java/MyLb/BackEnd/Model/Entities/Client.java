package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore; // 👈 AJOUT NÉCESSAIRE POUR BRISER LA BOUCLE

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String role;
    private Boolean isVerified;
    private String password;

    @Column(unique = true)
    private String email;

    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    // Cette relation est le côté "parent" de la sérialisation.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_self_detail_id", referencedColumnName = "self_detail_id")
    @JsonManagedReference
    private SelfDetail selfDetail;

    // 🚨 MODIFICATION CRITIQUE :
    // Cette relation bidirectionnelle cause la boucle Client -> ClientSecurity -> Client lors de la sérialisation.
    // L'ajout de @JsonIgnore empêche la sérialisation de cet objet, brisant ainsi la récursion JSON.
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 👈 AJOUT DE LA LIGNE CLÉ
    private ClientSecurity clientSecurity;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // 🚨 AJOUTEZ CETTE ANNOTATION
    private Set<ClientAction> clientActions;
    // ----------------------------------------------------------------------
    // Constructeurs
    // ----------------------------------------------------------------------
    public Client() {
        // Initialiser l'entité de sécurité immédiatement.
        this.clientSecurity = new ClientSecurity(this);
    }

    // ----------------------------------------------------------------------
    // Getters et Setters
    // ----------------------------------------------------------------------

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // 🚨 Changement : L'annotation @JsonIgnore a été déplacée sur le champ ci-dessus,
    // mais elle pourrait aussi être placée ici pour une plus grande clarté.
    // public ClientSecurity getClientSecurity() { return clientSecurity; }
    public ClientSecurity getClientSecurity() { return clientSecurity; }
    public void setClientSecurity(ClientSecurity clientSecurity) {
        this.clientSecurity = clientSecurity;
        if (clientSecurity != null && clientSecurity.getClient() != this) {
            clientSecurity.setClient(this);
        }
    }

    // ... (Reste des Getters et Setters inchangés) ...
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public SelfDetail getSelfDetail() { return selfDetail; }
    public void setSelfDetail(SelfDetail selfDetail) { this.selfDetail = selfDetail; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }
}