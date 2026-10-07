package com.ttrp.manager.helper.setup;

import com.ttrp.manager.config.AdminSetupProperties;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AdminInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSetupProperties adminSetupProperties;

    @Override
    public void run(String... args) throws Exception {

        if(!userRepository.existsByUserRole(UserRole.ADMIN)){
            User AdminUser = User.builder()
                    .username(adminSetupProperties.adminUsername())
                    .email(adminSetupProperties.adminEmail())
                    .password(passwordEncoder.encode(adminSetupProperties.adminPassword()))
                    .userRole(UserRole.ADMIN)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            userRepository.save(AdminUser);
            System.out.println("Created Admin-Account: " + adminSetupProperties.adminUsername() + " : " + adminSetupProperties.adminEmail());
        }

    }
}
