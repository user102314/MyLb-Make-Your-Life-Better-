package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;

/**
 * Entité représentant un propriétaire de société (Owner Property Owner)
 * Un client peut être propriétaire de plusieurs companies
 * Utilise une clé primaire composite (clientId, companyId)
 */
@Entity
@Table(name = "owner_po")
@IdClass(OwnerPOId.class)
public class OwnerPO {

    // ============================================================
    // Clés primaires (composite)
    // ============================================================

    @Id
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Id
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // ============================================================
    // Autres attributs
    // ============================================================

    @Column(name = "cin_number", length = 50)
    private String cinNumber;

    @Column(name = "role", length = 50)
    private String role;

    // ============================================================
    // Relations
    // ============================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    // ============================================================
    // Constructeurs
    // ============================================================

    /**
     * Constructeur par défaut requis par JPA
     */
    public OwnerPO() {
    }

    /**
     * Constructeur avec tous les arguments
     */
    public OwnerPO(Long clientId, Long companyId, String cinNumber, String role) {
        this.clientId = clientId;
        this.companyId = companyId;
        this.cinNumber = cinNumber;
        this.role = role;
    }

    /**
     * Constructeur simplifié
     */
    public OwnerPO(Long clientId, Long companyId) {
        this.clientId = clientId;
        this.companyId = companyId;
        this.role = "OWNER";
    }

    // ============================================================
    // Getters
    // ============================================================

    public Long getClientId() {
        return clientId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCinNumber() {
        return cinNumber;
    }

    public String getRole() {
        return role;
    }

    public Client getClient() {
        return client;
    }

    public Company getCompany() {
        return company;
    }

    // ============================================================
    // Setters
    // ============================================================

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public void setCinNumber(String cinNumber) {
        this.cinNumber = cinNumber;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // ============================================================
    // toString() pour le débogage
    // ============================================================

    @Override
    public String toString() {
        return "OwnerPO{" +
                "clientId=" + clientId +
                ", companyId=" + companyId +
                ", cinNumber='" + cinNumber + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    // ============================================================
    // equals() et hashCode() basés sur la clé composite
    // ============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OwnerPO ownerPO = (OwnerPO) o;

        if (clientId != null ? !clientId.equals(ownerPO.clientId) : ownerPO.clientId != null)
            return false;
        return companyId != null ? companyId.equals(ownerPO.companyId) : ownerPO.companyId == null;
    }

    @Override
    public int hashCode() {
        int result = clientId != null ? clientId.hashCode() : 0;
        result = 31 * result + (companyId != null ? companyId.hashCode() : 0);
        return result;
    }
}