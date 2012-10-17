package mobi.nowtechnologies.java.server.uits;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class MP4Manager {

	static final String uitsUUIDString = "99454E27-963A-4B56-8E76-1DB68C899CD4";
	static final int MP4_HEADER_SIZE = 8;
	static final int UUID_SIZE = 16;

	class MP4_SUBTYPES {

		public MP4_SUBTYPES(String extension, String comment) {
			this.extension = extension;
			this.comment = comment;

		}

		public String extension;
		public String comment;
	};

	MP4_SUBTYPES recognizedSubTypes[] = { new MP4_SUBTYPES("qt  ", "Apple QuickTime (.MOV/QT) File"),
			new MP4_SUBTYPES("M4A ", "Apple iTunes AAC-LC (.M4A) Audio File"),
			new MP4_SUBTYPES("M4B ", "Apple iTunes AAC-LC (.M4B) Audio Book File"), new MP4_SUBTYPES("M4V ", "Apple iTunes Video (.M4V) Video File"),
			new MP4_SUBTYPES("M4VP", "Apple iPhone (.M4V) File"), new MP4_SUBTYPES("mp42", "MP4 v2 [ISO 14496-14]") };

	@SuppressWarnings("unused")
	public int process(InputStream audioFile, OutputStream data, OutputStream header, UitsParameters params, String md5, boolean encrypt) {

		try {
			Atom ah = null;
			Atom ftypAtom = null;
			Atom moovAtom = null;
			Atom mdatAtom = null;

			boolean gotMdat = false;

			int pos = 0;

			ah = new Atom(audioFile);
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
				if (ah.valid)
					pos += ah.size;
				System.out.println("Got atom " + ah.type + " " + ah.size + " " + ah.valid);
				ah = new Atom(audioFile);
			}

			if (md5 == null) {
				Crypto crypto = new Crypto("SHA-256");
				crypto.addHash(Arrays.copyOfRange(mdatAtom.buffer, 8, (int) mdatAtom.size), (int) mdatAtom.size - 8);
				md5 = crypto.finalizeHash();
			}

			String uitsPayloadXML = XmlPayload.buildPayload(params, md5);
			/* write the UITS payload in a uuid atom */
			long payloadXMLSize = uitsPayloadXML.length();
			long atomSize = payloadXMLSize + 8 + UUID_SIZE;

			long moovOffset = (atomSize + ftypAtom.size + moovAtom.size) - mdatAtom.pos;
			fixMoovAtom(moovAtom, (int) moovOffset);

			if (encrypt) {
				// Some garbage
				header.write('G');
				header.write('G');
			}
			header.write(ftypAtom.buffer);
			header.write(moovAtom.buffer);
			// System.out.println("UUID atom size " + atomSize);
			byte[] b = new byte[4];
			b[3] = (byte) (atomSize & 0xff);
			atomSize >>= 8;
			b[2] = (byte) (atomSize & 0xff);
			atomSize >>= 8;
			b[1] = (byte) (atomSize & 0xff);
			atomSize >>= 8;
			b[0] = (byte) (atomSize & 0xff);
			atomSize >>= 8;
			header.write(b);
			header.write("uuid".getBytes("ISO-8859-1"));

			/* now write the UUID as hex */
			UUID uuid = UUID.fromString(uitsUUIDString);

			header.write(UitsAudioFileManager.toBytes(uuid));

			header.write(uitsPayloadXML.getBytes("ISO-8859-1"));

			header.write(Arrays.copyOfRange(mdatAtom.buffer, 0, 2048));

			// Split the file
			data.write(Arrays.copyOfRange(mdatAtom.buffer, 2048, (int) mdatAtom.size));
			return 1;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	/*
	 * process the header file and fix the UITS header with the given parameters
	 * return: 1: all ok 0: cannot process, no data returned to the output
	 * stream. the caller can still use that stream -1: cannot process, but some
	 * data might have been returned to the output stream. The stream is
	 * "corrupted" with that data and the transfer should be aborted.
	 */

	public int processHeader(InputStream header, OutputStream out, UitsParameters params, String md5) {

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
			/* write the UITS payload in a uuid atom */
			long payloadXMLSize = uitsPayloadXML.length();
			long atomSize = payloadXMLSize + 8 + UUID_SIZE;

			long moovOffset = atomSize - uuidAtom.size;
			if (moovOffset != 0)
				fixMoovAtom(moovAtom, (int) moovOffset);

			out.write(ftypAtom.buffer);
			out.write(moovAtom.buffer);
			// System.out.println("UUID atom size " + atomSize);
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

			/* now write the UUID as hex */
			UUID uuid = UUID.fromString(uitsUUIDString);

			out.write(UitsAudioFileManager.toBytes(uuid));

			out.write(uitsPayloadXML.getBytes("ISO-8859-1"));

			// copy the rest
			byte[] buffer = new byte[2048];
			int read;
			while ((read = header.read(buffer, 0, 2048)) > 0) {
				out.write(buffer, 0, read);
			}

			return 1;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

	public String getMediaHash(InputStream audioFile) {

		try {

			byte size[] = new byte[4];
			byte type[] = new byte[4];

			Crypto crypto = new Crypto("SHA-256");

			while (audioFile.read(size, 0, 4) > 0) {
				audioFile.read(type, 0, 4);
				String atomType = new String(type);
				int copySize = ByteBuffer.wrap(size).getInt() - 8;
				// System.out.println("Copy atom "+atomType +" "+copySize);

				while (copySize > 0) {
					byte[] copyBuffer = new byte[1024];
					int read = audioFile.read(copyBuffer, 0, copySize > 1024 ? 1024 : copySize);
					copySize = copySize - read;
					if ("mdat".equals(atomType)) { // Audio: compute hash
						crypto.addHash(copyBuffer, read);
					}
					if ("ftyp".equals(atomType)) { // Validate file type
						boolean found = false;
						String subtype = new String(copyBuffer, 0, 4);
						for (int i = 0; i < recognizedSubTypes.length; i++) {
							if (recognizedSubTypes[i].extension.equals(subtype)) {
								found = true;
							}
						}
						if (!found) {
							System.err.println("Invalide file type: " + subtype);
							return null;
						}

					}
				}

			}

			String mediaHash = crypto.finalizeHash();

			return mediaHash;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private static void fixMoovAtom(Atom moovAtom, int offset) {

		int idx = 0;
		for (idx = 4; idx < moovAtom.size - 4; idx++) {
			byte[] buffer = Arrays.copyOfRange(moovAtom.buffer, idx, idx + 4);
			if (new String(buffer).equalsIgnoreCase("stco")) {
				int stcoSize = patchStcoAtom(moovAtom, idx, offset);
				idx += stcoSize - 4;
			} else if (new String(buffer).equalsIgnoreCase("co64")) {
				int co64Size = patchCo64Atom(moovAtom, idx, offset);
				idx += co64Size - 4;
			}
		}

	}

	private static int patchStcoAtom(Atom ah, int idx, int offset) {
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

	private static int patchCo64Atom(Atom ah, int idx, int offset) {
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

	private static long bytesToLong(byte[] buffer) {

		long retVal = 0;

		for (int i = 0; i < buffer.length; i++) {
			retVal += ((buffer[i] & 0x00000000000000FF) << 8 * (buffer.length - i - 1));
		}

		return retVal;

	}

	public static class Atom {

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
				return;
			}
			size = ByteBuffer.wrap(sizeBuffer).getInt();

			// get atom type
			byte[] typeBuffer = new byte[4];
			i = input.read(typeBuffer, 0, 4);
			if (i != 4) {
				return;
			}
			type = new String(typeBuffer);
			// if (atomSize == 1) {
			// // 64 bit size. Read new size from body and store it
			// size = input.readLong();
			// }
			System.out.println("Reading buffer " + type + " " + (size));
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
				return;
			}
			valid = true;

		}

		public void fillBuffer(RandomAccessFile input) throws IOException {
			buffer = new byte[(int) size];
			input.readFully(buffer);
		}

	}

}
