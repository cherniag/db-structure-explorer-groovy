package mobi.nowtechnologies.server.persistence.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "tb_appVersions")
public class AppVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer i;

    @Column(name = "description", columnDefinition = "char(50)")
    private String description;

    @Column(name = "name", columnDefinition = "char(25)")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_appVersionCountry", joinColumns = {@JoinColumn(name = "appVersion_id")}, inverseJoinColumns = {@JoinColumn(name = "country_id")})
    private Set<Country> countries = new HashSet<Country>();

    public AppVersion() {
    }

    public Integer getI() {
        return this.i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("description", description).append("name", name).toString();
    }
}