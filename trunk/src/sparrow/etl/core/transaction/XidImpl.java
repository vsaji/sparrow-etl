package sparrow.etl.core.transaction;

import java.io.Serializable;
import java.util.Arrays;
import javax.transaction.xa.Xid;

public class XidImpl
    implements Xid, Serializable {
  private static int FORMAT_ID = 0x4765526f; // Gero
  private final int formatId;
  private final byte[] globalId;
  private final byte[] branchId;
  private int hash; //apparently never used by our code, so don't compute it.

  /**
   * Constructor taking a global id (for the main transaction)
   * @param globalId the global transaction id
   */
  public XidImpl(byte[] globalId) {
    this.formatId = FORMAT_ID;
    this.globalId = globalId;
    //this.hash = hash(0, globalId);
    branchId = new byte[Xid.MAXBQUALSIZE];
  }

  /**
   * Constructor for a branch id
   * @param global the xid of the global transaction this branch belongs to
   * @param branch the branch id
   */
  public XidImpl(Xid global, byte[] branch) {
    this.formatId = FORMAT_ID;
    //int hash;
    if (global instanceof XidImpl) {
      globalId = ( (XidImpl) global).globalId;
      //hash = ((XidImpl) global).hash;
    }
    else {
      globalId = global.getGlobalTransactionId();
      //hash = hash(0, globalId);
    }
    branchId = branch;
    //this.hash = hash(hash, branchId);
  }

  public XidImpl(int formatId, byte[] globalId, byte[] branchId) {
    this.formatId = formatId;
    this.globalId = globalId;
    this.branchId = branchId;
  }

  private int hash(int hash, byte[] id) {
    for (int i = 0; i < id.length; i++) {
      hash = (hash * 37) + id[i];
    }
    return hash;
  }

  public int getFormatId() {
    return formatId;
  }

  public byte[] getGlobalTransactionId() {
    return (byte[]) globalId.clone();
  }

  public byte[] getBranchQualifier() {
    return (byte[]) branchId.clone();
  }

  public boolean equals(Object obj) {
    if (obj instanceof XidImpl == false) {
      return false;
    }
    XidImpl other = (XidImpl) obj;
    return formatId == other.formatId
        && Arrays.equals(globalId, other.globalId)
        && Arrays.equals(branchId, other.branchId);
  }

  public int hashCode() {
    if (hash == 0) {
      hash = hash(hash(0, globalId), branchId);
    }
    return hash;
  }

  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("[globalId=");
    for (int i = 0; i < globalId.length; i++) {
      s.append(Integer.toHexString(globalId[i]));
    }
    s.append(",branchId=");
    for (int i = 0; i < branchId.length; i++) {
      s.append(Integer.toHexString(branchId[i]));
    }
    s.append("]");
    return s.toString();
  }
}
