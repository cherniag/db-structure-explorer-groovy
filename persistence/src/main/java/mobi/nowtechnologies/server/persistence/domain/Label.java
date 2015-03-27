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
@Table(name = "tb_labels")
public class Label implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long i;

    @Column(name = "name", columnDefinition = "char(30)")
    private String name;

    public Label() {
    }

    public Long getI() {
        return this.i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Label withName(String name) {
        setName(name);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("name", name).toString();
    }
}