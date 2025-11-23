package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.EtatFinance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtatFinanceRepository extends JpaRepository<EtatFinance, Long> {
    // Pas besoin d'ajouter findByCompanyId car companyId EST déjà la clé primaire (@Id)
    // findById(companyId) fonctionne directement !
}