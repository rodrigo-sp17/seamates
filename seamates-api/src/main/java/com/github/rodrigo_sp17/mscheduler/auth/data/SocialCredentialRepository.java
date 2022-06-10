package com.github.rodrigo_sp17.mscheduler.auth.data;

import com.github.rodrigo_sp17.mscheduler.auth.data.SocialCredential;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialCredentialRepository extends JpaRepository<SocialCredential, Long> {
    @EntityGraph(value = "SocialCredential.detail", type = EntityGraph.EntityGraphType.FETCH)
    Optional<SocialCredential> findBySocialIdAndRegistrationId(String socialId, String registrationId);
}
