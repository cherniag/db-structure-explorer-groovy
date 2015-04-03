/**
 *
 */

package mobi.nowtechnologies.server.trackrepo.factory;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;

import java.io.File;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public class AssetFileFactory {

    private File fileDir;

    public AssetFile anyAssetFile() {
        AssetFile assetFile = new AssetFile();

        assetFile.setMd5("8102e0132161803a43fb5f18901b48d4");
        assetFile.setPath(fileDir.getAbsolutePath() + File.separator + "image" + File.separator + "APPCAST_cover.png");
        assetFile.setType(FileType.IMAGE);

        return assetFile;
    }

    public void setFileDir(File fileDir) {
        this.fileDir = fileDir;
    }
}
