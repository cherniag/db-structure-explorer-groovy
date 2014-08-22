package mobi.nowtechnologies.server.persistence.domain.apptests;

import com.google.common.base.Joiner;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "fat_email")
public class Email {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "from_")
    private String from;

    @Column(name = "tos")
    private String tos;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "send_time")
    private Date date;

    @Column(name = "model")
    private String model;

    protected Email() {
    }

    public Email(String from, String[] to, String subject, String body) {
        this.from = from;
        this.tos = Joiner.on(',').join(to);
        this.subject = subject;
        this.body = body;
        this.date = new Date();
    }

    public Email withModel(String domain) {
        this.model = domain;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public String getTos() {
        return tos;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", tos='" + tos + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", date=" + date +
                ", model='" + model + '\'' +
                '}';
    }
}
