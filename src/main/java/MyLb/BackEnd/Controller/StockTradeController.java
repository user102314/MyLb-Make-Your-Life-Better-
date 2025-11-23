// MyLb.BackEnd.Controller.StockTradeController.java
package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.StockTradeService;
import MyLb.BackEnd.dto.BuyStockRequest;
import MyLb.BackEnd.dto.SellStockRequest;
import MyLb.BackEnd.dto.StockTradeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stock-trade")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class StockTradeController {

    private final StockTradeService stockTradeService;

    @Autowired
    public StockTradeController(StockTradeService stockTradeService) {
        this.stockTradeService = stockTradeService;
    }

    /**
     * Acheter des stocks
     * POST /api/stock-trade/buy
     */
    @PostMapping("/buy")
    public ResponseEntity<StockTradeResponse> buyStock(@RequestBody BuyStockRequest request) {
        System.out.println("🛒 [StockTradeController] POST /api/stock-trade/buy");
        System.out.println("   └─ Client: " + request.getIdClient() +
                ", Stock: " + request.getIdStock() +
                ", Quantité: " + request.getQuantite());

        try {
            StockTradeResponse response = stockTradeService.buyStock(request);

            if (response.isSuccess()) {
                System.out.println("✅ [StockTradeController] Achat réussi - Montant: " +
                        response.getMontantTotal() + " DT");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ [StockTradeController] Échec achat: " + response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [StockTradeController] Erreur: " + e.getMessage());
            StockTradeResponse errorResponse = new StockTradeResponse(false, e.getMessage(), 0.0, 0.0, null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Vendre des stocks
     * POST /api/stock-trade/sell
     */
    @PostMapping("/sell")
    public ResponseEntity<StockTradeResponse> sellStock(@RequestBody SellStockRequest request) {
        System.out.println("💰 [StockTradeController] POST /api/stock-trade/sell");
        System.out.println("   └─ Client: " + request.getIdClient() +
                ", StockWallet: " + request.getIdStockWallet() +
                ", Quantité: " + request.getQuantite());

        try {
            StockTradeResponse response = stockTradeService.sellStock(request);

            if (response.isSuccess()) {
                System.out.println("✅ [StockTradeController] Vente réussie - Montant: " +
                        response.getMontantTotal() + " DT");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ [StockTradeController] Échec vente: " + response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ [StockTradeController] Erreur: " + e.getMessage());
            StockTradeResponse errorResponse = new StockTradeResponse(false, e.getMessage(), 0.0, 0.0, null);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Endpoint de santé
     * GET /api/stock-trade/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        System.out.println("📊 [StockTradeController] GET /api/stock-trade/health");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "controller", "StockTradeController",
                "message", "Stock Trade API est opérationnelle"
        ));
    }
}