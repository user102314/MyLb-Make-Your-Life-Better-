package MyLb.BackEnd.dto;

import MyLb.BackEnd.Model.Entities.UserIdentity;
import MyLb.BackEnd.Model.Entities.CheckVerification;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserCompleteDetailsDTO {
    private Long clientId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String role;
    private Boolean isVerified;

    // SelfDetail information
    private String usagePurpose;
    private String cinNumber;
    private String phoneNumber;
    private Integer age;

    // UserIdentity information
    private Long identityId;
    private byte[] photocinRecto;
    private byte[] photocinVerso;
    private byte[] photocompletSelfie;
    private UserIdentity.ValidationStatus identityStatus;
    private LocalDateTime identityUploadDate;

    // CheckVerification information
    private Long verificationId;
    private boolean emailVerified;
    private boolean kycSubmitted;
    private boolean kycValidated;
    private boolean faceRecognition;
    private boolean fullyVerified;

    // Constructeurs
    public UserCompleteDetailsDTO() {}

    // Getters et Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public String getUsagePurpose() { return usagePurpose; }
    public void setUsagePurpose(String usagePurpose) { this.usagePurpose = usagePurpose; }

    public String getCinNumber() { return cinNumber; }
    public void setCinNumber(String cinNumber) { this.cinNumber = cinNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Long getIdentityId() { return identityId; }
    public void setIdentityId(Long identityId) { this.identityId = identityId; }

    public byte[] getPhotocinRecto() { return photocinRecto; }
    public void setPhotocinRecto(byte[] photocinRecto) { this.photocinRecto = photocinRecto; }

    public byte[] getPhotocinVerso() { return photocinVerso; }
    public void setPhotocinVerso(byte[] photocinVerso) { this.photocinVerso = photocinVerso; }

    public byte[] getPhotocompletSelfie() { return photocompletSelfie; }
    public void setPhotocompletSelfie(byte[] photocompletSelfie) { this.photocompletSelfie = photocompletSelfie; }

    public UserIdentity.ValidationStatus getIdentityStatus() { return identityStatus; }
    public void setIdentityStatus(UserIdentity.ValidationStatus identityStatus) { this.identityStatus = identityStatus; }

    public LocalDateTime getIdentityUploadDate() { return identityUploadDate; }
    public void setIdentityUploadDate(LocalDateTime identityUploadDate) { this.identityUploadDate = identityUploadDate; }

    public Long getVerificationId() { return verificationId; }
    public void setVerificationId(Long verificationId) { this.verificationId = verificationId; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public boolean isKycSubmitted() { return kycSubmitted; }
    public void setKycSubmitted(boolean kycSubmitted) { this.kycSubmitted = kycSubmitted; }

    public boolean isKycValidated() { return kycValidated; }
    public void setKycValidated(boolean kycValidated) { this.kycValidated = kycValidated; }

    public boolean isFaceRecognition() { return faceRecognition; }
    public void setFaceRecognition(boolean faceRecognition) { this.faceRecognition = faceRecognition; }

    public boolean isFullyVerified() { return fullyVerified; }
    public void setFullyVerified(boolean fullyVerified) { this.fullyVerified = fullyVerified; }
}