package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.Card;
import MyLb.BackEnd.dto.CardResponse;
import java.util.List;

public interface CardService {

    /**
     * Ajouter une nouvelle carte
     */
    CardResponse addCard(Card card);

    /**
     * Vérifier le solde d'une carte
     */
    Double checkSold(Long cardId);

    /**
     * Vérifier le solde d'une carte par numéro
     */
    Double checkSoldByCardNumber(String cardNumber);

    /**
     * Ajouter du solde à une carte
     */
    CardResponse addSold(Long cardId, Double montant);

    /**
     * Retirer du solde d'une carte
     */
    CardResponse withdrawSold(Long cardId, Double montant);

    /**
     * Transférer du solde de la carte vers le wallet
     */
    CardResponse transferToWallet(Long cardId, Double montant); // 🆕 NOUVELLE MÉTHODE

    /**
     * Transférer du solde du wallet vers la carte
     */
    CardResponse transferFromWallet(Long cardId, Double montant); // 🆕 NOUVELLE MÉTHODE

    /**
     * Obtenir toutes les cartes d'un client
     */
    List<CardResponse> getCardsByClient(Long idClient);

    /**
     * Désactiver une carte
     */
    CardResponse deactivateCard(Long cardId);

    /**
     * Vérifier si une carte est valide
     */
    boolean isCardValid(Long cardId);

    /**
     * Obtenir l'historique des transactions d'une carte
     */
    List<Object> getCardTransactions(Long cardId); // 🆕 NOUVELLE MÉTHODE
}