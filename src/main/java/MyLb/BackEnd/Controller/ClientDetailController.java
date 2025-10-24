package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client") // Base: /api/client
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class ClientDetailController {

    private final ClientService clientService;

    @Autowired
    public ClientDetailController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Endpoint unique : GET /api/client/details/{clientId}
     * Récupère les informations détaillées d'un client par son ID spécifique (pour admin/test).
     */
    @GetMapping("/details/{clientId}")
    public ResponseEntity<?> getClientDetailsById(@PathVariable Long clientId) {

        Optional<Client> clientOptional = clientService.getClientById(clientId);

        if (clientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client non trouvé avec l'ID: " + clientId);
        }

        Client client = clientOptional.get();

        // --- Préparation de la réponse ---

        // 1. Conversion de l'image en Base64
        String base64Image = null;
        if (client.getProfileImage() != null && client.getProfileImage().length > 0) {
            base64Image = Base64.getEncoder().encodeToString(client.getProfileImage());
        }

        // 2. Nettoyage de l'objet Client avant de l'ajouter à la réponse
        client.setPassword(null);
        client.setProfileImage(null); // On nullifie le champ byte[] pour ne pas le sérialiser en plus

        // 3. Construction de la Map de réponse
        Map<String, Object> response = new HashMap<>();
        response.put("client", client);
        response.put("profileImageBase64", base64Image);

        return ResponseEntity.ok(response);
    }

    // Assurez-vous qu'il n'y a AUCUN autre mapping ici pour éviter l'Ambiguous Mapping avec ClientController.
}