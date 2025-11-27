package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.WalletService;
import MyLb.BackEnd.dto.ModifySoldRequest;
import MyLb.BackEnd.dto.RechargeRequest;
import MyLb.BackEnd.dto.WalletResponse;
import jakarta.servlet.http.HttpSession;
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
     * Recharger le solde du client connecté
     * POST /api/wallets/recharger
     */
    @PostMapping("/recharger")
    public ResponseEntity<WalletResponse> rechargerSold(
            HttpSession session,
            @Valid @RequestBody RechargeRequest request) {

        Long clientId = getClientIdFromSession(session);

        System.out.println("📥 [WalletController] POST /api/wallets/recharger");
        System.out.println("   └─ Client ID: " + clientId);
        System.out.println("   └─ Montant: " + request.getMontant());

        try {
            WalletResponse response = walletService.rechargerSold(clientId, request.getMontant());
            System.out.println("✅ [WalletController] Rechargement réussi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [WalletController] Erreur lors du rechargement: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get solde du client connecté
     * GET /api/wallets/solde
     */
    @GetMapping("/solde")
    public ResponseEntity<Map<String, Double>> getSoldByIdClient(HttpSession session) {
        Long clientId = getClientIdFromSession(session);

        System.out.println("📥 [WalletController] GET /api/wallets/solde");
        System.out.println("   └─ Client ID: " + clientId);

        try {
            Double solde = walletService.getSoldByIdClient(clientId);
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
     * Modifier le solde du client connecté
     * PUT /api/wallets/modifier-solde
     */
    @PutMapping("/modifier-solde")
    public ResponseEntity<WalletResponse> modifySold(
            HttpSession session,
            @Valid @RequestBody ModifySoldRequest request) {

        Long clientId = getClientIdFromSession(session);

        System.out.println("📥 [WalletController] PUT /api/wallets/modifier-solde");
        System.out.println("   └─ Client ID: " + clientId);
        System.out.println("   └─ Nouveau solde: " + request.getNouveauSold());

        try {
            WalletResponse response = walletService.modifySold(clientId, request.getNouveauSold());
            System.out.println("✅ [WalletController] Solde modifié avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [WalletController] Erreur lors de la modification du solde: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Créer un wallet pour le client connecté
     * POST /api/wallets/create
     */
    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(HttpSession session) {
        Long clientId = getClientIdFromSession(session);

        System.out.println("📥 [WalletController] POST /api/wallets/create");
        System.out.println("   └─ Client ID: " + clientId);

        try {
            WalletResponse response = walletService.createWalletIfNotExists(clientId);
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