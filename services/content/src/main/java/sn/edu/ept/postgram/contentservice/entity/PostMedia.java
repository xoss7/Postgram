package sn.edu.ept.postgram.contentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import sn.edu.ept.postgram.contentservice.model.PostMediaType;

import java.util.UUID;

@Entity
@Table(name = "post_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private PostMediaType mediaType;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}