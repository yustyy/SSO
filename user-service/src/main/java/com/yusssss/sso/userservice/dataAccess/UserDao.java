package com.yusssss.sso.userservice.dataAccess;

import com.yusssss.sso.userservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<User, UUID> {
}
