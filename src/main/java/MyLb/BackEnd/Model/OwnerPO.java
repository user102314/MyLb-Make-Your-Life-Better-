package MyLb.BackEnd.Model;

import jakarta.persistence.*;
// Les imports de Lombok sont supprimés

@Entity
@Table(name = "owner_po")
public class OwnerPO {

    // Clé primaire ET Clé étrangère vers Client
    @Id
    @Column(name = "client_id")
    private Long clientId;

    private String cinNumber;

    // Lien One-to-One vers CompanyApplication (Relation inverse)
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private CompanyApplication companyApplication;

    // Lien One-to-One vers Client (Relation inverse : OwnerPO a un client)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indique que clientId est à la fois PK et FK
    @JoinColumn(name = "client_id")
    private Client client;

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument (Remplaçant @NoArgsConstructor)
    // ----------------------------------------------------------------------
    public OwnerPO() {
        // Constructeur par défaut requis par JPA
    }

    // ----------------------------------------------------------------------
    // 2. Constructeur Avec Tous les Arguments (Remplaçant @AllArgsConstructor)
    // ----------------------------------------------------------------------
    public OwnerPO(Long clientId, String cinNumber, CompanyApplication companyApplication, Client client) {
        this.clientId = clientId;
        this.cinNumber = cinNumber;
        this.companyApplication = companyApplication;
        this.client = client;
    }

    // ----------------------------------------------------------------------
    // 3. Getters et Setters (Remplaçant @Data)
    // ----------------------------------------------------------------------

    // Getters
    public Long getClientId() {
        return clientId;
    }

    public String getCinNumber() {
        return cinNumber;
    }

    public CompanyApplication getCompanyApplication() {
        return companyApplication;
    }

    public Client getClient() {
        return client;
    }

    // Setters
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setCinNumber(String cinNumber) {
        this.cinNumber = cinNumber;
    }

    public void setCompanyApplication(CompanyApplication companyApplication) {
        this.companyApplication = companyApplication;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}