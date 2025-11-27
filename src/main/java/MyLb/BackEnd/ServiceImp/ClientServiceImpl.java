package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.SelfDetail;
import MyLb.BackEnd.Model.Estnum.ActionType;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Repository.ClientRepository;
import MyLb.BackEnd.Repository.ClientSecurityRepository;
import MyLb.BackEnd.Service.ClientActionService;
import MyLb.BackEnd.Service.ClientSecurityService;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.GoogleAuthService;
import MyLb.BackEnd.Service.WalletService; // 🆕 IMPORT AJOUTÉ
import MyLb.BackEnd.dto.ClientUpdateRequest;
import MyLb.BackEnd.dto.PasswordChangeRequest;
import MyLb.BackEnd.dto.UserWithDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientSecurityRepository clientSecurityRepository;
    private final ClientSecurityService clientSecurityService;
    private final GoogleAuthService googleAuthService;
    private final ClientActionService clientActionService;
    private final WalletService walletService; // 🆕 DÉCLARATION AJOUTÉE

    @Autowired
    public ClientServiceImpl(
            ClientRepository clientRepository,
            ClientSecurityRepository clientSecurityRepository,
            ClientSecurityService clientSecurityService,
            GoogleAuthService googleAuthService,
            ClientActionService clientActionService,
            WalletService walletService // 🆕 INJECTION AJOUTÉE
    ) {
        this.clientRepository = clientRepository;
        this.clientSecurityRepository = clientSecurityRepository;
        this.clientSecurityService = clientSecurityService;
        this.googleAuthService = googleAuthService;
        this.clientActionService = clientActionService;
        this.walletService = walletService; // 🆕 INITIALISATION AJOUTÉE
    }

    @Override
    public Long authenticate(String email, String password) {
        Optional<Client> clientOpt = clientRepository.findByEmail(email);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();

            if (client.getPassword().equals(password)) {
                clientActionService.logAction(client.getClientId(), ActionType.LOGIN_SUCCESS, "Connexion réussie.");
                return client.getClientId();
            } else {
                clientActionService.logAction(client.getClientId(), ActionType.SECURITY_ALERT, "Tentative de connexion échouée (Mot de passe incorrect).");
                return null;
            }
        }

        return null;
    }

    @Override
    @Transactional
    public Client saveClientWithWallet(Client client) {
        System.out.println("💾 [ClientService] Sauvegarde du client avec création automatique du wallet");

        // 1. Sauvegarder le client
        Client savedClient = clientRepository.save(client);
        System.out.println("✅ [ClientService] Client sauvegardé avec ID: " + savedClient.getClientId());

        // 2. Créer le wallet automatiquement
        try {
            walletService.createWalletIfNotExists(savedClient.getClientId());
            System.out.println("💰 [ClientService] Wallet créé avec succès pour le client ID: " + savedClient.getClientId());
        } catch (Exception e) {
            System.err.println("❌ [ClientService] Erreur lors de la création du wallet: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la création du wallet", e);
        }

        return savedClient;
    }

    @Override
    public boolean isEmailUnique(String email) {
        return clientRepository.findByEmail(email).isEmpty();
    }

    @Override
    @Transactional
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Optional<Client> getClientById(Long clientId) {
        return clientRepository.findById(clientId);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public void deleteClient(Long clientId) {
        clientRepository.deleteById(clientId);
    }

    @Override
    public String getEmailById(Long clientId) {
        return getClientById(clientId)
                .map(Client::getEmail)
                .orElseThrow(() -> new NoSuchElementException("Client introuvable avec l'ID: " + clientId));
    }

    @Override
    @Transactional
    public Client updateClient(Long clientId, Client clientDetails) {
        Client existingClient = getClientById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé avec l'ID: " + clientId));

        existingClient.setFirstName(clientDetails.getFirstName());
        existingClient.setLastName(clientDetails.getLastName());
        existingClient.setBirthDate(clientDetails.getBirthDate());
        existingClient.setRole(clientDetails.getRole());
        existingClient.setIsVerified(clientDetails.getIsVerified());

        if (clientDetails.getPassword() != null && !clientDetails.getPassword().isEmpty()) {
            existingClient.setPassword(clientDetails.getPassword());
        }

        Client updatedClient = clientRepository.save(existingClient);
        clientActionService.logAction(clientId, ActionType.PROFILE_UPDATE, "Mise à jour d'informations générales.");

        return updatedClient;
    }

    @Override
    @Transactional
    public Client updateClientProfile(Long userId, ClientUpdateRequest updateRequest) {
        Client existingClient = clientRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé avec l'ID: " + userId));

        boolean isProfileChanged = false;

        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().equals(existingClient.getFirstName())) {
            existingClient.setFirstName(updateRequest.getFirstName());
            isProfileChanged = true;
        }
        if (updateRequest.getLastName() != null && !updateRequest.getLastName().equals(existingClient.getLastName())) {
            existingClient.setLastName(updateRequest.getLastName());
            isProfileChanged = true;
        }
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(existingClient.getEmail())) {
            existingClient.setEmail(updateRequest.getEmail());
            isProfileChanged = true;
        }

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            existingClient.setPassword(updateRequest.getPassword());
            isProfileChanged = true;
        }

        Client updatedClient = clientRepository.save(existingClient);

        if (isProfileChanged) {
            clientActionService.logAction(userId, ActionType.PROFILE_UPDATE, "Mise à jour des informations de profil (Nom/Email/etc.).");
        }

        return updatedClient;
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, PasswordChangeRequest request) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Client not found with ID: " + userId));

        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String authCode = request.getAuthCode();

        if (!client.getPassword().equals(currentPassword)) {
            clientActionService.logAction(userId, ActionType.SECURITY_ALERT, "Tentative de changement de mot de passe échouée (ancien mot de passe invalide).");
            return false;
        }

        Optional<String> secretOpt = clientSecurityService.getGoogleAuthSecret(userId);

        if (clientSecurityService.is2FaEnabled(userId)) {

            if (secretOpt.isEmpty() || authCode == null || authCode.length() != 6) {
                throw new SecurityException("Authentification à deux facteurs requise.");
            }

            try {
                int code = Integer.parseInt(authCode);
                boolean isVerified = googleAuthService.isCodeValid(secretOpt.get(), code);

                if (!isVerified) {
                    clientActionService.logAction(userId, ActionType.SECURITY_ALERT, "Tentative de changement de mot de passe échouée (Code 2FA invalide).");
                    throw new SecurityException("Code Google Authenticator invalide.");
                }
            } catch (NumberFormatException e) {
                throw new SecurityException("Le code 2FA doit être un nombre.");
            }
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return true;
        }

        client.setPassword(newPassword);
        clientRepository.save(client);

        clientActionService.logAction(userId, ActionType.PASSWORD_CHANGE, "Le mot de passe a été modifié avec succès.");

        return true;
    }
    @Override
    public List<UserWithDetailsDTO> getAllUsersWithDetails() {
        List<Client> clients = clientRepository.findAllWithSelfDetail();

        return clients.stream()
                .map(this::convertToUserWithDetailsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Client updateUserRole(Long userId, String role) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + userId));

        client.setRole(role);
        return clientRepository.save(client);
    }

    @Override
    public Client updateUserVerification(Long userId, Boolean isVerified) {
        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + userId));

        client.setIsVerified(isVerified);
        return clientRepository.save(client);
    }

    private UserWithDetailsDTO convertToUserWithDetailsDTO(Client client) {
        SelfDetail selfDetail = client.getSelfDetail();

        return new UserWithDetailsDTO(
                client.getClientId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getBirthDate(),
                client.getRole(),
                client.getIsVerified(),
                selfDetail != null ? selfDetail.getUsagePurpose() : null,
                selfDetail != null ? selfDetail.getCinNumber() : null,
                selfDetail != null ? selfDetail.getPhoneNumber() : null,
                selfDetail != null ? selfDetail.getAge() : null
        );
    }
}