package MyLb.BackEnd.dto;

// L'import de Lombok est supprimé
// import lombok.Data;

public class ClientUpdateRequest {
    // Les champs que le client peut modifier
    private String firstName;
    private String lastName;
    private String email;
    private String password; // Optionnel : si on veut changer le mot de passe
    // Ajoutez d'autres champs si nécessaire (ex: phoneNumber, address)

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument
    // ----------------------------------------------------------------------
    public ClientUpdateRequest() {
        // Constructeur par défaut
    }

    // Vous pouvez ajouter un constructeur avec arguments si vous en avez besoin.

    // ----------------------------------------------------------------------
    // 2. Getters et Setters
    // ----------------------------------------------------------------------

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}