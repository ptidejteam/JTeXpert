package csbst.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class IOUtil {

    public static byte[] readFile(String file) throws IOException {
    	
    	String url ="file:"+file;// "file:C:/data/projects/tutorials/web/WEB-INF/" +"classes/reflection/MyObject.class";
		URL myUrl = new URL(url);
		URLConnection connection = myUrl.openConnection();
		InputStream input = connection.getInputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int data = input.read();
		
		while(data != -1){
		    buffer.write(data);
		    data = input.read();
		}
		
		input.close();
		
		byte[] classData = buffer.toByteArray();
        return classData;//readFile(new File(file));
    }

//    public static byte[] readFile(File file) throws IOException {
//        // Open file
//        RandomAccessFile f = new RandomAccessFile(file, "r");
//        try {
//            // Get and check length
//            long longlength = f.length();
//            int length = (int) longlength;
//            if (length != longlength)
//                throw new IOException("File size >= 2 GB");
//            // Read file and return data
//            byte[] data = new byte[length];
//            f.readFully(data);
//            return data;
//        } finally {
//            f.close();
//        }
//    }
    
    
}