package com.witness.server.repository;

import com.witness.server.entity.SetLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetLogRepository extends JpaRepository<SetLog, Long> {
}
