package edu.shtoiko.authservice.repository;

import edu.shtoiko.authservice.model.SecuredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<SecuredUser, Long> {

    @Query("SELECT u FROM SecuredUser u JOIN FETCH u.roles WHERE u.email = :email")
    SecuredUser findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);
}
