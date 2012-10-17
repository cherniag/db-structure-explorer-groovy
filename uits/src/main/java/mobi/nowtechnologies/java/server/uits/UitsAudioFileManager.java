package mobi.nowtechnologies.java.server.uits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class UitsAudioFileManager {

	public static byte[] toBytes(UUID uuid) {
		byte[] bytesOriginal = asByteArray(uuid);
		return bytesOriginal;
		/*
		byte[] bytes = new byte[16];

		// Reverse the first 4 bytes
		bytes[0] = bytesOriginal[0];
		bytes[1] = bytesOriginal[1];
		bytes[2] = bytesOriginal[2];
		bytes[3] = bytesOriginal[3];
		// Reverse 6th and 7th
		bytes[4] = bytesOriginal[4];
		bytes[5] = bytesOriginal[5];
		// Reverse 8th and 9th
		bytes[6] = bytesOriginal[6];
		bytes[7] = bytesOriginal[7];
		// Copy the rest straight up
		for (int i = 8; i < 16; i++) {
			bytes[i] = bytesOriginal[i];
		}

		return bytes;*/
	}

	private static byte[] asByteArray(UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++) {
			buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;

	}

	public static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}


}
