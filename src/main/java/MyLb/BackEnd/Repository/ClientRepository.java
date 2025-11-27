package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Client c WHERE c.email = :email AND c.password = :password")
    Optional<Client> findByEmailAndPassword(String email, String password);
    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.selfDetail")
    List<Client> findAllWithSelfDetail();
    Long countByIsVerifiedTrue();
    List<Client> findByIsVerifiedTrue();    Optional<Client> findByEmail(String email);
}