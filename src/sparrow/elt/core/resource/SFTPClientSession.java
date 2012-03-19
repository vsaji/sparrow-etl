package sparrow.elt.core.resource;

import java.io.File;
import java.io.FileInputStream;

import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.vo.FTPParamHolder;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SFTPClientSession
    extends SCPClientSession
    implements FTPClientSession {

  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      SFTPClientSession.class);

//  private SftpClient client = null;
//  private SshClient ssh = null;

  private ChannelSftp ftp = null;

  /**
   *
   * @param ftpParamHolder FTPParamHolder
   */
  public SFTPClientSession(FTPParamHolder ftpParamHolder) {
    super(ftpParamHolder);
  }

  /**
   * changeDirectory
   *
   * @param directory String
   */
  public void changeDirectory(String directory) {
    try {
      logger.info("Changing directory to [" + directory + "]");
      ftp.cd(directory);
      //client.cd(directory);
    }
    catch (Exception ex) {
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

  }

  /**
   *
   * @return int
   */
  protected int getPort() {
    return ftpParamHolder.getSftpPort();
  }

  /**
   * close
   */
  public void close() {
    try {
      ftp.disconnect();
      session.disconnect();
      //client.quit();
      //ssh.disconnect();
      logger.info("FTP connection has been disconnected.");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * open
   */
  public void open() {
    try {
      super.open();
      Channel channel = session.openChannel("sftp");
      channel.connect();
      ftp = (ChannelSftp) channel;
    }
    catch (Exception e) {

    }

//    ssh = new SshClient();
//    try {
//      logger.info("Connecting host [" + ftpParamHolder.getHostName() + "]");
//      ssh.connect(ftpParamHolder.getHostName(), ftpParamHolder.getFtpPort());
//      //Authenticate
//      PasswordAuthenticationClient passwordAuthenticationClient = new
//          PasswordAuthenticationClient();
//      passwordAuthenticationClient.setUsername(ftpParamHolder.getUserName());
//      passwordAuthenticationClient.setPassword(ftpParamHolder.getPassword());
//
//      int result = ssh.authenticate(passwordAuthenticationClient);
//
//      if (result != AuthenticationProtocolState.COMPLETE) {
//        throw new SparrowRuntimeException("Login to " +
//                                        ftpParamHolder.getHostName() + ":" +
//                                        ftpParamHolder.getFtpPort() + " " +
//                                        ftpParamHolder.getUserName() +
//                                        " failed");
//      }
//      logger.info("Authenticated.");
//      client = ssh.openSftpClient();
//    }
//    catch (SparrowRuntimeException ex) {
//      throw ex;
//    }
//    catch (Exception ex) {
//      throw new SparrowRuntimeException("Unable to open SFTP connection [" +
//                                      ftpParamHolder.getHostName() + ":" +
//                                      ftpParamHolder.getFtpPort() + "]",ex);
//    }
//
  }

  /**
   * upload
   *
   * @param file File
   */
  public void upload(File file) {
    try {
      logger.info("Uploading file [" + file.getName() + "]");
      ftp.put(new FileInputStream(file), file.getName());
      //client.put(new FileInputStream(file), file.getName());
    }
    catch (Exception ex) {
      throw new SparrowRuntimeException(
          "Exception while uploading file [" + file.getName() + "]", ex);
    }

  }
}
