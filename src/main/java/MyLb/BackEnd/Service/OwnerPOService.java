package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.OwnerPO;
import java.util.Optional;

public interface OwnerPOService {

    // Création d'un profil OwnerPO lié à un Client existant
    OwnerPO createOwnerProfile(Long clientId, OwnerPO ownerDetails);

    // Lire (Find by Client ID)
    Optional<OwnerPO> getOwnerPOByClientId(Long clientId);

    // Lire (Find by Company ID)
    Optional<OwnerPO> getOwnerPOByCompanyId(Long companyId);

    // Mettre à jour (Modification des détails du propriétaire)
    OwnerPO updateOwnerCIN(Long clientId, String newCinNumber);

    // Suppression
    void deleteOwnerProfile(Long clientId);
}