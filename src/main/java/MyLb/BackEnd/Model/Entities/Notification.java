// MyLb.BackEnd.Model.Entities.Notification.java
package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotification;

    @Column(nullable = false)
    private String sujet;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private Integer etat; // 1 = lu, 0 = non lu

    @Column(nullable = false)
    private Long idClient;

    // Constructeurs
    public Notification() {
        this.dateCreation = LocalDateTime.now();
        this.etat = 0; // Par défaut non lu
    }

    public Notification(String sujet, String description, Long idClient) {
        this();
        this.sujet = sujet;
        this.description = description;
        this.idClient = idClient;
    }

    public Notification(String sujet, String description, Long idClient, Integer etat) {
        this(sujet, description, idClient);
        this.etat = etat;
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
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    // Méthodes utilitaires
    public boolean isLu() {
        return etat == 1;
    }

    public void marquerCommeLu() {
        this.etat = 1;
    }

    public void marquerCommeNonLu() {
        this.etat = 0;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "idNotification=" + idNotification +
                ", sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", dateCreation=" + dateCreation +
                ", etat=" + etat +
                ", idClient=" + idClient +
                '}';
    }
}