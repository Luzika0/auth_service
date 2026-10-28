package cam_sound.auth_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cam_sound.auth_service.constant.Constant;
import cam_sound.auth_service.constant.ErrorCode;
import cam_sound.auth_service.handler.AppException;
import cam_sound.auth_service.helper.CustomUserDetailsServiceHelper;
import cam_sound.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userNameOrEmail) throws UsernameNotFoundException {
        final var user = userRepository.findByUserNameOrEmail(
                userNameOrEmail,
                userNameOrEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, userNameOrEmail + Constant.NOT_FOUND));
        return new CustomUserDetailsServiceHelper(user);

    }

    public UserDetails loadUserById(Long userId) {

        final var user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.USER_NOT_FOUND,
                        userId + Constant.NOT_FOUND));

        return new CustomUserDetailsServiceHelper(user);
    }

}
