package mobi.nowtechnologies.server.persistence.domain.task;

/**
 * User: gch
 * Date: 12/16/13
 */

import mobi.nowtechnologies.server.persistence.domain.enums.TaskStatus;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "tb_tasks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "taskType", length = 50, discriminatorType = DiscriminatorType.STRING)
public abstract class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskStatus", columnDefinition = "char(25)")
    private TaskStatus taskStatus = TaskStatus.ACTIVE;

    @SuppressWarnings("unused")
    @Column(name = "taskType", insertable = false, updatable = false)
    private String taskType;

    protected Task() {

    }

    protected Task(Date serverTime) {
        executionTimestamp = serverTime.getTime();
        creationTimestamp = serverTime.getTime();
    }

    public abstract String getTaskType();

    private long executionTimestamp;

    private long creationTimestamp;

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("taskStatus", taskStatus).append("executionTimestamp", executionTimestamp).append("creationTimestamp", creationTimestamp).toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getExecutionTimestamp() {
        return executionTimestamp;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void scheduleAfter(long executeInterval) {
        long latest = Math.max(creationTimestamp, executionTimestamp);
        executionTimestamp = latest + executeInterval;
    }
}
