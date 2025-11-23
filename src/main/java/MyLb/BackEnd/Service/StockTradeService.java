// MyLb.BackEnd.Service.StockTradeService.java
package MyLb.BackEnd.Service;

import MyLb.BackEnd.dto.StockTradeResponse;
import MyLb.BackEnd.dto.BuyStockRequest;
import MyLb.BackEnd.dto.SellStockRequest;

public interface StockTradeService {

    // Acheter des stocks
    StockTradeResponse buyStock(BuyStockRequest request);

    // Vendre des stocks
    StockTradeResponse sellStock(SellStockRequest request);
}