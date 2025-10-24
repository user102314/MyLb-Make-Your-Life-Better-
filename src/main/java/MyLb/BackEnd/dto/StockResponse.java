package MyLb.BackEnd.dto;

public class StockResponse {

    private Long idStock;
    private String nomStock;
    private Integer stockDisponible;
    private Integer stockReste;
    private Double prixStock;
    // L'attribut 'ownerId' est supprimé ici.

    // ----------------------------------------------------------------------
    // Constructeur pour mapper l'entité Stock vers le DTO
    // ----------------------------------------------------------------------
    // Suppression du paramètre ownerId dans le constructeur
    public StockResponse(Long idStock, String nomStock, Integer stockDisponible, Integer stockReste, Double prixStock) {
        this.idStock = idStock;
        this.nomStock = nomStock;
        this.stockDisponible = stockDisponible;
        this.stockReste = stockReste;
        this.prixStock = prixStock;
    }

    // ----------------------------------------------------------------------
    // Getters et Setters (manuels)
    // ----------------------------------------------------------------------

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

    // Les accesseurs getOwnerId() et setOwnerId() sont supprimés.
}