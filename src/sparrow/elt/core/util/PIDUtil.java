package sparrow.elt.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * 
 * @author vsaji
 * 
 */
public class PIDUtil {
	public static String getPID() {
		String pid = System.getProperty("pid"); // NOI18N
		try {
			if (pid == null) {
				String cmd[]; 
				File tempFile = null;
				if (!Constants.OS.startsWith(Constants.OS_WINDOW)) {
					cmd = new String[] { "/bin/sh", "-c", "echo $$ $PPID" }; // NOI18N
				} else {
					tempFile = File.createTempFile("getpids", "exe"); // NOI18N
					pump(PIDUtil.class.getResourceAsStream("getpids.exe"),
							new FileOutputStream(tempFile), true, true); // NOI18N
					cmd = new String[] { tempFile.getAbsolutePath() };
				}
				if (cmd != null) {
					Process p = Runtime.getRuntime().exec(cmd);
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					pump(p.getInputStream(), bout, false, true);
					if (tempFile != null)
						tempFile.delete();

					StringTokenizer stok = new StringTokenizer(bout.toString());
					stok.nextToken(); // this is pid of the process we spanned
					pid = stok.nextToken();
					if (pid != null)
						System.setProperty("pid", pid); // NOI18N
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			pid="17071979";
		}
		return pid;
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(getPID());
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param closeIn
	 * @param closeOut
	 * @throws IOException
	 */
	public static void pump(InputStream in, OutputStream out, boolean closeIn,
			boolean closeOut) throws IOException {
		byte[] bytes = new byte[1024];
		int read;
		try {
			while ((read = in.read(bytes)) != -1)
				out.write(bytes, 0, read);
		} finally {
			if (closeIn)
				in.close();
			if (closeOut)
				out.close();
		}
	}

}