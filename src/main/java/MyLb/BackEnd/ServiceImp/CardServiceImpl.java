package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Card;
import MyLb.BackEnd.Model.Entities.Transaction;
import MyLb.BackEnd.Repository.CardRepository;
import MyLb.BackEnd.Service.CardService;
import MyLb.BackEnd.Service.WalletService;
import MyLb.BackEnd.Service.TransactionService;
import MyLb.BackEnd.dto.CardResponse;
import MyLb.BackEnd.dto.WalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final TransactionService transactionService;
    private final WalletService walletService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository,
                           TransactionService transactionService,
                           WalletService walletService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    @Override
    public CardResponse addCard(Card card) {
        System.out.println("💳 [CardService] Début de l'ajout d'une nouvelle carte");
        System.out.println("   └─ Client: " + card.getIdClient());
        System.out.println("   └─ Numéro: " + maskCardNumber(card.getCardNumber()));
        System.out.println("   └─ Titulaire: " + card.getCardHolderName());

        try {
            // Vérifier si le numéro de carte existe déjà
            System.out.println("🔍 [CardService] Vérification de l'unicité du numéro...");
            if (cardRepository.existsByCardNumber(card.getCardNumber())) {
                System.err.println("❌ [CardService] Numéro de carte déjà existant");
                throw new RuntimeException("Une carte avec ce numéro existe déjà");
            }

            // Valider la date d'expiration
            System.out.println("📅 [CardService] Validation de la date d'expiration...");
            if (!isExpiryDateValid(card.getExpiryDate())) {
                System.err.println("❌ [CardService] Date d'expiration invalide: " + card.getExpiryDate());
                throw new RuntimeException("La carte a expiré ou la date est invalide");
            }

            // S'assurer que les valeurs par défaut sont bien définies
            if (card.getSold() == null) {
                card.setSold(0.0);
                System.out.println("💰 [CardService] Solde initialisé à 0");
            }
            if (card.getIsActive() == null) {
                card.setIsActive(true);
                System.out.println("✅ [CardService] Carte marquée comme active");
            }
            if (card.getDailyLimit() == null) {
                card.setDailyLimit(5000.0);
                System.out.println("📊 [CardService] Limite quotidienne définie à 5000 DT");
            }
            if (card.getCreatedAt() == null) {
                card.setCreatedAt(LocalDateTime.now());
                System.out.println("🕐 [CardService] Date de création définie");
            }

            System.out.println("💾 [CardService] Sauvegarde de la carte en base...");
            Card savedCard = cardRepository.save(card);

            // 🆕 ENREGISTRER LA TRANSACTION DE CRÉATION DE CARTE
            try {
                transactionService.saveTransaction(new Transaction(
                        card.getIdClient(),
                        "CARD_CREATION",
                        0.0,
                        "Création de carte - " + maskCardNumber(card.getCardNumber()) + " - " + card.getCardType()
                ));
                System.out.println("💾 [CardService] Transaction de création de carte enregistrée");
            } catch (Exception e) {
                System.err.println("⚠️ [CardService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
            }

            System.out.println("✅ [CardService] Carte ajoutée avec succès - ID: " + savedCard.getId());
            return convertToResponse(savedCard);

        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la carte: " + e.getMessage(), e);
        }
    }

    @Override
    public CardResponse addSold(Long cardId, Double montant) {
        System.out.println("💰 [CardService] Ajout de " + montant + " DT à la carte ID: " + cardId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Carte non trouvée"));

        if (!card.getIsActive()) {
            throw new RuntimeException("La carte est désactivée");
        }

        Double ancienSold = card.getSold();
        Double nouveauSold = card.getSold() + montant;
        card.setSold(nouveauSold);
        card.setLastUsed(LocalDateTime.now());

        Card updatedCard = cardRepository.save(card);

        // 🆕 ENREGISTRER LA TRANSACTION
        try {
            transactionService.recordDeposit(card.getIdClient(), montant,
                    "Ajout solde carte - Carte: " + maskCardNumber(card.getCardNumber()), nouveauSold);
            System.out.println("💾 [CardService] Transaction d'ajout solde carte enregistrée");
        } catch (Exception e) {
            System.err.println("⚠️ [CardService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
        }

        System.out.println("✅ [CardService] Solde ajouté avec succès. Nouveau solde: " + nouveauSold + " DT");

        return convertToResponse(updatedCard);
    }

    @Override
    public CardResponse withdrawSold(Long cardId, Double montant) {
        System.out.println("💰 [CardService] Retrait de " + montant + " DT de la carte ID: " + cardId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Carte non trouvée"));

        if (!card.getIsActive()) {
            throw new RuntimeException("La carte est désactivée");
        }

        if (card.getSold() < montant) {
            throw new RuntimeException("Solde insuffisant");
        }

        if (montant > card.getDailyLimit()) {
            throw new RuntimeException("Le montant dépasse la limite quotidienne de " + card.getDailyLimit() + " DT");
        }

        Double ancienSold = card.getSold();
        Double nouveauSold = card.getSold() - montant;
        card.setSold(nouveauSold);
        card.setLastUsed(LocalDateTime.now());

        Card updatedCard = cardRepository.save(card);

        // 🆕 ENREGISTRER LA TRANSACTION
        try {
            transactionService.recordWithdrawal(card.getIdClient(), montant,
                    "Retrait carte - Carte: " + maskCardNumber(card.getCardNumber()), nouveauSold);
            System.out.println("💾 [CardService] Transaction de retrait carte enregistrée");
        } catch (Exception e) {
            System.err.println("⚠️ [CardService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
        }

        System.out.println("✅ [CardService] Retrait effectué avec succès. Nouveau solde: " + nouveauSold + " DT");

        return convertToResponse(updatedCard);
    }

    @Override
    public CardResponse transferToWallet(Long cardId, Double montant) {
        System.out.println("💰 [CardService] Transfert de " + montant + " DT de la carte vers le wallet - Carte ID: " + cardId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Carte non trouvée"));

        if (!card.getIsActive()) {
            throw new RuntimeException("La carte est désactivée");
        }

        if (card.getSold() < montant) {
            throw new RuntimeException("Solde insuffisant sur la carte");
        }

        // 1. Retirer de la carte
        Double ancienSoldCarte = card.getSold();
        Double nouveauSoldCarte = card.getSold() - montant;
        card.setSold(nouveauSoldCarte);
        card.setLastUsed(LocalDateTime.now());

        Card cardUpdated = cardRepository.save(card);

        // 2. Ajouter au wallet
        WalletResponse walletResponse = walletService.rechargerSold(card.getIdClient(), montant);

        // 🆕 ENREGISTRER LA TRANSACTION
        try {
            transactionService.recordCardToWallet(card.getIdClient(), montant, cardId, nouveauSoldCarte);
            System.out.println("💾 [CardService] Transaction carte -> wallet enregistrée");
        } catch (Exception e) {
            System.err.println("⚠️ [CardService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
        }

        System.out.println("✅ [CardService] Transfert carte -> wallet effectué avec succès");

        return convertToResponse(cardUpdated);
    }

    @Override
    public CardResponse transferFromWallet(Long cardId, Double montant) {
        System.out.println("💰 [CardService] Transfert de " + montant + " DT du wallet vers la carte - Carte ID: " + cardId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Carte non trouvée"));

        if (!card.getIsActive()) {
            throw new RuntimeException("La carte est désactivée");
        }

        // 1. Vérifier le solde du wallet
        Double soldeWallet = walletService.getSoldByIdClient(card.getIdClient());
        if (soldeWallet < montant) {
            throw new RuntimeException("Solde insuffisant dans le wallet");
        }

        // 2. Retirer du wallet
        walletService.modifySold(card.getIdClient(), soldeWallet - montant);

        // 3. Ajouter à la carte
        Double ancienSoldCarte = card.getSold();
        Double nouveauSoldCarte = card.getSold() + montant;
        card.setSold(nouveauSoldCarte);
        card.setLastUsed(LocalDateTime.now());

        Card cardUpdated = cardRepository.save(card);

        // 🆕 ENREGISTRER LA TRANSACTION
        try {
            transactionService.recordWalletToCard(card.getIdClient(), montant, cardId, nouveauSoldCarte);
            System.out.println("💾 [CardService] Transaction wallet -> carte enregistrée");
        } catch (Exception e) {
            System.err.println("⚠️ [CardService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
        }

        System.out.println("✅ [CardService] Transfert wallet -> carte effectué avec succès");

        return convertToResponse(cardUpdated);
    }

    @Override
    public List<Object> getCardTransactions(Long cardId) {
        System.out.println("📊 [CardService] Récupération des transactions pour la carte: " + cardId);

        try {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new RuntimeException("Carte non trouvée"));

            // Récupérer toutes les transactions du client et filtrer celles liées à cette carte
            List<Transaction> allTransactions = transactionService.getClientTransactions(card.getIdClient());

            List<Object> cardTransactions = allTransactions.stream()
                    .filter(transaction ->
                            transaction.getIdCarte() != null &&
                                    transaction.getIdCarte().equals(cardId) ||
                                    transaction.getDescription().contains(maskCardNumber(card.getCardNumber()))
                    )
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
                    .collect(Collectors.toList());

            System.out.println("✅ [CardService] " + cardTransactions.size() + " transaction(s) trouvée(s) pour la carte");
            return cardTransactions;
        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de la récupération des transactions: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des transactions de la carte");
        }
    }

    // ========== MÉTHODES EXISTANTES (inchangées) ==========

    @Override
    public Double checkSold(Long cardId) {
        System.out.println("💰 [CardService] Vérification du solde pour la carte ID: " + cardId);

        try {
            Double solde = cardRepository.findSoldById(cardId)
                    .orElseThrow(() -> {
                        System.err.println("❌ [CardService] Carte non trouvée ou inactive: " + cardId);
                        return new RuntimeException("Carte non trouvée ou inactive");
                    });

            System.out.println("✅ [CardService] Solde trouvé: " + solde + " DT");
            return solde;
        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de la vérification du solde: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Double checkSoldByCardNumber(String cardNumber) {
        System.out.println("💰 [CardService] Vérification du solde par numéro: " + maskCardNumber(cardNumber));

        try {
            Double solde = cardRepository.findSoldByCardNumber(cardNumber)
                    .orElseThrow(() -> {
                        System.err.println("❌ [CardService] Carte non trouvée ou inactive: " + maskCardNumber(cardNumber));
                        return new RuntimeException("Carte non trouvée ou inactive");
                    });

            System.out.println("✅ [CardService] Solde trouvé: " + solde + " DT");
            return solde;
        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de la vérification du solde: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<CardResponse> getCardsByClient(Long idClient) {
        System.out.println("💳 [CardService] Récupération des cartes pour le client: " + idClient);

        try {
            List<Card> cards = cardRepository.findByIdClientAndIsActiveTrue(idClient);
            System.out.println("✅ [CardService] " + cards.size() + " carte(s) trouvée(s)");

            return cards.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de la récupération des cartes: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public CardResponse deactivateCard(Long cardId) {
        System.out.println("💳 [CardService] Désactivation de la carte ID: " + cardId);

        try {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> {
                        System.err.println("❌ [CardService] Carte non trouvée: " + cardId);
                        return new RuntimeException("Carte non trouvée");
                    });

            card.setIsActive(false);
            Card updatedCard = cardRepository.save(card);

            // 🆕 ENREGISTRER LA TRANSACTION DE DÉSACTIVATION
            try {
                transactionService.saveTransaction(new Transaction(
                        card.getIdClient(),
                        "CARD_DEACTIVATION",
                        0.0,
                        "Désactivation de carte - " + maskCardNumber(card.getCardNumber())
                ));
                System.out.println("💾 [CardService] Transaction de désactivation de carte enregistrée");
            } catch (Exception e) {
                System.err.println("⚠️ [CardService] Erreur lors de l'enregistrement de la transaction: " + e.getMessage());
            }

            System.out.println("✅ [CardService] Carte désactivée avec succès");
            return convertToResponse(updatedCard);
        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de la désactivation: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isCardValid(Long cardId) {
        try {
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new RuntimeException("Carte non trouvée"));

            boolean isValid = card.getIsActive() && isExpiryDateValid(card.getExpiryDate());
            System.out.println("🔍 [CardService] Validité de la carte " + cardId + ": " + isValid);

            return isValid;
        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de la validation: " + e.getMessage());
            return false;
        }
    }

    // ========== MÉTHODES PRIVÉES ==========

    private boolean isExpiryDateValid(String expiryDate) {
        try {
            if (expiryDate == null || !expiryDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
                return false;
            }

            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]) + 2000;

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiry = LocalDateTime.of(year, month, 1, 0, 0)
                    .plusMonths(1)
                    .minusDays(1);

            boolean isValid = now.isBefore(expiry);
            System.out.println("📅 [CardService] Date d'expiration " + expiryDate + " valide: " + isValid);

            return isValid;
        } catch (Exception e) {
            System.err.println("❌ [CardService] Erreur lors de la validation de la date: " + e.getMessage());
            return false;
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) return "****";
        return cardNumber.substring(0, 4) + "********" + cardNumber.substring(12);
    }

    private CardResponse convertToResponse(Card card) {
        return new CardResponse(
                card.getId(),
                maskCardNumber(card.getCardNumber()),
                card.getCardHolderName(),
                card.getExpiryDate(),
                card.getSold(),
                card.getIdClient(),
                card.getIsActive(),
                card.getCardType(),
                card.getDailyLimit()
        );
    }
}