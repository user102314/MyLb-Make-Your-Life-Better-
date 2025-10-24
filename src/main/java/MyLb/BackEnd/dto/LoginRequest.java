package MyLb.BackEnd.dto;

// Les imports de Lombok sont supprimés
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

public class LoginRequest {

    private String email;
    private String password;

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument (Remplaçant @NoArgsConstructor)
    // ----------------------------------------------------------------------
    public LoginRequest() {
        // Constructeur par défaut requis
    }

    // ----------------------------------------------------------------------
    // 2. Constructeur Avec Tous les Arguments (Remplaçant @AllArgsConstructor)
    // ----------------------------------------------------------------------
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ----------------------------------------------------------------------
    // 3. Getters et Setters (Remplaçant @Data)
    // ----------------------------------------------------------------------

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}