// MyLb.BackEnd.dto.StockTradeResponse.java
package MyLb.BackEnd.dto;

public class StockTradeResponse {
    private boolean success;
    private String message;
    private Double montantTotal;
    private Double nouveauSolde;
    private StockWalletResponse stockWallet;

    // Constructeurs
    public StockTradeResponse() {}

    public StockTradeResponse(boolean success, String message, Double montantTotal, Double nouveauSolde, StockWalletResponse stockWallet) {
        this.success = success;
        this.message = message;
        this.montantTotal = montantTotal;
        this.nouveauSolde = nouveauSolde;
        this.stockWallet = stockWallet;
    }

    // Getters et Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public Double getNouveauSolde() { return nouveauSolde; }
    public void setNouveauSolde(Double nouveauSolde) { this.nouveauSolde = nouveauSolde; }

    public StockWalletResponse getStockWallet() { return stockWallet; }
    public void setStockWallet(StockWalletResponse stockWallet) { this.stockWallet = stockWallet; }
}