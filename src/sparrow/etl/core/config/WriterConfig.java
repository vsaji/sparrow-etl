/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sparrow.etl.core.config;

import java.util.Map;

import sparrow.etl.core.util.ConfigKeyConstants;
import sparrow.etl.core.util.SparrowUtil;
import sparrow.etl.jaxb.WRITERType;

public interface WriterConfig
    extends ConfigParam, DependentIndexingSupport {

  abstract String getClassName();

  abstract boolean isSingleInstance();

  abstract String getTiggerEvent();

  abstract DataWriterType getType();

}

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Saji Venugopalan
 * @version 1.0
 */
class WriterConfigImpl
    extends ConfigParamImpl
    implements WriterConfig {

  public static final Map WRITER_EVENT_MAPPING = SparrowUtil.getImplConfig(
      "writer_event");

  private final String name;
  private final String className;
  private final String depends;
  private final boolean singleInstance;
  private final DataWriterType type;
  private String tiggerEvent;

  WriterConfigImpl(WRITERType writer) {
    super(writer.getPARAM());
    this.name = writer.getNAME();
    this.className = writer.getCLASS();
    this.depends = writer.getDEPENDS();
    this.singleInstance = writer.isSINGLETON();

    if (this.className == null || this.className.trim().equals("")) {
      String stype = (writer.getTYPE() == null) ? "TEST" :
          writer.getTYPE().toUpperCase();
      this.type = (DataWriterType) DataWriterType.TYPE_IDENTIFIER.get(
          stype);
    }
    else {
      this.type = new DataWriterType("OTHER", className);

    }

    String defaultTriggerEvent = (String) WRITER_EVENT_MAPPING.get(type.
        getWriterTypeAsString());
    defaultTriggerEvent = defaultTriggerEvent.substring(defaultTriggerEvent.
        lastIndexOf(";")+1);

    this.tiggerEvent = writer.getTRIGGEREVENT();
    this.tiggerEvent = ("request".equals(tiggerEvent)) ?
        defaultTriggerEvent : tiggerEvent;

  }

  public String getName() {
    return this.name;
  }

  public String getClassName() {
    return this.className;
  }

  /**
   * getDepends
   *
   * @return String
   */
  public String getDepends() {
    return this.depends;
  }

  /**
   * isSingleInstance
   *
   * @return boolean
   */
  public boolean isSingleInstance() {
    return singleInstance;
  }

  /**
   * getType
   *
   * @return DataWriterType
   */
  public DataWriterType getType() {
    return type;
  }

  /**
   * getTiggerEvent
   *
   * @return String
   */
  public String getTiggerEvent() {
    return this.tiggerEvent;
  }

}
