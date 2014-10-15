package mobi.nowtechnologies.server.persistence.apptests.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * Author: Gennadii Cherniaiev
 * Date: 8/28/2014
 */
@Entity
@Table(name = "fat_job_trigger_request")
public class JobTriggerRequest {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "group_name", nullable = false)
    private String jobGroupName;

    @Column(name = "execution_timestamp", nullable = false)
    private long executeTimestamp;

    protected JobTriggerRequest() {

    }

    public JobTriggerRequest(String jobName, String jobGroupName) {
        this(jobName, jobGroupName, new Date().getTime());
    }

    public JobTriggerRequest(String jobName, String jobGroupName, long executeTimestamp) {
        this.jobName = jobName;
        this.jobGroupName = jobGroupName;
        this.executeTimestamp = executeTimestamp;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("jobName", jobName)
                .append("jobGroupName", jobGroupName)
                .append("executeTimestamp", executeTimestamp)
                .toString();
    }
}
