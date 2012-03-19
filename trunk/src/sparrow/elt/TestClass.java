package sparrow.elt;

import java.io.FileOutputStream;
import java.io.FileWriter;

public class TestClass {

  public String convertStringToHex(String str) {

    char[] chars = str.toCharArray();

    StringBuffer hex = new StringBuffer();
    for (int i = 0; i < chars.length; i++) {
      hex.append(Integer.toHexString( (int) chars[i]));
    }

    return hex.toString();
  }

  public String convertHexToString(String hex) {

    StringBuffer sb = new StringBuffer();
    StringBuffer temp = new StringBuffer();

    //49204c6f7665204a617661 split into two characters 49, 20, 4c...
    for (int i = 0; i < hex.length() - 1; i += 2) {

      //grab the hex in pairs
      String output = hex.substring(i, (i + 2));
      //convert hex to decimal
      int decimal = Integer.parseInt(output, 16);
      //convert the decimal to character
      sb.append( (char) decimal);

      temp.append(decimal);
    }
    // System.out.println("Decimal : " + temp.toString());

    return sb.toString();
  }

  /**
   *
   * @param args String[]
   */
  public static void main(String[] args) {

    try {

    	new Thread(new Runnable(){
			public void run() {
				try {
					FileOutputStream fos = new FileOutputStream("c:/app/temp/testfile.txt",true);
					fos.write(new String("This is 1st String").getBytes());
					//FileWriter fw = new FileWriter("c:/app/temp/testfile.txt");
					//fw.write("Thread 1:String written by FW");
					System.out.println("Thread 1 wrote");
					Thread.sleep(30000);
					System.out.println("Thread 1 After Sleep");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	).start();
    	
		Thread.sleep(10000);
		
		
		new Thread(new Runnable(){

			public void run() {
				try {
					System.out.println("Befor Writing Thread 2");
					FileOutputStream fos = new FileOutputStream("c:/app/temp/testfile.txt",true);
					fos.write(new String("This is 2nd String").getBytes());
					//FileWriter fw = new FileWriter("c:/app/temp/testfile.txt");
					//fw.write("Thread 2:String written by FW");
					System.out.println("Thread 2 wrote");
					Thread.sleep(1000);
					System.out.println("Thread 2 After Sleep");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	).start();
    	
      //System.out.println(System.getProperty("os.name"));
      //System.out.println(Math.round(30000 / 30000));
//      Class.forName("com.ibm.db2.jcc.DB2Driver");
//      System.out.println("--> Class Loaded");
//      Connection c = DriverManager.getConnection(
//          "jdbc:db2://mvsgbll.corpny.csfb.com:449/PRD2GBLL", "dd0504b",
//          "vbnm1234");
//      System.out.println("--> Connection Obtained");
//      Statement s = c.createStatement();
//      ResultSet rs = s.executeQuery("SELECT P_ID_C,COMP_ID_C FROM PNB.TTRANS_ACTVY_LOG A WHERE  PNB_FILE_ACCS_C LIKE '%T/D%'  GROUP BY P_ID_C,COMP_ID_C HAVING ( (SUM (CASE PNB_L_S_ACTVY_I WHEN 'S' THEN -1 * A.PNB_UPDT_QTY_Q  ELSE A.PNB_UPDT_QTY_Q END ) ) <> 0 ) WITH UR");
//      System.out.println("--> Query Executed");
//      while(rs.next()){
//        System.out.println("-->"+rs.getString(1));
//      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

//    System.out.println(System.getProperty("password"));
//
//    TestClass strToHex = new TestClass();
//    System.out.println("\n***** Convert ASCII to Hex *****");
//    String str = "{ENC}2";
//    System.out.println("Original input : " + str);
//
//    String hex = strToHex.convertStringToHex(str);
//
//    System.out.println("Hex : " + hex.toUpperCase());
//
//    System.out.println("\n***** Convert Hex to ASCII *****");
//    System.out.println("Hex : " + hex.toUpperCase());
//    System.out.println("ASCII : " + strToHex.convertHexToString(hex.toUpperCase()));
  }
}
