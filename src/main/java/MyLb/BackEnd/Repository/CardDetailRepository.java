package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.CardDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CardDetailRepository extends JpaRepository<CardDetail, Long> {

    // Trouver le détail de carte lié à un Investor
    Optional<CardDetail> findByInvestorClientId(Long investorId);
}