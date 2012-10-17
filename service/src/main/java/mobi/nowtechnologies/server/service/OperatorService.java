package mobi.nowtechnologies.server.service;

import java.util.Collection;

import mobi.nowtechnologies.server.persistence.domain.Operator;

public interface OperatorService {
	public Collection<Operator> getOperators();
}