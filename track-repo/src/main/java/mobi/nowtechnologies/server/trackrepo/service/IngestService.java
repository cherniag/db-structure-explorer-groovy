package mobi.nowtechnologies.server.trackrepo.service;

import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;

/**
 * @author Alexander Kolpakov (akolpakov)
 * This interface contains all methods to work with Tracks in Track Repository.
 */
public interface IngestService {

    /**
     * Scans all drops of given ingestor on the file system and checks, indexes them and then returns.
     *
     * @param  ingestor name of ingestor whose drops will be search in according directory if null drops will be searched for all ingestors.
     *
     * @return IngestWizardData drop data without drop tracks
     */
	
	IngestWizardData getDrops(String... ingestors) throws Exception;

    /**
     * Select drops which needs to commit.
     *
     * @param data data about selected drops.
     */
    IngestWizardData selectDrops(IngestWizardData data) throws Exception;

    /**
     * Select drop tracks which needs to commit.
     *
     * @param data data about selected drop tracks.
     */
    IngestWizardData selectDropTracks(IngestWizardData data) throws Exception;

    /**
     * Commit all selected tracks to database like indexed tracks with all their assert files, and then mark them like ingested.
     *
     * @param data data about selected drops.
     *
     * @return whether tracks saved to database or not
     */
    boolean commitDrops(IngestWizardData data) throws Exception;

}
