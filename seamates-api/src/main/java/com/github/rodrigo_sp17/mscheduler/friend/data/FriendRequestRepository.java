package com.github.rodrigo_sp17.mscheduler.friend.data;

import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("select fr from FriendRequest fr where fr.source.userInfo.username = :sourceName " +
            "and fr.target.userInfo.username = :targetName")
    FriendRequest findBySourceUsernameAndTargetUsername(String sourceName, String targetName);

    @Query("select fr from FriendRequest fr where fr.source.userInfo.username = :username " +
            "or fr.target.userInfo.username = :username")
    List<FriendRequest> findRequestsContaining(String username);

    @Query("select fr from FriendRequest fr where fr.source.userInfo.username = :username " +
            "and fr.id = :id")
    FriendRequest findByIdAndSourceUsername(Long id, String username);

    @Transactional
    @Modifying
    @Query("delete from FriendRequest fr where fr.source.userId = :id " +
            "or fr.target.userId = :id")
    void deleteAllById(Long id);
}
