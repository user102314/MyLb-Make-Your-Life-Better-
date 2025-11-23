package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByIdClient(Long idClient);

    Optional<Card> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    List<Card> findByIdClientAndIsActiveTrue(Long idClient);

    @Query("SELECT c.sold FROM Card c WHERE c.id = :cardId AND c.isActive = true")
    Optional<Double> findSoldById(@Param("cardId") Long cardId);

    @Query("SELECT c.sold FROM Card c WHERE c.cardNumber = :cardNumber AND c.isActive = true")
    Optional<Double> findSoldByCardNumber(@Param("cardNumber") String cardNumber);
}