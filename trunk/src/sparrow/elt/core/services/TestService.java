package sparrow.elt.core.services;

import java.sql.Connection;

import sparrow.elt.core.config.SparrowServiceConfig;
import sparrow.elt.core.context.SparrowContext;


/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TestService
    implements PluggableService {

  private SparrowContext context;

  public TestService(SparrowServiceConfig config) {
    this.context = config.getContext();
  }

  /**
   * beginApplication
   */
  public void beginApplication() {
    try {
      //Connection con = context.getDBConnection("MSSQL");
      //con.createStatement().execute("alter table d_dbiicaps001..ca_CounterParty add Status int CONSTRAINT DF_ca_CounterParty_Status DEFAULT 0 NOT NULL");
      //con.createStatement().execute("CREATE NONCLUSTERED INDEX IX_ca_CounterParty_Status ON d_dbiicaps001..ca_CounterParty(Status)");

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * endApplication
   */
  public void endApplication() {
    try {
      //Connection con = context.getDBConnection("MSSQL");
      //con.createStatement().execute(
      //    "DROP INDEX IX_ca_CounterParty_Status ON d_dbiicaps001..ca_CounterParty");
      //con.createStatement().execute("ALTER TABLE d_dbiicaps001..ca_CounterParty DROP CONSTRAINT DF_ca_CounterParty_Status ALTER TABLE d_dbiicaps001..ca_CounterParty DROP COLUMN Status ");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  /**
   * beginCycle
   */
  public void beginCycle() {
  }

  /**
   * endCycle
   */
  public void endCycle() {
  }

  /**
   * getService
   *
   * @param serviceName Object
   * @return Object
   */
  public Object getService(Object serviceName) {
    return "";
  }

  /**
   * getService
   *
   * @return Object
   */
  public Object getService() {
    return "";
  }

  /**
   * initialize
   *
   * @param config SparrowServiceConfig
   */
  public void initialize() {

  }

  /**
   * getPriority
   *
   * @return int
   */
  public int getPriority() {
    return 0;
  }
}
