package MyLb.BackEnd.dto;

import java.time.LocalDate; // NOUVEL IMPORT

public class SignUpRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate; // Date de naissance
    private String email;
    private String password;
    private String profileImageUrl; // URL ou chemin de l'image de profil
    // Le rôle et isVerified seront définis dans le contrôleur

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument
    // ----------------------------------------------------------------------
    public SignUpRequest() {
    }

    // ----------------------------------------------------------------------
    // 2. Constructeur Avec Tous les Arguments
    // ----------------------------------------------------------------------
    public SignUpRequest(String firstName, String lastName, LocalDate birthDate, String email, String password, String profileImageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
    }

    // ----------------------------------------------------------------------
    // 3. Getters et Setters
    // ----------------------------------------------------------------------
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}