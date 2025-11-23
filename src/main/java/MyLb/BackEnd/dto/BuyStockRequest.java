// MyLb.BackEnd.dto.BuyStockRequest.java
package MyLb.BackEnd.dto;

public class BuyStockRequest {
    private Long idClient;
    private Long idStock;
    private Integer quantite;

    // Constructeurs
    public BuyStockRequest() {}

    public BuyStockRequest(Long idClient, Long idStock, Integer quantite) {
        this.idClient = idClient;
        this.idStock = idStock;
        this.quantite = quantite;
    }

    // Getters et Setters
    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public Long getIdStock() { return idStock; }
    public void setIdStock(Long idStock) { this.idStock = idStock; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}