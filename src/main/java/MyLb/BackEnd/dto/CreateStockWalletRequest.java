// MyLb.BackEnd.dto.CreateStockWalletRequest.java
package MyLb.BackEnd.dto;

public class CreateStockWalletRequest {
    private Long idClient;
    private Long idStock;
    private String nomStock;
    private Double prix;
    private Integer quantite;

    // Constructeurs
    public CreateStockWalletRequest() {}

    public CreateStockWalletRequest(Long idClient, Long idStock, String nomStock, Double prix, Integer quantite) {
        this.idClient = idClient;
        this.idStock = idStock;
        this.nomStock = nomStock;
        this.prix = prix;
        this.quantite = quantite;
    }

    // Getters et Setters
    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public Long getIdStock() { return idStock; }
    public void setIdStock(Long idStock) { this.idStock = idStock; }

    public String getNomStock() { return nomStock; }
    public void setNomStock(String nomStock) { this.nomStock = nomStock; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}