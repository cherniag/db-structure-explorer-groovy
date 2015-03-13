package mobi.nowtechnologies.server.trackrepo.uits;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MP3Manager {

    private static final long BIT_RATES[] = {0, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, -1};
    private static final long SAMPLE_RATES[] = {44100, 48000, 32000, 0};
    private static final int MP3_HEADER_SIZE = 10;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public int process(InputStream audioFile, OutputStream out, UitsParameters params, String mediaHash) {
        logger.debug("Start processing, params : {}, mediaHash : {}", params, mediaHash);
        try {
            MP3_ID3_HEADER id3Header = new MP3_ID3_HEADER();

            byte[] privateFrame = new byte[5000];
            int privateSize = getPrivateFrame(params, mediaHash, privateFrame);
            byte[] ID3Header = new byte[MP3_HEADER_SIZE];
            audioFile.read(ID3Header, 0, MP3_HEADER_SIZE);
            readID3Header(id3Header, ID3Header);
            long copySize = id3Header.size;
            // Update the ID3V2 tag size to add the UITS private TAG size
            id3Header.size = id3Header.size + privateSize;

            writeID3Header(out, id3Header);

            // Copy all ID3 headers
            byte[] copyBuffer = new byte[1024];
            while (copySize > 0) {
                int read = audioFile.read(copyBuffer, 0, (int) (copySize > 1024 ?
                                                                1024 :
                                                                copySize));
                out.write(copyBuffer, 0, read);
                copySize -= read;
            }

            // Write UITS header
            out.write(privateFrame, 0, privateSize);

            // Copy remaining data
            int readSize = audioFile.read(copyBuffer, 0, 1024);
            while (readSize > 0) {
                out.write(copyBuffer, 0, readSize);
                readSize = audioFile.read(copyBuffer, 0, 1024);
            }

            return 1;
        } catch (IOException e) {
            logger.error("IO error, message: {}", e.getMessage(), e);
        }
        return 0;

    }

    public String getMP3MediaHash(String audioFileName) throws IOException {
        logger.debug("Get mp3 media hash for file : {}", audioFileName);
        MP3_ID3_HEADER mp3ID3Header = new MP3_ID3_HEADER();
        MP3_AUDIO_FRAME_HEADER mp3AudioFrameHeader;
        int audioFrameStart;
        String mediaHashString;
        int foundPadBytes = 0;
        int id3v1TagCount;

        FileInputStream audioInputStream = new FileInputStream(audioFileName);

        byte[] buffer = new byte[MP3_HEADER_SIZE];
        audioInputStream.read(buffer, 0, MP3_HEADER_SIZE);
        // The file should start with an ID3 tag header
        readID3Header(mp3ID3Header, buffer);

        // seek to the first audio frame, which will be after the MP3 ID3 tag
        // header (10 bytes)
        audioFrameStart = (int) (MP3_HEADER_SIZE + mp3ID3Header.size);
        audioInputStream.skip(audioFrameStart - MP3_HEADER_SIZE);
        logger.debug("Header size : {}", mp3ID3Header.size);

        byte[] pad = new byte[1];
        audioInputStream.read(pad, 0, 1);
        while (pad[0] == 0) {
            foundPadBytes++;
            audioInputStream.read(pad, 0, 1);
        }

        if (foundPadBytes > 0) {
            logger.warn("Warning: Illegal MP3 file {}. ID3 frame header size does not include pad bytes", audioFileName);
        }
        byte[] headerBuf = new byte[4];
        headerBuf[0] = pad[0];
        audioInputStream.read(headerBuf, 1, 3);

        mp3AudioFrameHeader = readAudioFrameHeader(headerBuf);
        byte[] frame0 = new byte[(int) mp3AudioFrameHeader.frameLength];
        // Put header back
        System.arraycopy(headerBuf, 0, frame0, 0, 4);
        audioInputStream.read(frame0, 4, (int) mp3AudioFrameHeader.frameLength - 4);

        File temp = new File(audioFileName);
        byte[] audio = new byte[(int) temp.length()];
        int offset = 0;
        // if the first frame of audio is a VBR frame (XING, Info, VBR), skip it
        if (!isVBRFrame(frame0, mp3AudioFrameHeader)) {
            offset = (int) mp3AudioFrameHeader.frameLength;
            System.arraycopy(frame0, 0, audio, 0, offset);
        }
        int nRead = audioInputStream.read(audio, offset, (int) temp.length() - offset);

        // skip the 128 byte ID3v1 tag at the end of the file, if it's there
        // note that some files have been found to have more than 1 ID3V1
        // tag, so don't include any of them
        id3v1TagCount = getID3V1TagCount(audio, offset + nRead);
        int totalLen = offset + nRead;
        totalLen -= id3v1TagCount * 128;

        Crypto crypto = new Crypto("SHA-256");
        crypto.addHash(audio, totalLen);

        mediaHashString = crypto.finalizeHash();

        IOUtils.closeQuietly(audioInputStream);
        return mediaHashString;
    }

    private void writeID3Header(OutputStream out, MP3_ID3_HEADER mp3Header) throws IOException {

        byte[] header = new byte[MP3_HEADER_SIZE];
        // the first 3 bytes are 'I' 'D' '3'
        header[0] = 'I';
        header[1] = 'D';
        header[2] = '3';

        // major and minor version are in next two bytes
        header[3] = (byte) mp3Header.majorVersion;
        header[4] = (byte) mp3Header.minorVersion;

        // the next byte is the flags byte
        header[5] = (byte) mp3Header.flags;

        // The next 4 bytes have the size in sync-safe format. Need to convert
        // from integer and handle endianness.
        long l1, l2, l3, l4;

        l4 = mp3Header.size & 0x7f;
        l3 = mp3Header.size & 0x3f80;
        l3 <<= 1L;
        l2 = mp3Header.size & 0x1fc000;
        l2 <<= 2L;
        l1 = mp3Header.size & 0x0fe00000;
        l1 <<= 3L;
        l4 += l1 + l2 + l3;
        mp3Header.size = l4;

        byte[] size = new byte[4];
        ByteBuffer bf = ByteBuffer.wrap(size);
        bf.putInt((int) mp3Header.size);
        System.arraycopy(size, 0, header, 6, 4);

        out.write(header, 0, MP3_HEADER_SIZE);
    }

    private int getPrivateFrame(UitsParameters params, String mediaHash, byte[] frame) {
        String uitsPayloadXML = XmlPayload.buildPayload(params, mediaHash);
        logger.debug("PayloadXML : {}", uitsPayloadXML);
        int privFrameLen;
        int privFrameMailtoLen;
        int privFrameuitsLen;

        privFrameMailtoLen = 0;
        privFrameuitsLen = uitsPayloadXML.length();
        privFrameLen = privFrameMailtoLen + 1 + privFrameuitsLen + 1;
        int fullsize = MP3_HEADER_SIZE + privFrameLen + 2;

        // Write header
        frame[0] = 'P';
        frame[1] = 'R';
        frame[2] = 'I';
        frame[3] = 'V';
        frame[7] = (byte) (privFrameLen & 0x000000ff);
        privFrameLen >>= 8;
        frame[6] = (byte) (privFrameLen & 0x000000ff);
        privFrameLen >>= 8;
        frame[5] = (byte) (privFrameLen & 0x000000ff);
        privFrameLen >>= 8;
        frame[4] = (byte) (privFrameLen & 0x000000ff);

        frame[8] = '\0';
        frame[9] = '\0';

        // write a null terminator for the email
        frame[10] = 0;

        // write the frame data UITS xml string
        System.arraycopy(uitsPayloadXML.getBytes(), 0, frame, 11, privFrameuitsLen);

        frame[fullsize - 1] = 0;
        return fullsize;
    }

    private int getID3V1TagCount(byte[] buffer, int size) {
        int id3v1TagCount = 0;
        int id3v1TagSize = 128;
        boolean done = false;

        // keep looking for id3v1 tags until there are no more
        int index = size - id3v1TagSize;
        while (!done) {
            if (buffer[index] == 'T' && buffer[index + 1] == 'A' && buffer[index + 2] == 'G') {
                id3v1TagCount++;
                index -= 128;
            } else {
                done = true;
            }
        }

        if (id3v1TagCount > 1) {
            logger.warn("WARNING: found more than 1 ID3V1 tags at end of file : {}", id3v1TagCount);
        }
        return (id3v1TagCount);
    }

    private boolean isVBRFrame(byte[] audioBuffer, MP3_AUDIO_FRAME_HEADER frameHeader) {
        int aIndex;

        if (frameHeader.stereoFlag == 1) {
            aIndex = 32 + 4;
        } else {
            aIndex = 17 + 4;
        }

        if (audioBuffer[aIndex] == 'X' && audioBuffer[aIndex + 1] == 'i' && audioBuffer[aIndex + 2] == 'n' && audioBuffer[aIndex + 3] == 'g') {
            return true;
        } else if (audioBuffer[aIndex] == 'I' && audioBuffer[aIndex + 1] == 'n' && audioBuffer[aIndex + 2] == 'f' && audioBuffer[aIndex + 3] == 'o') {
            return true;
        } else if (audioBuffer[aIndex] == 'V' && audioBuffer[aIndex + 1] == 'B' && audioBuffer[aIndex + 2] == 'R' && audioBuffer[aIndex + 3] == 'I') {
            return true;
        }

        return false;
    }

    private void readID3Header(MP3_ID3_HEADER mp3Header, byte[] buffer) throws IOException {

        // ID 3 tag header starts with 'I' 'D' '3'
        if (buffer[0] == 'I' && buffer[1] == 'D' && buffer[2] == '3') {
            // major and minor version number are in next two bytes
            // eg. $03 $00 = ID3V2.3.0
            mp3Header.majorVersion = (int) buffer[3];
            mp3Header.minorVersion = (int) buffer[4];

            // the next byte is the flags byte
            mp3Header.flags = (char) buffer[5];

            // The next 4 bytes have the size in sync-safe format. Need to
            // convert to integer.

            byte[] size = new byte[4];
            System.arraycopy(buffer, 6, size, 0, 4);

            int s = ByteBuffer.wrap(size).getInt();
            int l1 = s & 0x7f000000;
            l1 >>= 3L;
            int l2 = s & 0x7f0000;
            l2 >>= 2L;
            int l3 = s & 0x7f00;
            l3 >>= 1L;
            int l4 = s & 0x7f;
            l4 += l1 + l2 + l3;
            mp3Header.size = l4;

            // uitsMake28From32(&tagsize); // we have to remove the high bit of
            // each byte in size - moronic.

            if ((mp3Header.flags & 0x40) > 0) {
                logger.warn("WARNING: MP3 file contains an extended header");
            }

        } else if (logger.isDebugEnabled()) {
            logger.debug("File does not start with ID3 tag : {} {}", Integer.toHexString(0xff & buffer[0]), Integer.toHexString(0xff & buffer[1]));
        }

    }

    private MP3_AUDIO_FRAME_HEADER readAudioFrameHeader(byte[] header) throws IOException {
        MP3_AUDIO_FRAME_HEADER frameHeader = new MP3_AUDIO_FRAME_HEADER();
        byte byteBuffer;

        // Make sure this is an audio frame header
        // Sync bytes - start of an MP3 audio frame, always eleven 1's.
        if (!(((header[0] & 0xff) == 0xff) && ((header[1] & 0xe0) == 0xe0))) {
            if (logger.isDebugEnabled()) {
                logger.debug("Couldn't read audio frame header got {} {}", Integer.toHexString(0xff & header[0]), Integer.toHexString(0xff & header[1]));
                logger.debug("Does not match {} - {}", Integer.toHexString(0xff & header[0]), Integer.toHexString(0xff & header[1] & 0xe0));
            }
        }

        byteBuffer = (byte) ((header[1] >> 3) & 0x03);
        if (byteBuffer == 3) {
            frameHeader.mpeg1Flag = 1;
        } else {
            frameHeader.mpeg1Flag = 0;
        }

        byteBuffer = (byte) ((header[1] >> 1) & 0x03);
        frameHeader.layer3Flag = byteBuffer;

        byteBuffer = (byte) (header[1] & 0x01);
        frameHeader.crcFlag = byteBuffer;

        byteBuffer = (byte) ((header[2] >> 4) & 0x0f);
        frameHeader.bitrate = BIT_RATES[(int) byteBuffer];

        byteBuffer = (byte) ((header[2] >> 2) & 0x03);
        frameHeader.samplerate = SAMPLE_RATES[(int) byteBuffer];
        if (frameHeader.samplerate == 0) {
            logger.warn("Cannot get sample rate for index {}", byteBuffer);
            frameHeader.samplerate = 44100;
        }

        byteBuffer = (byte) ((header[3] >> 6) & 0x03);

        if ((int) byteBuffer < 3) {
            // not a mono file
            frameHeader.stereoFlag = 1;
        } else {
            frameHeader.stereoFlag = 0;
        }

        frameHeader.frameLength = ((144L * frameHeader.bitrate) / frameHeader.samplerate) + frameHeader.paddedFlag;

        return frameHeader;
    }

    private class MP3_ID3_HEADER {

        int majorVersion;
        int minorVersion;
        char flags;
        long size;
    }

    ;

    private class MP3_AUDIO_FRAME_HEADER {

        int mpeg1Flag; // 1= "MPEG1" 0= "NOMP1"
        int layer3Flag; // 1= "LAYER3" 0= "NOTLY3"
        int crcFlag; // 1= "CRC" 0= "NOCRC"
        long bitrate;
        long samplerate;
        int paddedFlag; // 1= "PADDED" 0= "NOPAD"
        int privateFlag; // 1= "PRIVATE" 0= "NO PRIV"
        String chanmode;
        int stereoFlag; // 1= "STEREO" 0= "MONO"
        String modeExtension;
        int copyrightFlag; // 1= "1" 0= "0"
        int origFlag; // 1= "orig" 0= "copy"
        String emphasis;
        long frameLength;
        int vbrHeaderflag; // 1= 'xing', 'Info', or 'VBR' frame
    }

    ;

}
