/**
 * 
 */
package mobi.nowtechnologies.server.trackrepo.factory;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class AssetFileFactory {
	private File fileDir;
	
	public AssetFile anyAssetFile() {
		AssetFile assetFile = new AssetFile();
		
		assetFile.setMd5("8102e0132161803a43fb5f18901b48d4");
		assetFile.setPath(fileDir.getAbsolutePath()+"/image/APPCAST_cover.png");
		assetFile.setType(FileType.IMAGE);
		
		return assetFile;
	}
	
	public List<AssetFile> anyAssetFiles(int amount) {
		List<AssetFile> items = new ArrayList<AssetFile>();
		for (int i=0; i<amount; i++)
			items.add(anyAssetFile());
		return items;
	}
	
	public List<AssetFile> cloneAssetFiles(AssetFile item, int amount) {
		List<AssetFile> items = new ArrayList<AssetFile>();
		for (int i=0; i<amount; i++)
			items.add(item);
		return items;
	}
	
	public static List<AssetFile> getEmptyAssetFiles() {
		return new ArrayList<AssetFile>();
	}

	public void setFileDir(File fileDir) {
		this.fileDir = fileDir;
	}
}
