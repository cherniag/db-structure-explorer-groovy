package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "tb_accountLogTypes")
public class AccountLogType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private byte i;

    @Column(name = "name", columnDefinition = "char(40)")
    private String name;

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

}