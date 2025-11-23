package MyLb.BackEnd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CardOperationRequest {

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;

    public CardOperationRequest() {}

    public CardOperationRequest(Double montant) {
        this.montant = montant;
    }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
}