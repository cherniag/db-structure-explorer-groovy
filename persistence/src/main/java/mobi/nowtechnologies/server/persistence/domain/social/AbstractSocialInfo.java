package mobi.nowtechnologies.server.persistence.domain.social;

import javax.persistence.*;

/**
 * Created by oar on 2/10/14.
 */
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
@DiscriminatorColumn(name = "source")
@Table(name="abstractSocialInfo")
public abstract class AbstractSocialInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public Long getId() {
        return id;
    }
}

