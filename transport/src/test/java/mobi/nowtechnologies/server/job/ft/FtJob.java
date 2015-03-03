package mobi.nowtechnologies.server.job.ft;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Author: Gennadii Cherniaiev Date: 7/11/2014
 */
public class FtJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Job runs ..." + new Date().getTime());
    }
}
