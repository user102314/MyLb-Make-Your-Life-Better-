// Créez cette classe dans le package dto
package MyLb.BackEnd.dto;

public class UserIdentityStatusRequest {
    private String status; // "0" ou "1" ou "PENDING", "APPROVED", "REJECTED"

    public UserIdentityStatusRequest() {}

    public UserIdentityStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}