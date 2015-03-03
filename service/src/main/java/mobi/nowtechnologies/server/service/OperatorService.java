package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Operator;

import java.util.Collection;

public interface OperatorService {

    public Collection<Operator> getOperators();
}