package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * User: gch Date: 12/17/13
 */
public interface TaskRepository<T extends Task> extends JpaRepository<T, Long> {

    @Query("select task from UserTask task join task.user user where user.id = :userId and task.taskStatus = 'ACTIVE' and task.taskType = :taskType")
    List<UserTask> findActiveUserTasksByUserIdAndType(@Param("userId") int userId, @Param("taskType") String taskType);

    @Modifying
    @Query("delete from UserTask task where task.user.id = :userId and task.taskType = :taskType")
    int deleteByUserIdAndTaskType(@Param("userId") int userId, @Param("taskType") String taskType);

    @Query("select task from Task task where task.taskStatus = 'ACTIVE' and task.executionTimestamp <= :executionTimestamp and task.taskType in :supportedTypes")
    Page<Task> findTasksToExecute(@Param("executionTimestamp") long executionTimestamp, @Param("supportedTypes") Collection<String> supportedTypes, Pageable pageable);

    @Modifying
    @Query("update Task task set task.executionTimestamp = :newExecutionTimestamp where task.id = :id")
    int updateExecutionTimestamp(@Param("id") Long id, @Param("newExecutionTimestamp") long newExecutionTimestamp);
}
