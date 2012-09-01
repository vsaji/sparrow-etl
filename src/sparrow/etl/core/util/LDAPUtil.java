package sparrow.etl.core.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class LDAPUtil {

  /**
   *
   */
  public LDAPUtil() {
  }

  /**
   *
   * @param user String
   * @param mnemonic String
   * @throws Exception
   * @return String
   */
  private static String connect(String user, String mnemonic) throws Exception {
    String attribute = "";
    try {
      Hashtable ht = new Hashtable();
      ht.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      ht.put("java.naming.security.principal",
             "uid=wlUser,ou=people,o=Administrators,dc=cs-group,dc=com");
      ht.put("java.naming.security.credentials", "gPMeK6JujCiqw");
      ht.put("java.naming.provider.url", "ldap://esd.csfb.net:389/");
      LdapContext ctx = new InitialLdapContext(ht, null);
      Attributes matchAttrs = new BasicAttributes(true);
      String filter = "(&(uid=" + user + ")(mail=*))";
      SearchControls ctls = new SearchControls();
      ctls.setSearchScope(2);
      NamingEnumeration answer = ctx.search(
          "ou=People,dc=csfb,dc=cs-group,dc=com", filter, ctls);
      if (answer != null) {
        SearchResult sr = (SearchResult) answer.next();
        Attributes attr = sr.getAttributes();
        attribute = attr.get(mnemonic).toString();
        String coll[] = attribute.split(":");
        if (null != coll) {
          attribute = coll[1].trim();
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return attribute;
  }

  /**
   *
   * @throws Exception
   * @return DirContext
   */
  private static DirContext getContext() throws Exception {
    LdapContext ctx;
    Hashtable ht = new Hashtable();
    ht.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
    ht.put("java.naming.security.principal",
           "uid=wlUser,ou=people,o=Administrators,dc=cs-group,dc=com");
    ht.put("java.naming.security.credentials", "gPMeK6JujCiqw");
    ht.put("java.naming.provider.url", "ldap://esd.csfb.net:389/");
    ctx = new InitialLdapContext(ht, null);
    return ctx;
  }

  /**
   *
   * @param strGroupName String
   * @throws Exception
   * @return List
   */
  public static List getLDAPGroupMembers(String strGroupName) throws Exception {
    List usersList = null;
    DirContext ctx = null;
    try {
      List list;
      try {
        ctx = getContext();
        usersList = groupMembers(ctx, strGroupName);
        list = usersList;
      }
      catch (NamingException e) {
        e.printStackTrace();
        throw e;
      }
      catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
      }
      return list;
    }
    finally {
      try {
        if (ctx != null) {
          ctx.close();
        }
      }
      catch (Exception e) {}
    }
  }

  /**
   *
   * @param user String
   * @throws Exception
   * @return String
   */
  public static String getUserCommonName(String user) throws Exception {
    return connect(user, "cn");
  }

  /**
   *
   * @param user String
   * @throws Exception
   * @return String
   */
  public static String getUserEmailAddress(String user) throws Exception {
    return connect(user, "mail");
  }

  /**
   *
   * @param user String
   * @throws Exception
   * @return String
   */
  public static String getUserGivenName(String user) throws Exception {
    return connect(user, "givenname");
  }

  /**
   *
   * @param user String
   * @throws Exception
   * @return String
   */
  public static String getUserGroups(String user) throws Exception {
    return connect(user, "memberOf");
  }

  /**
   *
   * @param ctx DirContext
   * @param strGroupName String
   * @throws Exception
   * @return List
   */
  private static List groupMembers(DirContext ctx, String strGroupName) throws
      Exception {
    int totalResults;
    List usersList;
    String attribute = "";
    totalResults = 0;
    usersList = new ArrayList(100);
    String actualUserId = "";
    String corUserId = "";
    NamingEnumeration answer;
    SearchControls searchCtls = new SearchControls();
    String searchFilter = "(|(&((cn=" + strGroupName +
        "))(objectclass=groupofUniqueNames))(&((cn=" + strGroupName +
        "))(objectclass=groupOfURLs)))";
    String searchBase = "ou=Groups,dc=CSFB,dc=CS-Group,dc=com";
    answer = ctx.search(searchBase, searchFilter, searchCtls);

    if (answer != null) {
      while (answer.hasMore()) {
        Attributes attrs;
        SearchResult sr = (SearchResult) answer.next();
        attrs = sr.getAttributes();
        if (attrs == null) {
          continue;
        }
        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
          Attribute attr = (Attribute) ae.next();

          for (NamingEnumeration ne = attr.getAll(); ne.hasMore(); ) {
            String strUserId = (String) ne.next();
            if (strUserId.indexOf(",") != -1) {
              actualUserId = strUserId.substring(0, strUserId.indexOf(","));
              corUserId = actualUserId.substring(actualUserId.indexOf("=") +
                                                 1);
              usersList.add(corUserId);
            }
            totalResults++;
          }
        }
      }
    }
    return usersList;
  }

  /**
   *
   * @param args String[]
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    List li = new ArrayList();
    String strGroupName = "wGCSAit";
    li = getLDAPGroupMembers(strGroupName);
    for (int i = 0; i < li.size(); i++) {
      System.out.println(li.get(i));
    }
  }

  /**
   *
   * @param ctx DirContext
   * @param user String
   * @param mnemonic String
   * @throws Exception
   * @return String
   */
  private static String userDetails(DirContext ctx, String user,
                                    String mnemonic) throws Exception {
    String attribute = "";
    Attributes matchAttrs = new BasicAttributes(true);
    String filter = "(&(uid=" + user + ")(mail=*))";
    SearchControls ctls = new SearchControls();
    ctls.setSearchScope(2);
    NamingEnumeration nm = ctx.search("ou=People,dc=csfb,dc=cs-group,dc=com",
                                      filter, ctls);
    if (nm != null) {
      SearchResult sr = (SearchResult) nm.next();
      Attributes attr = sr.getAttributes();
      attribute = attr.get(mnemonic).toString();
      String coll[] = attribute.split(":");
      if (null != coll) {
        attribute = coll[1].trim();
      }
    }
    return attribute;
  }
}
