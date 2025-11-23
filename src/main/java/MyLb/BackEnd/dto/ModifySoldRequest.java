package MyLb.BackEnd.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO pour modifier le solde
 */
public class ModifySoldRequest {

    @NotNull(message = "Le nouveau solde est obligatoire")
    private Double nouveauSold;

    // Constructeurs
    public ModifySoldRequest() {}

    public ModifySoldRequest(Double nouveauSold) {
        this.nouveauSold = nouveauSold;
    }

    // Getters et Setters
    public Double getNouveauSold() {
        return nouveauSold;
    }

    public void setNouveauSold(Double nouveauSold) {
        this.nouveauSold = nouveauSold;
    }
}