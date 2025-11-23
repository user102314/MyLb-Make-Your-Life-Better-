// src/main/java/MyLb/BackEnd/Service/ClientActionService.java

package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Estnum.ActionType;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.ClientAction;
import MyLb.BackEnd.Repository.ClientActionRepository;
import MyLb.BackEnd.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClientActionService {

    private final ClientActionRepository actionRepository;
    private final ClientRepository clientRepository; // Nécessaire pour obtenir l'entité Client

    @Autowired
    public ClientActionService(ClientActionRepository actionRepository, ClientRepository clientRepository) {
        this.actionRepository = actionRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * 🚀 Enregistre une nouvelle action pour un client.
     */
    @Transactional
    public ClientAction logAction(Long clientId, ActionType type, String details) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé: " + clientId));

        ClientAction newAction = new ClientAction(type, client, details);
        return actionRepository.save(newAction);
    }

    /**
     * Récupère l'historique de toutes les actions d'un client.
     */
    public List<ClientAction> getClientHistory(Long clientId) {
        // Utilise la méthode personnalisée du Repository
        return actionRepository.findByClientClientIdOrderByActionDateDesc(clientId);
    }

    /**
     * Récupère l'historique d'un type d'action spécifique.
     */
    public List<ClientAction> getClientActionsByType(Long clientId, ActionType type) {
        return actionRepository.findByClientClientIdAndActionTypeOrderByActionDateDesc(clientId, type);
    }
}