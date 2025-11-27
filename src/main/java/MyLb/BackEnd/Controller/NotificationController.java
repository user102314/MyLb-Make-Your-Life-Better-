package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Service.NotificationService;
import MyLb.BackEnd.dto.NotificationDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Récupérer l'ID du client depuis la session
     * Cette version gère plusieurs formats possibles
     */
    private Long getClientIdFromSession(HttpSession session) {
        // Option 1 : ID directement stocké

        Long idClient = (Long) session.getAttribute("USER_ID");


        // Debug : afficher tous les attributs de session
        System.out.println("=== DEBUG SESSION NOTIFICATIONS ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Session est nouvelle ? " + session.isNew());
        System.out.println("Attributs de session:");
        java.util.Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attrName = attributeNames.nextElement();
            Object attrValue = session.getAttribute(attrName);
            System.out.println("  - " + attrName + " = " + attrValue);
            if (attrValue != null) {
                System.out.println("    Type: " + attrValue.getClass().getName());
            }
        }
        System.out.println("ID Client trouvé : " + idClient);
        System.out.println("===================================");

        if (idClient == null) {
            throw new RuntimeException("Session expirée ou utilisateur non authentifié");
        }
        return idClient;
    }

    /**
     * GET /api/notifications
     * Récupérer toutes les notifications du client connecté
     * la  notification  dans  ce  cadre le  ici  ede   mieux  comprendre  la situation  de  la  main  de  controle  de   le cas  de  echoueé  la configuration  de  la user  connnecteé   dans  ce  cas  la tu  vas testeé  si  le  user a  a  le id connecte é  ou  non
     */
    @GetMapping
    public ResponseEntity<?> getAllNotifications(HttpSession session) {
        try {
            Long idClient = getClientIdFromSession(session);
            List<NotificationDTO> notifications = notificationService.getAllNotificationsByClient(idClient);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            System.err.println("Erreur d'authentification : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des notifications"));
        }
    }

    /**
     * GET /api/notifications/nombre-non-lues
     * Compter le nombre de notifications non lues
     */
    @GetMapping("/nombre-non-lues")
    public ResponseEntity<?> getNombreNonLues(HttpSession session) {
        try {
            Long idClient = getClientIdFromSession(session);
            Long count = notificationService.countNonLues(idClient);

            Map<String, Long> response = new HashMap<>();
            response.put("nombreNonLues", count);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du comptage des notifications"));
        }
    }

    /**
     * PUT /api/notifications/{id}/marquer-lue
     * Marquer une notification comme lue
     */
    @PutMapping("/{id}/marquer-lue")
    public ResponseEntity<?> marquerCommeLue(@PathVariable("id") Long idNotification, HttpSession session) {
        try {
            Long idClient = getClientIdFromSession(session);
            NotificationDTO notification = notificationService.marquerCommeLue(idNotification, idClient);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du marquage de la notification"));
        }
    }

    /**
     * PUT /api/notifications/marquer-toutes-lues
     * Marquer toutes les notifications non lues comme lues
     */
    @PutMapping("/marquer-toutes-lues")
    public ResponseEntity<?> marquerToutesCommeLues(HttpSession session) {
        try {
            Long idClient = getClientIdFromSession(session);
            Map<String, Object> result = notificationService.marquerToutesCommeLues(idClient);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du marquage global"));
        }
    }

    /**
     * DELETE /api/notifications/{id}
     * Supprimer une notification spécifique
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerNotification(@PathVariable("id") Long idNotification, HttpSession session) {
        try {
            Long idClient = getClientIdFromSession(session);
            Map<String, Object> result = notificationService.supprimerNotification(idNotification, idClient);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression"));
        }
    }

    /**
     * DELETE /api/notifications/nettoyer
     * Nettoyer les notifications anciennes (plus de 30 jours)
     */
    @DeleteMapping("/nettoyer")
    public ResponseEntity<?> nettoyerNotificationsAnciennes(HttpSession session) {
        try {
            Long idClient = getClientIdFromSession(session);
            Map<String, Object> result = notificationService.nettoyerNotificationsAnciennes(idClient);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du nettoyage"));
        }
    }
}