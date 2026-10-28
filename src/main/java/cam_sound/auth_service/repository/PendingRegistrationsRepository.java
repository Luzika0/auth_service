package cam_sound.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cam_sound.auth_service.model.PendingRegistrations;

public interface PendingRegistrationsRepository extends JpaRepository<PendingRegistrations, Integer> {

    Optional<PendingRegistrations> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<PendingRegistrations> findByUserNameOrEmail(String emailOrUserName, String emailOrUserName2);

}
