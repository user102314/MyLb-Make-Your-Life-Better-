package MyLb.BackEnd.dto;

import java.time.LocalDateTime;

public class TransactionResponse {
    private Long id;
    private Long idClient;
    private String typeOperation;
    private Double montant;
    private String description;
    private String statut;
    private Long idDestinataire;
    private String emailDestinataire;
    private Long idCarte;
    private LocalDateTime dateCreation;
    private Double soldeApresOperation;

    // Constructeurs
    public TransactionResponse() {}

    public TransactionResponse(Long id, Long idClient, String typeOperation, Double montant,
                               String description, String statut, LocalDateTime dateCreation,
                               Double soldeApresOperation) {
        this.id = id;
        this.idClient = idClient;
        this.typeOperation = typeOperation;
        this.montant = montant;
        this.description = description;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.soldeApresOperation = soldeApresOperation;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public String getTypeOperation() { return typeOperation; }
    public void setTypeOperation(String typeOperation) { this.typeOperation = typeOperation; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Long getIdDestinataire() { return idDestinataire; }
    public void setIdDestinataire(Long idDestinataire) { this.idDestinataire = idDestinataire; }

    public String getEmailDestinataire() { return emailDestinataire; }
    public void setEmailDestinataire(String emailDestinataire) { this.emailDestinataire = emailDestinataire; }

    public Long getIdCarte() { return idCarte; }
    public void setIdCarte(Long idCarte) { this.idCarte = idCarte; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Double getSoldeApresOperation() { return soldeApresOperation; }
    public void setSoldeApresOperation(Double soldeApresOperation) { this.soldeApresOperation = soldeApresOperation; }
}