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
@Table(name = "tb_country")
public class Country implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i", columnDefinition = "smallint(5) unsigned")
    private int i;
    @Column(name = "name", columnDefinition = "char(10)")
    private String name;
    private String fullName;

    protected Country() {}

    public Country(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
    }

    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("name", name).append("fullName", fullName).toString();
    }

}