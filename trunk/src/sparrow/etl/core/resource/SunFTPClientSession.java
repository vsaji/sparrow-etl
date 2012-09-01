package sparrow.etl.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.vo.FTPParamHolder;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SunFTPClientSession
    implements FTPClientSession {

  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      SunFTPClientSession.class);

  private final FTPParamHolder ftpParamHolder;
  private FtpClient ftp = null;
  /**
   *
   * @param ftpParamHolder FTPParamHolder
   */
  public SunFTPClientSession(FTPParamHolder ftpParamHolder) {
    this.ftpParamHolder = ftpParamHolder;
  }

  /**
   * changeDirectory
   *
   * @param directory String
   */
  public void changeDirectory(String directory) {
    try {
      if(logger.isDebugEnabled()){
        logger.info("Changing directory to [" + directory + "]");
      }
      ftp.cd(directory);
    }
    catch (IOException ex) {
      throw new SparrowRuntimeException(
          "Unable to change directory to [" + directory + "]", ex);
    }
  }

  /**
   * changeMode
   *
   * @param mode int
   */
  public void changeMode(int mode) {
    try {
      if (FTPClientSession.BINARY == mode) {
        ftp.binary();
      }
      if(FTPClientSession.ASCII == mode) {
        ftp.ascii();
      }
    }
    catch (IOException ex) {
      throw new SparrowRuntimeException(
          "Unable to change mode to [" + mode + "]", ex);
    }

  }

  /**
   * close
   */
  public void close() {
    try {
      ftp.closeServer();
      ftp = null;
      logger.info("FTP connection has been disconnected.");
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * open
   */
  public void open() {
    ftp = new FtpClient();

    logger.info("Connecting host [" + ftpParamHolder.getHostName() + "]");

    /********** Connecting to Host *************************/
    try {
      ftp.openServer(ftpParamHolder.getHostName());
    }
    catch (Exception e) {
      throw new SparrowRuntimeException(
          "Unable to establish connection with host [" +
          ftpParamHolder.getHostName() + "]", e);
    }

    /********** Logging in *************************/
    try {
      logger.info("Attempting login using [" + ftpParamHolder.getUserName() +
                  "]");
      ftp.login(ftpParamHolder.getUserName(), ftpParamHolder.getPassword()); // login
      logger.info("Login Successfull.");
    }
    catch (Exception e) {
      throw new SparrowRuntimeException(
          "Login failed [" + e.getMessage() + "]", e);
    }
  }

  /**
   * upload
   *
   * @param file File
   */
  public void upload(File file) {
    try {
      logger.info("Uploading file [" + file.getName() + "]");
      TelnetOutputStream out = ftp.put(file.getName());
      upload(out, file);
    }
    catch (Exception ex) {
      throw new SparrowRuntimeException(
          "Exception while uploading file [" + file.getName() + "]", ex);
    }
  }



  /**
   *
   * @param out OutputStream
   * @param file File
   * @throws Exception
   */
  private void upload(OutputStream out, File file) throws Exception {
    InputStream in = new FileInputStream(file);
    byte c[] = new byte[4096];
    int read = 0;
    while ( (read = in.read(c)) != -1) {
      out.write(c, 0, read);
    }
    in.close(); //close the io streams
    out.close();
  }


}
