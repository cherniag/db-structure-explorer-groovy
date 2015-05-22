package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "tb_mediaLogTypes")
public class MediaLogType implements Serializable {
    public static final String DOWNLOAD_ORIGINAL = "DOWNLOAD_ORIGINAL";

    private static final long serialVersionUID = 1L;
    private int i;
    private String name;

    public MediaLogType() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    @Column(name = "name", columnDefinition = "char(20)")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("name", name).toString();
    }

    public static enum Fields {
        name();
    }
}