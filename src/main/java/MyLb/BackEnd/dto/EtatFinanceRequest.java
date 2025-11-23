package MyLb.BackEnd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
// Assurez-vous que cette société a une relation avec l'EtatFinance

public class EtatFinanceRequest {

    @NotNull(message = "L'ID de la société est obligatoire.")
    private Long companyId;

    // Bilan
    @NotNull @PositiveOrZero(message = "L'actif total doit être positif ou zéro.")
    private Double actifTotal;
    @NotNull @PositiveOrZero
    private Double actifImmobilise;
    @NotNull @PositiveOrZero
    private Double actifCirculant;
    @NotNull @PositiveOrZero
    private Double passifTotal;
    @NotNull @PositiveOrZero
    private Double capitauxPropres;
    @NotNull @PositiveOrZero
    private Double dettes;

    // Compte de Résultat
    @NotNull @PositiveOrZero
    private Double produitsTotal;
    @NotNull @PositiveOrZero
    private Double chargesTotal;
    @NotNull
    private Double resultatNet; // Peut être négatif
    @NotNull @PositiveOrZero
    private Double chiffreAffaires;

    // Flux de Trésorerie
    @NotNull
    private Double fluxOperationnels;
    @NotNull
    private Double fluxInvestissement;
    @NotNull
    private Double fluxFinancement;
    @NotNull
    private Double variationNetteTresorerie;

    // Getters et Setters (Omis ici pour la concision, mais nécessaires)

    // ... (Ajoutez tous les Getters et Setters) ...

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
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
}