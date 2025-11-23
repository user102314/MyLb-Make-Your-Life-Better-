// MyLb.BackEnd.dto.UpdateStockWalletRequest.java
package MyLb.BackEnd.dto;

public class UpdateStockWalletRequest {
    private Integer quantite;
    private Double prix;

    // Constructeurs
    public UpdateStockWalletRequest() {}

    public UpdateStockWalletRequest(Integer quantite, Double prix) {
        this.quantite = quantite;
        this.prix = prix;
    }

    // Getters et Setters
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
}