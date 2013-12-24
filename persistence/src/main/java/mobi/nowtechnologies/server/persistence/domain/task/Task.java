package mobi.nowtechnologies.server.persistence.domain.task;

/**
 * User: gch
 * Date: 12/16/13
 */

import mobi.nowtechnologies.server.persistence.domain.enums.TaskStatus;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tb_tasks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "taskType", length = 50, discriminatorType = DiscriminatorType.STRING)
public abstract class Task implements Serializable {
    private static final long serialVersionUID = 5704319982970830025L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskStatus", columnDefinition = "char(25)")
    private TaskStatus taskStatus;

    @SuppressWarnings("unused")
    @Column(name = "taskType", insertable = false, updatable = false)
    private String taskType;

    private long executionTimestamp;

    private long creationTimestamp;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("taskStatus", taskStatus)
                .append("executionTimestamp", executionTimestamp)
                .append("creationTimestamp", creationTimestamp)
                .toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public long getExecutionTimestamp() {
        return executionTimestamp;
    }

    public void setExecutionTimestamp(long executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
}
