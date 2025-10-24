package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.OwnerPO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// L'ID utilisé ici (Long) est le clientId de l'OwnerPO
public interface OwnerPORepository extends JpaRepository<OwnerPO, Long> {

    // Méthode pour trouver l'OwnerPO à partir de l'ID de la Compagnie
    // (Cette méthode dépend de la relation dans le modèle OwnerPO/CompanyApplication)
    Optional<OwnerPO> findByCompanyApplicationCompanyApplicationId(Long companyId);
}