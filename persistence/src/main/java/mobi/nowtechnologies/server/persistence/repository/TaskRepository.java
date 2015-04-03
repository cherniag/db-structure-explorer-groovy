package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.task.Task;
import mobi.nowtechnologies.server.persistence.domain.task.UserTask;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * User: gch Date: 12/17/13
 */
public interface TaskRepository<T extends Task> extends JpaRepository<T, Long> {

    @Query("select task from UserTask task join task.user user where " + "user.id = :userId and task.taskStatus = 'ACTIVE' and task.taskType = :taskType")
    public List<UserTask> findActiveUserTasksByUserIdAndType(@Param("userId") int userId, @Param("taskType") String taskType);

    @Modifying
    @Query("delete from UserTask task where " + "task.user.id = :userId and task.taskType = :taskType")
    public int deleteByUserIdAndTaskType(@Param("userId") int userId, @Param("taskType") String taskType);

    @Query("select task from Task task where task.taskStatus = 'ACTIVE' and task.executionTimestamp < :executionTimestamp")
    List<Task> findTasksToExecute(@Param("executionTimestamp") long executionTimestamp, Pageable pageable);

    @Query("select count(task) from Task task where task.taskStatus = 'ACTIVE' and task.executionTimestamp < :executionTimestamp")
    long countTasksToExecute(@Param("executionTimestamp") long executionTimestamp);

    @Modifying
    @Query("update Task task set task.executionTimestamp = :newExecutionTimestamp where task.id = :id")
    int updateExecutionTimestamp(@Param("id") Long id, @Param("newExecutionTimestamp") long newExecutionTimestamp);
}
