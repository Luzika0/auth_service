package cam_sound.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cam_sound.auth_service.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    void deleteAllByUserId(Integer userId);

    void deleteByToken(String refreshToken);

    RefreshToken findByUserId(Long userId);

    boolean existsByTokenAndRevokedTrue(String token);

    Optional<RefreshToken> findByToken(String refreshToken);

}
