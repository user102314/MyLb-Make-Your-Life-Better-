package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.Model.OwnerPO;
import MyLb.BackEnd.Repository.OwnerPORepository;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.OwnerPOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.NoSuchElementException; // ⬅️ IMPORT NÉCESSAIRE

@Service
public class OwnerPOServiceImpl implements OwnerPOService {

    private final OwnerPORepository ownerPORepository;
    private final ClientService clientService;

    @Autowired
    public OwnerPOServiceImpl(OwnerPORepository ownerPORepository, ClientService clientService) {
        this.ownerPORepository = ownerPORepository;
        this.clientService = clientService;
    }

    @Override
    public OwnerPO createOwnerProfile(Long clientId, OwnerPO ownerDetails) {
        // 🚨 CORRECTION : Utilisation de orElseThrow pour déballer l'Optional.
        // Cela extrait le Client s'il est présent, ou lance une exception s'il est manquant.
        Client client = clientService.getClientById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé avec l'ID: " + clientId));

        OwnerPO newOwner = new OwnerPO();
        newOwner.setClientId(clientId);
        newOwner.setCinNumber(ownerDetails.getCinNumber());
        newOwner.setClient(client);

        // Mettre à jour le rôle du client
        client.setRole("PO");
        clientService.saveClient(client); // Utilise l'objet 'client' déballé

        return ownerPORepository.save(newOwner);
    }

    @Override
    public Optional<OwnerPO> getOwnerPOByClientId(Long clientId) {
        return ownerPORepository.findById(clientId);
    }

    @Override
    public Optional<OwnerPO> getOwnerPOByCompanyId(Long companyId) {
        // TO DO: Implémenter la méthode findByCompanyId dans OwnerPORepository
        return Optional.empty();
    }

    @Override
    public OwnerPO updateOwnerCIN(Long clientId, String newCinNumber) {
        OwnerPO owner = getOwnerPOByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Profil OwnerPO non trouvé pour le Client ID: " + clientId));

        owner.setCinNumber(newCinNumber);
        return ownerPORepository.save(owner);
    }

    @Override
    public void deleteOwnerProfile(Long clientId) {
        // 🚨 CORRECTION : Utilisation de orElse pour gérer l'Optional dans la suppression.
        // Ici, nous utilisons orElse(null) ou orElseThrow, mais l'approche la plus sûre est orElseThrow,
        // car si le client n'existe pas, il ne peut pas être mis à jour.

        ownerPORepository.deleteById(clientId);

        // Récupérer le Client pour changer son rôle
        Optional<Client> clientOptional = clientService.getClientById(clientId);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setRole("CIVIL");
            clientService.saveClient(client);
        }

        // NOTE: Si vous voulez le même comportement d'orElseThrow que les autres méthodes,
        // vous devriez l'appliquer ici aussi. Pour la suppression, l'approche 'isPresent()' est souvent préférée.
    }
}