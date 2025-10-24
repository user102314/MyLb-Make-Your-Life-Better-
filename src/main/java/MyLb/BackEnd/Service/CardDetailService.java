package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.CardDetail;
import java.util.Optional;

public interface CardDetailService {

    // Ajouter ou mettre à jour un détail de carte pour un Investor (Création/Mise à jour)
    CardDetail saveOrUpdateCardDetail(Long investorId, CardDetail cardDetails);

    // Lire
    Optional<CardDetail> getCardDetailByInvestorId(Long investorId);

    // Suppression
    void deleteCardDetail(Long cardDetailId);

    // Logique métier spécifique
    boolean isValidCardNumber(String cardNumber);
}