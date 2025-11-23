// src/main/java/com/mylb/backend/dto/PasswordChangeRequest.java

package MyLb.BackEnd.dto;
public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
    private String authCode; // 🔑 NOUVEAU CHAMP POUR LE CODE 2FA

    // Constructeur par défaut
    public PasswordChangeRequest() {
    }

    // --- Getters ---
    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getAuthCode() { // 🔑 Nouveau Getter
        return authCode;
    }

    // --- Setters ---
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setAuthCode(String authCode) { // 🔑 Nouveau Setter
        this.authCode = authCode;
    }
}