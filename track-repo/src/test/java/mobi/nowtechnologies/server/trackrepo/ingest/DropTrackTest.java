package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;

import org.junit.*;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: Alexsandr_Kolpakov Date: 9/11/13 Time: 2:21 PM To change this template use File | Settings | File Templates.
 */
public class DropTrackTest {

    @Test
    public void testHasAnyMediaResources_NullFiles_Successful() throws Exception {
        DropTrack track = new DropTrack();
        track.files = null;

        boolean result = track.hasAnyMediaResources();

        assertEquals(false, result);
    }

    @Test
    public void testHasAnyMediaResources_EmptyFiles_Successful() throws Exception {
        DropTrack track = new DropTrack();

        boolean result = track.hasAnyMediaResources();

        assertEquals(false, result);
    }

    @Test
    public void testHasAnyMediaResources_NoMediaFiles_Successful() throws Exception {
        DropAssetFile mobileFile = new DropAssetFile();
        mobileFile.type = AssetFile.FileType.MOBILE;

        DropAssetFile previewFile = new DropAssetFile();
        mobileFile.type = AssetFile.FileType.PREVIEW;

        DropAssetFile imageFile = new DropAssetFile();
        mobileFile.type = AssetFile.FileType.IMAGE;

        DropTrack track = new DropTrack();
        track.files.add(mobileFile);
        track.files.add(previewFile);
        track.files.add(imageFile);

        boolean result = track.hasAnyMediaResources();

        assertEquals(false, result);
    }

    @Test
    public void testHasAnyMediaResources_VideoMediaFiles_Successful() throws Exception {
        DropAssetFile mobileFile = new DropAssetFile();
        mobileFile.type = AssetFile.FileType.VIDEO;

        DropTrack track = new DropTrack();
        track.files.add(mobileFile);

        boolean result = track.hasAnyMediaResources();

        assertEquals(true, result);
    }

    @Test
    public void testHasAnyMediaResources_AudioMediaFiles_Successful() throws Exception {
        DropAssetFile mobileFile = new DropAssetFile();
        mobileFile.type = AssetFile.FileType.DOWNLOAD;

        DropTrack track = new DropTrack();
        track.files.add(mobileFile);

        boolean result = track.hasAnyMediaResources();

        assertEquals(true, result);
    }
}
