package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.task.SendChargeNotificationTask;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by oar on 2/10/14.
 */
public interface SendChargeNotificationTaskRepository extends JpaRepository<SendChargeNotificationTask, Long> {}
