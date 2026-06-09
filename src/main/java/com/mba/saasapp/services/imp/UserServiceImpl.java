package com.mba.saasapp.services.imp;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.config.TenantContext;
import com.mba.saasapp.entities.User;


import com.mba.saasapp.entities.UserRole;
import com.mba.saasapp.entities.requests.UserRequest;
import com.mba.saasapp.entities.responses.UserResponse;
import com.mba.saasapp.exceptions.DuplicateResourceException;
import com.mba.saasapp.exceptions.InvalidRequestException;
import com.mba.saasapp.mappers.UserMapper;
import com.mba.saasapp.repositories.TenantRepository;
import com.mba.saasapp.repositories.UserRepository;
import com.mba.saasapp.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl      implements UserService {


      private  final UserMapper userMapper ;
      private final UserRepository  repository ;
     private final TenantRepository  tenantRepository ;
     private final PasswordEncoder passwordEncoder ;


        @Override
        public void createUser(final UserRequest request) {
            final String tenantId = TenantContext.getCurrentTenant();

            log.info("creating user for tenant: {}", tenantId);

            // validate if username exists
            if (this.repository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }

            // check if email exists
            if (this.repository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }

            // validate role (cannot be PLATFORM_ADMIN)
            if (request.getRole()==( UserRole.ROLE_PLATFORM_ADMIN)) {
                throw new InvalidRequestException("Role is required");
            }
            final User user = this.userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(request.getPassword())); // 👈 Hache le mot de passe
            user.setEnabled(true);
            if (user.getRole() == null) {
                user.setRole(com.mba.saasapp.entities.UserRole.ROLE_COMPANY_ADMIN);
            }

            this.repository.save(user);

            log.info("user created with success");

        }

    @Override
    public void updateUser(final String userId, final UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Updating user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // check if username is being changed and if it is already taken
        if (!user.getUsername().equals(request.getUsername()) && this.repository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email is being changed and if it is already taken
        if (!user.getEmail().equals(request.getEmail()) && this.repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        // validate role (cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            throw new InvalidRequestException("Role is required");
        }

        // update user details
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
       // user.getLastName((request.getLastName()) ;
        if (user.getRole() == null) {
            user.setRole(UserRole.ROLE_COMPANY_ADMIN); // Assigne automatiquement le rôle de gestionnaire de tenant si aucun rôle n'est spécifié
        }


        this.repository.save(user);
        log.info("used updated  successfully");
    }

    @Override
    public void deleteUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Deleting user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setDeleted(true);
        this.repository.save(user);
        log.info("User deleted successfully");
    }


    @Override
    public UserResponse getUserById(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        return this.userMapper.toResponse(user);
    }


    @Override
    public PageResponse<UserResponse> getAllUsers(final int page, final int size) {
        final String tenantId = TenantContext.getCurrentTenant();
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<User> userPage = this.repository.findAllByTenantId(tenantId, pageRequest);
        final Page<UserResponse> userResponses = userPage.map(this.userMapper::toResponse);
        return PageResponse.of(userResponses);
    }


    @Override
    public void enableUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(true);
        this.repository.save(user);
        log.info("User enabled successfully");
    }

    @Override
    public void disableUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(false);
        this.repository.save(user);
        log.info("User disabled successfully");
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return this.repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user was found with: " + username));
    }
}
