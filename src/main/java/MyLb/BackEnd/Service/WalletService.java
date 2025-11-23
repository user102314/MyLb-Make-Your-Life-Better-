package MyLb.BackEnd.Service;

import MyLb.BackEnd.dto.WalletResponse;
import java.util.List;

public interface WalletService {

    /**
     * Recharger le solde d'un wallet par ID client
     */
    WalletResponse rechargerSold(Long idClient, Double montant);

    /**
     * Get solde by ID client
     */
    Double getSoldByIdClient(Long idClient);

    /**
     * Modifier le solde d'un wallet par ID client
     */
    WalletResponse modifySold(Long idClient, Double nouveauSold);

    /**
     * Créer un wallet pour un client s'il n'existe pas
     */
    WalletResponse createWalletIfNotExists(Long idClient);

    /**
     * Obtenir l'historique des transactions du wallet
     */
    List<Object> getWalletHistory(Long idClient); // 🆕 NOUVELLE MÉTHODE

    /**
     * Obtenir les statistiques du wallet
     */
    Object getWalletStats(Long idClient); // 🆕 NOUVELLE MÉTHODE
}