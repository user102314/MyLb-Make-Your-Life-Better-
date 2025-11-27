package MyLb.BackEnd.dto;

public class UserVerificationRequest {
    private Boolean isVerified;

    // Constructeurs
    public UserVerificationRequest() {}

    public UserVerificationRequest(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    // Getters et Setters
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
}