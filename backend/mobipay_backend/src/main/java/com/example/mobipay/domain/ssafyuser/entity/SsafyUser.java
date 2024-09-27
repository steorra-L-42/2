package com.example.mobipay.domain.ssafyuser.entity;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.global.authentication.dto.SsafyUserResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ssafy_user")
public class SsafyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 40)
    private String userId;

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "user_key", nullable = false, unique = true, length = 60)
    private String userKey;

    @Column(name = "created", nullable = false)
    private OffsetDateTime created;

    @Column(name = "modified", nullable = false)
    private OffsetDateTime modified;

    @OneToOne(mappedBy = "ssafyUser", fetch = FetchType.LAZY)
    private MobiUser mobiUser;

    private SsafyUser(String userId, String username, String userKey, OffsetDateTime created, OffsetDateTime modified) {
        this.userId = userId;
        this.username = username;
        this.userKey = userKey;
        this.created = created;
        this.modified = modified;
    }

    public static <T extends SsafyUserResponse> SsafyUser of(T response) {
        return new SsafyUser(
                response.getUserId(),
                response.getUserName(),
                response.getUserKey(),
                response.getCreated(),
                response.getModified()
        );
    }

    public void changeMobiUser(MobiUser mobiUser) {
        this.mobiUser = mobiUser;
    }
}
