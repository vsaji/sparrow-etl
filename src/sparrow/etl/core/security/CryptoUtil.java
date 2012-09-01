package sparrow.etl.core.security;

import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.KeyException;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CryptoUtil {

  public static final byte[] www =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".getBytes();

  public static final char[] rrr = new char[] {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
      'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '@', '!', '~', '$',
      '%', '^', '&', '*', '+', '|', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
      'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
      'y', 'z'};

  private static SecretKeyFactory keyFactory = null;

  private final int ccc;

  private static CryptoUtil instance = null;
  /**
   *
   */
  static {
    Arrays.sort(rrr);
    try {
      keyFactory = SecretKeyFactory.getInstance("DES");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param seed String
   */
  private CryptoUtil(String seed) {
    this.ccc = vvv(seed);
  }

  /**
   *
   */
  public static final CryptoUtil getInstance(String seed) {
    return new CryptoUtil(seed);
  }

  /**
   *
   * @return byte[]
   */
  private static final byte[] getWWW() {
    byte[] b = new byte[8];
    Random r = new Random();
    for (int i = 0; i < 8; i++) {
      int a = r.nextInt(www.length);
      //System.out.println(a);
      b[i] = www[a];
    }
    return b;
  }

  /**
   *
   * @param www byte[]
   * @return String
   */
  private static final String convertToString(byte[] www) {
    return new String(www);
  }

  /**
   *
   * @param input String
   * @return String[]
   */
  public final String[] gggg(String input) {
    String yyy = convertToString(getWWW());
    String[] sss = null;
    try {
      Cipher ecipher = kkk(yyy, 1);
      byte[] utf8 = input.getBytes("UTF8");
      byte[] mmm = ecipher.doFinal(utf8);
      String ttt = new sun.misc.BASE64Encoder().encode(mmm);
      //    System.out.println("ENC==>" + encStr);
      ttt = zzz(ttt, yyy);
      sss = new String[] {
          yyy,  ttt};
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return sss;
  }

  /**
   *
   * @param input String
   * @return String[]
   */
  private final String zzz(String vvv, String lll) {

    int qqq = (vvv.length() > rrr.length) ? rrr.length : vvv.length();
    int pos = new Random().nextInt(qqq);
    String aa = vvv.substring(0, pos);
    String bb = vvv.substring(pos, vvv.length());
    return String.valueOf(rrr[pos]) + aa + String.valueOf(rrr[ccc]) + lll + bb;
  }

  /**
   *
   * @param xxx String
   * @return String
   */
  final String fff1(String xxx) {
    String bbb = null;

    try {

      int aaa = Arrays.binarySearch(rrr, xxx.charAt(0)) + 1;
      int sss = aaa - 1;
      xxx = xxx.substring(1);
      String ddd = xxx.substring(sss, aaa);
      int ggg = Arrays.binarySearch(rrr, ddd.charAt(0));
      bbb(ggg);
      String rtKey = xxx.substring(aaa, aaa + 8);
      String zzz = xxx.substring(0, sss) + xxx.substring(aaa + 8, xxx.length());
      byte[] qqq = new sun.misc.BASE64Decoder().decodeBuffer(zzz);
      Cipher dcipher = kkk(rtKey, 2);
      byte[] ooo = dcipher.doFinal(qqq);
      bbb = new String(ooo, "UTF8");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return bbb;
  }

  /**
   *
   * @param xxx String
   * @return String
   */
  public final String fff(String xxx, String zzz, String iii) throws sparrow.etl.core.exception.
      SecurityException{
    String bbb = null;

    try {

      int aaa = Arrays.binarySearch(rrr, xxx.charAt(0)) + 1;
      int sss = aaa - 1;
      xxx = xxx.substring(1);
      String ddd = xxx.substring(sss, aaa);
      int ggg = Arrays.binarySearch(rrr, ddd.charAt(0));
      bbb(ggg);
      byte[] qqq = new sun.misc.BASE64Decoder().decodeBuffer(zzz);
      Cipher dcipher = kkk(iii, 2);
      byte[] ooo = dcipher.doFinal(qqq);
      bbb = new String(ooo, "UTF8");
    }
    catch(sparrow.etl.core.exception.SecurityException ex){
      throw ex;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return bbb;
  }

  /**
   *
   * @param nnn int
   * @throws KeyException
   */
  private final void bbb(int nnn) throws sparrow.etl.core.exception.
      SecurityException {
    if (nnn != ccc) {
      throw new sparrow.etl.core.exception.SecurityException(
          "ENC_SEED_MISMATCH", "PROCESS-ID Mismatch. Please verify the PROCESS-ID used while encrypting is same as mention in <PROCESS-ID> block");
    }
  }

  /**
   *
   * @param xxx String
   * @return int
   */
  private static final int vvv(String xxx) {
    return Integer.parseInt(String.valueOf(String.valueOf(Math.abs(xxx.hashCode())).
                                           charAt(3)));
  }

  /**
   *
   * @param hhh String
   * @param jjj int
   * @return Cipher
   */
  private static final Cipher kkk(String hhh, int jjj) throws
      Exception {
    DESKeySpec desKeySpec = new DESKeySpec(hhh.getBytes());
    SecretKey key = keyFactory.generateSecret(desKeySpec);
    Cipher cipher = Cipher.getInstance(key.getAlgorithm());
    cipher.init(jjj, key);
    return cipher;
  }


  /**
   *
   * @param hex String
   * @return String
   */
  public static final String convertHexToString(String hex){

      StringBuffer sb = new StringBuffer();
      StringBuffer temp = new StringBuffer();

      for( int i=0; i<hex.length()-1; i+=2 ){
          String output = hex.substring(i, (i + 2));
          int decimal = Integer.parseInt(output, 16);
          sb.append((char)decimal);
          temp.append(decimal);
      }
      return sb.toString();
    }

    /**
     *
     * @param str String
     * @return String
     */
    public static final String convertStringToHex(String str){

        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
          hex.append(Integer.toHexString((int)chars[i]));
        }
         return hex.toString();
      }


}
