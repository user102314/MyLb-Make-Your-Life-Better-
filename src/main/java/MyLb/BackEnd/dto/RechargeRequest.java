package MyLb.BackEnd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO pour recharger le solde
 */
public class RechargeRequest {

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;

    // Constructeurs
    public RechargeRequest() {}

    public RechargeRequest(Double montant) {
        this.montant = montant;
    }

    // Getters et Setters
    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }
}