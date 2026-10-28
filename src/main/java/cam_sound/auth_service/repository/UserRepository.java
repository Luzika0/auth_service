package cam_sound.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cam_sound.auth_service.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);

    Optional<User> findByUserNameOrEmail(String userNameOrEmail, String userNameOrEmail1);

    Optional<User> findByUserId(Long userId);

    
        
}
