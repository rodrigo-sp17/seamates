package com.github.rodrigo_sp17.mscheduler.user.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    @Query("select user from AppUser user where user.userInfo.username = :username")
    AppUser findByUsername(String username);

    @Query("select user from AppUser user where user.userInfo.email = :email")
    AppUser findByEmail(String email);

    @Query("select user.userInfo.username from AppUser user")
    List<String> findUsernames();

}
