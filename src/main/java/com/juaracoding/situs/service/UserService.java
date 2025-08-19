package com.juaracoding.situs.service;

import com.juaracoding.situs.model.User;
import com.juaracoding.situs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_EDITOR"); // Default role
        }
        return userRepository.save(user);
    }

    @PostConstruct
    public void init() {
        createInitialUsers();
    }

    private void createInitialUsers() {
        // Create ROLE_ADMIN if not exists
        Optional<User> adminUser = userRepository.findByUsername("admin");
        if (adminUser.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Consider stronger password in production
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("Created admin user: admin");
        }

        // Create ROLE_EDITOR if not exists
        Optional<User> editorUser = userRepository.findByUsername("editor");
        if (editorUser.isEmpty()) {
            User editor = new User();
            editor.setUsername("editor");
            editor.setPassword(passwordEncoder.encode("editor123")); // Consider stronger password in production
            editor.setRole("ROLE_EDITOR");
            userRepository.save(editor);
            System.out.println("Created editor user: editor");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
