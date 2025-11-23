// MyLb.BackEnd.Model.Entities.StockWallet.java
package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_wallet")
public class StockWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_client", nullable = false)
    private Long idClient;

    @Column(name = "id_stock", nullable = false)
    private Long idStock;

    @Column(name = "nom_stock", nullable = false)
    private String nomStock;

    @Column(name = "prix_achat", nullable = false)
    private Double prix;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "prix_total", nullable = false)
    private Double prixTotal;

    @Column(name = "date_achat", nullable = false)
    private LocalDateTime dateAchat;

    // Constructeurs
    public StockWallet() {}

    public StockWallet(Long idClient, Long idStock, String nomStock, Double prix, Integer quantite, Double prixTotal) {
        this.idClient = idClient;
        this.idStock = idStock;
        this.nomStock = nomStock;
        this.prix = prix;
        this.quantite = quantite;
        this.prixTotal = prixTotal;
        this.dateAchat = LocalDateTime.now();
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