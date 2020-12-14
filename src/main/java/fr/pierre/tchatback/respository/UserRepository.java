package fr.pierre.tchatback.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.pierre.tchatback.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByProviderId(String providerId);
    boolean existsByProviderId(String providerId);
    User findById(int id);
    
    @Query("SELECT u FROM User u") 
    List<User> findAll();
}
