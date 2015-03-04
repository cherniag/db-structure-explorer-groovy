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
@Table(name = "tb_fileTypes")
public class FileType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private byte i;

    @Column(name = "name", columnDefinition = "char(25)")
    private String name;

    public FileType() {
    }

    public byte getI() {
        return this.i;
    }

    public void setI(byte i) {
        this.i = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileType withI(byte i) {
        setI(i);
        return this;
    }

    public FileType withName(String name) {
        setName(name);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("name", name).toString();
    }
}