package sn.edu.ept.postgram.socialservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowDto {
    private UUID followerId;
    private UUID followeeId;
}