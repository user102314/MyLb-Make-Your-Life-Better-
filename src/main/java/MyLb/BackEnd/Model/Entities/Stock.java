package MyLb.BackEnd.Model.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock")
    private Long idStock;
//[id_stock,nom_stock,stock_disponible,stock_reste,prix_stock,etat,id_componey,owner_client_id .... ]
    @Column(name = "nom_stock", nullable = false)
    private String nomStock;

    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;

    @Column(name = "stock_reste")
    private Integer stockReste;

    @Column(name = "prix_stock", nullable = false)
    private Double prixStock;

    @Column(name = "etat", length = 50)
    private String etat;

    @Column(name = "id_componey", nullable = false)
    private Long idComponey;

    // ============================================================
    // Relation ManyToOne vers OwnerPO avec clé composite
    // ============================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "owner_client_id", referencedColumnName = "client_id", nullable = false),
            @JoinColumn(name = "owner_company_id", referencedColumnName = "company_id", nullable = false)
    })
    private OwnerPO ownerPO;

    // ============================================================
    // Relation ManyToOne vers Company (optionnel)
    // ============================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_componey", insertable = false, updatable = false)
    private Company company;

    // ============================================================
    // Constructeurs
    // ============================================================

    public Stock() {
    }

    public Stock(String nomStock, Integer stockDisponible, Integer stockReste,
                 Double prixStock, String etat, Long idComponey) {
        this.nomStock = nomStock;
        this.stockDisponible = stockDisponible;
        this.stockReste = stockReste;
        this.prixStock = prixStock;
        this.etat = etat;
        this.idComponey = idComponey;
    }

    // ============================================================
    // Getters
    // ============================================================

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

    public String getEtat() {
        return etat;
    }

    public Long getIdComponey() {
        return idComponey;
    }

    public OwnerPO getOwnerPO() {
        return ownerPO;
    }

    public Company getCompany() {
        return company;
    }

    // ============================================================
    // Setters
    // ============================================================

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

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setIdComponey(Long idComponey) {
        this.idComponey = idComponey;
    }

    public void setOwnerPO(OwnerPO ownerPO) {
        this.ownerPO = ownerPO;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // ============================================================
    // toString()
    // ============================================================

    @Override
    public String toString() {
        return "Stock{" +
                "idStock=" + idStock +
                ", nomStock='" + nomStock + '\'' +
                ", stockDisponible=" + stockDisponible +
                ", stockReste=" + stockReste +
                ", prixStock=" + prixStock +
                ", etat='" + etat + '\'' +
                ", idComponey=" + idComponey +
                '}';
    }
}