package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_validations")
public class CompanyValidation {

    @Id
    private Long companyId; // Clé primaire et clé étrangère vers Company

    // CHAMPS D'INFORMATIONS LÉGALES DÉTAILLÉES (EXISTANTS)
    private String nomLegalComplet;
    private String numeroImmatriculation;
    private String adresseSiegeSocial;
    private String nomPrenomPresidentLegal;
    private String numeroTvaTaxe;

    // CHAMPS DE FICHIERS (EXISTANTS)
    @Lob
    private byte[] certificatImmatriculation; // Kbis / Certificat d'enregistrement
    @Lob
    private byte[] pieceIdentiteRepresentantLegal; // Pièce d'identité du président
    @Lob
    private byte[] statutsSociete; // Statuts
    @Lob
    private byte[] justificatifDomiciliationCommerciale; // Justificatif d'adresse

    // NOUVEAUX CHAMPS D'ÉTAT ET DE CHEMIN (CORRESPONDANT AU DTO)

    public CompanyValidation() {}

    // ---------------------- GETTERS ET SETTERS ----------------------

    // Getters et Setters pour les NOUVEAUX CHAMPS (Correction des erreurs)



    // Getters et Setters pour les CHAMPS EXISTANTS (inclus pour la complétude)

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getNomLegalComplet() {
        return nomLegalComplet;
    }

    public void setNomLegalComplet(String nomLegalComplet) {
        this.nomLegalComplet = nomLegalComplet;
    }

    public String getNumeroImmatriculation() {
        return numeroImmatriculation;
    }

    public void setNumeroImmatriculation(String numeroImmatriculation) {
        this.numeroImmatriculation = numeroImmatriculation;
    }

    public String getAdresseSiegeSocial() {
        return adresseSiegeSocial;
    }

    public void setAdresseSiegeSocial(String adresseSiegeSocial) {
        this.adresseSiegeSocial = adresseSiegeSocial;
    }

    public String getNomPrenomPresidentLegal() {
        return nomPrenomPresidentLegal;
    }

    public void setNomPrenomPresidentLegal(String nomPrenomPresidentLegal) {
        this.nomPrenomPresidentLegal = nomPrenomPresidentLegal;
    }

    public String getNumeroTvaTaxe() {
        return numeroTvaTaxe;
    }

    public void setNumeroTvaTaxe(String numeroTvaTaxe) {
        this.numeroTvaTaxe = numeroTvaTaxe;
    }

    public byte[] getCertificatImmatriculation() {
        return certificatImmatriculation;
    }

    public void setCertificatImmatriculation(byte[] certificatImmatriculation) {
        this.certificatImmatriculation = certificatImmatriculation;
    }

    public byte[] getPieceIdentiteRepresentantLegal() {
        return pieceIdentiteRepresentantLegal;
    }

    public void setPieceIdentiteRepresentantLegal(byte[] pieceIdentiteRepresentantLegal) {
        this.pieceIdentiteRepresentantLegal = pieceIdentiteRepresentantLegal;
    }

    public byte[] getStatutsSociete() {
        return statutsSociete;
    }

    public void setStatutsSociete(byte[] statutsSociete) {
        this.statutsSociete = statutsSociete;
    }

    public byte[] getJustificatifDomiciliationCommerciale() {
        return justificatifDomiciliationCommerciale;
    }

    public void setJustificatifDomiciliationCommerciale(byte[] justificatifDomiciliationCommerciale) {
        this.justificatifDomiciliationCommerciale = justificatifDomiciliationCommerciale;
    }
}