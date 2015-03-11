package mobi.nowtechnologies.server.trackrepo.utils;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.utils.image.ImageGenerator;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.IMAGE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang3.StringUtils.defaultString;

import org.springframework.core.io.Resource;

public class EncodeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncodeManager.class);
    private ExternalCommand commandGetEncoding;
    private ExternalCommand commandEncodeUnknown;
    private ExternalCommand commandEncodeMP3;
    private ExternalCommand commandEncodeACC;
    private ExternalCommand commandEncodeWAV;
    private ExternalCommand commandDecodeMP3;
    private ExternalCommand commandEncodeMobileAudio;
    private ExternalCommand commandEncodePreviewAudio;
    private ExternalCommand commandAddTag;
    private ImageGenerator imageGenerator;
    private UITSCommandAdapter commandUITS;
    private String workDirAbsolutePath;
    private Resource neroHome;
    private UploadToCloudFileManager cloudUploadFileManager;

    public void encode(Track track, Boolean isHighRate, Boolean licensed) throws IOException, InterruptedException {
        LOGGER.info("Start encoding track : {}", track.getUniqueTrackId());

        File tmpDir = createTmpDir(track);
        try {
            List<File> toPrivateContainerFileList = new ArrayList<>();
            List<File> toDataContainerFileList = new ArrayList<>();

            toPrivateContainerFileList.addAll(generateThumbnailFiles(tmpDir, track, licensed));

            if (!track.isVideo()) {
                //Generating download audio
                String audioFilePath = track.getFileName(AssetFile.FileType.DOWNLOAD);
                LOGGER.debug("Start encoding for DOWNLOAD file: {}", audioFilePath);

                Encoding encoding = getEncoding(audioFilePath);
                LOGGER.debug("Encoding is {}", encoding);

                if (encoding.equals(Encoding.MP4)) {
                    throw new RuntimeException("Unsupported audio format: " + encoding);
                }

                if (Encoding.UNKNOWN.equals(encoding)) {
                    LOGGER.debug("Trying to fix unknown audio file: {}", audioFilePath);

                    String fixedFileName = workDirAbsolutePath + track.getUniqueTrackId() + "_fix.mp3";
                    commandEncodeUnknown.executeCommand(audioFilePath, fixedFileName);
                    audioFilePath = fixedFileName;
                    encoding = getEncoding(audioFilePath);
                    LOGGER.debug("Encoding after fixing is {}", encoding);
                }

                toPrivateContainerFileList.add(generatePurchasedFile(tmpDir, track, audioFilePath, encoding));

                String mobileAudioTmpFilePath = generateMobileAudioFile(tmpDir, track, audioFilePath, encoding);

                String coverImageTmpFilePath = getCoverImageFilePath(tmpDir, track);

                toPrivateContainerFileList.addAll(generateBitRateFiles(tmpDir, track, isHighRate, mobileAudioTmpFilePath, coverImageTmpFilePath));

                toDataContainerFileList.add(generatePreviewAudioFile(tmpDir, track, mobileAudioTmpFilePath, coverImageTmpFilePath));
            }

            cloudUploadFileManager.uploadFilesToCloud(track, toPrivateContainerFileList, toDataContainerFileList);
            LOGGER.info("Encoding done");
        } finally {
            FileUtils.deleteQuietly(tmpDir);
        }
    }

    private File createTmpDir(Track track) throws IOException {
        return Files.createTempDirectory(Paths.get(workDirAbsolutePath), String.format("%s_", track.getUniqueTrackId())).toFile();
    }

    private List<File> generateThumbnailFiles(File dir, Track track, Boolean licensed) {
        try {
            if (licensed) {
                LOGGER.info("encoding licensed");
                return imageGenerator.generateThumbnails(dir, track.getFileName(IMAGE), track.getUniqueTrackId(), track.isVideo());
            } else {
                return imageGenerator.generateThumbnailsWithWatermark(dir, track.getFileName(IMAGE), track.getUniqueTrackId(), track.isVideo());
            }
        } catch (Exception e) {
            LOGGER.error("Image generating failed for track {}", track.getUniqueTrackId(), e);
        }
        return Collections.emptyList();
    }

    private File generatePurchasedFile(File tmpDir, Track track, String audioFilePath, Encoding encoding) throws IOException, InterruptedException {
        File purchasedTmpFile = new File(tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + "_.mp3");
        LOGGER.debug("Purchased file: {}", purchasedTmpFile);

        if (Encoding.MP3.equals(encoding)) {
            commandEncodeMP3.executeCommand(audioFilePath, purchasedTmpFile.getAbsolutePath());
        } else if (Encoding.ACC.equals(encoding)) {
            commandEncodeACC.executeCommand(audioFilePath, purchasedTmpFile.getAbsolutePath());
        } else if (Encoding.WAV.equals(encoding)) {
            commandEncodeWAV.executeCommand(audioFilePath, purchasedTmpFile.getAbsolutePath());
        }
        LOGGER.debug("Purchased file is created: {}", purchasedTmpFile.exists());

        String mp3TempFilePath = tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + ".mp3";
        LOGGER.debug("Temp file is {}", mp3TempFilePath);
        commandUITS.executeDownloadFiles(purchasedTmpFile.getAbsolutePath(), mp3TempFilePath);

        return new File(mp3TempFilePath);
    }

    private File generatePreviewAudioFile(File tmpDir, Track track, String encodeInputFile, String coverImageFileName) throws IOException, InterruptedException {
        String previewTmpFilePath = tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + "P.m4a";
        LOGGER.debug("Preview file is {}", previewTmpFilePath);
        File previewTmpFile = new File(previewTmpFilePath);

        commandEncodePreviewAudio.executeCommand(encodeInputFile, previewTmpFilePath);
        LOGGER.debug("Preview file is {} and exists: {}", previewTmpFilePath, previewTmpFile.exists());

        commandAddTag.executeCommand(neroHome.getFile().getAbsolutePath(), previewTmpFilePath, defaultString(track.getTitle()), defaultString(track.getArtist()), defaultString(track.getAlbum()),
                                     defaultString(track.getGenre()), defaultString(track.getYear()), "", defaultString(track.getCopyright()), defaultString(track.getUniqueTrackId()),
                                     coverImageFileName);


        LOGGER.debug("Preview file with tag is {} and exists: {}", previewTmpFilePath, previewTmpFile.exists());

        return previewTmpFile;
    }

    private String generateMobileAudioFile(File tmpDir, Track track, String audioFilePath, Encoding encoding) throws IOException, InterruptedException {
        LOGGER.debug("Start generating mobile audio: {}", audioFilePath);

        String encodeInputFilePth = audioFilePath;
        LOGGER.debug("Working file is {}", encodeInputFilePth);
        if (Encoding.MP3.equals(encoding)) {

            LOGGER.debug("MP3 decoding to wav");

            String decodedMP3FilePath = tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + ".wav";
            LOGGER.debug("MP3 decoded file: {}", decodedMP3FilePath);
            commandDecodeMP3.executeCommand(decodedMP3FilePath, audioFilePath);
            encodeInputFilePth = decodedMP3FilePath;
            LOGGER.debug("Working file's been changed to {}", encodeInputFilePth);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Working file exists: {}", new File(encodeInputFilePth).exists());
        }
        return encodeInputFilePth;
    }

    private List<File> generateBitRateFiles(File tmpDir, Track track, Boolean isHighRate, String encodeInputFilePath, String coverImageFilePath) throws IOException, InterruptedException {
        List<File> bitRateTmpFileList = new ArrayList<>();
        for (BitRate currentBitRate : BitRate.values()) {
            String bitRate = currentBitRate.getValue();
            LOGGER.debug("Start converting for bitRate {}", bitRate);

            String bitRateFilePath = tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + "_" + bitRate + ".m4a";
            commandEncodeMobileAudio.executeCommand(encodeInputFilePath, bitRate, bitRateFilePath);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Encoded file to bitRate {} : {} and it exists: {}", bitRate, bitRateFilePath, new File(bitRateFilePath).exists());
            }

            commandAddTag.executeCommand(neroHome.getFile().getAbsolutePath(), bitRateFilePath, defaultString(track.getTitle()), defaultString(track.getArtist()), defaultString(track.getAlbum()),
                                         defaultString(track.getGenre()), defaultString(track.getYear()), "", defaultString(track.getCopyright()), defaultString(track.getUniqueTrackId()),
                                         coverImageFilePath);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Encoded file to bitRate {} with tag: {} and it exists: {}", bitRate, bitRateFilePath, new File(bitRateFilePath).exists());
            }

            File audioTmpFile = new File(tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + "_" + bitRate + ".aud");
            File hdrTmpFile = new File(tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + "_" + bitRate + ".hdr");
            File encTmpFile = new File(tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + "_" + bitRate + ".enc");

            commandUITS.executeMobileFiles(bitRateFilePath, audioTmpFile.getAbsolutePath(), hdrTmpFile.getAbsolutePath(), encTmpFile.getAbsolutePath());

            LOGGER.debug("Audio file is {} and exists: {}", audioTmpFile.getAbsolutePath(), audioTmpFile.exists());
            LOGGER.debug("Header file is {} and exists: {}", hdrTmpFile.getAbsolutePath(), hdrTmpFile.exists());
            LOGGER.debug("Encoded file is {} and exists: {}", encTmpFile.getAbsolutePath(), encTmpFile.exists());

            bitRateTmpFileList.add(audioTmpFile);
            bitRateTmpFileList.add(hdrTmpFile);
            bitRateTmpFileList.add(encTmpFile);

            if (isHighRate == (currentBitRate.equals(BitRate.BITRATE96))) {
                File audioNoBitRateTmpFile = new File(tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + ".aud");
                Files.copy(audioTmpFile.toPath(), audioNoBitRateTmpFile.toPath());
                bitRateTmpFileList.add(audioNoBitRateTmpFile);

                File encNoBitRateTmpFile = new File(tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + ".enc");
                Files.copy(encTmpFile.toPath(), encNoBitRateTmpFile.toPath());
                bitRateTmpFileList.add(encNoBitRateTmpFile);
            }
        }
        return bitRateTmpFileList;
    }

    private String getCoverImageFilePath(File tmpDir, Track track) {
        String coverImageFilePath = tmpDir.getAbsolutePath() + File.separator + track.getUniqueTrackId() + "_cover.png";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cover file: {} and it exists: {}", coverImageFilePath, new File(coverImageFilePath).exists());
        }
        return coverImageFilePath;
    }

    private Encoding getEncoding(String audioFilePath) throws IOException, InterruptedException {
        String encoding = commandGetEncoding.executeCommand(audioFilePath);
        LOGGER.debug("Encoding is {}", encoding);
        return Encoding.getInstance(encoding);
    }

    public void setCommandGetEncoding(ExternalCommand commandGetEncoding) {
        this.commandGetEncoding = commandGetEncoding;
    }

    public void setCommandEncodeUnknown(ExternalCommand commandEncodeUnknown) {
        this.commandEncodeUnknown = commandEncodeUnknown;
    }

    public void setCommandEncodeMP3(ExternalCommand commandEncodeMP3) {
        this.commandEncodeMP3 = commandEncodeMP3;
    }

    public void setCommandEncodeACC(ExternalCommand commandEncodeACC) {
        this.commandEncodeACC = commandEncodeACC;
    }

    public void setCommandEncodeWAV(ExternalCommand commandEncodeWAV) {
        this.commandEncodeWAV = commandEncodeWAV;
    }

    public void setCommandDecodeMP3(ExternalCommand commandDecodeMP3) {
        this.commandDecodeMP3 = commandDecodeMP3;
    }

    public void setCommandEncodeMobileAudio(ExternalCommand commandEncodeMobileAudio) {
        this.commandEncodeMobileAudio = commandEncodeMobileAudio;
    }

    public void setCommandEncodePreviewAudio(ExternalCommand commandEncodePreviewAudio) {
        this.commandEncodePreviewAudio = commandEncodePreviewAudio;
    }

    public void setCommandAddTag(ExternalCommand commandAddTag) {
        this.commandAddTag = commandAddTag;
    }

    public void setImageGenerator(ImageGenerator imageGenerator) {
        this.imageGenerator = imageGenerator;
    }

    public void setCommandUITS(UITSCommandAdapter commandUITS) {
        this.commandUITS = commandUITS;
    }

    public void setWorkDir(Resource workDir) throws IOException {
        this.workDirAbsolutePath = workDir.getFile().getAbsolutePath() + File.separator;
    }

    public void setNeroHome(Resource neroHome) {
        this.neroHome = neroHome;
    }

    public void setCloudUploadFileManager(UploadToCloudFileManager cloudUploadFileManager) {
        this.cloudUploadFileManager = cloudUploadFileManager;
    }

    private static enum Encoding {
        MP3("mp3"), ACC("acc"), WAV("wav"), PCMS16LE("pcm_s16le"), ACC_MP4A("aac (mp4a"), MP4("mp4"), UNKNOWN("unknown");

        String code;

        Encoding(String code) {
            this.code = code;
        }

        public static Encoding getInstance(String code) {
            if (MP3.code.equalsIgnoreCase(code)) {
                return MP3;
            } else if (ACC.code.equalsIgnoreCase(code)) {
                return ACC;
            } else if (code != null && code.toLowerCase().startsWith(PCMS16LE.code)) {
                return WAV;
            } else if (code != null && code.toLowerCase().startsWith(ACC_MP4A.code)) {
                return MP4;
            } else {
                return UNKNOWN;
            }
        }
    }
}
