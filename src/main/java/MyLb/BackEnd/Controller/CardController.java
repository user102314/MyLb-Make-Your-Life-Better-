package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Card;
import MyLb.BackEnd.Service.CardService;
import MyLb.BackEnd.dto.CardOperationRequest;
import MyLb.BackEnd.dto.CardResponse;
import MyLb.BackEnd.dto.CreateCardRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Ajouter une nouvelle carte
     * POST /api/cards
     */
    @PostMapping
    public ResponseEntity<?> addCard(@RequestBody CreateCardRequest request) {
        System.out.println("🎯 ========== DEBUT AJOUT CARTE ==========");
        System.out.println("📥 [CardController] POST /api/cards - Données reçues:");
        System.out.println("   ├─ cardNumber: " + request.getCardNumber());
        System.out.println("   ├─ cardHolderName: " + request.getCardHolderName());
        System.out.println("   ├─ expiryDate: " + request.getExpiryDate());
        System.out.println("   ├─ cvv: " + request.getCvv());
        System.out.println("   ├─ idClient: " + request.getIdClient());
        System.out.println("   └─ cardType: " + request.getCardType());

        try {
            // Validation manuelle des données
            if (request.getCardNumber() == null || request.getCardNumber().length() != 16) {
                throw new RuntimeException("Numéro de carte invalide: doit contenir 16 chiffres");
            }
            if (request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty()) {
                throw new RuntimeException("Nom du titulaire requis");
            }
            if (request.getExpiryDate() == null || !request.getExpiryDate().matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
                throw new RuntimeException("Date d'expiration invalide (format MM/YY requis)");
            }
            if (request.getCvv() == null || request.getCvv().length() != 3) {
                throw new RuntimeException("CVV invalide: doit contenir 3 chiffres");
            }
            if (request.getIdClient() == null || request.getIdClient() <= 0) {
                throw new RuntimeException("ID client invalide");
            }
            if (request.getCardType() == null || !List.of("VISA", "MASTERCARD", "AMEX").contains(request.getCardType())) {
                throw new RuntimeException("Type de carte invalide");
            }

            System.out.println("✅ [CardController] Validation des données réussie");

            Card card = new Card(
                    request.getCardNumber(),
                    request.getCardHolderName(),
                    request.getExpiryDate(),
                    request.getCvv(),
                    request.getIdClient(),
                    request.getCardType()
            );

            System.out.println("🔄 [CardController] Création de l'entité Card...");

            CardResponse response = cardService.addCard(card);

            System.out.println("✅ [CardController] Carte créée avec succès - ID: " + response.getId());
            System.out.println("🎯 ========== FIN AJOUT CARTE SUCCES ==========");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("❌ [CardController] ERREUR LORS DE LA CRÉATION:");
            System.err.println("   └─ Message: " + e.getMessage());
            System.err.println("   └─ Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "Aucune"));
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("error", "Erreur lors de la création de la carte");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("path", "/api/cards");

            System.out.println("🎯 ========== FIN AJOUT CARTE ERREUR ==========");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Vérifier le solde d'une carte
     * GET /api/cards/{cardId}/solde
     */
    @GetMapping("/{cardId}/solde")
    public ResponseEntity<?> checkSold(@PathVariable Long cardId) {
        System.out.println("📥 [CardController] GET /api/cards/" + cardId + "/solde");

        try {
            Double solde = cardService.checkSold(cardId);
            Map<String, Double> response = new HashMap<>();
            response.put("solde", solde);

            System.out.println("✅ [CardController] Solde récupéré: " + solde + " DT");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [CardController] Erreur: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("error", "Erreur lors de la récupération du solde");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Ajouter du solde à une carte
     * PUT /api/cards/{cardId}/ajouter-solde
     */
    @PutMapping("/{cardId}/ajouter-solde")
    public ResponseEntity<?> addSold(
            @PathVariable Long cardId,
            @RequestBody CardOperationRequest request) {

        System.out.println("📥 [CardController] PUT /api/cards/" + cardId + "/ajouter-solde");
        System.out.println("   └─ Montant: " + request.getMontant() + " DT");

        try {
            CardResponse response = cardService.addSold(cardId, request.getMontant());
            System.out.println("✅ [CardController] Solde ajouté avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [CardController] Erreur: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("error", "Erreur lors de l'ajout de solde");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Retirer du solde d'une carte
     * PUT /api/cards/{cardId}/retirer-solde
     */
    @PutMapping("/{cardId}/retirer-solde")
    public ResponseEntity<?> withdrawSold(
            @PathVariable Long cardId,
            @RequestBody CardOperationRequest request) {

        System.out.println("📥 [CardController] PUT /api/cards/" + cardId + "/retirer-solde");
        System.out.println("   └─ Montant: " + request.getMontant() + " DT");

        try {
            CardResponse response = cardService.withdrawSold(cardId, request.getMontant());
            System.out.println("✅ [CardController] Retrait effectué avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [CardController] Erreur: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("error", "Erreur lors du retrait");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Obtenir les cartes d'un client
     * GET /api/cards/client/{idClient}
     */
    @GetMapping("/client/{idClient}")
    public ResponseEntity<?> getCardsByClient(@PathVariable Long idClient) {
        System.out.println("📥 [CardController] GET /api/cards/client/" + idClient);

        try {
            List<CardResponse> cards = cardService.getCardsByClient(idClient);
            System.out.println("✅ [CardController] " + cards.size() + " carte(s) trouvée(s)");
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            System.err.println("❌ [CardController] Erreur: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("error", "Erreur lors de la récupération des cartes");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Désactiver une carte
     * PUT /api/cards/{cardId}/desactiver
     */
    @PutMapping("/{cardId}/desactiver")
    public ResponseEntity<?> deactivateCard(@PathVariable Long cardId) {
        System.out.println("📥 [CardController] PUT /api/cards/" + cardId + "/desactiver");

        try {
            CardResponse response = cardService.deactivateCard(cardId);
            System.out.println("✅ [CardController] Carte désactivée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [CardController] Erreur: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("error", "Erreur lors de la désactivation");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Endpoint de santé
     * GET /api/cards/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        System.out.println("📥 [CardController] GET /api/cards/health");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "controller", "CardController",
                "message", "Card API est opérationnelle",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}