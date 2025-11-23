package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Wallet;
import MyLb.BackEnd.Repository.WalletRepository;
import MyLb.BackEnd.Service.WalletService;
import MyLb.BackEnd.Service.TransactionService; // 🆕 IMPORT
import MyLb.BackEnd.dto.WalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService; // 🆕 AJOUT

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             TransactionService transactionService) { // 🆕 MODIFICATION
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    @Override
    public WalletResponse rechargerSold(Long idClient, Double montant) {
        System.out.println("💰 [WalletService] Rechargement de " + montant + " pour le client: " + idClient);

        // Vérifier si le wallet existe, sinon le créer
        Wallet wallet = walletRepository.findByIdClient(idClient)
                .orElseGet(() -> {
                    System.out.println("🆕 [WalletService] Création d'un nouveau wallet pour le client: " + idClient);
                    return walletRepository.save(new Wallet(idClient, 0.0));
                });

        // Recharger le solde
        Double ancienSold = wallet.getSold();
        Double nouveauSold = ancienSold + montant;
        wallet.setSold(nouveauSold);

        Wallet walletUpdated = walletRepository.save(wallet);

        // 🆕 ENREGISTRER LA TRANSACTION
        try {
            transactionService.recordDeposit(idClient, montant,
                    "Rechargement wallet - Ancien solde: " + formatSold(ancienSold) + " DT", nouveauSold);
            System.out.println("💾 [WalletService] Transaction de dépôt enregistrée");
        } catch (Exception e) {
            System.err.println("⚠️ [WalletService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
        }

        System.out.println("✅ [WalletService] Rechargement réussi. Nouveau solde: " + formatSold(nouveauSold));

        return convertToResponse(walletUpdated);
    }

    @Override
    public Double getSoldByIdClient(Long idClient) {
        System.out.println("💰 [WalletService] Récupération du solde pour le client: " + idClient);

        Optional<Wallet> wallet = walletRepository.findByIdClient(idClient);

        if (wallet.isPresent()) {
            Double solde = wallet.get().getSold();
            System.out.println("✅ [WalletService] Solde trouvé: " + formatSold(solde));
            return solde;
        } else {
            System.out.println("⚠️ [WalletService] Aucun wallet trouvé pour le client: " + idClient);
            return 0.0;
        }
    }

    @Override
    public WalletResponse modifySold(Long idClient, Double nouveauSold) {
        System.out.println("💰 [WalletService] Modification du solde pour le client: " + idClient + " -> " + formatSold(nouveauSold));

        // Vérifier si le wallet existe, sinon le créer
        Wallet wallet = walletRepository.findByIdClient(idClient)
                .orElseGet(() -> {
                    System.out.println("🆕 [WalletService] Création d'un nouveau wallet pour le client: " + idClient);
                    return new Wallet(idClient);
                });

        Double ancienSold = wallet.getSold();
        wallet.setSold(nouveauSold);

        Wallet walletUpdated = walletRepository.save(wallet);

        // 🆕 ENREGISTRER LA TRANSACTION
        try {
            Double difference = nouveauSold - ancienSold;
            String description = "Modification solde - Ancien: " + formatSold(ancienSold) + " DT, Nouveau: " + formatSold(nouveauSold) + " DT";

            if (difference >= 0) {
                transactionService.recordDeposit(idClient, difference, description, nouveauSold);
            } else {
                transactionService.recordWithdrawal(idClient, Math.abs(difference), description, nouveauSold);
            }
            System.out.println("💾 [WalletService] Transaction de modification enregistrée");
        } catch (Exception e) {
            System.err.println("⚠️ [WalletService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
        }

        System.out.println("✅ [WalletService] Solde modifié avec succès: " + formatSold(nouveauSold));

        return convertToResponse(walletUpdated);
    }

    @Override
    public WalletResponse createWalletIfNotExists(Long idClient) {
        System.out.println("💰 [WalletService] Vérification/Création du wallet pour le client: " + idClient);

        Optional<Wallet> existingWallet = walletRepository.findByIdClient(idClient);

        if (existingWallet.isPresent()) {
            System.out.println("✅ [WalletService] Wallet existe déjà pour le client: " + idClient);
            return convertToResponse(existingWallet.get());
        } else {
            System.out.println("🆕 [WalletService] Création d'un nouveau wallet pour le client: " + idClient);
            Wallet newWallet = walletRepository.save(new Wallet(idClient, 0.0));
            return convertToResponse(newWallet);
        }
    }

    @Override
    public List<Object> getWalletHistory(Long idClient) {
        System.out.println("📊 [WalletService] Récupération de l'historique pour le client: " + idClient);

        try {
            // Utiliser le TransactionService pour récupérer l'historique
            var transactions = transactionService.getClientTransactions(idClient);

            // Convertir en format générique pour la réponse
            List<Object> history = transactions.stream()
                    .map(transaction -> {
                        Map<String, Object> transactionMap = new HashMap<>();
                        transactionMap.put("id", transaction.getId());
                        transactionMap.put("type", transaction.getTypeOperation());
                        transactionMap.put("amount", transaction.getMontant());
                        transactionMap.put("description", transaction.getDescription());
                        transactionMap.put("date", transaction.getDateCreation());
                        transactionMap.put("status", transaction.getStatut());
                        transactionMap.put("balanceAfter", transaction.getSoldeApresOperation());
                        return transactionMap;
                    })
                    .collect(java.util.stream.Collectors.toList());

            System.out.println("✅ [WalletService] " + history.size() + " transaction(s) dans l'historique");
            return history;
        } catch (Exception e) {
            System.err.println("❌ [WalletService] Erreur lors de la récupération de l'historique: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération de l'historique");
        }
    }

    @Override
    public Object getWalletStats(Long idClient) {
        System.out.println("📈 [WalletService] Récupération des statistiques pour le client: " + idClient);

        try {
            Double totalDeposits = transactionService.getTotalDeposits(idClient);
            Double totalWithdrawals = transactionService.getTotalWithdrawals(idClient);
            Double currentBalance = getSoldByIdClient(idClient);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDeposits", totalDeposits);
            stats.put("totalWithdrawals", totalWithdrawals);
            stats.put("currentBalance", currentBalance);
            stats.put("netFlow", totalDeposits - totalWithdrawals);
            stats.put("transactionCount", transactionService.getClientTransactions(idClient).size());

            System.out.println("✅ [WalletService] Statistiques récupérées avec succès");
            return stats;
        } catch (Exception e) {
            System.err.println("❌ [WalletService] Erreur lors de la récupération des statistiques: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des statistiques");
        }
    }

    /**
     * Convertir l'entité Wallet en DTO WalletResponse
     */
    private WalletResponse convertToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getIdClient(),
                wallet.getSold()
        );
    }

    /**
     * Formater le solde pour l'affichage
     */
    private String formatSold(Double sold) {
        return String.format("%.3f", sold);
    }
}