package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.dto.ClientUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Long authenticate(String email, String password);
    boolean isEmailUnique(String email);
    Optional<Client> getClientById(Long clientId); // ⬅️ Doit être implémentée
    Client saveClient(Client client); // Création/Mise à jour (save)
    List<Client> getAllClients(); // Lecture (all)
    void deleteClient(Long clientId); // Suppression (delete)
    Client updateClient(Long clientId, Client clientDetails);
    Client updateClientProfile(Long userId, ClientUpdateRequest updateRequest);
}