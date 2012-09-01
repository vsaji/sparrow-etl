package sparrow.etl.core.util;

import java.io.File;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import sparrow.etl.core.log.SparrowLogger;
import sparrow.etl.core.log.SparrowrLoggerFactory;
import sparrow.etl.core.vo.EmailContentHolder;
import sparrow.etl.core.vo.EmailContentHolder.Attachment;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class Mailer {

  private static SparrowLogger logger = SparrowrLoggerFactory.getCurrentInstance(
      Mailer.class);

  private static final String DEFAULT_SHELL = "/bin/csh";

  /**
   *
   * @param to String
   * @param subject String
   * @param body String
   */
  public static final void sendUNIXMail(String to, String subject, String body) {
    sendUNIXMail(null, to, null, null, subject, body, DEFAULT_SHELL);
  }

  public static final void sendUNIXMail(String from, String to, String cc,
                                        String bcc, String subject,
                                        String body) {
    sendUNIXMail(from, to, cc, bcc, subject, body, DEFAULT_SHELL);
  }

  /**
   *
   * @param to String
   * @param subject String
   * @param body String
   * @param shell String
   */
  public static final void sendUNIXMail(String from, String to, String cc,
                                        String bcc, String subject,
                                        String body,
                                        String shell) {
    sendUNIXMail(from, to, cc, bcc, subject, body, shell, null);
  }

  /**
   *
   * @param to String
   * @param subject String
   * @param body String
   * @param attachments String[]
   */
  public static final void sendUNIXMail(String to, String subject, String body,
                                        String[] attachments) {
    sendUNIXMail(null, to, null, null, subject, body, DEFAULT_SHELL,
                 attachments);
  }

  /**
   *
   * @param to String
   * @param subject String
   * @param body String
   * @param attachments String[]
   */
  public static final void sendUNIXMail(String from, String to, String subject,
                                        String body, String[] attachments) {
    sendUNIXMail(from, to, null, null, subject, body, DEFAULT_SHELL,
                 attachments);
  }

  /**
   *
   * @param from String
   * @param to String
   * @param cc String
   * @param bcc String
   * @param subject String
   * @param body String
   * @param attachments String[]
   */
  public static final void sendUNIXMail(String from, String to, String cc,
                                        String bcc, String subject,
                                        String body, String[] attachments) {
    sendUNIXMail(from, to, cc, bcc, subject, body, DEFAULT_SHELL, attachments);
  }

  /**
   *
   * @param to String
   * @param subject String
   * @param body String
   * @param shell String
   */
  public static final void sendUNIXMail(String from, String to, String cc,
                                        String bcc, String subject,
                                        String body,
                                        String shell, String[] attachments) {
    try {
      String command = null;
      if (Constants.OS.startsWith(Constants.OS_SUN) ||
          Constants.OS.startsWith(Constants.OS_UNIX)) {
        command = formEmaiSendCommandForSunOS(from, to, cc,
                                              bcc, subject,
                                              body, attachments);
      }
      else if (Constants.OS.startsWith(Constants.OS_WINDOW)) {
        logger.warn("Unix Emailer cannot be used from Windows OS. Please use SMTP Emailer or test this process on Linux or Unix OS");
        return;
      }
      else {
        command = formEmaiSendCommandForLinux(from, to, cc,
                                              bcc, subject,
                                              body, attachments);

      }

      logger.debug("Unix Mailer Command [" + command + "]");

      String[] cmd = new String[3];
      cmd[0] = shell;
      cmd[1] = "-c";
      cmd[2] = command;
      Runtime.getRuntime().exec(cmd);

      logger.info("Email has been sent. From ID [" + from + "],To ID(s) [" + to +
                  "], CC(s) [" +
                  cc + "], BCC(s) [" + cc + "], No. attachments [" +
                  ( (attachments != null && attachments.length > 0) ?
                   attachments.length : 0) + "]");
    }
    catch (Exception ex) {
      logger.error("Fail to send email [" + to + "]", ex);
    }
  }

  /**
   *
   * @param from String
   * @param to String
   * @param cc String
   * @param bcc String
   * @param subject String
   * @param body String
   * @param shell String
   * @param attachments String[]
   * @return String
   */
  private static String formEmaiSendCommandForSunOS(String from, String to,
      String cc,
      String bcc, String subject,
      String body,
      String[] attachments) {

    StringBuffer sb = new StringBuffer("(echo \"" + body + "\"");
    if (attachments != null && attachments.length > 0) {
      sb.append("; ");
      for (int i = 0; i < attachments.length; i++) {
        sb.append("uuencode ").append(attachments[i] +
                                      " ").append(
            attachments[i].substring(attachments[i].lastIndexOf("/") + 1));
        if ( (i + 1) < attachments.length) {
          sb.append(" && ");
        }
      }
    }
    sb.append(")");

    from = (from != null) ? " -- -r " + from : "";
    cc = (cc != null) ? " -c " + cc : "";
    bcc = (bcc != null) ? " -b " + bcc : "";

    return sb.toString() + " | mail -s \"" + subject + "\" " + to + cc +
        bcc + from;
  }

  /**
   *
   * @param from String
   * @param to String
   * @param cc String
   * @param bcc String
   * @param subject String
   * @param body String
   * @param attachments String[]
   * @return String
   */
  private static String formEmaiSendCommandForLinux(String from, String to,
      String cc,
      String bcc, String subject,
      String body,
      String[] attachments) {
    //echo "This is the body" | mutt -e "unmy_hdr from; my_hdr From: venugopalan.saji@credit-suisse.com" -a /app/fao/csar/uat/u
//at1/cncapac2/config/log4j.properties -s "this is the subject line" venugopalan.saji@credit-suisse.com

    StringBuffer sb = new StringBuffer("(echo \"" + body + "\")");
    sb.append("| mutt ");
    from = (from != null) ? " -e \"unmy_hdr from; my_hdr From: " + from + "\"" :
        "";
    sb.append(from);

    if (attachments != null && attachments.length > 0) {
      for (int i = 0; i < attachments.length; i++) {
        sb.append(" -a ").append(attachments[i]);
      }
    }

    sb.append(" -s \"" + subject + "\" ");
    sb.append(to);

    cc = (cc != null) ? " -c " + cc : "";
    sb.append(cc);

    bcc = (bcc != null) ? " -b " + bcc : "";
    sb.append(bcc);

    return sb.toString();
  }

  /**
   *
   * @param ech EmailContentHolder
   * @param s Session
   */
  public static final void sendSMTPMail(EmailContentHolder ech, Session s) throws
      MessagingException {

    MimeMultipart container = new MimeMultipart();

    MimeMessage message = new MimeMessage(s);
    message.setSubject(ech.getSubject());
    message.setContent(container);
    message.setFrom(ech.getFromAddress());

    message.setRecipients(RecipientType.TO, ech.getToAddress());

    if (ech.isCcAddressExist()) {
      message.setRecipients(RecipientType.CC, ech.getCcAddress());
    }

    if (ech.isBccAddressExist()) {
      message.setRecipients(RecipientType.CC, ech.getBccAddress());
    }

    MimeBodyPart bodyPart = new MimeBodyPart();
    bodyPart.setContent(ech.getContent(), ech.getContentType());
    container.addBodyPart(bodyPart);

    if (ech.isAttachmentExist()) {

      for (Iterator it = ech.getAttachments().iterator(); it.hasNext(); ) {

        Attachment a = (Attachment) it.next();
        File f = a.getAttachment();
        DataSource ds = new FileDataSource(f);

        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setDisposition(javax.mail.Part.ATTACHMENT);
        mbp.setFileName(f.getName());
        mbp.setDescription(a.getDescription());
        mbp.setDataHandler(new DataHandler(ds));

        container.addBodyPart(mbp);
      }
    }
    s.getTransport().send(message);

    logger.info("Email has been sent. From ID [" + ech.getFromAddress() +
                "],To ID(s) [" + ech.getToAddress() +
                "], CC(s) [" +
                ech.getCcAddress() + "], BCC(s) [" + ech.getBccAddress() +
                "], No. attachments [" +
                ( (ech.getAttachments() != null &&
                   ech.getAttachments().size() > 0) ?
                 ech.getAttachments().size() : 0) + "]");

  }

}
