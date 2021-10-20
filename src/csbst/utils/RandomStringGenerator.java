package csbst.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;

public class RandomStringGenerator{

    private  int randomStringLength = 25 ;
    private  boolean allowSpecialCharacters = true ;  
    private  boolean allowDuplicates = false ;
    private  boolean isAlphanum = false;
    private  boolean isNumeric = false;
    private  boolean isAlpha = false;
    private  boolean mixCase = false;
    
    public static final String specialCharacters =" !@$%*-_+:.><&()[]{}_=#;'^~`|/?\"";// " \r\t!@$%*-_+:.><\"/\\#?&()_=^¬;'";//
    public static final String alphabet = "abcdefghijklmnopqrstuvwxyz";    
    public static final String capAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String num = "0123456789";
    private static String seeding;
    
    public RandomStringGenerator(int len, boolean sc,boolean d, boolean an, boolean n, boolean a, boolean mc,String seeding){
    	randomStringLength=len;
    	allowSpecialCharacters=sc;
    	allowDuplicates=d;
    	isAlphanum=an;
    	isNumeric=n;
    	isAlpha=a;
    	mixCase=mc;
    	this.seeding=seeding;
    }

    public RandomStringGenerator(int len, boolean sc,boolean d, boolean an, boolean n, boolean a, boolean mc){
    	this( len,  sc, d,  an,  n,  a,  mc,"");
    	
    }
    
    public  String getRandomString() {
//    	if (true)
//    		return capAlpha;
        String returnVal = "";
        int specialCharactersCount = 0;
        int maxspecialCharacters = randomStringLength/4;
        if(maxspecialCharacters<1)
        	maxspecialCharacters=1;

        try {
            StringBuffer values = buildList();
            if(values.length()==0||randomStringLength==0){
            	Random rand=new Random();
            	if(rand.nextBoolean())
            		return null;
            	else
            		return ""; 
            }
            if(values.length()<randomStringLength && !allowDuplicates )
            	randomStringLength=values.length();
            for (int inx = 0; inx < randomStringLength; inx++) {
                int selChar = (int) (Math.random() * (values.length() - 1));
                if (allowSpecialCharacters)
                {
                    if (specialCharacters.indexOf("" + values.charAt(selChar)) > -1)
                    {
                        specialCharactersCount ++;
//                        if (specialCharactersCount > maxspecialCharacters)
//                        {
//                            while (specialCharacters.indexOf("" + values.charAt(selChar)) != -1)
//                            {
//                                selChar = (int) (Math.random() * (values.length() - 1));
//                            }
//                        }
                    }
                }
//                System.out.println(values);
//                System.out.println("********"+selChar);
//                System.out.println(returnVal);
                returnVal += values.charAt(selChar);
                if (!allowDuplicates) {
                    values.deleteCharAt(selChar);
                }
            }
        } catch (Exception e) {
            returnVal = "Error While Processing Values";
            e.printStackTrace();
        }
		String newString="";
		try {
			newString = new String(returnVal.getBytes(System.getProperty("file.encoding")), System.getProperty("file.encoding")); ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
        return newString;
    }

    private StringBuffer buildList() {
        StringBuffer list = new StringBuffer(10);
        
        list.append(this.seeding);
        
        if (isNumeric || isAlphanum) {
            list.append(num);
        }
        if (isAlpha || isAlphanum) {
            list.append(alphabet);
            if (mixCase) {
                list.append(capAlpha);
            }
        }
        if (allowSpecialCharacters)
        {
            list.append(specialCharacters);
        }
        int currLen = list.length();
        String returnVal = "";
        for (int inx = 0; inx < currLen; inx++) {
            int selChar = (int) (Math.random() * (list.length() - 1));
            returnVal += list.charAt(selChar);
            list.deleteCharAt(selChar);
        }
        list = new StringBuffer(returnVal);
        return list;
    }   

}
