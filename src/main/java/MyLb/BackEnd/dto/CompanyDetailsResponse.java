// package MyLb.BackEnd.dto;
// Nom de fichier : CompanyDetailsResponse.java

package MyLb.BackEnd.dto;

import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Model.Entities.CompanyValidation;
import MyLb.BackEnd.Model.Entities.EtatFinance;

/**
 * DTO qui agrège TOUTES les informations d'une société, y compris les données binaires (byte[]).
 */
public class CompanyDetailsResponse {

    // --- 1. Champs de Company ---
    private Long companyId; // Type : Long
    private Long ownerID;   // Type : Long
    private String companyName; // Type : String
    private String dateInscri;  // Type : String
    private String status;      // Type : String

    // --- 2. Champs de CompanyValidation ---
    private String nomLegalComplet;     // Type : String
    private String numeroImmatriculation; // Type : String
    private String adresseSiegeSocial;  // Type : String
    private String nomPrenomPresidentLegal; // Type : String
    private String numeroTvaTaxe;       // Type : String

    // CHAMPS BINAIRES (byte[]) - AFFICHAGE EN TYPE RÉEL
    private byte[] certificatImmatriculation; // Type : byte[]
    private byte[] pieceIdentiteRepresentantLegal; // Type : byte[]
    private byte[] statutsSociete;             // Type : byte[]
    private byte[] justificatifDomiciliationCommerciale; // Type : byte[]

    // --- 3. Champs d'EtatFinance ---
    // Bilan
    private Double actifTotal; // Type : Double
    private Double actifImmobilise; // Type : Double
    private Double actifCirculant; // Type : Double
    private Double passifTotal; // Type : Double
    private Double capitauxPropres; // Type : Double
    private Double dettes; // Type : Double

    // Compte de Résultat
    private Double produitsTotal; // Type : Double
    private Double chargesTotal; // Type : Double
    private Double resultatNet; // Type : Double
    private Double chiffreAffaires; // Type : Double

    // Flux de Trésorerie
    private Double fluxOperationnels; // Type : Double
    private Double fluxInvestissement; // Type : Double
    private Double fluxFinancement; // Type : Double
    private Double variationNetteTresorerie; // Type : Double

    // CHAMP BINAIRE (byte[]) - AFFICHAGE EN TYPE RÉEL
    private byte[] rapportEtatFinancier; // Type : byte[]

    // ---------------------------------------------------
    // Constructeur Sans Argument
    // ---------------------------------------------------
    public CompanyDetailsResponse() {}

    // ---------------------------------------------------
    // Constructeur d'Assemblage à partir des Entités (MIS À JOUR)
    // ---------------------------------------------------
    public CompanyDetailsResponse(Company company, CompanyValidation validation, EtatFinance finance) {
        // Company
        this.companyId = company.getCompanyId();
        this.ownerID = company.getOwnerID();
        this.companyName = company.getCompanyName();
        this.dateInscri = company.getDateInscri();
        this.status = company.getStatus();

        // Validation
        if (validation != null) {
            this.nomLegalComplet = validation.getNomLegalComplet();
            this.numeroImmatriculation = validation.getNumeroImmatriculation();
            this.adresseSiegeSocial = validation.getAdresseSiegeSocial();
            this.nomPrenomPresidentLegal = validation.getNomPrenomPresidentLegal();
            this.numeroTvaTaxe = validation.getNumeroTvaTaxe();

            // Inclusion des champs byte[]
            this.certificatImmatriculation = validation.getCertificatImmatriculation();
            this.pieceIdentiteRepresentantLegal = validation.getPieceIdentiteRepresentantLegal();
            this.statutsSociete = validation.getStatutsSociete();
            this.justificatifDomiciliationCommerciale = validation.getJustificatifDomiciliationCommerciale();
        }

        // Finance
        if (finance != null) {
            this.actifTotal = finance.getActifTotal();
            this.actifImmobilise = finance.getActifImmobilise();
            this.actifCirculant = finance.getActifCirculant();
            this.passifTotal = finance.getPassifTotal();
            this.capitauxPropres = finance.getCapitauxPropres();
            this.dettes = finance.getDettes();
            this.produitsTotal = finance.getProduitsTotal();
            this.chargesTotal = finance.getChargesTotal();
            this.resultatNet = finance.getResultatNet();
            this.chiffreAffaires = finance.getChiffreAffaires();
            this.fluxOperationnels = finance.getFluxOperationnels();
            this.fluxInvestissement = finance.getFluxInvestissement();
            this.fluxFinancement = finance.getFluxFinancement();
            this.variationNetteTresorerie = finance.getVariationNetteTresorerie();

            // Inclusion du champ byte[]
            this.rapportEtatFinancier = finance.getRapportEtatFinancier();
        }
    }


    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public Long getOwnerID() { return ownerID; }
    public void setOwnerID(Long ownerID) { this.ownerID = ownerID; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getDateInscri() { return dateInscri; }
    public void setDateInscri(String dateInscri) { this.dateInscri = dateInscri; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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
    public byte[] getCertificatImmatriculation() { return certificatImmatriculation; }
    public void setCertificatImmatriculation(byte[] certificatImmatriculation) { this.certificatImmatriculation = certificatImmatriculation; }
    public byte[] getPieceIdentiteRepresentantLegal() { return pieceIdentiteRepresentantLegal; }
    public void setPieceIdentiteRepresentantLegal(byte[] pieceIdentiteRepresentantLegal) { this.pieceIdentiteRepresentantLegal = pieceIdentiteRepresentantLegal; }
    public byte[] getStatutsSociete() { return statutsSociete; }
    public void setStatutsSociete(byte[] statutsSociete) { this.statutsSociete = statutsSociete; }
    public byte[] getJustificatifDomiciliationCommerciale() { return justificatifDomiciliationCommerciale; }
    public void setJustificatifDomiciliationCommerciale(byte[] justificatifDomiciliationCommerciale) { this.justificatifDomiciliationCommerciale = justificatifDomiciliationCommerciale; }
    public Double getActifTotal() { return actifTotal; }
    public void setActifTotal(Double actifTotal) { this.actifTotal = actifTotal; }
    public Double getActifImmobilise() { return actifImmobilise; }
    public void setActifImmobilise(Double actifImmobilise) { this.actifImmobilise = actifImmobilise; }
    public Double getActifCirculant() { return actifCirculant; }
    public void setActifCirculant(Double actifCirculant) { this.actifCirculant = actifCirculant; }
    public Double getPassifTotal() { return passifTotal; }
    public void setPassifTotal(Double passifTotal) { this.passifTotal = passifTotal; }
    public Double getCapitauxPropres() { return capitauxPropres; }
    public void setCapitauxPropres(Double capitauxPropres) { this.capitauxPropres = capitauxPropres; }
    public Double getDettes() { return dettes; }
    public void setDettes(Double dettes) { this.dettes = dettes; }
    public Double getProduitsTotal() { return produitsTotal; }
    public void setProduitsTotal(Double produitsTotal) { this.produitsTotal = produitsTotal; }
    public Double getChargesTotal() { return chargesTotal; }
    public void setChargesTotal(Double chargesTotal) { this.chargesTotal = chargesTotal; }
    public Double getResultatNet() { return resultatNet; }
    public void setResultatNet(Double resultatNet) { this.resultatNet = resultatNet; }
    public Double getChiffreAffaires() { return chiffreAffaires; }
    public void setChiffreAffaires(Double chiffreAffaires) { this.chiffreAffaires = chiffreAffaires; }
    public Double getFluxOperationnels() { return fluxOperationnels; }
    public void setFluxOperationnels(Double fluxOperationnels) { this.fluxOperationnels = fluxOperationnels; }
    public Double getFluxInvestissement() { return fluxInvestissement; }
    public void setFluxInvestissement(Double fluxInvestissement) { this.fluxInvestissement = fluxInvestissement; }
    public Double getFluxFinancement() { return fluxFinancement; }
    public void setFluxFinancement(Double fluxFinancement) { this.fluxFinancement = fluxFinancement; }
    public Double getVariationNetteTresorerie() { return variationNetteTresorerie; }
    public void setVariationNetteTresorerie(Double variationNetteTresorerie) { this.variationNetteTresorerie = variationNetteTresorerie; }
    public byte[] getRapportEtatFinancier() { return rapportEtatFinancier; }
    public void setRapportEtatFinancier(byte[] rapportEtatFinancier) { this.rapportEtatFinancier = rapportEtatFinancier; }
}