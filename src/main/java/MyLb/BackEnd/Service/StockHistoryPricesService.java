package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.StockHistoryPrices;
import java.util.List;

public interface StockHistoryPricesService {

    /**
     * Get all history by stock ID
     */
    List<StockHistoryPrices> getStockHistory(Long idStock);

    /**
     * Get last price by stock ID
     */
    Double getLastPriceByIdStock(Long idStock);

    /**
     * Get max price during the last day
     */
    Double getMaxPriceLast24Hours(Long idStock);

    /**
     * Get min price during the last day
     */
    Double getMinPriceLast24Hours(Long idStock);
}