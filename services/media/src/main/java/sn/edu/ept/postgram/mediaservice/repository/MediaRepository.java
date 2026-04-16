package sn.edu.ept.postgram.mediaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.mediaservice.entity.Media;
import sn.edu.ept.postgram.mediaservice.model.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findByUploaderIdOrderByCreatedAtDesc(UUID uploaderId);

    Optional<Media> findByUploaderIdAndType(UUID uploaderId, MediaType type);
}
