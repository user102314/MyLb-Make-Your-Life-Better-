package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Transaction;
import MyLb.BackEnd.Service.TransactionService;
import MyLb.BackEnd.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Obtenir l'historique des transactions d'un client
     * GET /api/transactions/client/{idClient}
     */
    @GetMapping("/client/{idClient}")
    public ResponseEntity<List<TransactionResponse>> getClientTransactions(@PathVariable Long idClient) {
        System.out.println("📊 [TransactionController] GET /api/transactions/client/" + idClient);

        try {
            List<Transaction> transactions = transactionService.getClientTransactions(idClient);
            List<TransactionResponse> response = transactions.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            System.out.println("✅ [TransactionController] " + response.size() + " transaction(s) trouvée(s)");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [TransactionController] Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtenir les transactions récentes (30 derniers jours)
     * GET /api/transactions/client/{idClient}/recent
     */
    @GetMapping("/client/{idClient}/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(@PathVariable Long idClient) {
        System.out.println("📊 [TransactionController] GET /api/transactions/client/" + idClient + "/recent");

        try {
            List<Transaction> transactions = transactionService.getRecentTransactions(idClient);
            List<TransactionResponse> response = transactions.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            System.out.println("✅ [TransactionController] " + response.size() + " transaction(s) récente(s) trouvée(s)");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [TransactionController] Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtenir les statistiques de transactions
     * GET /api/transactions/client/{idClient}/stats
     */
    @GetMapping("/client/{idClient}/stats")
    public ResponseEntity<Map<String, Object>> getTransactionStats(@PathVariable Long idClient) {
        System.out.println("📊 [TransactionController] GET /api/transactions/client/" + idClient + "/stats");

        try {
            Double totalDeposits = transactionService.getTotalDeposits(idClient);
            Double totalWithdrawals = transactionService.getTotalWithdrawals(idClient);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDeposits", totalDeposits);
            stats.put("totalWithdrawals", totalWithdrawals);
            stats.put("netFlow", totalDeposits - totalWithdrawals);

            System.out.println("✅ [TransactionController] Statistiques récupérées");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("❌ [TransactionController] Erreur: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la récupération des statistiques");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtenir les transactions par type d'opération
     * GET /api/transactions/client/{idClient}/type/{typeOperation}
     */
    @GetMapping("/client/{idClient}/type/{typeOperation}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(
            @PathVariable Long idClient,
            @PathVariable String typeOperation) {

        System.out.println("📊 [TransactionController] GET /api/transactions/client/" + idClient + "/type/" + typeOperation);

        try {
            List<Transaction> transactions = transactionService.getTransactionsByType(idClient, typeOperation);
            List<TransactionResponse> response = transactions.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            System.out.println("✅ [TransactionController] " + response.size() + " transaction(s) de type " + typeOperation + " trouvée(s)");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [TransactionController] Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de santé
     * GET /api/transactions/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        System.out.println("📊 [TransactionController] GET /api/transactions/health");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "controller", "TransactionController",
                "message", "Transaction API est opérationnelle"
        ));
    }

    /**
     * Convertir l'entité en DTO
     */
    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setIdClient(transaction.getIdClient());
        response.setTypeOperation(transaction.getTypeOperation());
        response.setMontant(transaction.getMontant());
        response.setDescription(transaction.getDescription());
        response.setStatut(transaction.getStatut());
        response.setDateCreation(transaction.getDateCreation());
        response.setSoldeApresOperation(transaction.getSoldeApresOperation());
        response.setIdDestinataire(transaction.getIdDestinataire());
        response.setEmailDestinataire(transaction.getEmailDestinataire());
        response.setIdCarte(transaction.getIdCarte());

        return response;
    }
}