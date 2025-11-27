package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Transaction;
import MyLb.BackEnd.Service.TransactionService;
import MyLb.BackEnd.dto.TransactionResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * Obtenir l'historique des transactions du client connecté
     * GET /api/transactions
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getClientTransactions(HttpSession session) {
        Long clientId = getClientIdFromSession(session);
        System.out.println("📊 [TransactionController] GET /api/transactions");
        System.out.println("   └─ Client ID: " + clientId);

        try {
            List<Transaction> transactions = transactionService.getClientTransactions(clientId);
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
     * Obtenir les transactions récentes (30 derniers jours) du client connecté
     * GET /api/transactions/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(HttpSession session) {
        Long clientId = getClientIdFromSession(session);
        System.out.println("📊 [TransactionController] GET /api/transactions/recent");
        System.out.println("   └─ Client ID: " + clientId);

        try {
            List<Transaction> transactions = transactionService.getRecentTransactions(clientId);
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
     * Obtenir les statistiques de transactions du client connecté
     * GET /api/transactions/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTransactionStats(HttpSession session) {
        Long clientId = getClientIdFromSession(session);
        System.out.println("📊 [TransactionController] GET /api/transactions/stats");
        System.out.println("   └─ Client ID: " + clientId);

        try {
            Double totalDeposits = transactionService.getTotalDeposits(clientId);
            Double totalWithdrawals = transactionService.getTotalWithdrawals(clientId);

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
     * Obtenir les transactions par type d'opération pour le client connecté
     * GET /api/transactions/type/{typeOperation}
     */
    @GetMapping("/type/{typeOperation}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(
            HttpSession session,
            @PathVariable String typeOperation) {

        Long clientId = getClientIdFromSession(session);
        System.out.println("📊 [TransactionController] GET /api/transactions/type/" + typeOperation);
        System.out.println("   └─ Client ID: " + clientId);

        try {
            List<Transaction> transactions = transactionService.getTransactionsByType(clientId, typeOperation);
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
     * Obtenir toutes les transactions de stocks de tous les clients (Admin)
     * GET /api/transactions/admin/stock-transactions
     */
    @GetMapping("/admin/stock-transactions")
    public ResponseEntity<?> getAllStockTransactions() {
        System.out.println("📊 [TransactionController] GET /api/transactions/admin/stock-transactions");

        try {
            // Récupérer toutes les transactions
            List<Transaction> allTransactions = transactionService.getAllTransactions();

            // Filtrer les transactions de stocks
            List<Transaction> stockTransactions = allTransactions.stream()
                    .filter(transaction -> transaction.getDescription() != null &&
                            (transaction.getDescription().startsWith("Achat de") ||
                                    transaction.getDescription().startsWith("Vente de")))
                    .collect(Collectors.toList());

            System.out.println("✅ [TransactionController] " + stockTransactions.size() + " transaction(s) de stock trouvée(s)");

            // Extraire et formater les informations des transactions de stocks
            List<Object[]> formattedTransactions = extractStockTransactionInfo(stockTransactions);

            // Trouver les 3 meilleurs stocks
            List<String> topStocks = findTopStocks(stockTransactions);

            Map<String, Object> response = new HashMap<>();
            response.put("stockTransactions", formattedTransactions);
            response.put("topStocks", topStocks);
            response.put("totalStockTransactions", stockTransactions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [TransactionController] Erreur: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la récupération des transactions de stocks");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Extraire les informations des transactions de stocks
     */
    private List<Object[]> extractStockTransactionInfo(List<Transaction> stockTransactions) {
        List<Object[]> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("(Achat de|Vente de)\\s+(\\d+)\\s+actions?\\s+(.+?)\\s+à\\s+(\\d+\\.?\\d*)\\s+DT");

        for (Transaction transaction : stockTransactions) {
            String description = transaction.getDescription();
            Matcher matcher = pattern.matcher(description);

            if (matcher.find()) {
                String type = matcher.group(1); // "Achat de" ou "Vente de"
                String quantite = matcher.group(2); // quantité
                String nomStock = matcher.group(3).trim(); // nom du stock
                String prix = matcher.group(4); // prix unitaire

                Object[] transactionInfo = {
                        null, // Premier élément null comme demandé
                        nomStock,
                        Integer.parseInt(quantite),
                        type,
                        Double.parseDouble(prix),
                        transaction.getDateCreation()
                };
                result.add(transactionInfo);
            } else {
                // Fallback pour d'autres formats de description
                Object[] transactionInfo = {
                        null,
                        extractStockName(description),
                        extractQuantity(description),
                        extractTransactionType(description),
                        0.0,
                        transaction.getDateCreation()
                };
                result.add(transactionInfo);
            }
        }

        return result;
    }

    /**
     * Trouver les 3 meilleurs stocks (les plus vendus)
     */
    private List<String> findTopStocks(List<Transaction> stockTransactions) {
        // Compter les ventes par stock
        Map<String, Integer> stockSales = new HashMap<>();
        Pattern pattern = Pattern.compile("Vente de\\s+\\d+\\s+actions?\\s+(.+?)\\s+à");

        for (Transaction transaction : stockTransactions) {
            String description = transaction.getDescription();
            if (description.startsWith("Vente de")) {
                Matcher matcher = pattern.matcher(description);
                if (matcher.find()) {
                    String stockName = matcher.group(1).trim();
                    stockSales.put(stockName, stockSales.getOrDefault(stockName, 0) + 1);
                }
            }
        }

        // Trier par nombre de ventes décroissant et prendre les 3 premiers
        return stockSales.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Méthode fallback pour extraire le nom du stock
     */
    private String extractStockName(String description) {
        // Logique simple pour extraire le nom du stock
        if (description.contains("actions")) {
            String[] parts = description.split("actions");
            if (parts.length > 1) {
                return parts[1].split("à")[0].trim();
            }
        }
        return "Stock inconnu";
    }

    /**
     * Méthode fallback pour extraire la quantité
     */
    private int extractQuantity(String description) {
        try {
            Pattern quantityPattern = Pattern.compile("\\d+");
            Matcher matcher = quantityPattern.matcher(description);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        } catch (Exception e) {
            // Ignorer les erreurs de parsing
        }
        return 0;
    }

    /**
     * Méthode fallback pour déterminer le type de transaction
     */
    private String extractTransactionType(String description) {
        if (description.startsWith("Achat")) {
            return "Achat";
        } else if (description.startsWith("Vente")) {
            return "Vente";
        }
        return "Inconnu";
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