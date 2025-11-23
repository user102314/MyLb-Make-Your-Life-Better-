package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.StockService;
import MyLb.BackEnd.dto.CreateStockRequest;
import MyLb.BackEnd.dto.StockResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des stocks
 * Base URL: /api/stocks
 */
@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Créer un nouveau stock associé à une company
     * POST /api/stocks
     *
     * @param request Données du stock à créer
     * @return Le stock créé avec code 201
     */
    @PostMapping
    public ResponseEntity<StockResponse> creerStock(@Valid @RequestBody CreateStockRequest request) {
        System.out.println("📥 [StockController] POST /api/stocks - Création de stock");
        System.out.println("   └─ Données reçues: " + request.getNomStock() +
                " | Company: " + request.getIdComponey() +
                " | Owner: " + request.getOwnerId());

        try {
            StockResponse stockCree = stockService.creerStock(request);
            System.out.println("✅ [StockController] Stock créé avec succès - ID: " + stockCree.getIdStock());
            return ResponseEntity.status(HttpStatus.CREATED).body(stockCree);
        } catch (Exception e) {
            System.err.println("❌ [StockController] Erreur lors de la création: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Récupérer tous les stocks
     * GET /api/stocks
     *
     * @return Liste de tous les stocks
     */
    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        System.out.println("📥 [StockController] GET /api/stocks - Récupération de tous les stocks");
        List<StockResponse> stocks = stockService.getAllStocks();
        System.out.println("✅ [StockController] " + stocks.size() + " stock(s) trouvé(s)");
        return ResponseEntity.ok(stocks);
    }

    /**
     * Récupérer les stocks d'une company spécifique
     * GET /api/stocks/company/{idComponey}
     *
     * @param idComponey ID de la company
     * @return Liste des stocks de la company
     */
    @GetMapping("/company/{idComponey}")
    public ResponseEntity<List<StockResponse>> getStocksByCompany(@PathVariable Long idComponey) {
        System.out.println("📥 [StockController] GET /api/stocks/company/" + idComponey);
        List<StockResponse> stocks = stockService.getStocksByCompany(idComponey);
        System.out.println("✅ [StockController] " + stocks.size() + " stock(s) pour la company " + idComponey);
        return ResponseEntity.ok(stocks);
    }

    /**
     * Modifier l'état d'un stock
     * PUT /api/stocks/{idStock}/etat
     *
     * @param idStock ID du stock
     * @param request Nouvel état
     * @return Stock modifié
     */
    @PutMapping("/{idStock}/etat")
    public ResponseEntity<StockResponse> modifierEtat(
            @PathVariable Long idStock,
            @RequestBody EtatUpdateRequest request) {
        System.out.println("📥 [StockController] PUT /api/stocks/" + idStock + "/etat - Nouvel état: " + request.getEtat());
        StockResponse stockModifie = stockService.modifierEtatById(idStock, request.getEtat());
        System.out.println("✅ [StockController] État modifié avec succès");
        return ResponseEntity.ok(stockModifie);
    }

    /**
     * Modifier le stock disponible
     * PUT /api/stocks/{idStock}/stock-disponible
     *
     * @param idStock ID du stock
     * @param request Nouveau stock disponible
     * @return Stock modifié
     */
    @PutMapping("/{idStock}/stock-disponible")
    public ResponseEntity<StockResponse> modifierStockDisponible(
            @PathVariable Long idStock,
            @RequestBody StockDisponibleUpdateRequest request) {
        System.out.println("📥 [StockController] PUT /api/stocks/" + idStock + "/stock-disponible - Nouvelle quantité: " + request.getStockDisponible());
        StockResponse stockModifie = stockService.modifierStockDisponible(idStock, request.getStockDisponible());
        System.out.println("✅ [StockController] Stock disponible modifié avec succès");
        return ResponseEntity.ok(stockModifie);
    }

    /**
     * Modifier le stock restant
     * PUT /api/stocks/{idStock}/stock-reste
     *
     * @param idStock ID du stock
     * @param request Nouveau stock restant
     * @return Stock modifié
     */
    @PutMapping("/{idStock}/stock-reste")
    public ResponseEntity<StockResponse> modifierStockReste(
            @PathVariable Long idStock,
            @RequestBody StockResteUpdateRequest request) {
        System.out.println("📥 [StockController] PUT /api/stocks/" + idStock + "/stock-reste - Nouvelle quantité: " + request.getStockReste());
        StockResponse stockModifie = stockService.modifierStockReste(idStock, request.getStockReste());
        System.out.println("✅ [StockController] Stock restant modifié avec succès");
        return ResponseEntity.ok(stockModifie);
    }

    /**
     * Modifier le prix du stock
     * PUT /api/stocks/{idStock}/prix
     *
     * @param idStock ID du stock
     * @param request Nouveau prix
     * @return Stock modifié
     */
    @PutMapping("/{idStock}/prix")
    public ResponseEntity<StockResponse> modifierPrixStock(
            @PathVariable Long idStock,
            @RequestBody PrixStockUpdateRequest request) {
        System.out.println("📥 [StockController] PUT /api/stocks/" + idStock + "/prix - Nouveau prix: " + request.getPrixStock());
        StockResponse stockModifie = stockService.modifierPrixStock(idStock, request.getPrixStock());
        System.out.println("✅ [StockController] Prix modifié avec succès");
        return ResponseEntity.ok(stockModifie);
    }

    /**
     * Endpoint de test pour vérifier que le contrôleur fonctionne
     * GET /api/stocks/health
     *
     * @return Message de confirmation
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        System.out.println("📥 [StockController] GET /api/stocks/health - Health check");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "controller", "StockController",
                "message", "Stock API est opérationnelle"
        ));
    }

    // ============================================================
    // Classes internes pour les requêtes de mise à jour
    // ============================================================

    /**
     * Requête pour modifier l'état d'un stock
     */
    public static class EtatUpdateRequest {
        private String etat;

        public EtatUpdateRequest() {}

        public String getEtat() {
            return etat;
        }

        public void setEtat(String etat) {
            this.etat = etat;
        }
    }

    /**
     * Requête pour modifier le stock disponible
     */
    public static class StockDisponibleUpdateRequest {
        private Integer stockDisponible;

        public StockDisponibleUpdateRequest() {}

        public Integer getStockDisponible() {
            return stockDisponible;
        }

        public void setStockDisponible(Integer stockDisponible) {
            this.stockDisponible = stockDisponible;
        }
    }

    /**
     * Requête pour modifier le stock restant
     */
    public static class StockResteUpdateRequest {
        private Integer stockReste;

        public StockResteUpdateRequest() {}

        public Integer getStockReste() {
            return stockReste;
        }

        public void setStockReste(Integer stockReste) {
            this.stockReste = stockReste;
        }
    }

    /**
     * Requête pour modifier le prix du stock
     */
    public static class PrixStockUpdateRequest {
        private Double prixStock;

        public PrixStockUpdateRequest() {}

        public Double getPrixStock() {
            return prixStock;
        }

        public void setPrixStock(Double prixStock) {
            this.prixStock = prixStock;
        }
    }
}