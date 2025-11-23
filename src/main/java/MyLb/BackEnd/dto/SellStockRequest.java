// MyLb.BackEnd.dto.SellStockRequest.java
package MyLb.BackEnd.dto;

public class SellStockRequest {
    private Long idClient;
    private Long idStockWallet;
    private Integer quantite;

    // Constructeurs
    public SellStockRequest() {}

    public SellStockRequest(Long idClient, Long idStockWallet, Integer quantite) {
        this.idClient = idClient;
        this.idStockWallet = idStockWallet;
        this.quantite = quantite;
    }

    // Getters et Setters
    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public Long getIdStockWallet() { return idStockWallet; }
    public void setIdStockWallet(Long idStockWallet) { this.idStockWallet = idStockWallet; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}