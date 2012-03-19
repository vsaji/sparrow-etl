package sparrow.elt.core.vo;

import sparrow.elt.core.config.ConfigParam;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.SparrowUtil;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class FTPParamHolder {

  private String hostName, userName, password;
  private int ftpPort, scpPort, sftpPort;

  /**
   *
   * @param param ConfigParam
   */
  public FTPParamHolder(ConfigParam param) {
    hostName = param.getParameterValue(ConfigKeyConstants.
                                       PARAM_HOST_NAME);
    userName = param.getParameterValue(
        ConfigKeyConstants.PARAM_USER_NAME);
    password = param.getParameterValue(
        ConfigKeyConstants.PARAM_PASSWORD);
    ftpPort = SparrowUtil.performTernary(param, ConfigKeyConstants.PARAM_PORT, 21);
    scpPort = SparrowUtil.performTernary(param, ConfigKeyConstants.PARAM_PORT, 22);
    sftpPort = SparrowUtil.performTernary(param, ConfigKeyConstants.PARAM_PORT, 22);
  }

  /**
   *
   * @param server String
   * @param userName String
   * @param password String
   * @param secure boolean
   */
  public FTPParamHolder(String hostName, String userName, String password,
                        int ftpPort, int scpPort, int sftpPort) {
    this.hostName = hostName;
    this.userName = userName;
    this.password = password;
    this.ftpPort = ftpPort;
    this.scpPort = scpPort;
    this.sftpPort= sftpPort;
  }

  public String getHostName() {
    return hostName;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public int getScpPort() {
    return scpPort;
  }

  public int getFtpPort() {
    return ftpPort;
  }

  public int getSftpPort() {
    return sftpPort;
  }

}
