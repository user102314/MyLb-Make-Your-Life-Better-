package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.ClientSecurity;
import MyLb.BackEnd.Repository.ClientSecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class ClientSecurityService {

    private final ClientSecurityRepository clientSecurityRepository;
    // ❌ DÉPENDANCE SUPPRIMÉE : private final ClientService clientService;

    @Autowired
    public ClientSecurityService(ClientSecurityRepository clientSecurityRepository) {
        this.clientSecurityRepository = clientSecurityRepository;
        // ❌ Retrait de l'initialisation de clientService
    }

    // ----------------------------------------------------
    // Méthodes de Gestion du Secret et du Statut 2FA
    // ----------------------------------------------------

    /**
     * Met à jour l'enregistrement de sécurité et stocke le secret 2FA.
     */
    @Transactional
    public void saveGoogleAuthSecret(Long clientId, String secretKey) {
        // La recherche par ID du client est possible grâce à @MapsId dans l'entité
        ClientSecurity security = clientSecurityRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Erreur système: Enregistrement ClientSecurity non trouvé pour l'ID: " + clientId));

        security.setGoogleAuthSecret(secretKey);
        security.setIs2FaEnabled(false);

        clientSecurityRepository.save(security);
    }

    /**
     * Récupère la clé secrète 2FA.
     */
    public Optional<String> getGoogleAuthSecret(Long clientId) {
        return clientSecurityRepository.findById(clientId)
                .map(ClientSecurity::getGoogleAuthSecret);
    }

    /**
     * Met à jour le statut d'activation du 2FA après validation réussie.
     */
    @Transactional
    public void set2FaEnabled(Long clientId, boolean enabled) {
        clientSecurityRepository.findById(clientId).ifPresent(security -> {
            security.setIs2FaEnabled(enabled);
            clientSecurityRepository.save(security);
        });
    }

    /**
     * Vérifie si la double authentification (2FA) est activée pour un client.
     */
    public boolean is2FaEnabled(Long clientId) {
        Optional<ClientSecurity> securityOpt = clientSecurityRepository.findById(clientId);

        // Retourne true si l'entité existe ET si is2FaEnabled est true
        return securityOpt.isPresent() && securityOpt.get().isIs2FaEnabled();
    }
}