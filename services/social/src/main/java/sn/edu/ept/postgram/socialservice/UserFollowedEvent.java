package sn.edu.ept.postgram.socialservice;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFollowedEvent {
    private UUID followerId;
    private UUID followeeId;
}