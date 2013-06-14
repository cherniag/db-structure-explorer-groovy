package mobi.nowtechnologies.server.service;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Set;

public class HazelcastService {

    public final static String QUEUE_O2_USERS_FOR_UPDATE = "O2_USERS_FOR_UPDATE";

    public HazelcastInstance getHazelcastInstance() {
        Set<HazelcastInstance> instances = Hazelcast.getAllHazelcastInstances();
        if (instances.isEmpty())
            return Hazelcast.newHazelcastInstance(null);
        else
            return instances.iterator().next();
    }
}
