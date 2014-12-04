package mobi.nowtechnologies.server.trackrepo.service;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public interface FileService {
	/**
	 * This method get asset file of media resource.
	 * 
	 * @param  id   identifier of asset file.
     *
     * @return asset file with type without content of resource.
	 */
	AssetFile getFile(Long id);
}
