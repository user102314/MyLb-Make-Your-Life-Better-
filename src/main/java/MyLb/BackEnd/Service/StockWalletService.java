// MyLb.BackEnd.Service.StockWalletService.java
package MyLb.BackEnd.Service;

import MyLb.BackEnd.dto.StockWalletResponse;
import MyLb.BackEnd.dto.CreateStockWalletRequest;
import MyLb.BackEnd.dto.UpdateStockWalletRequest;
import MyLb.BackEnd.dto.StockWalletStats;

import java.util.List;

public interface StockWalletService {

    // Ajouter un stock au wallet
    StockWalletResponse addStockToWallet(CreateStockWalletRequest request);

    // Récupérer tous les stocks d'un client
    List<StockWalletResponse> getClientStocks(Long idClient);

    // Récupérer un stock spécifique
    StockWalletResponse getStockWalletById(Long id, Long idClient);

    // Mettre à jour un stock (quantité ou prix)
    StockWalletResponse updateStockWallet(Long id, Long idClient, UpdateStockWalletRequest request);

    // Supprimer un stock du wallet
    void deleteStockFromWallet(Long id, Long idClient);

    // Calculer le total investi par client
    Double getTotalInvestment(Long idClient);

    // Récupérer les statistiques du wallet - CORRIGÉ: retourne StockWalletStats
    StockWalletStats getWalletStats(Long idClient);
}