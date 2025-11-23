// MyLb.BackEnd.dto.StockWalletResponse.java
package MyLb.BackEnd.dto;

import java.time.LocalDateTime;

public class StockWalletResponse {
    private Long id;
    private Long idClient;
    private Long idStock;
    private String nomStock;
    private Double prix;
    private Integer quantite;
    private Double prixTotal;
    private LocalDateTime dateAchat;

    // Constructeurs
    public StockWalletResponse() {}

    public StockWalletResponse(Long id, Long idClient, Long idStock, String nomStock,
                               Double prix, Integer quantite, Double prixTotal, LocalDateTime dateAchat) {
        this.id = id;
        this.idClient = idClient;
        this.idStock = idStock;
        this.nomStock = nomStock;
        this.prix = prix;
        this.quantite = quantite;
        this.prixTotal = prixTotal;
        this.dateAchat = dateAchat;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(Double prixTotal) { this.prixTotal = prixTotal; }

    public LocalDateTime getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDateTime dateAchat) { this.dateAchat = dateAchat; }
}