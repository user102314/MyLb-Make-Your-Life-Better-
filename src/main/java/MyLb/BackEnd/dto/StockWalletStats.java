// MyLb.BackEnd.dto.StockWalletStats.java
package MyLb.BackEnd.dto;

public class StockWalletStats {
    private Integer totalStocks;
    private Long distinctStocks;
    private Double totalInvestment;
    private Double totalQuantity;

    // Constructeur
    public StockWalletStats(Integer totalStocks, Long distinctStocks, Double totalInvestment, Double totalQuantity) {
        this.totalStocks = totalStocks;
        this.distinctStocks = distinctStocks;
        this.totalInvestment = totalInvestment;
        this.totalQuantity = totalQuantity;
    }

    // Constructeur par défaut
    public StockWalletStats() {}

    // Getters et Setters
    public Integer getTotalStocks() { return totalStocks; }
    public void setTotalStocks(Integer totalStocks) { this.totalStocks = totalStocks; }

    public Long getDistinctStocks() { return distinctStocks; }
    public void setDistinctStocks(Long distinctStocks) { this.distinctStocks = distinctStocks; }

    public Double getTotalInvestment() { return totalInvestment; }
    public void setTotalInvestment(Double totalInvestment) { this.totalInvestment = totalInvestment; }

    public Double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Double totalQuantity) { this.totalQuantity = totalQuantity; }
}