package com.appswave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appswave.model.enums.RoleEnum;
import com.appswave.model.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Role findByName(RoleEnum name);
}
