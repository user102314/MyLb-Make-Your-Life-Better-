package MyLb.BackEnd.dto;

public class StockResponse {
    private Long idStock;
    private String nomStock;
    private Integer stockDisponible;
    private Integer stockReste;
    private Double prixStock;
    private String etat;
    private Long idComponey;
    private Long ownerId;

    // Constructeur complet
    public StockResponse(Long idStock, String nomStock, Integer stockDisponible,
                         Integer stockReste, Double prixStock, String etat,
                         Long idComponey, Long ownerId) {
        this.idStock = idStock;
        this.nomStock = nomStock;
        this.stockDisponible = stockDisponible;
        this.stockReste = stockReste;
        this.prixStock = prixStock;
        this.etat = etat;
        this.idComponey = idComponey;
        this.ownerId = ownerId;
    }

    // Getters et Setters
    public Long getIdStock() {
        return idStock;
    }

    public void setIdStock(Long idStock) {
        this.idStock = idStock;
    }

    public String getNomStock() {
        return nomStock;
    }

    public void setNomStock(String nomStock) {
        this.nomStock = nomStock;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Integer getStockReste() {
        return stockReste;
    }

    public void setStockReste(Integer stockReste) {
        this.stockReste = stockReste;
    }

    public Double getPrixStock() {
        return prixStock;
    }

    public void setPrixStock(Double prixStock) {
        this.prixStock = prixStock;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Long getIdComponey() {
        return idComponey;
    }

    public void setIdComponey(Long idComponey) {
        this.idComponey = idComponey;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}