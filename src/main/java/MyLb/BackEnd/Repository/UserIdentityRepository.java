package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {
    @Query("SELECT ui FROM UserIdentity ui WHERE ui.iduser = :userId")
    Optional<UserIdentity> findByUserId(@Param("userId") Long userId);    Optional<UserIdentity> findByIduser(Long iduser);
}