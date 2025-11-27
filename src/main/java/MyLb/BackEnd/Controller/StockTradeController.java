// MyLb.BackEnd.Controller.StockTradeController.java
package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.StockTradeService;
import MyLb.BackEnd.dto.BuyStockRequest;
import MyLb.BackEnd.dto.SellStockRequest;
import MyLb.BackEnd.dto.StockTradeResponse;
import jakarta.servlet.http.HttpSession;
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
     * Récupérer l'ID client depuis la session
     */
    private Long getClientIdFromSession(HttpSession session) {
        Long clientId = (Long) session.getAttribute("USER_ID");
        if (clientId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return clientId;
    }

    /**
     * Acheter des stocks pour le client connecté
     * POST /api/stock-trade/buy
     */
    @PostMapping("/buy")
    public ResponseEntity<StockTradeResponse> buyStock(
            HttpSession session,
            @RequestBody BuyStockRequest request) {

        Long clientId = getClientIdFromSession(session);

        System.out.println("🛒 [StockTradeController] POST /api/stock-trade/buy");
        System.out.println("   └─ Client (session): " + clientId +
                ", Stock: " + request.getIdStock() +
                ", Quantité: " + request.getQuantite());

        try {
            // Utiliser l'ID client de la session
            request.setIdClient(clientId);

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
     * Vendre des stocks pour le client connecté
     * POST /api/stock-trade/sell
     */
    @PostMapping("/sell")
    public ResponseEntity<StockTradeResponse> sellStock(
            HttpSession session,
            @RequestBody SellStockRequest request) {

        Long clientId = getClientIdFromSession(session);

        System.out.println("💰 [StockTradeController] POST /api/stock-trade/sell");
        System.out.println("   └─ Client (session): " + clientId +
                ", StockWallet: " + request.getIdStockWallet() +
                ", Quantité: " + request.getQuantite());

        try {
            // Utiliser l'ID client de la session
            request.setIdClient(clientId);

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