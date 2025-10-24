package MyLb.BackEnd.Model;

import jakarta.persistence.*;
// Les imports de Lombok sont supprimés

@Entity
@Table(name = "investor")
public class Investor {

    // Clé primaire ET Clé étrangère vers Client
    @Id
    @Column(name = "client_id")
    private Long clientId;

    private String cinNumber;
    private String gradeType;

    // Relation One-to-One vers CardDetail (l'inverse de CardDetail)
    @OneToOne(mappedBy = "investor", cascade = CascadeType.ALL)
    private CardDetail cardDetail;

    // Lien One-to-One vers Client (Relation inverse : Investor a un client)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indique que clientId est à la fois PK et FK
    @JoinColumn(name = "client_id")
    private Client client;

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument (Remplaçant @NoArgsConstructor)
    // ----------------------------------------------------------------------
    public Investor() {
        // Constructeur par défaut requis par JPA
    }

    // ----------------------------------------------------------------------
    // 2. Constructeur Avec Tous les Arguments (Remplaçant @AllArgsConstructor)
    // ----------------------------------------------------------------------
    public Investor(Long clientId, String cinNumber, String gradeType, CardDetail cardDetail, Client client) {
        this.clientId = clientId;
        this.cinNumber = cinNumber;
        this.gradeType = gradeType;
        this.cardDetail = cardDetail;
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

    public String getGradeType() {
        return gradeType;
    }

    public CardDetail getCardDetail() {
        return cardDetail;
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

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public void setCardDetail(CardDetail cardDetail) {
        this.cardDetail = cardDetail;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}