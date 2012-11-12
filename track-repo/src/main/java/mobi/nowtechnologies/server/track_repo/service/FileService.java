package mobi.nowtechnologies.server.track_repo.service;

import mobi.nowtechnologies.server.track_repo.dto.AssetFileDto;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public interface FileService {
	/**
	 * This method get asset file of media resource.
	 * 
	 * @param  id   identifier of asset file.
     *
     * @return asset file DTO with type and content of resource.
	 */
	AssetFileDto getFile(Long id);
}
