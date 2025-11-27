package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.StockWalletService;
import MyLb.BackEnd.dto.StockWalletResponse;
import MyLb.BackEnd.dto.CreateStockWalletRequest;
import MyLb.BackEnd.dto.UpdateStockWalletRequest;
import MyLb.BackEnd.dto.StockWalletStats;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-wallet")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class StockWalletController {

    private final StockWalletService stockWalletService;

    @Autowired
    public StockWalletController(StockWalletService stockWalletService) {
        this.stockWalletService = stockWalletService;
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
     * Ajouter un stock au wallet du client connecté
     * POST /api/stock-wallet
     */
    @PostMapping
    public ResponseEntity<StockWalletResponse> addStockToWallet(
            HttpSession session,
            @RequestBody CreateStockWalletRequest request) {

        Long clientId = getClientIdFromSession(session);
        // S'assurer que l'ID client dans la requête correspond à la session
        request.setIdClient(clientId);

        System.out.println("📥 [StockWalletController] POST /api/stock-wallet");
        System.out.println("   └─ Client: " + clientId + ", Stock: " + request.getNomStock());

        try {
            StockWalletResponse response = stockWalletService.addStockToWallet(request);
            System.out.println("✅ [StockWalletController] Stock ajouté au wallet - ID: " + response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupérer tous les stocks du client connecté
     * GET /api/stock-wallet
     */
    @GetMapping
    public ResponseEntity<List<StockWalletResponse>> getClientStocks(HttpSession session) {
        Long clientId = getClientIdFromSession(session);
        System.out.println("📥 [StockWalletController] GET /api/stock-wallet");
        System.out.println("   └─ Client ID: " + clientId);

        try {
            List<StockWalletResponse> stocks = stockWalletService.getClientStocks(clientId);
            System.out.println("✅ [StockWalletController] " + stocks.size() + " stock(s) trouvé(s)");
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer un stock spécifique du client connecté
     * GET /api/stock-wallet/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockWalletResponse> getStockWallet(
            HttpSession session,
            @PathVariable Long id) {

        Long clientId = getClientIdFromSession(session);
        System.out.println("📥 [StockWalletController] GET /api/stock-wallet/" + id);
        System.out.println("   └─ Client ID: " + clientId);

        try {
            StockWalletResponse stock = stockWalletService.getStockWalletById(id, clientId);
            System.out.println("✅ [StockWalletController] Stock trouvé: " + stock.getNomStock());
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mettre à jour un stock du client connecté
     * PUT /api/stock-wallet/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockWalletResponse> updateStockWallet(
            HttpSession session,
            @PathVariable Long id,
            @RequestBody UpdateStockWalletRequest request) {

        Long clientId = getClientIdFromSession(session);
        System.out.println("📥 [StockWalletController] PUT /api/stock-wallet/" + id);
        System.out.println("   └─ Client ID: " + clientId);

        try {
            StockWalletResponse updatedStock = stockWalletService.updateStockWallet(id, clientId, request);
            System.out.println("✅ [StockWalletController] Stock mis à jour: " + updatedStock.getNomStock());
            return ResponseEntity.ok(updatedStock);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer un stock du client connecté
     * DELETE /api/stock-wallet/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockWallet(
            HttpSession session,
            @PathVariable Long id) {

        Long clientId = getClientIdFromSession(session);
        System.out.println("📥 [StockWalletController] DELETE /api/stock-wallet/" + id);
        System.out.println("   └─ Client ID: " + clientId);

        try {
            stockWalletService.deleteStockFromWallet(id, clientId);
            System.out.println("✅ [StockWalletController] Stock supprimé - ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupérer les statistiques du wallet du client connecté
     * GET /api/stock-wallet/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<StockWalletStats> getWalletStats(HttpSession session) {
        Long clientId = getClientIdFromSession(session);
        System.out.println("📥 [StockWalletController] GET /api/stock-wallet/stats");
        System.out.println("   └─ Client ID: " + clientId);

        try {
            StockWalletStats stats = stockWalletService.getWalletStats(clientId);
            System.out.println("✅ [StockWalletController] Statistiques récupérées");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de santé
     * GET /api/stock-wallet/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        System.out.println("📥 [StockWalletController] GET /api/stock-wallet/health");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "controller", "StockWalletController",
                "message", "Stock Wallet API est opérationnelle"
        ));
    }
}