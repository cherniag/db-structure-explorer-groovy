package mobi.nowtechnologies.server.persistence.domain.streamzine.visual;

import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sz_block_access_policy")
public class AccessPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Permission permission;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "sz_granted_to_types", joinColumns = @JoinColumn(name = "access_policy_id"))
    @Column(name = "granted_to")
    private Set<GrantedToType> userStatusTypes = new HashSet<GrantedToType>();

    @OneToOne
    @JoinColumn(name = "block_id")
    private Block block;

    protected AccessPolicy() {
    }

    public static AccessPolicy enabledForVipOnly() {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.permission = Permission.RESTRICTED;
        accessPolicy.userStatusTypes.add(GrantedToType.LIMITED);
        accessPolicy.userStatusTypes.add(GrantedToType.FREETRIAL);
        return accessPolicy;
    }

    public static AccessPolicy hiddenForSubscribed() {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.permission = Permission.HIDDEN;
        accessPolicy.userStatusTypes.add(GrantedToType.SUBSCRIBED);
        return accessPolicy;
    }

    public Permission getPermission() {
        return permission;
    }

    public Set<GrantedToType> getUserStatusTypes() {
        return Collections.unmodifiableSet(userStatusTypes);
    }

    public long getId() {
        return id;
    }

    public AccessPolicy copy(Block block) {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.permission = permission;
        accessPolicy.block = block;
        accessPolicy.userStatusTypes.addAll(userStatusTypes);
        return accessPolicy;
    }

    public boolean isVipMediaContent() {
        return this.equals(enabledForVipOnly());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessPolicy that = (AccessPolicy) o;

        if (permission != that.permission) return false;
        if (!userStatusTypes.equals(that.userStatusTypes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = permission != null ? permission.hashCode() : 0;
        result = 31 * result + userStatusTypes.hashCode();
        return result;
    }
}
