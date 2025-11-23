// MyLb.BackEnd.Controller.StockWalletController.java
package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.StockWalletService;
import MyLb.BackEnd.dto.StockWalletResponse;
import MyLb.BackEnd.dto.CreateStockWalletRequest;
import MyLb.BackEnd.dto.UpdateStockWalletRequest;
import MyLb.BackEnd.dto.StockWalletStats;
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
     * Ajouter un stock au wallet
     * POST /api/stock-wallet
     */
    @PostMapping
    public ResponseEntity<StockWalletResponse> addStockToWallet(@RequestBody CreateStockWalletRequest request) {
        System.out.println("📥 [StockWalletController] POST /api/stock-wallet");
        System.out.println("   └─ Client: " + request.getIdClient() + ", Stock: " + request.getNomStock());

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
     * Récupérer tous les stocks d'un client
     * GET /api/stock-wallet/client/{idClient}
     */
    @GetMapping("/client/{idClient}")
    public ResponseEntity<List<StockWalletResponse>> getClientStocks(@PathVariable Long idClient) {
        System.out.println("📥 [StockWalletController] GET /api/stock-wallet/client/" + idClient);

        try {
            List<StockWalletResponse> stocks = stockWalletService.getClientStocks(idClient);
            System.out.println("✅ [StockWalletController] " + stocks.size() + " stock(s) trouvé(s)");
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer un stock spécifique
     * GET /api/stock-wallet/{id}/client/{idClient}
     */
    @GetMapping("/{id}/client/{idClient}")
    public ResponseEntity<StockWalletResponse> getStockWallet(
            @PathVariable Long id,
            @PathVariable Long idClient) {
        System.out.println("📥 [StockWalletController] GET /api/stock-wallet/" + id + "/client/" + idClient);

        try {
            StockWalletResponse stock = stockWalletService.getStockWalletById(id, idClient);
            System.out.println("✅ [StockWalletController] Stock trouvé: " + stock.getNomStock());
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mettre à jour un stock
     * PUT /api/stock-wallet/{id}/client/{idClient}
     */
    @PutMapping("/{id}/client/{idClient}")
    public ResponseEntity<StockWalletResponse> updateStockWallet(
            @PathVariable Long id,
            @PathVariable Long idClient,
            @RequestBody UpdateStockWalletRequest request) {
        System.out.println("📥 [StockWalletController] PUT /api/stock-wallet/" + id + "/client/" + idClient);

        try {
            StockWalletResponse updatedStock = stockWalletService.updateStockWallet(id, idClient, request);
            System.out.println("✅ [StockWalletController] Stock mis à jour: " + updatedStock.getNomStock());
            return ResponseEntity.ok(updatedStock);
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer un stock
     * DELETE /api/stock-wallet/{id}/client/{idClient}
     */
    @DeleteMapping("/{id}/client/{idClient}")
    public ResponseEntity<Void> deleteStockWallet(
            @PathVariable Long id,
            @PathVariable Long idClient) {
        System.out.println("📥 [StockWalletController] DELETE /api/stock-wallet/" + id + "/client/" + idClient);

        try {
            stockWalletService.deleteStockFromWallet(id, idClient);
            System.out.println("✅ [StockWalletController] Stock supprimé - ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ [StockWalletController] Erreur: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupérer les statistiques du wallet
     * GET /api/stock-wallet/client/{idClient}/stats
     */
    @GetMapping("/client/{idClient}/stats")
    public ResponseEntity<StockWalletStats> getWalletStats(@PathVariable Long idClient) {
        System.out.println("📥 [StockWalletController] GET /api/stock-wallet/client/" + idClient + "/stats");

        try {
            StockWalletStats stats = stockWalletService.getWalletStats(idClient);
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