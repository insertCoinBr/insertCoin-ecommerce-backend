package org.insertcoin.insertcoin_auth_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.insertcoin.insertcoin_auth_service.dtos.UserEmailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.insertcoin.insertcoin_auth_service.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    @Query("""
    SELECT new org.insertcoin.insertcoin_auth_service.dtos.UserEmailDTO(u.id, u.email)
    FROM UserEntity u
    JOIN u.roles r
    WHERE r.name IN :roles
    AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
""")
    Page<UserEmailDTO> findAllByRolesAndEmailContaining(
            @Param("roles") List<String> roles,
            @Param("email") String email,
            Pageable pageable
    );

}
