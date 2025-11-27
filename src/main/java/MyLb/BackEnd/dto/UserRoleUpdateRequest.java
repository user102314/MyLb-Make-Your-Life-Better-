package MyLb.BackEnd.dto;

public class UserRoleUpdateRequest {
    private String role;

    // Constructeurs
    public UserRoleUpdateRequest() {}

    public UserRoleUpdateRequest(String role) {
        this.role = role;
    }

    // Getters et Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}