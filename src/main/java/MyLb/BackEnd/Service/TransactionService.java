package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.Transaction;
import MyLb.BackEnd.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Obtenir toutes les transactions de tous les clients (Admin)
     */
    public List<Transaction> getAllTransactions() {
        System.out.println("📊 [TransactionService] Récupération de toutes les transactions");
        List<Transaction> allTransactions = transactionRepository.findAllByOrderByDateCreationDesc();
        System.out.println("✅ [TransactionService] " + allTransactions.size() + " transaction(s) trouvée(s) au total");
        return allTransactions;
    }

    /**
     * Obtenir toutes les transactions avec pagination (Admin)
     */
    public List<Transaction> getAllTransactions(int page, int size) {
        System.out.println("📊 [TransactionService] Récupération des transactions - Page: " + page + ", Taille: " + size);
        // Implémentation de la pagination si nécessaire
        return transactionRepository.findAllByOrderByDateCreationDesc();
    }

    /**
     * Obtenir les transactions de stocks de tous les clients
     */
    public List<Transaction> getAllStockTransactions() {
        System.out.println("📊 [TransactionService] Récupération de toutes les transactions de stocks");
        List<Transaction> allTransactions = getAllTransactions();

        // Filtrer les transactions de stocks
        List<Transaction> stockTransactions = allTransactions.stream()
                .filter(transaction -> transaction.getDescription() != null &&
                        (transaction.getDescription().startsWith("Achat de") ||
                                transaction.getDescription().startsWith("Vente de")))
                .collect(java.util.stream.Collectors.toList());

        System.out.println("✅ [TransactionService] " + stockTransactions.size() + " transaction(s) de stock trouvée(s)");
        return stockTransactions;
    }

    /**
     * Obtenir les statistiques globales de toutes les transactions
     */
    public java.util.Map<String, Object> getGlobalStats() {
        System.out.println("📊 [TransactionService] Calcul des statistiques globales");
        List<Transaction> allTransactions = getAllTransactions();

        double totalDeposits = allTransactions.stream()
                .filter(t -> "DEPOSIT".equals(t.getTypeOperation()) || "CARD_TO_WALLET".equals(t.getTypeOperation()))
                .mapToDouble(Transaction::getMontant)
                .sum();

        double totalWithdrawals = allTransactions.stream()
                .filter(t -> "WITHDRAW".equals(t.getTypeOperation()) || "WALLET_TO_CARD".equals(t.getTypeOperation()))
                .mapToDouble(Transaction::getMontant)
                .sum();

        double totalStockTransactions = allTransactions.stream()
                .filter(t -> t.getDescription() != null &&
                        (t.getDescription().startsWith("Achat de") || t.getDescription().startsWith("Vente de")))
                .mapToDouble(Transaction::getMontant)
                .sum();

        long totalClients = allTransactions.stream()
                .map(Transaction::getIdClient)
                .distinct()
                .count();

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalTransactions", allTransactions.size());
        stats.put("totalDeposits", totalDeposits);
        stats.put("totalWithdrawals", totalWithdrawals);
        stats.put("totalStockTransactions", totalStockTransactions);
        stats.put("netFlow", totalDeposits - totalWithdrawals);
        stats.put("totalClients", totalClients);
        stats.put("averageTransactionAmount", allTransactions.isEmpty() ? 0 :
                allTransactions.stream().mapToDouble(Transaction::getMontant).average().orElse(0));

        System.out.println("✅ [TransactionService] Statistiques globales calculées");
        return stats;
    }

    // ... LE RESTE DE VOTRE CODE EXISTANT ...

    /**
     * Enregistrer une transaction
     */
    public Transaction saveTransaction(Transaction transaction) {
        System.out.println("💾 [TransactionService] Enregistrement d'une transaction: " + transaction.getTypeOperation());
        return transactionRepository.save(transaction);
    }

    /**
     * Enregistrer un dépôt
     */
    public Transaction recordDeposit(Long idClient, Double montant, String description, Double soldeApresOperation) {
        Transaction transaction = new Transaction(idClient, "DEPOSIT", montant, description);
        transaction.setSoldeApresOperation(soldeApresOperation);
        return saveTransaction(transaction);
    }

    /**
     * Enregistrer un retrait
     */
    public Transaction recordWithdrawal(Long idClient, Double montant, String description, Double soldeApresOperation) {
        Transaction transaction = new Transaction(idClient, "WITHDRAW", montant, description);
        transaction.setSoldeApresOperation(soldeApresOperation);
        return saveTransaction(transaction);
    }

    /**
     * Enregistrer un transfert carte -> wallet
     */
    public Transaction recordCardToWallet(Long idClient, Double montant, Long idCarte, Double soldeApresOperation) {
        String description = "Transfert carte vers wallet - Carte ID: " + idCarte;
        Transaction transaction = new Transaction(idClient, "CARD_TO_WALLET", montant, description, idCarte);
        transaction.setSoldeApresOperation(soldeApresOperation);
        return saveTransaction(transaction);
    }

    /**
     * Enregistrer un transfert wallet -> carte
     */
    public Transaction recordWalletToCard(Long idClient, Double montant, Long idCarte, Double soldeApresOperation) {
        String description = "Transfert wallet vers carte - Carte ID: " + idCarte;
        Transaction transaction = new Transaction(idClient, "WALLET_TO_CARD", montant, description, idCarte);
        transaction.setSoldeApresOperation(soldeApresOperation);
        return saveTransaction(transaction);
    }

    /**
     * Enregistrer un transfert vers un utilisateur
     */
    public Transaction recordUserTransfer(Long idClient, Double montant, String emailDestinataire, Double soldeApresOperation) {
        String description = "Transfert vers utilisateur: " + emailDestinataire;
        Transaction transaction = new Transaction(idClient, "USER_TRANSFER", montant, description);
        transaction.setEmailDestinataire(emailDestinataire);
        transaction.setSoldeApresOperation(soldeApresOperation);
        return saveTransaction(transaction);
    }

    /**
     * Enregistrer la création d'une carte
     */
    public Transaction recordCardCreation(Long idClient, String cardNumber, String cardType) {
        String description = "Création de carte - " + maskCardNumber(cardNumber) + " - " + cardType;
        Transaction transaction = new Transaction(idClient, "CARD_CREATION", 0.0, description);
        return saveTransaction(transaction);
    }

    /**
     * Enregistrer la désactivation d'une carte
     */
    public Transaction recordCardDeactivation(Long idClient, String cardNumber) {
        String description = "Désactivation de carte - " + maskCardNumber(cardNumber);
        Transaction transaction = new Transaction(idClient, "CARD_DEACTIVATION", 0.0, description);
        return saveTransaction(transaction);
    }

    /**
     * Obtenir l'historique des transactions d'un client
     */
    public List<Transaction> getClientTransactions(Long idClient) {
        System.out.println("📊 [TransactionService] Récupération des transactions pour le client: " + idClient);
        return transactionRepository.findByIdClientOrderByDateCreationDesc(idClient);
    }

    /**
     * Obtenir les transactions par type d'opération
     */
    public List<Transaction> getTransactionsByType(Long idClient, String typeOperation) {
        System.out.println("📊 [TransactionService] Récupération des transactions de type " + typeOperation + " pour le client: " + idClient);
        return transactionRepository.findByIdClientAndTypeOperationOrderByDateCreationDesc(idClient, typeOperation);
    }

    /**
     * Obtenir les transactions récentes (30 derniers jours)
     */
    public List<Transaction> getRecentTransactions(Long idClient) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        return transactionRepository.findRecentTransactions(idClient, startDate);
    }

    /**
     * Obtenir les transactions sur une période spécifique
     */
    public List<Transaction> getTransactionsByDateRange(Long idClient, LocalDateTime startDate, LocalDateTime endDate) {
        System.out.println("📊 [TransactionService] Récupération des transactions pour la période " + startDate + " à " + endDate);
        return transactionRepository.findByIdClientAndDateCreationBetweenOrderByDateCreationDesc(idClient, startDate, endDate);
    }

    /**
     * Obtenir le solde total des dépôts
     */
    public Double getTotalDeposits(Long idClient) {
        Double total = transactionRepository.getTotalDepositsByClient(idClient);
        System.out.println("💰 [TransactionService] Total des dépôts pour le client " + idClient + ": " + total + " DT");
        return total;
    }

    /**
     * Obtenir le solde total des retraits
     */
    public Double getTotalWithdrawals(Long idClient) {
        Double total = transactionRepository.getTotalWithdrawalsByClient(idClient);
        System.out.println("💰 [TransactionService] Total des retraits pour le client " + idClient + ": " + total + " DT");
        return total;
    }

    /**
     * Obtenir le solde net (dépôts - retraits)
     */
    public Double getNetFlow(Long idClient) {
        Double deposits = getTotalDeposits(idClient);
        Double withdrawals = getTotalWithdrawals(idClient);
        Double netFlow = deposits - withdrawals;
        System.out.println("💰 [TransactionService] Flux net pour le client " + idClient + ": " + netFlow + " DT");
        return netFlow;
    }

    /**
     * Obtenir le nombre total de transactions
     */
    public Long getTransactionCount(Long idClient) {
        Long count = transactionRepository.countByIdClient(idClient);
        System.out.println("📊 [TransactionService] Nombre total de transactions pour le client " + idClient + ": " + count);
        return count;
    }

    /**
     * Obtenir le montant moyen des transactions
     */
    public Double getAverageTransactionAmount(Long idClient) {
        List<Transaction> transactions = getClientTransactions(idClient);
        if (transactions.isEmpty()) {
            return 0.0;
        }

        Double total = transactions.stream()
                .mapToDouble(Transaction::getMontant)
                .sum();
        Double average = total / transactions.size();
        System.out.println("💰 [TransactionService] Montant moyen des transactions: " + average + " DT");
        return average;
    }

    /**
     * Vérifier si une transaction existe pour un client
     */
    public boolean hasTransactions(Long idClient) {
        boolean hasTransactions = !getClientTransactions(idClient).isEmpty();
        System.out.println("🔍 [TransactionService] Le client " + idClient + " a des transactions: " + hasTransactions);
        return hasTransactions;
    }

    /**
     * Obtenir la dernière transaction d'un client
     */
    public Transaction getLastTransaction(Long idClient) {
        List<Transaction> transactions = getClientTransactions(idClient);
        if (transactions.isEmpty()) {
            return null;
        }
        Transaction lastTransaction = transactions.get(0);
        System.out.println("📊 [TransactionService] Dernière transaction: " + lastTransaction.getTypeOperation() + " - " + lastTransaction.getMontant() + " DT");
        return lastTransaction;
    }

    /**
     * Masquer le numéro de carte pour la sécurité
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        return cardNumber.substring(0, 4) + "********" + cardNumber.substring(12);
    }

    /**
     * Nettoyer les anciennes transactions (garder seulement 2 ans)
     */
    public void cleanupOldTransactions() {
        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
        List<Transaction> oldTransactions = transactionRepository.findByDateCreationBefore(twoYearsAgo);

        if (!oldTransactions.isEmpty()) {
            transactionRepository.deleteAll(oldTransactions);
            System.out.println("🧹 [TransactionService] " + oldTransactions.size() + " ancienne(s) transaction(s) supprimée(s)");
        }
    }

    /**
     * Obtenir les statistiques complètes d'un client
     */
    public Object getClientStats(Long idClient) {
        Double totalDeposits = getTotalDeposits(idClient);
        Double totalWithdrawals = getTotalWithdrawals(idClient);
        Double netFlow = getNetFlow(idClient);
        Long transactionCount = getTransactionCount(idClient);
        Double averageAmount = getAverageTransactionAmount(idClient);
        Transaction lastTransaction = getLastTransaction(idClient);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalDeposits", totalDeposits);
        stats.put("totalWithdrawals", totalWithdrawals);
        stats.put("netFlow", netFlow);
        stats.put("transactionCount", transactionCount);
        stats.put("averageAmount", averageAmount);
        stats.put("hasTransactions", hasTransactions(idClient));
        stats.put("lastTransactionDate", lastTransaction != null ? lastTransaction.getDateCreation() : null);
        stats.put("lastTransactionType", lastTransaction != null ? lastTransaction.getTypeOperation() : null);
        stats.put("lastTransactionAmount", lastTransaction != null ? lastTransaction.getMontant() : null);

        System.out.println("📈 [TransactionService] Statistiques complètes générées pour le client " + idClient);
        return stats;
    }
}