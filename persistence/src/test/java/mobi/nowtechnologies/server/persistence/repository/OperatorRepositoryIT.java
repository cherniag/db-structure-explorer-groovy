/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Operator;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class OperatorRepositoryIT extends AbstractRepositoryIT {

    @Resource
    OperatorRepository operatorRepository;

    @Test
    public void testFindOperators(){
        List<Operator> operators = operatorRepository.findOperators(5, "PSMS");
        assertEquals(5, operators.size());
    }

    @Test
    public void testFindFirst(){
        Operator operator = operatorRepository.findFirst();
        assertNotNull(operator);
    }

}