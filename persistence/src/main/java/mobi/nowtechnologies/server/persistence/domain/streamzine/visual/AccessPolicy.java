package mobi.nowtechnologies.server.persistence.domain.streamzine.visual;

import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.user.GrantedToType;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
    private Set<GrantedToType> grantedToTypes = new HashSet<GrantedToType>();

    @OneToOne
    @JoinColumn(name = "block_id")
    private Block block;

    protected AccessPolicy() {
    }

    public static AccessPolicy enabledForVipOnly() {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.permission = Permission.RESTRICTED;
        accessPolicy.grantedToTypes.add(GrantedToType.LIMITED);
        accessPolicy.grantedToTypes.add(GrantedToType.FREETRIAL);
        return accessPolicy;
    }

    public static AccessPolicy hiddenForSubscribed() {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.permission = Permission.HIDDEN;
        accessPolicy.grantedToTypes.add(GrantedToType.SUBSCRIBED);
        return accessPolicy;
    }

    public Permission getPermission() {
        return permission;
    }

    public Set<GrantedToType> getGrantedToTypes() {
        return Collections.unmodifiableSet(grantedToTypes);
    }

    public long getId() {
        return id;
    }

    public AccessPolicy copy(Block block) {
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.permission = permission;
        accessPolicy.block = block;
        accessPolicy.grantedToTypes.addAll(grantedToTypes);
        return accessPolicy;
    }

    public boolean isVipMediaContent() {
        return this.equals(enabledForVipOnly());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccessPolicy that = (AccessPolicy) o;

        if (permission != that.permission) {
            return false;
        }
        if (!grantedToTypes.equals(that.grantedToTypes)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = permission != null ?
                     permission.hashCode() :
                     0;
        result = 31 * result + grantedToTypes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AccessPolicy{" +
               "grantedToTypes=" + grantedToTypes +
               ", permission=" + permission +
               ", id=" + id +
               '}';
    }
}
