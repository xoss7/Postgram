package sn.edu.ept.postgram.socialservice;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowResponse {
    private UUID followerId;
    private UUID followeeId;
}