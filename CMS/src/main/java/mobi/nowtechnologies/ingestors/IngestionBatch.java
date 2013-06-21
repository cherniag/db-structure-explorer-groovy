package mobi.nowtechnologies.ingestors;

import mobi.nowtechnologies.service.IngestService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




public class IngestionBatch implements Runnable {
	
	protected static final Log LOG = LogFactory.getLog(IngestionBatch.class);

	

	private static final int UPDATE_JOB_SLEEP_TIME_MS = 1800000;

	private IngestService ingestService;
	
	
	public IngestionBatch() {
	//	Thread batch  = new Thread(this);
		
	//	batch.start();
	}

	

	public void setIngestService(IngestService ingestService) {
		this.ingestService = ingestService;
	}



	public void run() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}

		while(true) {
			LOG.info("Running ingestion batch");
			try{
				ingestService.processAllDrops();
			}catch (Exception e) {
			}
			try {
				Thread.sleep(UPDATE_JOB_SLEEP_TIME_MS);
			} catch (InterruptedException e) {
			}
		}
	}

}
