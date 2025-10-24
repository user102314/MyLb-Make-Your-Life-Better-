package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.Repository.ClientRepository;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.dto.ClientUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException; // ⬅️ CORRECTION: Import manquant ajouté

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // --- Méthodes d'Authentification / Vérification ---

    @Override
    public Long authenticate(String email, String password) {
        // Utilisation de la requête explicite du Repository
        Optional<Client> client = clientRepository.findByEmailAndPassword(email, password);
        // Retourne l'ID si trouvé, sinon null (cause de l'erreur 401)
        return client.map(Client::getClientId).orElse(null);
    }

    @Override
    public boolean isEmailUnique(String email) {
        return clientRepository.findByEmail(email).isEmpty();
    }

    // --- Méthodes CRUD ---

    // Création/Mise à jour (Save)
    @Override
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    // Lecture (Find by ID) - Reste Optional<Client> dans l'implémentation
    @Override
    public Optional<Client> getClientById(Long clientId) {
        return clientRepository.findById(clientId);
    }

    @Override
    public Client updateClientProfile(Long userId, ClientUpdateRequest updateRequest) {
        // 1. Trouver l'utilisateur existant et déballer l'Optional.
        // Si l'Optional est vide, lance une exception.
        Client existingClient = getClientById(userId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé avec l'ID: " + userId)); // ⬅️ CORRECTION

        // 2. Appliquer les mises à jour (vérifiez si le champ n'est pas null dans la requête)
        if (updateRequest.getFirstName() != null) {
            existingClient.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            existingClient.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            existingClient.setEmail(updateRequest.getEmail());
        }

        // 3. Gérer le changement de mot de passe (si fourni)
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            existingClient.setPassword(updateRequest.getPassword());
        }

        // 4. Sauvegarder l'entité mise à jour
        return clientRepository.save(existingClient);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Suppression (Delete)
    @Override
    public void deleteClient(Long clientId) {
        clientRepository.deleteById(clientId);
    }

    // Modification (Update)
    @Override
    public Client updateClient(Long clientId, Client clientDetails) {
        // 1. Trouver l'utilisateur existant et déballer l'Optional.
        Client existingClient = getClientById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé avec l'ID: " + clientId)); // ⬅️ CORRECTION

        // 2. Mise à jour des champs non-sensibles
        existingClient.setFirstName(clientDetails.getFirstName());
        existingClient.setLastName(clientDetails.getLastName());
        existingClient.setBirthDate(clientDetails.getBirthDate());
        existingClient.setRole(clientDetails.getRole());
        existingClient.setIsVerified(clientDetails.getIsVerified());

        if (clientDetails.getPassword() != null && !clientDetails.getPassword().isEmpty()) {
            existingClient.setPassword(clientDetails.getPassword());
        }

        return clientRepository.save(existingClient);
    }
}