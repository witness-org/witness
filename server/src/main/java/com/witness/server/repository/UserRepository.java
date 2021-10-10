package com.witness.server.repository;

import com.witness.server.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailEqualsIgnoreCase(String email);

  Optional<User> findByFirebaseIdEquals(String firebaseId);
}