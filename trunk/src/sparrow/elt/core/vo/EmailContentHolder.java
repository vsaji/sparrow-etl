package sparrow.elt.core.vo;

import java.util.List;
import java.util.ArrayList;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.*;
import java.io.File;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class EmailContentHolder {

  private InternetAddress fromAddress;
  private String subject;
  private String content;
  private String contentType;

  private List toAddresses;
  private List ccAddresses;
  private List bccAddresses;
  private List attachments;

  private boolean ccAddressExist;
  private boolean bccAddressesExist;
  private boolean attachmentExist;

  /**
   *
   */
  public EmailContentHolder() {
    toAddresses = new ArrayList();
    ccAddresses = new ArrayList();
    bccAddresses = new ArrayList();
    attachments = new ArrayList();
  }

  /**
   *
   * @param content String
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   *
   * @param contentType String
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   *
   * @param subject String
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   *
   * @param fromAddress String
   */
  public void setFromAddress(String fromAddress) {
    try {
      this.fromAddress = new InternetAddress(fromAddress);
    }
    catch (AddressException ex) {
      ex.printStackTrace();
    }
  }

  /**
   *
   * @param toAddress String[]
   */
  public void setToAddress(String[] toAddress) {
    fillAddress(toAddresses, toAddress);
  }

  /**
   *
   * @param ccAddress String[]
   */
  public void setCcAddress(String[] ccAddress) {
    fillAddress(ccAddresses, ccAddress);
  }

  /**
   *
   * @param bccAddress String[]
   */
  public void setBccAddress(String[] bccAddress) {
    fillAddress(bccAddresses, bccAddress);
  }

  /**
   *
   * @param bccAddressAsString String
   */
  public void setBccAddressAsString(String bccAddressAsString) {
    setBccAddress(bccAddressAsString.split("[,]"));
  }

  /**
   *
   * @param ccAddressAsString String
   */
  public void setCcAddressAsString(String ccAddressAsString) {
    setCcAddress(ccAddressAsString.split("[,]"));
  }

  /**
   *
   * @param toAddressAsString String
   */
  public void setToAddressAsString(String toAddressAsString) {
    setToAddress(toAddressAsString.split("[,]"));
  }

  /**
   *
   * @return String
   */
  public String getContentType() {
    return contentType;
  }

  /**
   *
   * @return String
   */
  public String getSubject() {
    return subject;
  }

  /**
   *
   * @return String
   */
  public String getContent() {
    return content;
  }

  /**
   *
   * @return String
   */
  public InternetAddress getFromAddress() {
    return fromAddress;
  }

  /**
   *
   * @return String[]
   */
  public InternetAddress[] getToAddress() {
    return (InternetAddress[]) toAddresses.toArray(new InternetAddress[
        toAddresses.size()]);
  }

  /**
   *
   * @return String[]
   */
  public InternetAddress[] getCcAddress() {
    return (InternetAddress[]) ccAddresses.toArray(new InternetAddress[
        ccAddresses.size()]);
  }

  /**
   *
   * @return String[]
   */
  public InternetAddress[] getBccAddress() {
    return (InternetAddress[]) bccAddresses.toArray(new InternetAddress[
        bccAddresses.size()]);
  }

  /**
   *
   * @return String[]
   */
  public void addToAddress(String toAddress) {
    fillAddress(toAddresses, toAddress);
  }

  /**
   *
   * @return String[]
   */
  public void addCcAddress(String ccAddress) {
    fillAddress(ccAddresses, ccAddress);
  }

  /**
   *
   * @return String[]
   */
  public void addBccAddress(String bccAddress) {
    fillAddress(bccAddresses, bccAddress);
  }

  /**
   *
   * @param attachment byte[]
   */
  public void addAttachment(File attachment,String desc) {
    attachments.add(new Attachment(attachment,desc));
  }

  /**
   *
   * @return List
   */
  public List getAttachments() {
    return attachments;
  }

  /**
   *
   * @return boolean
   */
  public boolean isBccAddressExist() {
    return !bccAddresses.isEmpty();
  }

  /**
   *
   * @return boolean
   */
  public boolean isAttachmentExist() {
    return !attachments.isEmpty();
  }


  /**
   *
   * @return boolean
   */
  public boolean isCcAddressExist() {
    return !ccAddresses.isEmpty();
  }

  /**
   *
   * @param coll List
   * @param address String[]
   */
  private void fillAddress(List coll, String[] address) {
    try {
      for (int i = 0; i < address.length; i++) {
        coll.add(new InternetAddress(address[i]));
      }
    }
    catch (AddressException ex) {
      ex.printStackTrace();
    }
  }

  /**
   *
   * @param coll List
   * @param address String[]
   */
  private void fillAddress(List coll, String address) {
    try {
      coll.add(new InternetAddress(address));
    }
    catch (AddressException ex) {
      ex.printStackTrace();
    }
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
  public class Attachment{
    File attachment;
    String description;

    /**
     *
     * @param attachment File
     * @param fileName String
     * @param contentType String
     * @param description String
     */
    Attachment(File attachment,      String description){
      this.attachment = attachment;
      this.description= description;
    }
    /**
     *
     * @return File
     */
    public File getAttachment(){
      return this.attachment;
    }


    /**
     *
     * @return String
     */
    public String getDescription(){
      return this.description;
    }
  }

}
