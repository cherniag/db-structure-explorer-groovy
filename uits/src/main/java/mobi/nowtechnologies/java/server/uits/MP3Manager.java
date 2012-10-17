package mobi.nowtechnologies.java.server.uits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MP3Manager {

	static final long bitrates[] = { 0, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, -1 };
	static final long samplerates[] = { 44100, 48000, 32000, 0 };
	static final int MP3_HEADER_SIZE = 10;
	static final String uitsUUIDString = "99454E27-963A-4B56-8E76-1DB68C899CD4";
	static final int EXTENDED_HEADER_FLAG = 0x40;

	enum FRAME_TYPE {AUDIOFRAME, ID3V2TAG, ID3FRAME, ID3V1TAG, PADDING};

	class MP3_ID3_HEADER {
		int majorVersion;
		int minorVersion;
		char flags;
		long size;
	};

	class MP3_AUDIO_FRAME_HEADER {
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
	};



	public int process(InputStream audioFile, OutputStream out, UitsParameters params, String mediaHash) {

		try {
			MP3_ID3_HEADER id3Header = new MP3_ID3_HEADER();

			byte[] privateFrame = new byte[5000];
			int privateSize = getPrivateFrame(params, mediaHash, privateFrame);
			byte[] ID3Header = new byte[MP3_HEADER_SIZE];
			audioFile.read(ID3Header, 0, MP3_HEADER_SIZE);
			mp3ReadID3Header(id3Header, ID3Header);
			long copySize = id3Header.size;
			/* Update the ID3V2 tag size to add the UITS private TAG size*/
			id3Header.size = id3Header.size + privateSize;

			mp3WriteID3Header(out, id3Header);
			
			// Copy all ID3 headers
			byte[] copyBuffer = new byte[1024];
			while (copySize > 0) {
				int read = audioFile.read(copyBuffer, 0, (int) (copySize > 1024 ? 1024 : copySize));
				out.write(copyBuffer, 0, read);
				copySize -= read;
			}

			// Write UITS header
			out.write(privateFrame, 0, privateSize);
			//out.write(ID3Header, 0, MP3_HEADER_SIZE);

			// Copy remaing data
			int readSize = audioFile.read(copyBuffer, 0, 1024);
			while (readSize > 0) {
				out.write(copyBuffer, 0, readSize);
				readSize = audioFile.read(copyBuffer, 0, 1024);
			}

			return 1;
		} catch (FileNotFoundException e) {
			System.err.println("File not found "+e.getMessage());
		} catch (IOException e) {
			System.err.println("IO error "+e.getMessage());
		}
		return 0;

	}

	void mp3WriteID3Header(OutputStream out, MP3_ID3_HEADER mp3Header) throws IOException {

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

		// vprintf("MP3 Header Flags byte: %02x\n", mp3Header->flags);

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

	int getPrivateFrame(UitsParameters params, String mediaHash, byte[] frame) {
		String uitsPayloadXML = XmlPayload.buildPayload(params, mediaHash);
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

		/* write a null terminator for the email */
		frame[10] = 0;

		/* write the frame data UITS xml string */
		System.arraycopy(uitsPayloadXML.getBytes(), 0, frame, 11, privFrameuitsLen);

		frame[fullsize - 1] = 0;
		return fullsize;
	}

	FRAME_TYPE mp3IdentifyFrame(byte[] header) throws IOException

	{
		FRAME_TYPE type;
		if ((header[0] == 0xff) && ((header[1] & 0xe0) == 0xe0)) // Sync bytes -
		{
			type = FRAME_TYPE.AUDIOFRAME;
		} else if (header[0] == 'I' && header[1] == 'D' && header[2] == '3') { // outer
			type = FRAME_TYPE.ID3V2TAG;
		} else if (header[0] == 'T' && header[1] == 'A' && header[2] == 'G') { // outer
			type = FRAME_TYPE.ID3V1TAG;
		} else if (header[0] == (char) 0) {
			type = FRAME_TYPE.PADDING;
		} else {
			System.err.println("Cannot identify frame type");
			type = FRAME_TYPE.ID3FRAME; // no other choices, must be this
		}
		return type;
	}

	public String mp3GetMediaHash(String audioFileName) throws IOException {
		FileInputStream audioFP;
		MP3_ID3_HEADER mp3ID3Header = new MP3_ID3_HEADER();
		MP3_AUDIO_FRAME_HEADER mp3AudioFrameHeader;
		int audioFrameStart;
		String mediaHashString = null;
		int foundPadBytes = 0;
		int id3v1TagCount;

		audioFP = new FileInputStream(audioFileName);

		byte[] buffer = new byte[MP3_HEADER_SIZE];
		audioFP.read(buffer, 0, MP3_HEADER_SIZE);
		/* The file should start with an ID3 tag header */
		mp3ReadID3Header(mp3ID3Header, buffer);

		/*
		 * seek to the first audio frame, which will be after the MP3 ID3 tag
		 * header (10 bytes)
		 */
		audioFrameStart = (int) (MP3_HEADER_SIZE + mp3ID3Header.size);
		audioFP.skip(audioFrameStart - MP3_HEADER_SIZE);
		System.out.println("Header size "+mp3ID3Header.size);

		byte[] pad = new byte[1];
		audioFP.read(pad, 0, 1);
		//System.out.println(Integer.toHexString(0xff & pad[0])+" "+pad[0]);
		while (pad[0] == 0) {
			foundPadBytes++;
			audioFP.read(pad, 0, 1);
			//System.out.println(Integer.toHexString(0xff & pad[0])+" "+pad[0]);
		}

		if (foundPadBytes > 0) {
			System.out.println("Warning: Illegal MP3 file. ID3 frame header size does not include pad bytes");
		}
		byte[] headerBuf = new byte[4];
		headerBuf[0] = pad[0];
		audioFP.read(headerBuf, 1, 3);

		mp3AudioFrameHeader = mp3ReadAudioFrameHeader(headerBuf);
		byte[] frame0 = new byte[(int) mp3AudioFrameHeader.frameLength];
		for (int i = 0; i < 4; i++) { // Put header back
			frame0[i] = headerBuf[i];
		}
		audioFP.read(frame0, 4, (int) mp3AudioFrameHeader.frameLength - 4);

		File temp = new File(audioFileName);
		byte[] audio = new byte[(int) temp.length()];
		int offset = 0;
		/* if the first frame of audio is a VBR frame (XING, Info, VBR), skip it */
		if (!mp3IsVBRFrame(frame0, mp3AudioFrameHeader)) {
			//System.out.println("Not VBR");
			offset = (int) mp3AudioFrameHeader.frameLength;
			for (int i = 0; i < mp3AudioFrameHeader.frameLength; i++)
				audio[i] = frame0[i];
		} else {
			//System.out.println("VBR");
		}
		//System.out.println(offset+" "+temp.length() );
		int nread = audioFP.read(audio, offset, (int) temp.length()-offset);
//		for (int i = 0; i < 10; i++) {
//			System.out.print(Integer.toHexString(0xFF & audio[i]) + " ");
//		}

		//		
		// /* skip the 128 byte ID3v1 tag at the end of the file, if it's there
		// */
		// /* note that some files have been found to have more than 1 ID3V1
		// tag, so don't include any of them */
		id3v1TagCount = mp3GetID3V1TagCount(audio, offset + nread);
		int totalLen = offset + nread;
		totalLen -= id3v1TagCount * 128;

		Crypto crypto = new Crypto("SHA-256");
		crypto.addHash(audio, totalLen);

		mediaHashString = crypto.finalizeHash();
		return (mediaHashString);
	}

	int mp3GetID3V1TagCount(byte[] buffer, int size) {

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
			System.out.println("WARNING: found more than 1 ID3V1 tags at end of file\n");
		}
		return (id3v1TagCount);
	}

	boolean mp3IsVBRFrame(byte[] audiobuf, MP3_AUDIO_FRAME_HEADER frameHeader) {
		int aindex;

		if (frameHeader.stereoFlag == 1)
			aindex = 32 + 4;
		else
			aindex = 17 + 4;

		if (audiobuf[aindex] == 'X' && audiobuf[aindex + 1] == 'i' && audiobuf[aindex + 2] == 'n' && audiobuf[aindex + 3] == 'g') {
			return true;
		} else if (audiobuf[aindex] == 'I' && audiobuf[aindex + 1] == 'n' && audiobuf[aindex + 2] == 'f' && audiobuf[aindex + 3] == 'o') {
			return true;
		} else if (audiobuf[aindex] == 'V' && audiobuf[aindex + 1] == 'B' && audiobuf[aindex + 2] == 'R' && audiobuf[aindex + 3] == 'I') {
			return true;
		}

		return false;
	}

	void mp3ReadID3Header(MP3_ID3_HEADER mp3Header, byte[] buffer) throws IOException {

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

			/* print a warning for the extended header, if it is there */
			if ((mp3Header.flags & 0x40) > 0) {
				System.out.println("WARNING: MP3 file contains an extended header\n");
			}

		} else {
			System.out.println("File does not start with ID3 tag"+Integer.toHexString(0xff & buffer[0])+" "+Integer.toHexString(0xff & buffer[1]));
		}

	}

	MP3_AUDIO_FRAME_HEADER mp3ReadAudioFrameHeader(byte[] header) throws IOException {
		MP3_AUDIO_FRAME_HEADER frameHeader = new MP3_AUDIO_FRAME_HEADER();
		// function was called
		byte bytebuf;


		// Make sure this is an audio frame header
		// Sync bytes - start of an MP3 audio frame, always eleven 1's.

		if (!(((header[0] & 0xff) == 0xff) && ((header[1] & 0xe0) == 0xe0))) {
			System.out.println("Couldn't read audio frame header got "+Integer.toHexString(0xff & header[0])+" "+Integer.toHexString(0xff & header[1]));
			System.out.println("Does not match "+Integer.toHexString(0xff & header[0])+" "+Integer.toHexString(0xff & header[1] & 0xe0));
		}

		bytebuf = (byte) ((header[1] >> 3) & 0x03);
		if (bytebuf == 3)
			frameHeader.mpeg1Flag = 1;
		else
			frameHeader.mpeg1Flag = 0;

		bytebuf = (byte) ((header[1] >> 1) & 0x03);
		frameHeader.layer3Flag = bytebuf;

		bytebuf = (byte) (header[1] & 0x01);
		frameHeader.crcFlag = bytebuf;

		bytebuf = (byte) ((header[2] >> 4) & 0x0f);
		frameHeader.bitrate = bitrates[(int) bytebuf];

		bytebuf = (byte) ((header[2] >> 2) & 0x03);
		frameHeader.samplerate = samplerates[(int) bytebuf];
		if (frameHeader.samplerate == 0) {
			System.err.println("Cannot get samplerate for index "+bytebuf);
			frameHeader.samplerate=44100;
		}

		// bytebuf = header[2] & 0x02;
		// frameHeader->paddedFlag = bytebuf;

		// bytebuf = header[2] & 0x01;
		// frameHeader->privateFlag = bytebuf;

		bytebuf = (byte) ((header[3] >> 6) & 0x03);
		// frameHeader.chanmode = channel_modes[(int) bytebuf];

		if ((int) bytebuf < 3) // not a mono file
			frameHeader.stereoFlag = 1;
		else
			frameHeader.stereoFlag = 0;
		//
		// bytebuf = (header[3] >> 4) & 0x03;
		// frameHeader->modeExtension = mode_extensions[(int) bytebuf];
		//			
		// bytebuf = (header[3] >> 3) & 0x01;
		// frameHeader->copyrightFlag = bytebuf;
		//			
		// bytebuf = (header[3] >> 2) & 0x01;
		// frameHeader->origFlag = bytebuf;

		// bytebuf = (header[3]& 0x03);
		// frameHeader->emphasis = emphasis[(int) bytebuf];

		if (frameHeader.samplerate == 0) {
			System.err.println("Cannot get bitrate for index ");
		}
		frameHeader.frameLength = ((144L * frameHeader.bitrate) / frameHeader.samplerate) + frameHeader.paddedFlag;

		// next_frame = (ftello(fpin)) + frame_length - 4L;

		// frameHeader->vbrHeaderflag = mp3IsVBRFrame (fpin, frameHeader);

		return (frameHeader);

	}

}
