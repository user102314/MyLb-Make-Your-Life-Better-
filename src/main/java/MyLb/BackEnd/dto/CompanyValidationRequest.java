package MyLb.BackEnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CompanyValidationRequest {

    @NotNull(message = "L'ID de la société est obligatoire.")
    private Long companyId;

    @NotBlank(message = "Le nom légal complet est obligatoire.")
    private String nomLegalComplet;

    @NotBlank(message = "Le numéro d'immatriculation est obligatoire.")
    private String numeroImmatriculation;

    @NotBlank(message = "L'adresse du siège social est obligatoire.")
    private String adresseSiegeSocial;

    @NotBlank(message = "Le nom et prénom du président sont obligatoires.")
    private String nomPrenomPresidentLegal;

    private String numeroTvaTaxe;

    // Les fichiers (byte[]) ne sont pas inclus ici car ils sont gérés via MultipartFile dans le contrôleur.
    // L'état (approvalStatus) sera défini par défaut dans le contrôleur car il n'est pas dans le modèle de requête du Front-end.

    // Getters et Setters...

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getNomLegalComplet() { return nomLegalComplet; }
    public void setNomLegalComplet(String nomLegalComplet) { this.nomLegalComplet = nomLegalComplet; }

    public String getNumeroImmatriculation() { return numeroImmatriculation; }
    public void setNumeroImmatriculation(String numeroImmatriculation) { this.numeroImmatriculation = numeroImmatriculation; }

    public String getAdresseSiegeSocial() { return adresseSiegeSocial; }
    public void setAdresseSiegeSocial(String adresseSiegeSocial) { this.adresseSiegeSocial = adresseSiegeSocial; }

    public String getNomPrenomPresidentLegal() { return nomPrenomPresidentLegal; }
    public void setNomPrenomPresidentLegal(String nomPrenomPresidentLegal) { this.nomPrenomPresidentLegal = nomPrenomPresidentLegal; }

    public String getNumeroTvaTaxe() { return numeroTvaTaxe; }
    public void setNumeroTvaTaxe(String numeroTvaTaxe) { this.numeroTvaTaxe = numeroTvaTaxe; }
}