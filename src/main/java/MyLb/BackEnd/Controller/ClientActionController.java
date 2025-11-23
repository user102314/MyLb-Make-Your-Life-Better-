// src/main/java/MyLb/BackEnd/Controller/ClientActionController.java

package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.ClientAction;
import MyLb.BackEnd.Service.ClientActionService;
import MyLb.BackEnd.Service.ClientService; // Pour l'authentification/ID
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actions")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class ClientActionController {

    private final ClientActionService actionService;
    private final ClientService clientService; // Utilisé pour récupérer l'ID client

    @Autowired
    public ClientActionController(ClientActionService actionService, ClientService clientService) {
        this.actionService = actionService;
        this.clientService = clientService;
    }

    private Long getUserId(HttpSession session) {
        // 🚨 IMPORTANT : Adapter la récupération de l'ID utilisateur selon votre session ou JWT
        return (Long) session.getAttribute("USER_ID");
    }

    /**
     * GET /api/actions/history
     * Récupère l'historique complet des actions de l'utilisateur authentifié.
     */
    @GetMapping("/history")
    public ResponseEntity<?> getMyActionHistory(HttpSession session) {
        Long userId = getUserId(session);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Utilisateur non authentifié."));
        }

        List<ClientAction> actions = actionService.getClientHistory(userId);

        // Nous retournons la liste des actions
        return ResponseEntity.ok(actions);
    }

    // Vous pouvez ajouter un endpoint pour filtrer par type si nécessaire.
    // Ex: /api/actions/alerts
}