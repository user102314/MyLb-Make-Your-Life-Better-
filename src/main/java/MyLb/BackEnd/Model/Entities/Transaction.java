package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @Column(name = "type_operation", nullable = false)
    private String typeOperation; // DEPOSIT, WITHDRAW, TRANSFER, CARD_TO_WALLET, WALLET_TO_CARD

    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "description")
    private String description;

    @Column(name = "statut", nullable = false)
    private String statut = "COMPLETED"; // COMPLETED, PENDING, FAILED

    @Column(name = "id_destinataire")
    private Long idDestinataire; // ID du destinataire pour les transferts

    @Column(name = "email_destinataire")
    private String emailDestinataire; // Email du destinataire

    @Column(name = "id_carte")
    private Long idCarte; // ID de la carte concernée

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "solde_apres_operation")
    private Double soldeApresOperation;

    // Constructeurs
    public Transaction() {
        this.dateCreation = LocalDateTime.now();
        this.statut = "COMPLETED";
    }

    public Transaction(Long idClient, String typeOperation, Double montant, String description) {
        this();
        this.idClient = idClient;
        this.typeOperation = typeOperation;
        this.montant = montant;
        this.description = description;
    }

    public Transaction(Long idClient, String typeOperation, Double montant, String description, Long idDestinataire, String emailDestinataire) {
        this(idClient, typeOperation, montant, description);
        this.idDestinataire = idDestinataire;
        this.emailDestinataire = emailDestinataire;
    }

    public Transaction(Long idClient, String typeOperation, Double montant, String description, Long idCarte) {
        this(idClient, typeOperation, montant, description);
        this.idCarte = idCarte;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Long getIdDestinataire() {
        return idDestinataire;
    }

    public void setIdDestinataire(Long idDestinataire) {
        this.idDestinataire = idDestinataire;
    }

    public String getEmailDestinataire() {
        return emailDestinataire;
    }

    public void setEmailDestinataire(String emailDestinataire) {
        this.emailDestinataire = emailDestinataire;
    }

    public Long getIdCarte() {
        return idCarte;
    }

    public void setIdCarte(Long idCarte) {
        this.idCarte = idCarte;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Double getSoldeApresOperation() {
        return soldeApresOperation;
    }

    public void setSoldeApresOperation(Double soldeApresOperation) {
        this.soldeApresOperation = soldeApresOperation;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", idClient=" + idClient +
                ", typeOperation='" + typeOperation + '\'' +
                ", montant=" + montant +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}