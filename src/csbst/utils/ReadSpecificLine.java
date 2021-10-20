package csbst.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadSpecificLine {
    public static String getLine(String filname,int lineNumber) throws IOException {
        String line = null;
        int lineNo;
        FileReader fr=null;
        BufferedReader br=null;
        try {
                fr = new FileReader(filname);
                br = new BufferedReader(fr);
                lineNo = 1;
                while (lineNo <= lineNumber) {
                	line = br.readLine();
                    if (lineNo == lineNumber)
                        return line;
                    lineNo++;
                }
        }finally{
        	if(fr!=null)
        		fr.close();
        	if(br!=null)
        		br.close();
        }
        return line;
    }
}
