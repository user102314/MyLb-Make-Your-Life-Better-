package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "etat_finances")
public class EtatFinance {

    @Id
    private Long companyId;

    // Bilan
    private Double actifTotal;
    private Double actifImmobilise;
    private Double actifCirculant;
    private Double passifTotal;
    private Double capitauxPropres;
    private Double dettes;

    // Compte de Résultat
    private Double produitsTotal;
    private Double chargesTotal;
    private Double resultatNet;
    private Double chiffreAffaires;

    // Flux de Trésorerie
    private Double fluxOperationnels;
    private Double fluxInvestissement;
    private Double fluxFinancement;
    private Double variationNetteTresorerie;

    @Lob
    private byte[] rapportEtatFinancier;

    public EtatFinance() {}

    // Getters et Setters

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Double getActifTotal() {
        return actifTotal;
    }

    public void setActifTotal(Double actifTotal) {
        this.actifTotal = actifTotal;
    }

    public Double getActifImmobilise() {
        return actifImmobilise;
    }

    public void setActifImmobilise(Double actifImmobilise) {
        this.actifImmobilise = actifImmobilise;
    }

    public Double getActifCirculant() {
        return actifCirculant;
    }

    public void setActifCirculant(Double actifCirculant) {
        this.actifCirculant = actifCirculant;
    }

    public Double getPassifTotal() {
        return passifTotal;
    }

    public void setPassifTotal(Double passifTotal) {
        this.passifTotal = passifTotal;
    }

    public Double getCapitauxPropres() {
        return capitauxPropres;
    }

    public void setCapitauxPropres(Double capitauxPropres) {
        this.capitauxPropres = capitauxPropres;
    }

    public Double getDettes() {
        return dettes;
    }

    public void setDettes(Double dettes) {
        this.dettes = dettes;
    }

    public Double getProduitsTotal() {
        return produitsTotal;
    }

    public void setProduitsTotal(Double produitsTotal) {
        this.produitsTotal = produitsTotal;
    }

    public Double getChargesTotal() {
        return chargesTotal;
    }

    public void setChargesTotal(Double chargesTotal) {
        this.chargesTotal = chargesTotal;
    }

    public Double getResultatNet() {
        return resultatNet;
    }

    public void setResultatNet(Double resultatNet) {
        this.resultatNet = resultatNet;
    }

    public Double getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(Double chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public Double getFluxOperationnels() {
        return fluxOperationnels;
    }

    public void setFluxOperationnels(Double fluxOperationnels) {
        this.fluxOperationnels = fluxOperationnels;
    }

    public Double getFluxInvestissement() {
        return fluxInvestissement;
    }

    public void setFluxInvestissement(Double fluxInvestissement) {
        this.fluxInvestissement = fluxInvestissement;
    }

    public Double getFluxFinancement() {
        return fluxFinancement;
    }

    public void setFluxFinancement(Double fluxFinancement) {
        this.fluxFinancement = fluxFinancement;
    }

    public Double getVariationNetteTresorerie() {
        return variationNetteTresorerie;
    }

    public void setVariationNetteTresorerie(Double variationNetteTresorerie) {
        this.variationNetteTresorerie = variationNetteTresorerie;
    }

    public byte[] getRapportEtatFinancier() {
        return rapportEtatFinancier;
    }

    // LIGNE CORRIGÉE ICI :
    public void setRapportEtatFinancier(byte[] rapportEtatFinancier) {
        this.rapportEtatFinancier = rapportEtatFinancier; // Le nom du paramètre est utilisé pour affecter la variable de classe
    }
}