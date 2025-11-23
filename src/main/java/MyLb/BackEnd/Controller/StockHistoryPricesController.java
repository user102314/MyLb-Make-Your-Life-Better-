package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.StockHistoryPrices;
import MyLb.BackEnd.Service.StockHistoryPricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-history")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class StockHistoryPricesController {

    private final StockHistoryPricesService stockHistoryPricesService;

    @Autowired
    public StockHistoryPricesController(StockHistoryPricesService stockHistoryPricesService) {
        this.stockHistoryPricesService = stockHistoryPricesService;
    }

    /**
     * GET /api/stock-history/{idStock} - Get all history by stock ID
     */
    @GetMapping("/{idStock}")
    public ResponseEntity<List<StockHistoryPrices>> getStockHistory(@PathVariable Long idStock) {
        try {
            List<StockHistoryPrices> history = stockHistoryPricesService.getStockHistory(idStock);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/stock-history/{idStock}/last-price - Get last price by stock ID
     */
    @GetMapping("/{idStock}/last-price")
    public ResponseEntity<Map<String, Double>> getLastPrice(@PathVariable Long idStock) {
        try {
            Double lastPrice = stockHistoryPricesService.getLastPriceByIdStock(idStock);
            Map<String, Double> response = new HashMap<>();
            response.put("lastPrice", lastPrice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/stock-history/{idStock}/stats-24h - Get max and min prices for last 24h
     */
    @GetMapping("/{idStock}/stats-24h")
    public ResponseEntity<Map<String, Double>> get24hStats(@PathVariable Long idStock) {
        try {
            Double maxPrice = stockHistoryPricesService.getMaxPriceLast24Hours(idStock);
            Double minPrice = stockHistoryPricesService.getMinPriceLast24Hours(idStock);

            Map<String, Double> response = new HashMap<>();
            response.put("maxPrice24h", maxPrice);
            response.put("minPrice24h", minPrice);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}