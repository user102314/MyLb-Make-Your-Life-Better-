package MyLb.BackEnd.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long idNotification;
    private String sujet;
    private String description;
    private LocalDateTime dateCreation;
    private Integer etat;
    private Long idClient;
    private String statut; // "Lu" ou "Non lu"

    // Constructeurs
    public NotificationDTO() {}

    public NotificationDTO(Long idNotification, String sujet, String description,
                           LocalDateTime dateCreation, Integer etat, Long idClient) {
        this.idNotification = idNotification;
        this.sujet = sujet;
        this.description = description;
        this.dateCreation = dateCreation;
        this.etat = etat;
        this.idClient = idClient;
        this.statut = (etat == 1) ? "Lu" : "Non lu";
    }

    // Getters et Setters
    public Long getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(Long idNotification) {
        this.idNotification = idNotification;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Integer getEtat() {
        return etat;
    }

    public void setEtat(Integer etat) {
        this.etat = etat;
        this.statut = (etat == 1) ? "Lu" : "Non lu";
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}