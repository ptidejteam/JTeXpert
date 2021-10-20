package csbst.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileEditor {
	
	public static void unit2File(String unit, String fileName){
		BufferedWriter out=null;
		try {
			//StringBuffer sb;
				File file = new File(fileName);
				file.getParentFile().mkdirs();

				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));//,"iso-8859-1"
				out.write(unit);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	finally {
	        	if(out!=null)
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
//	            if(os!=null)
//	            	os.close();
	        }		
	}
    
    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            dest.setExecutable(false);
            dest.setWritable(true, false);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
        	if(is!=null)
        		is.close();
            if(os!=null)
            	os.close();
        }
    }

}
