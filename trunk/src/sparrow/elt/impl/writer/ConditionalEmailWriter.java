/**
 *
 */
package sparrow.elt.impl.writer;

import sparrow.elt.core.config.SparrowDataWriterConfig;
import sparrow.elt.core.util.ConfigKeyConstants;
import sparrow.elt.core.util.SparrowUtil;

/**
 * @author syadav15
 *
 */
public class ConditionalEmailWriter
    extends EmailWriter {

  protected String emailConditionIfValue;

  protected String emailConditionIfSubject;

  protected String emailConditionIfContent;

  protected String emailConditionElseSubject;

  protected String emailConditionElseContent;

  protected boolean emailAttachmentIf;

  protected boolean emailAttachmentElse;

  /**
   *
   * @param config
   *            SparrowDataWriterConfig
   */
  public ConditionalEmailWriter(SparrowDataWriterConfig config) {
    super(config);
  }

  /**
   *
   */
  public void initialize() {

    super.initialize();

    emailConditionIfValue = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_EMAIL_CONDITION_IF_VALUE);

    emailConditionIfSubject = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_EMAIL_CONDITION_IF_SUBJECT);

    emailConditionIfContent = config.getInitParameter().getParameterValue(
        ConfigKeyConstants.PARAM_EMAIL_CONDITION_IF_CONTENT);

    emailConditionElseSubject = config.getInitParameter()
        .getParameterValue(
        ConfigKeyConstants.PARAM_EMAIL_CONDITION_ELSE_SUBJECT);

    emailConditionElseContent = config.getInitParameter()
        .getParameterValue(
        ConfigKeyConstants.PARAM_EMAIL_CONDITION_ELSE_CONTENT);

    emailAttachmentIf = SparrowUtil.performTernary(config.getInitParameter(),
                                                 ConfigKeyConstants.
                                                 PARAM_ATTACHMENT_IF, true);

    emailAttachmentElse = SparrowUtil.performTernary(config
        .getInitParameter(), ConfigKeyConstants.PARAM_ATTACHMENT_ELSE,
        true);

  }

  /**
   *
   */
  public void doEndOfProcess() {

    boolean condition = super.evaluateCondition(emailConditionIfValue);
    boolean emailRequiredToSend = false;

    /**************************************/
    if (condition) {
      emailSubject = replaceToken(emailConditionIfSubject);
      emailContent = replaceToken(emailConditionIfContent);

      if (emailAttachmentIf == false) {
    	  super.disableAttachments();
      }
      logger.info("Condition [" + emailConditionIfValue + "] satisfied.");
      emailRequiredToSend = true;
    }
    else {
      if (emailConditionElseSubject != null
          && emailConditionElseContent != null) {
        emailSubject = replaceToken(emailConditionElseSubject);
        emailContent = replaceToken(emailConditionElseContent);

        if (emailAttachmentElse == false) {
        	super.disableAttachments();
        }
        logger.info("Condition[" + emailConditionIfValue
                    + "] did not satisfiy.");
        emailRequiredToSend = true;
      }
    }
    /**************************************/

    if (emailRequiredToSend) {
      sendMail();
    }

  }


  /**
   *
   */
  protected void validateParam() {
    SparrowUtil.validateParam(new String[] {
                            ConfigKeyConstants.PARAM_EMAIL_TO,
                            ConfigKeyConstants.PARAM_EMAIL_FROM,
                            ConfigKeyConstants.PARAM_EMAIL_CONDITION_IF_VALUE,
                            ConfigKeyConstants.PARAM_EMAIL_CONDITION_IF_SUBJECT,
                            ConfigKeyConstants.PARAM_EMAIL_CONDITION_IF_CONTENT}
                            ,
                            "EmailWriter", config.getInitParameter());

  }
}
