package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import MyLb.BackEnd.Model.Estnum.CompanyStatus;
import java.util.List;
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByStatus(CompanyStatus status);
    List<Company> findAllByOwnerID(Long ownerID);
    Company findByOwnerID(Long ownerID);
}