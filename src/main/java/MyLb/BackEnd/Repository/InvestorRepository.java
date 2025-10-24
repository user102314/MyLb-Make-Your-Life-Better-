package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;

// L'ID utilisé ici (Long) est le clientId de l'Investor
public interface InvestorRepository extends JpaRepository<Investor, Long> {

    // On pourrait ajouter d'autres méthodes de recherche si nécessaire,
    // par exemple pour les rapports :
    // List<Investor> findByGradeType(String gradeType);
}