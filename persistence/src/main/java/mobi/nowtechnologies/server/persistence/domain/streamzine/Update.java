package mobi.nowtechnologies.server.persistence.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.modelmapper.internal.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "sz_update")
public class Update {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name = "sz_update_users",
            joinColumns = @JoinColumn(name = "update_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "i")
    )
    private List<User> users = new ArrayList<User>();

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "update_id")
    private List<Block> blocks = new ArrayList<Block>();

    @OneToOne
    private Community community;

    protected Update() {
    }

    public Update(Date date, Community community) {
        Assert.notNull(date);
        Assert.notNull(community);
        Assert.isTrue(date.after(new Date()));
        this.date = date;
        this.community = community;
    }

    public void updateDate(Date incoming) {
        Assert.notNull(incoming);
        this.date = incoming;
    }

    public long getId() {
        return id;
    }

    public void addBlock(Block toAdd) {
        blocks.add(toAdd);
    }

    public List<Block> getBlocks() {
        return new ArrayList<Block>(blocks);
    }

    public List<Block> getIncludedBlocks() {
        List<Block> onlyIncluded = getBlocks();

        cleanUpNotIncluded(onlyIncluded);

        return onlyIncluded;
    }

    private void cleanUpNotIncluded(List<Block> onlyIncluded) {
        Iterator<Block> iterator = onlyIncluded.iterator();
        while(iterator.hasNext()) {
            Block next = iterator.next();
            if(!next.isIncluded()) {
                iterator.remove();
            }
        }
    }

    public Date getDate() {
        return date;
    }

    public boolean canEdit() {
        Date now = new Date();
        return date.after(now);
    }

    public void updateFrom(Update incoming) {
        Assert.isTrue(canEdit());

        copyUsers(incoming);
        copyBlocks(incoming);
    }

    public void cloneBlocks(Update lastOne) {
        Assert.isTrue(canEdit());

        if(lastOne != null) {
            copyBlocksForClone(lastOne);
        }
    }

    private void copyBlocksForClone(Update incoming) {
        copyBlocks(incoming);

        includeAllBlocks();
    }

    private void includeAllBlocks() {
        for (Block block : blocks) {
            block.include();
        }
    }

    public void copyBlocks(Update incoming) {
        this.blocks.clear();

        for (Block block : incoming.getBlocks()) {
            this.blocks.add(new Block(block));
        }
    }

    public void copyUsers(Update incoming){
        this.users.clear();
        this.users.addAll(incoming.users);
    }

    public List<User> getUsers() {
        return new ArrayList<User>(users);
    }

    public void addUser(User user){
        users.add(user);
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Community getCommunity() {
        return community;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("date", date)
                .append("block size", blocks.size())
                .toString();
    }
}
