package sparrow.elt.core.transaction;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.transaction.xa.Xid;

public class XidFactory {
  private final byte[] baseId = new byte[Xid.MAXGTRIDSIZE];
  private long count = 1;

  public XidFactory(byte[] tmId) {
    System.arraycopy(tmId, 0, baseId, 8, tmId.length);
  }

  public XidFactory() {
    byte[] hostid;
    try {
      hostid = InetAddress.getLocalHost().getAddress();
    }
    catch (UnknownHostException e) {
      hostid = new byte[] {
          127, 0, 0, 1};
    }
    int uid = System.identityHashCode(this);
    baseId[8] = (byte) uid;
    baseId[9] = (byte) (uid >>> 8);
    baseId[10] = (byte) (uid >>> 16);
    baseId[11] = (byte) (uid >>> 24);
    System.arraycopy(hostid, 0, baseId, 12, hostid.length);
  }

  public Xid createXid() {
    byte[] globalId = (byte[]) baseId.clone();
    long id;
    synchronized (this) {
      id = count++;
    }
    globalId[0] = (byte) id;
    globalId[1] = (byte) (id >>> 8);
    globalId[2] = (byte) (id >>> 16);
    globalId[3] = (byte) (id >>> 24);
    globalId[4] = (byte) (id >>> 32);
    globalId[5] = (byte) (id >>> 40);
    globalId[6] = (byte) (id >>> 48);
    globalId[7] = (byte) (id >>> 56);
    return new XidImpl(globalId);
  }

  public Xid createBranch(Xid globalId, int branch) {
    byte[] branchId = (byte[]) baseId.clone();
    branchId[0] = (byte) branch;
    branchId[1] = (byte) (branch >>> 8);
    branchId[2] = (byte) (branch >>> 16);
    branchId[3] = (byte) (branch >>> 24);
    return new XidImpl(globalId, branchId);
  }

}
