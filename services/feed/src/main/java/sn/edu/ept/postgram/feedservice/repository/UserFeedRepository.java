package sn.edu.ept.postgram.feedservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.edu.ept.postgram.feedservice.model.UserEntity;

import java.util.UUID;

public interface UserFeedRepository extends JpaRepository<UserEntity, UUID> {
}
