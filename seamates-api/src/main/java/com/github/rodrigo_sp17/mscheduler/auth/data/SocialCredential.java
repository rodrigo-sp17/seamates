package com.github.rodrigo_sp17.mscheduler.auth.data;

import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import lombok.Data;

import javax.persistence.*;

@Entity
@NamedEntityGraph(name = "SocialCredential.detail",
        attributeNodes = @NamedAttributeNode("socialUser"))
@Data
public class SocialCredential {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    private AppUser socialUser;
    private String socialId;
    private String registrationId;
}
