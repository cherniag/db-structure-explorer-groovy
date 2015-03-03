package mobi.nowtechnologies.server.trackrepo.uits;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MP4Manager implements IMP4Manager {

    private static final String UITS_UUID_STRING = "99454E27-963A-4B56-8E76-1DB68C899CD4";
    private static final int UUID_SIZE = 16;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private MP4_SUBTYPES recognizedSubTypes[] = {new MP4_SUBTYPES("qt  ", "Apple QuickTime (.MOV/QT) File"), new MP4_SUBTYPES("M4A ", "Apple iTunes AAC-LC (.M4A) Audio File"), new MP4_SUBTYPES("M4B ",
                                                                                                                                                                                                 "Apple iTunes AAC-LC (.M4B) Audio Book File"), new MP4_SUBTYPES(
        "M4V ", "Apple iTunes Video (.M4V) Video File"), new MP4_SUBTYPES("M4VP", "Apple iPhone (.M4V) File"), new MP4_SUBTYPES("mp42", "MP4 v2 [ISO 14496-14]")};

    @Override
    public int process(InputStream inputStream, OutputStream audioOutputStream, OutputStream headerOutputStream, OutputStream encodedOutputStream, UitsParameters params, String md5, boolean encrypt) {
        logger.debug("Start process, params : {}, md5 : {}, encrypt : {}", params, md5, encrypt);
        try {
            Atom ah;
            Atom ftypAtom = null;
            Atom moovAtom = null;
            Atom mdatAtom = null;

            boolean gotMdat = false;

            int pos = 0;

            ah = new Atom(inputStream);
            while (ah.valid) {
                if ("ftyp".equals(ah.type)) {
                    ftypAtom = ah;
                }
                if ("moov".equals(ah.type)) {
                    moovAtom = ah;
                }
                if ("mdat".equals(ah.type)) {
                    mdatAtom = ah;
                    mdatAtom.pos = pos;
                    gotMdat = true;
                }
                if (ah.valid) {
                    pos += ah.size;
                }

                logger.debug("Got atom " + ah.type + " " + ah.size + " " + ah.valid);
                ah = new Atom(inputStream);
            }

            if (md5 == null) {
                Crypto crypto = new Crypto("SHA-256");
                crypto.addHash(Arrays.copyOfRange(mdatAtom.buffer, 8, (int) mdatAtom.size), (int) mdatAtom.size - 8);
                md5 = crypto.finalizeHash();
            }

            String uitsPayloadXML = XmlPayload.buildPayload(params, md5);
            logger.debug("PayloadXML : {}", uitsPayloadXML);
            // write the UITS payload in a uuid atom
            long payloadXMLSize = uitsPayloadXML.length();
            long atomSize = payloadXMLSize + 8 + UUID_SIZE;

            long moovOffset = (atomSize + ftypAtom.size + moovAtom.size) - mdatAtom.pos;
            fixMoovAtom(moovAtom, (int) moovOffset);

            ByteArrayOutputStream headerData = new ByteArrayOutputStream();

            if (encrypt) {
                // Some garbage
                headerData.write('G');
                headerData.write('G');
            }
            headerData.write(ftypAtom.buffer);
            headerData.write(moovAtom.buffer);
            logger.debug("UUID atom size : {}", atomSize);

            byte[] b = new byte[4];
            b[3] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            b[2] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            b[1] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            b[0] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            headerData.write(b);
            headerData.write("uuid".getBytes("ISO-8859-1"));

            // now write the UUID as hex
            UUID uuid = UUID.fromString(UITS_UUID_STRING);

            headerData.write(UitsAudioFileManager.toBytes(uuid));
            headerData.write(uitsPayloadXML.getBytes("ISO-8859-1"));
            headerData.write(Arrays.copyOfRange(mdatAtom.buffer, 0, 2048));

            // Split the file
            headerOutputStream.write(headerData.toByteArray());

            audioOutputStream.write(Arrays.copyOfRange(mdatAtom.buffer, 2048, (int) mdatAtom.size));

            encodedOutputStream.write(headerData.toByteArray());
            encodedOutputStream.write(Arrays.copyOfRange(mdatAtom.buffer, 2048, (int) mdatAtom.size));

            logger.debug("Finish process");
            return 1;
        }
        catch (FileNotFoundException e) {
            logger.error("FileNotFoundException processing file: {}", e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error("IOException processing file: {}", e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int process(String inputFile, String audioFile, String headerFile, String encodedFile, UitsParameters params, String md5, boolean encrypt) throws IOException {
        logger.debug("Start process : inputFile {}, audioFile {}, headerFile {}, encodedFile {}, params {}, md5 {}, encrypt {}", inputFile, audioFile, headerFile, encodedFile, params, md5, encrypt);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream audioOutputStream = new FileOutputStream(audioFile);
        FileOutputStream headerOutputStream = new FileOutputStream(headerFile);
        FileOutputStream encodedOutputStream = new FileOutputStream(encodedFile);
        try {
            return process(inputStream, audioOutputStream, headerOutputStream, encodedOutputStream, params, md5, encrypt);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(audioOutputStream);
            IOUtils.closeQuietly(headerOutputStream);
            IOUtils.closeQuietly(encodedOutputStream);
        }
    }

	/*
     * process the header file and fix the UITS header with the given parameters
	 * return: 1: all ok 0: cannot process, no data returned to the output
	 * stream. the caller can still use that stream -1: cannot process, but some
	 * data might have been returned to the output stream. The stream is
	 * "corrupted" with that data and the transfer should be aborted.
	 */

    @Override
    public int processHeader(InputStream header, OutputStream out, UitsParameters params, String md5) {
        logger.debug("Start process header");
        try {
            Atom ftypAtom = null;
            Atom moovAtom = null;
            Atom uuidAtom = null;

            // Skip garbage
            header.skip(2);

            ftypAtom = new Atom(header);
            if (ftypAtom == null || !"ftyp".equals(ftypAtom.type)) {
                return 0;
            }
            moovAtom = new Atom(header);
            if (moovAtom == null || !"moov".equals(moovAtom.type)) {
                return 0;
            }
            uuidAtom = new Atom(header);
            if (uuidAtom == null || !"uuid".equals(uuidAtom.type)) {
                return 0;
            }

            // Some garbage
            out.write('G');
            out.write('G');

            String uitsPayloadXML = XmlPayload.buildPayload(params, md5);
            logger.debug("PayloadXML : {}", uitsPayloadXML);
            // write the UITS payload in a uuid atom
            long payloadXMLSize = uitsPayloadXML.length();
            long atomSize = payloadXMLSize + 8 + UUID_SIZE;

            long moovOffset = atomSize - uuidAtom.size;
            if (moovOffset != 0) {
                fixMoovAtom(moovAtom, (int) moovOffset);
            }

            out.write(ftypAtom.buffer);
            out.write(moovAtom.buffer);
            logger.debug("UUID atom size : {}", atomSize);

            byte[] b = new byte[4];
            b[3] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            b[2] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            b[1] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            b[0] = (byte) (atomSize & 0xff);
            atomSize >>= 8;
            out.write(b);
            out.write("uuid".getBytes("ISO-8859-1"));

            // now write the UUID as hex
            UUID uuid = UUID.fromString(UITS_UUID_STRING);

            out.write(UitsAudioFileManager.toBytes(uuid));
            out.write(uitsPayloadXML.getBytes("ISO-8859-1"));

            // copy the rest
            byte[] buffer = new byte[2048];
            int read;
            while ((read = header.read(buffer, 0, 2048)) > 0) {
                out.write(buffer, 0, read);
            }

            return 1;
        }
        catch (IOException e) {
            logger.error("processHeader IOException processing file: {}", e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public String getMediaHash(String audioFile) {
        logger.debug("Get mp4 media hash for file : {}", audioFile);
        InputStream audioInputStream = null;
        try {
            audioInputStream = new FileInputStream(audioFile);
            byte size[] = new byte[4];
            byte type[] = new byte[4];

            Crypto crypto = new Crypto("SHA-256");

            while (audioInputStream.read(size, 0, 4) > 0) {
                audioInputStream.read(type, 0, 4);
                String atomType = new String(type);
                int copySize = ByteBuffer.wrap(size).getInt() - 8;
                logger.debug("Copy atom {} size {}", atomType, copySize);

                while (copySize > 0) {
                    byte[] copyBuffer = new byte[1024];
                    int read = audioInputStream.read(copyBuffer, 0, copySize > 1024 ?
                                                                    1024 :
                                                                    copySize);
                    copySize = copySize - read;
                    if ("mdat".equals(atomType)) { // Audio: compute hash
                        crypto.addHash(copyBuffer, read);
                    }
                    if ("ftyp".equals(atomType)) { // Validate file type
                        boolean found = false;
                        String subtype = new String(copyBuffer, 0, 4);
                        for (MP4_SUBTYPES recognizedSubType : recognizedSubTypes) {
                            if (recognizedSubType.extension.equals(subtype)) {
                                found = true;
                            }
                        }
                        if (!found) {
                            logger.warn("Invalid file type: " + subtype);
                            return null;
                        }
                    }
                }
            }

            return crypto.finalizeHash();
        }
        catch (IOException e) {
            logger.error("getMediaHash IOException processing file: {}", e.getMessage(), e);
        }
        finally {
            IOUtils.closeQuietly(audioInputStream);
        }
        return null;
    }

    private void fixMoovAtom(Atom moovAtom, int offset) {
        for (int idx = 4; idx < moovAtom.size - 4; idx++) {
            byte[] buffer = Arrays.copyOfRange(moovAtom.buffer, idx, idx + 4);
            if (new String(buffer).equalsIgnoreCase("stco")) {
                int stcoSize = patchStcoAtom(moovAtom, idx, offset);
                idx += stcoSize - 4;
            }
            else if (new String(buffer).equalsIgnoreCase("co64")) {
                int co64Size = patchCo64Atom(moovAtom, idx, offset);
                idx += co64Size - 4;
            }
        }
    }

    private int patchStcoAtom(Atom ah, int idx, int offset) {
        int stcoSize = (int) bytesToLong(Arrays.copyOfRange(ah.buffer, idx - 4, idx));

        int offsetCount = (int) bytesToLong(Arrays.copyOfRange(ah.buffer, idx + 8, idx + 12));
        for (int j = 0; j < offsetCount; j++) {
            int currentOffset = (int) bytesToLong(Arrays.copyOfRange(ah.buffer, idx + 12 + j * 4, (idx + 12 + j * 4) + 4));
            currentOffset += offset;
            int offsetIdx = idx + 12 + j * 4;
            ah.buffer[offsetIdx + 0] = (byte) ((currentOffset >> 24) & 0xFF);
            ah.buffer[offsetIdx + 1] = (byte) ((currentOffset >> 16) & 0xFF);
            ah.buffer[offsetIdx + 2] = (byte) ((currentOffset >> 8) & 0xFF);
            ah.buffer[offsetIdx + 3] = (byte) ((currentOffset >> 0) & 0xFF);
        }

        return stcoSize;
    }

    private int patchCo64Atom(Atom ah, int idx, int offset) {
        int co64Size = (int) bytesToLong(Arrays.copyOfRange(ah.buffer, idx - 4, idx));

        int offsetCount = (int) bytesToLong(Arrays.copyOfRange(ah.buffer, idx + 8, idx + 12));
        for (int j = 0; j < offsetCount; j++) {
            long currentOffset = bytesToLong(Arrays.copyOfRange(ah.buffer, idx + 12 + j * 8, (idx + 12 + j * 8) + 8));
            currentOffset += offset;
            int offsetIdx = idx + 12 + j * 8;
            ah.buffer[offsetIdx + 0] = (byte) ((currentOffset >> 56) & 0xFF);
            ah.buffer[offsetIdx + 1] = (byte) ((currentOffset >> 48) & 0xFF);
            ah.buffer[offsetIdx + 2] = (byte) ((currentOffset >> 40) & 0xFF);
            ah.buffer[offsetIdx + 3] = (byte) ((currentOffset >> 32) & 0xFF);
            ah.buffer[offsetIdx + 4] = (byte) ((currentOffset >> 24) & 0xFF);
            ah.buffer[offsetIdx + 5] = (byte) ((currentOffset >> 16) & 0xFF);
            ah.buffer[offsetIdx + 6] = (byte) ((currentOffset >> 8) & 0xFF);
            ah.buffer[offsetIdx + 7] = (byte) ((currentOffset >> 0) & 0xFF);
        }

        return co64Size;
    }

    private long bytesToLong(byte[] buffer) {
        long retVal = 0;
        for (int i = 0; i < buffer.length; i++) {
            retVal += ((buffer[i] & 0x00000000000000FF) << 8 * (buffer.length - i - 1));
        }
        return retVal;
    }

    private class Atom {

        public long offset;
        public long size;
        public long pos;
        public String type;
        public byte[] buffer = null;
        boolean valid = false;

        public Atom(InputStream input) throws IOException {
            int i;
            // get atom size
            byte sizeBuffer[] = new byte[4];
            i = input.read(sizeBuffer, 0, 4);
            if (i != 4) {
                logger.debug("bytes for size read : {}", i);
                return;
            }
            size = ByteBuffer.wrap(sizeBuffer).getInt();

            // get atom type
            byte[] typeBuffer = new byte[4];
            i = input.read(typeBuffer, 0, 4);
            if (i != 4) {
                logger.debug("bytes for type read : {}", i);
                return;
            }
            type = new String(typeBuffer);
            // if (atomSize == 1) {
            // // 64 bit size. Read new size from body and store it
            // size = input.readLong();
            // }
            logger.debug("Reading buffer type : {} size {}", type, size);

            buffer = new byte[(int) size];
            buffer[0] = sizeBuffer[0];
            buffer[1] = sizeBuffer[1];
            buffer[2] = sizeBuffer[2];
            buffer[3] = sizeBuffer[3];
            buffer[4] = typeBuffer[0];
            buffer[5] = typeBuffer[1];
            buffer[6] = typeBuffer[2];
            buffer[7] = typeBuffer[3];
            i = input.read(buffer, 8, (int) size - 8);
            if (i != size - 8) {
                logger.debug("bytes read : {}", i);
                return;
            }
            valid = true;
        }
    }

    class MP4_SUBTYPES {

        public final String extension;
        public final String comment;

        public MP4_SUBTYPES(String extension, String comment) {
            this.extension = extension;
            this.comment = comment;
        }
    }

}
