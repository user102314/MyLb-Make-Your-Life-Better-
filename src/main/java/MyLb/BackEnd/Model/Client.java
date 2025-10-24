package MyLb.BackEnd.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonManagedReference; // NÉCESSAIRE

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

    // Rôle initial: CIVIL, INVESTOR, PO (Owner)
    private String role;

    // 🚨 CORRECTION : Utilisation de FetchType.EAGER pour forcer le chargement immédiat.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_self_detail_id", referencedColumnName = "self_detail_id")
    @JsonManagedReference // Pour la sérialisation JSON
    private SelfDetail selfDetail;

    private Boolean isVerified;

    private String password;

    @Column(unique = true)
    private String email;

    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    // ----------------------------------------------------------------------
    // Constructeurs (Requis par JPA)
    // ----------------------------------------------------------------------
    public Client() {
    }

    public Client(Long clientId, String firstName, String lastName, LocalDate birthDate, String role, SelfDetail selfDetail, Boolean isVerified, String password, String email, byte[] profileImage) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.role = role;
        this.selfDetail = selfDetail;
        this.isVerified = isVerified;
        this.password = password;
        this.email = email;
        this.profileImage = profileImage;
    }

    // ----------------------------------------------------------------------
    // Getters et Setters
    // ----------------------------------------------------------------------

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }
}