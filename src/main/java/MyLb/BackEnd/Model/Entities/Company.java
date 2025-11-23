package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Représente l'entité principale d'une Société dans la base de données.
 */
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    private Long ownerID; // ID de l'utilisateur qui a créé la société
    private String companyName;
    private String dateInscri; // Stocke la date d'inscription (ex: format ISO string YYYY-MM-DD)
    private String status; // Statut de vérification (PENDING, ACTIVE, REJECTED)

    // Constructeur par défaut (Obligatoire pour JPA)
    public Company() {}

    // Constructeur avec tous les champs (sauf l'ID auto-généré, souvent pratique)
    public Company(Long ownerID, String companyName, String dateInscri, String status) {
        this.ownerID = ownerID;
        this.companyName = companyName;
        this.dateInscri = dateInscri;
        this.status = status;
    }

    // Getters et Setters

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDateInscri() {
        return dateInscri;
    }

    public void setDateInscri(String dateInscri) {
        this.dateInscri = dateInscri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}