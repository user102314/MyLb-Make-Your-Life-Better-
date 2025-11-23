package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.WalletService;
import MyLb.BackEnd.dto.ModifySoldRequest;
import MyLb.BackEnd.dto.RechargeRequest;
import MyLb.BackEnd.dto.WalletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Recharger le solde d'un client
     * POST /api/wallets/{idClient}/recharger
     */
    @PostMapping("/{idClient}/recharger")
    public ResponseEntity<WalletResponse> rechargerSold(
            @PathVariable Long idClient,
            @Valid @RequestBody RechargeRequest request) {

        System.out.println("📥 [WalletController] POST /api/wallets/" + idClient + "/recharger");
        System.out.println("   └─ Montant: " + request.getMontant());

        try {
            WalletResponse response = walletService.rechargerSold(idClient, request.getMontant());
            System.out.println("✅ [WalletController] Rechargement réussi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [WalletController] Erreur lors du rechargement: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get solde by ID client
     * GET /api/wallets/{idClient}/solde
     */
    @GetMapping("/{idClient}/solde")
    public ResponseEntity<Map<String, Double>> getSoldByIdClient(@PathVariable Long idClient) {
        System.out.println("📥 [WalletController] GET /api/wallets/" + idClient + "/solde");

        try {
            Double solde = walletService.getSoldByIdClient(idClient);
            Map<String, Double> response = new HashMap<>();
            response.put("solde", solde);

            System.out.println("✅ [WalletController] Solde récupéré: " + solde);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [WalletController] Erreur lors de la récupération du solde: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Modifier le solde d'un client
     * PUT /api/wallets/{idClient}/modifier-solde
     */
    @PutMapping("/{idClient}/modifier-solde")
    public ResponseEntity<WalletResponse> modifySold(
            @PathVariable Long idClient,
            @Valid @RequestBody ModifySoldRequest request) {

        System.out.println("📥 [WalletController] PUT /api/wallets/" + idClient + "/modifier-solde");
        System.out.println("   └─ Nouveau solde: " + request.getNouveauSold());

        try {
            WalletResponse response = walletService.modifySold(idClient, request.getNouveauSold());
            System.out.println("✅ [WalletController] Solde modifié avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [WalletController] Erreur lors de la modification du solde: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Créer un wallet si il n'existe pas
     * POST /api/wallets/{idClient}/create
     */
    @PostMapping("/{idClient}/create")
    public ResponseEntity<WalletResponse> createWallet(@PathVariable Long idClient) {
        System.out.println("📥 [WalletController] POST /api/wallets/" + idClient + "/create");

        try {
            WalletResponse response = walletService.createWalletIfNotExists(idClient);
            System.out.println("✅ [WalletController] Wallet créé/vérifié avec succès");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("❌ [WalletController] Erreur lors de la création du wallet: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Endpoint de santé
     * GET /api/wallets/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        System.out.println("📥 [WalletController] GET /api/wallets/health");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "controller", "WalletController",
                "message", "Wallet API est opérationnelle"
        ));
    }
}