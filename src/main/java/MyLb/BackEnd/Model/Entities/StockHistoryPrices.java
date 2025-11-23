package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité pour l'historique des prix des stocks par minute
 */
@Entity
@Table(name = "stock_history_prices")
public class StockHistoryPrices {
//[id.id_stock.prix.date_creation]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_stock", nullable = false)
    private Long idStock;

    @Column(name = "prix", nullable = false)
    private Double prix;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    // Constructeurs
    public StockHistoryPrices() {
        this.dateCreation = LocalDateTime.now();
    }

    public StockHistoryPrices(Long idStock, Double prix) {
        this();
        this.idStock = idStock;
        this.prix = prix;
    }

    public StockHistoryPrices(Long idStock, Double prix, LocalDateTime dateCreation) {
        this.idStock = idStock;
        this.prix = prix;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdStock() {
        return idStock;
    }

    public void setIdStock(Long idStock) {
        this.idStock = idStock;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "StockHistoryPrices{" +
                "id=" + id +
                ", idStock=" + idStock +
                ", prix=" + prix +
                ", dateCreation=" + dateCreation +
                '}';
    }
}