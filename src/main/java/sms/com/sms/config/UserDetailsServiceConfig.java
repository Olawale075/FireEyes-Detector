package sms.com.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sms.com.sms.repository.UsersRepository;
import sms.com.sms.model.Users;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public UserDetailsService userDetailsService(UsersRepository repository) {
        return username -> {
            Users user = repository.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String role = user.getRole().name().startsWith("ROLE_") ?
                    user.getRole().name() : "ROLE_" + user.getRole().name();

            return User.builder()
                    .username(user.getPhonenumber())
                    .password(user.getPassword())
                    .authorities(new SimpleGrantedAuthority(role))
                    .build();
        };
    }
}
