package vn.ean.legia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import vn.ean.legia.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);

    User findByToken(String token);

    boolean existsByUsername(String username);

    boolean existsByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.token = :token WHERE u.username = :username")
    int updateTokenByUsername(@Param("username") String username, @Param("token") String token);
}
