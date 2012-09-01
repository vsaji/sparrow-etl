package sparrow.etl.core.security;

import java.util.Arrays;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class Encryptor {
  public Encryptor() {
  }

  /**
   *
   * @param args String[]
   */
  public static void main(String[] args){
    if(args.length==0){
      System.out.println("[ERROR]Argument Expected.");
      System.out.println("Usage Pattern:- C:\\>java -cp sparrow.<VERSION>.jar sparrow.etl.core.security.Encryptor  <String to encrypt> <Process Id>");
      System.exit(-1);
    }

    if(args.length>2){
      System.out.println("[ERROR] Cannot encrypt more than one string. Please quote the string if the string has space.");
      System.exit(-1);
    }

    try{

      String enc,dec;
      String[] ddd = CryptoUtil.getInstance(args[1]).gggg(args[0]);
      enc = CryptoUtil.convertStringToHex(ddd[1]);
      System.out.println("[INPUT]==> " + args[0]);
      System.out.println("[ENC  ]==> " + enc);
      System.out.println("[ENC  ]==> " + ddd[1]);
      String[] discover = urts(ddd[1]);
      dec = CryptoUtil.getInstance(args[1]).fff(CryptoUtil.convertHexToString(enc), discover[1], discover[0]);
      System.out.println("[DEC  ]==> " +  dec);
      System.out.println("[NOTE ]==> In the param value, pre-fix \"{ENC}\" with the encrypted value. Ex.{ENC}" +
                         enc);
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }



  /**
   *
   * @param dfg String
   * @return String[]
   */
  private static final String[] urts(String dfg) {
    int aaa = Arrays.binarySearch(CryptoUtil.rrr, dfg.charAt(0)) + 1;
    int sss = aaa - 1;
    dfg = dfg.substring(1);
    String jjj = dfg.substring(aaa, aaa + 8);
    String zzz = dfg.substring(0, sss) + dfg.substring(aaa + 8, dfg.length());
    return new String[] {
        jjj, zzz};
  }


}
