package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.dto.ClientUpdateRequest;
import MyLb.BackEnd.dto.PasswordChangeRequest; // 👈 NOUVEL IMPORT
import java.util.List;
import java.util.Optional;

public interface ClientService {

    Long authenticate(String email, String password);
    boolean isEmailUnique(String email);
    Optional<Client> getClientById(Long clientId);
    Client saveClient(Client client); // ⬅️ Méthode d'inscription/sauvegarde
    List<Client> getAllClients();
    void deleteClient(Long clientId);
    Client updateClient(Long clientId, Client clientDetails);
    Client updateClientProfile(Long userId, ClientUpdateRequest updateRequest);
    String getEmailById(Long clientId);
    Client saveClientWithWallet(Client client);

    boolean changePassword(Long userId, PasswordChangeRequest request); // 👈 NOUVELLE MÉTHODE
}