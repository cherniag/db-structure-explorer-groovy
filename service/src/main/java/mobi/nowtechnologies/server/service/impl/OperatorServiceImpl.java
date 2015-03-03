package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.service.OperatorService;

import java.util.Collection;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class OperatorServiceImpl implements OperatorService {

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public Collection<Operator> getOperators() {
        return OperatorDao.getMapAsIds().values();
    }
}
