package sparrow.elt.impl.writer;

import java.io.File;
import javax.mail.Session;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.exception.SparrowRuntimeException;
import sparrow.elt.core.log.SparrowLogger;
import sparrow.elt.core.log.SparrowrLoggerFactory;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.Constants;
import sparrow.elt.core.util.Mailer;
import sparrow.elt.core.util.SparrowUtil;
import sparrow.elt.core.vo.EmailContentHolder;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class EmailWriter
    extends AbstractEOAWriter {

  protected String[] files = null;
  protected static final SparrowLogger logger = SparrowrLoggerFactory.
      getCurrentInstance(EmailWriter.class);
  protected String emailTo, emailCC, emailBCC, emailFrom, emailContent,
      emailSubject, resource, emailContentType, emailMode, shell;
  protected boolean attachmentExist = false;

  /**
   *
   * @param config SparrowDataWriterConfig
   */
  public EmailWriter(SparrowDataWriterConfig config) {
    super(config);
    validateParam();

    emailMode = SparrowUtil.performTernary(config.getInitParameter(),
                                         ConfigKeyConstants.PARAM_EMAIL_MODE,
                                         Constants.UNIX);

    if (emailMode.equals(Constants.SMTP)) {
      SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_RESOURCE}
                              , "EmailWriter", config.getInitParameter());
    }
  }

/**
 *
 *
 */
  protected void validateParam(){
	  SparrowUtil.validateParam(new String[] {ConfigKeyConstants.PARAM_EMAIL_TO,
              ConfigKeyConstants.PARAM_EMAIL_FROM,
              ConfigKeyConstants.PARAM_EMAIL_SUBJECT,
              ConfigKeyConstants.PARAM_EMAIL_CONTENT}
              , "EmailWriter", config.getInitParameter());

  }
  /**
   *
   */
  public void initialize() {

    if (config.getInitParameter().isParameterExist(ConfigKeyConstants.
        PARAM_ATTACHMENT)) {
      files = getFileNamesFromFileWriter(config.getInitParameter().
                                         getParameterValue(ConfigKeyConstants.
          PARAM_ATTACHMENT));
      attachmentExist = true;
    }

    emailTo = config.getInitParameter().getParameterValue(ConfigKeyConstants.
        PARAM_EMAIL_TO);
    emailCC = config.getInitParameter().getParameterValue(ConfigKeyConstants.
        PARAM_EMAIL_CC);
    emailBCC = config.getInitParameter().getParameterValue(ConfigKeyConstants.
        PARAM_EMAIL_BCC);
    emailFrom = config.getInitParameter().getParameterValue(ConfigKeyConstants.
        PARAM_EMAIL_FROM);
    emailContent = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_EMAIL_CONTENT);

    emailSubject = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_EMAIL_SUBJECT);

    resource = config.getInitParameter().getParameterValue(ConfigKeyConstants.
        PARAM_RESOURCE);
    emailContentType = SparrowUtil.performTernary(config.getInitParameter(),
                                                ConfigKeyConstants.
                                                PARAM_EMAIL_CONTENT_TYPE,
                                                "text/plain");

    shell = SparrowUtil.performTernary(config.getInitParameter(),
                                     ConfigKeyConstants.PARAM_UNIX_SHELL,
                                     "/bin/csh");

  }

  /**
   *
   * @param flag int
   */
  public void doEndOfProcess() {

    emailContent = replaceToken(emailContent);
    emailSubject = replaceToken(emailSubject);

    sendMail();

  }

  /**
   *
   *
   */
  protected void sendMail() {
  if (emailMode.equals(Constants.UNIX)) {
      sendUnixMail();
    }
    else {
      sendSMTPMail();
    }
  }

  /**
   *
   */
  protected void sendUnixMail() {
    Mailer.sendUNIXMail(emailFrom, emailTo, emailCC, emailBCC, emailSubject,
                        emailContent, shell, files);
    //logSendMessage();
  }

  /**
   *
   */
  protected void sendSMTPMail() {
    try {

      EmailContentHolder ech = new EmailContentHolder();
      ech.setToAddressAsString(emailTo);
      ech.setFromAddress(emailFrom);
      ech.setSubject(emailSubject);
      ech.setContentType(emailContentType);
      ech.setContent(emailContent);

      if (emailBCC != null) {
        ech.setBccAddressAsString(emailBCC);
      }

      if (emailCC != null) {
        ech.setCcAddressAsString(emailCC);
      }

      if (attachmentExist) {
        for (int i = 0; i < files.length; i++) {
          File fl = new File(files[i]);
          ech.addAttachment(fl, fl.getName());
        }
      }

      Session s = (Session) config.getContext().getResource(resource).
          getResource();
      Mailer.sendSMTPMail(ech, s);
      //logSendMessage();
    }
    catch (Exception e) {
      logger.error("Email Sending failed",e);
      e.printStackTrace();
      throw new SparrowRuntimeException(
          "Exception occured while send email", e);
    }

  }

  /**
   *
   */
  protected void logSendMessage() {
    logger.info("Email has been sent. From ID [" + emailFrom + "],To ID(s) [" + emailTo + "], CC(s) [" +
                emailCC + "], BCC(s) [" + emailBCC + "], No. attachments [" +
                ((files!=null && files.length>0) ? files.length : 0) + "]");
  }
  
  
  /**
   * 
   */
  protected void disableAttachments() {
	  attachmentExist = false;
	  files = null;
  }
}
