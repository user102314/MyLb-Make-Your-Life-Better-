package MyLb.BackEnd.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long idStock; // id stock

    @Column(name = "nom_stock", nullable = false)
    private String nomStock; // nom stock

    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible; // stock disponible

    @Column(name = "stock_restant", nullable = false)
    private Integer stockReste; // stock reste

    @Column(name = "prix_stock", nullable = false)
    private Double prixStock; // prix stock

    // Clé étrangère vers OwnerPO (Relation Many-to-One)
    // Plusieurs stocks peuvent appartenir à un seul OwnerPO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false) // Le nom de la colonne FK dans la table 'stock'
    private OwnerPO ownerPO; // idpo (représenté par l'entité OwnerPO)

    // ----------------------------------------------------------------------
    // 1. Constructeur Sans Argument
    // ----------------------------------------------------------------------
    public Stock() {
        // Constructeur par défaut requis par JPA
    }

    // ----------------------------------------------------------------------
    // 2. Constructeur Avec Tous les Arguments
    // ----------------------------------------------------------------------
    public Stock(Long idStock, String nomStock, Integer stockDisponible, Integer stockReste, Double prixStock, OwnerPO ownerPO) {
        this.idStock = idStock;
        this.nomStock = nomStock;
        this.stockDisponible = stockDisponible;
        this.stockReste = stockReste;
        this.prixStock = prixStock;
        this.ownerPO = ownerPO;
    }

    // ----------------------------------------------------------------------
    // 3. Getters et Setters
    // ----------------------------------------------------------------------

    // Getters
    public Long getIdStock() {
        return idStock;
    }

    public String getNomStock() {
        return nomStock;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public Integer getStockReste() {
        return stockReste;
    }

    public Double getPrixStock() {
        return prixStock;
    }

    public OwnerPO getOwnerPO() {
        return ownerPO;
    }

    // Setters
    public void setIdStock(Long idStock) {
        this.idStock = idStock;
    }

    public void setNomStock(String nomStock) {
        this.nomStock = nomStock;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public void setStockReste(Integer stockReste) {
        this.stockReste = stockReste;
    }

    public void setPrixStock(Double prixStock) {
        this.prixStock = prixStock;
    }

    public void setOwnerPO(OwnerPO ownerPO) {
        this.ownerPO = ownerPO;
    }
}