package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    // Champs ajoutés pour le dashboard
    private String phoneNumber;
    private String cinNumber;
    private Integer age;
    private String usagePurpose;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photocinRecto;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photocinVerso;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photocompletSelfie;

    private String identityStatus;
    private LocalDateTime createdAt;

    // Relations existantes
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_self_detail_id", referencedColumnName = "self_detail_id")
    @JsonManagedReference
    private SelfDetail selfDetail;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ClientSecurity clientSecurity;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ClientAction> clientActions;

    public Client() {
        this.clientSecurity = new ClientSecurity(this);
        this.createdAt = LocalDateTime.now();
        this.isVerified = false;
    }

    // Getters et Setters
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

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }

    public SelfDetail getSelfDetail() { return selfDetail; }
    public void setSelfDetail(SelfDetail selfDetail) { this.selfDetail = selfDetail; }

    public ClientSecurity getClientSecurity() { return clientSecurity; }
    public void setClientSecurity(ClientSecurity clientSecurity) {
        this.clientSecurity = clientSecurity;
        if (clientSecurity != null && clientSecurity.getClient() != this) {
            clientSecurity.setClient(this);
        }
    }

    public Set<ClientAction> getClientActions() { return clientActions; }
    public void setClientActions(Set<ClientAction> clientActions) { this.clientActions = clientActions; }

    // Nouveaux getters et setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCinNumber() { return cinNumber; }
    public void setCinNumber(String cinNumber) { this.cinNumber = cinNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getUsagePurpose() { return usagePurpose; }
    public void setUsagePurpose(String usagePurpose) { this.usagePurpose = usagePurpose; }

    public byte[] getPhotocinRecto() { return photocinRecto; }
    public void setPhotocinRecto(byte[] photocinRecto) { this.photocinRecto = photocinRecto; }

    public byte[] getPhotocinVerso() { return photocinVerso; }
    public void setPhotocinVerso(byte[] photocinVerso) { this.photocinVerso = photocinVerso; }

    public byte[] getPhotocompletSelfie() { return photocompletSelfie; }
    public void setPhotocompletSelfie(byte[] photocompletSelfie) { this.photocompletSelfie = photocompletSelfie; }

    public String getIdentityStatus() { return identityStatus; }
    public void setIdentityStatus(String identityStatus) { this.identityStatus = identityStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Méthode utilitaire pour la compatibilité
    public boolean isVerified() {
        return Boolean.TRUE.equals(isVerified);
    }
}