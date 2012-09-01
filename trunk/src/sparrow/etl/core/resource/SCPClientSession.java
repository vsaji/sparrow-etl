package sparrow.etl.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import sparrow.etl.core.exception.SparrowRuntimeException;
import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.vo.FTPParamHolder;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SCPClientSession {

  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      SCPClientSession.class);

  //private SshClient ssh = null;
  protected final FTPParamHolder ftpParamHolder;
  protected Session session = null;

  /**
   *
   * @param ftpParamHolder FTPParamHolder
   */
  public SCPClientSession(FTPParamHolder ftpParamHolder) {
    this.ftpParamHolder = ftpParamHolder;
  }

  /**
   * close
   */
  public void close() {
    try {
      session.disconnect();
      //ssh.disconnect();
      logger.info("SCP connection has been disconnected.");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   *
   * @return int
   */
  protected int getPort(){
    return ftpParamHolder.getScpPort();
  }
  /**
   * open
   */
  public void open() {

    JSch jsch = new JSch();
    try {
      session = jsch.getSession(ftpParamHolder.getUserName(),
                                ftpParamHolder.getHostName(),
                                getPort());
      UserInfo ui = new UserInfoImpl();
      session.setUserInfo(ui);
      session.setTimeout(10000);
      session.connect();
      logger.info("Secure Connection has been established for host [" + ftpParamHolder.getHostName() + "]");
    }
    catch (Exception ex) {
      throw new SparrowRuntimeException("Unable to open SCP connection [" +
                                      ftpParamHolder.getHostName() + ":" +
                                      ftpParamHolder.getScpPort() + "]", ex);

    }

//    ssh = new SshClient();
//    try {
//      logger.info("Connecting host [" + ftpParamHolder.getHostName() + "]");
//      ssh.connect(ftpParamHolder.getHostName(), new IgnoreHostKeyVerification());
//
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
//                                        ftpParamHolder.getPort() + " " +
//                                        ftpParamHolder.getUserName() +
//                                        " failed");
//      }
//      logger.info("Authenticated.");
//    }
//    catch (SparrowRuntimeException ex) {
//      throw ex;
//    }
//    catch (Exception ex) {
//      throw new SparrowRuntimeException("Unable to open SCP connection [" +
//                                      ftpParamHolder.getHostName() + ":" +
//                                      ftpParamHolder.getPort() + "]",ex);
//    }

  }

  /**
   * upload
   *
   * @param file File
   */
  public void upload(String localFile, String remoteFile) {
    try {

      logger.info("Uploading file [" + localFile + "]");

      String command = "scp -p -t " + remoteFile;
      //logger.info("Session Status[" + session.isConnected() + "]");
      Channel channel = session.openChannel("exec");
      ( (ChannelExec) channel).setCommand(command);

      // get I/O streams for remote scp
      OutputStream out = channel.getOutputStream();
      InputStream in = channel.getInputStream();

      channel.connect();

      // send "C0644 filesize filename", where filename should not include '/'
      long filesize = (new File(localFile)).length();
      command = "C0644 " + filesize + " ";
      if (localFile.lastIndexOf('/') > 0) {
        command += localFile.substring(localFile.lastIndexOf('/') + 1);
      }
      else {
        command += localFile;
      }
      command += "\n";
      out.write(command.getBytes());
      out.flush();
      // send a content of lfile
      FileInputStream fis = new FileInputStream(localFile);
      byte[] buf = new byte[1024];
      while (true) {
        int len = fis.read(buf, 0, buf.length);
        if (len <= 0) {
          break;
        }
        out.write(buf, 0, len); //out.flush();
      }
      fis.close();
      fis = null;
      // send '\0'
      buf[0] = 0;
      out.write(buf, 0, 1);
      out.flush();
      out.close();
      Thread.sleep(3000);
      channel.disconnect();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new SparrowRuntimeException(
          "Exception while uploading file [" + localFile + "]", ex);
    }

//    try {
//      ScpClient client = ssh.openScpClient();
//      logger.info("Uploading file [" + localFile + "]");
//      client.put(localFile, remoteFile, false);
//      client = null;
//    }
//    catch (Exception ex) {
//      throw new SparrowRuntimeException(
//          "Exception while uploading file [" + localFile + "]", ex);
//    }

  }

  /**
   *
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 1.0
   */
  protected class UserInfoImpl
      implements UserInfo {
    /**
     * getPassphrase
     *
     * @return String
     */
    public String getPassphrase() {
      return ftpParamHolder.getUserName();
    }

    /**
     * getPassword
     *
     * @return String
     */
    public String getPassword() {
      return ftpParamHolder.getPassword();
    }

    /**
     * promptPassword
     *
     * @param string String
     * @return boolean
     */
    public boolean promptPassword(String string) {
      if (logger.isDebugEnabled()) {
        logger.debug("Password:" + string);
      }
      return true;
    }

    /**
     * promptPassphrase
     *
     * @param string String
     * @return boolean
     */
    public boolean promptPassphrase(String string) {
      if (logger.isDebugEnabled()) {
        logger.debug("Passphrase:" + string);
      }
      return true;
    }

    /**
     * promptYesNo
     *
     * @param string String
     * @return boolean
     */
    public boolean promptYesNo(String string) {
      if (logger.isDebugEnabled()) {
        logger.debug("YesNo:" + string);
      }
      return true;
    }

    /**
     * showMessage
     *
     * @param string String
     */
    public void showMessage(String string) {
      if (logger.isDebugEnabled()) {
        logger.debug("Message:" + string);
      }
    }

  }

}
