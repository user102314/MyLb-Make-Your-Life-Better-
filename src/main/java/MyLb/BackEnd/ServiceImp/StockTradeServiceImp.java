// MyLb.BackEnd.ServiceImp.StockTradeServiceImp.java
package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Stock;
import MyLb.BackEnd.Model.Entities.StockWallet;
import MyLb.BackEnd.Model.Entities.Transaction;
import MyLb.BackEnd.Repository.StockRepository;
import MyLb.BackEnd.Service.*;
import MyLb.BackEnd.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StockTradeServiceImp implements StockTradeService {

    private final StockService stockService;
    private final StockWalletService stockWalletService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final StockRepository stockRepository;

    @Autowired
    public StockTradeServiceImp(StockService stockService,
                                StockWalletService stockWalletService,
                                WalletService walletService,
                                TransactionService transactionService,
                                StockRepository stockRepository) {
        this.stockService = stockService;
        this.stockWalletService = stockWalletService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.stockRepository = stockRepository;
    }

    @Override
    public StockTradeResponse buyStock(BuyStockRequest request) {
        try {
            System.out.println("🛒 [StockTradeService] Début d'achat de stock - Client: " + request.getIdClient() +
                    ", Stock: " + request.getIdStock() + ", Quantité: " + request.getQuantite());

            // 1. Vérifier si le stock existe
            Stock stock = stockRepository.findById(request.getIdStock())
                    .orElseThrow(() -> new RuntimeException("Stock non trouvé"));

            // 2. Vérifier la quantité disponible
            if (stock.getStockDisponible() < request.getQuantite()) {
                throw new RuntimeException("Quantité insuffisante. Disponible: " + stock.getStockDisponible());
            }

            // 3. Calculer le montant total
            Double montantTotal = stock.getPrixStock() * request.getQuantite();
            System.out.println("💰 [StockTradeService] Montant total: " + montantTotal + " DT");

            // 4. Vérifier le solde du client
            Double soldeClient = walletService.getSoldByIdClient(request.getIdClient());
            if (soldeClient < montantTotal) {
                throw new RuntimeException("Solde insuffisant. Solde: " + soldeClient + " DT, Nécessaire: " + montantTotal + " DT");
            }

            // 5. Débiter le wallet du client
            Double nouveauSolde = soldeClient - montantTotal;
            walletService.modifySold(request.getIdClient(), nouveauSolde);

            // 6. Mettre à jour le stock disponible
            Integer nouveauStockDisponible = stock.getStockDisponible() - request.getQuantite();
            stockService.modifierStockDisponible(request.getIdStock(), nouveauStockDisponible);

            // 7. Ajouter au stock wallet
            CreateStockWalletRequest walletRequest = new CreateStockWalletRequest(
                    request.getIdClient(),
                    request.getIdStock(),
                    stock.getNomStock(),
                    stock.getPrixStock(),
                    request.getQuantite()
            );
            StockWalletResponse stockWallet = stockWalletService.addStockToWallet(walletRequest);

            // 8. Enregistrer la transaction
            String description = "Achat de " + request.getQuantite() + " actions " + stock.getNomStock() + " à " + stock.getPrixStock() + " DT";
            transactionService.recordWithdrawal(request.getIdClient(), montantTotal, description, nouveauSolde);

            System.out.println("✅ [StockTradeService] Achat réussi - Montant: " + montantTotal + " DT, Nouveau solde: " + nouveauSolde + " DT");

            return new StockTradeResponse(true, "Achat réussi", montantTotal, nouveauSolde, stockWallet);

        } catch (Exception e) {
            System.err.println("❌ [StockTradeService] Erreur lors de l'achat: " + e.getMessage());
            return new StockTradeResponse(false, e.getMessage(), 0.0, 0.0, null);
        }
    }

    @Override
    public StockTradeResponse sellStock(SellStockRequest request) {
        try {
            System.out.println("💰 [StockTradeService] Début de vente de stock - Client: " + request.getIdClient() +
                    ", StockWallet: " + request.getIdStockWallet() + ", Quantité: " + request.getQuantite());

            // 1. Vérifier si le stock existe dans le wallet
            StockWalletResponse stockWallet = stockWalletService.getStockWalletById(request.getIdStockWallet(), request.getIdClient());

            // 2. Vérifier la quantité disponible dans le wallet
            if (stockWallet.getQuantite() < request.getQuantite()) {
                throw new RuntimeException("Quantité insuffisante dans votre wallet. Disponible: " + stockWallet.getQuantite());
            }

            // 3. Récupérer le prix actuel du stock
            Stock stock = stockRepository.findById(stockWallet.getIdStock())
                    .orElseThrow(() -> new RuntimeException("Stock non trouvé"));

            // 4. Calculer le montant total de la vente
            Double montantTotal = stock.getPrixStock() * request.getQuantite();
            System.out.println("💰 [StockTradeService] Montant de vente: " + montantTotal + " DT");

            // 5. Créditer le wallet du client
            Double soldeActuel = walletService.getSoldByIdClient(request.getIdClient());
            Double nouveauSolde = soldeActuel + montantTotal;
            walletService.modifySold(request.getIdClient(), nouveauSolde);

            // 6. Mettre à jour le stock disponible
            Integer nouveauStockDisponible = stock.getStockDisponible() + request.getQuantite();
            stockService.modifierStockDisponible(stockWallet.getIdStock(), nouveauStockDisponible);

            // 7. Gérer le stock wallet
            if (stockWallet.getQuantite().equals(request.getQuantite())) {
                // Vente totale - supprimer du wallet
                stockWalletService.deleteStockFromWallet(request.getIdStockWallet(), request.getIdClient());
            } else {
                // Vente partielle - mettre à jour la quantité
                UpdateStockWalletRequest updateRequest = new UpdateStockWalletRequest();
                updateRequest.setQuantite(stockWallet.getQuantite() - request.getQuantite());
                stockWalletService.updateStockWallet(request.getIdStockWallet(), request.getIdClient(), updateRequest);
            }

            // 8. Enregistrer la transaction
            String description = "Vente de " + request.getQuantite() + " actions " + stockWallet.getNomStock() + " à " + stock.getPrixStock() + " DT";
            transactionService.recordDeposit(request.getIdClient(), montantTotal, description, nouveauSolde);

            System.out.println("✅ [StockTradeService] Vente réussie - Montant: " + montantTotal + " DT, Nouveau solde: " + nouveauSolde + " DT");

            return new StockTradeResponse(true, "Vente réussie", montantTotal, nouveauSolde, stockWallet);

        } catch (Exception e) {
            System.err.println("❌ [StockTradeService] Erreur lors de la vente: " + e.getMessage());
            return new StockTradeResponse(false, e.getMessage(), 0.0, 0.0, null);
        }
    }
}