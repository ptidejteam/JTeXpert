package csbst.testing;

import java.io.File;
import java.security.Permission;

public class SystemExitControl {
	 
    public static class ExitTrappedException extends SecurityException {
    }
 
    public static void forbidSystemExitCall() {
    	//this is the only way to make a securitymanager works correctly
        final SecurityManager securityManager =  new	SecurityManager() {
            @Override
            public void checkPermission(Permission permission) {
                if (permission.getName().contains("exitVM")) {
                	if (!permission.getName().contains(".5555"))
                		throw new ExitTrappedException();
                	else
                		super.checkExit(5555);
                }
            }
            
	        @Override
	        public void checkWrite(String file){
	        	//only 
	        	//System.out.println("file: "+file);
	        	//System.out.println("jteOutputPath: "+jteOutputPath);
	        	//System.out.println("jteOutputPath: "+!file.startsWith(jteOutputPath));
	        	File f=new File(file);
	        	File jteop=new File(JTE.jteOutputPath);
	        	File tc=new File(JTE.testCasesPath);
	        	//File tcopy=new File("jtexpert_4th/copy/");
	        	
	        	if(!(f.getAbsolutePath().startsWith(tc.getAbsolutePath()) || f.getAbsolutePath().startsWith(jteop.getAbsolutePath()))
	        			&& !f.getAbsolutePath().contains(File.separator+"tmp"+File.separator)
	        			&& !f.getAbsolutePath().contains(File.separator+"temp"+File.separator)){
	        		//cutCallsSystemExist=true;
	        		throw new SecurityException();
	        	}
	        		 //SecurityException("Write a File is Not allowed."+file);
	        	//super.checkDelete(file);
	        }
	        
	        @Override
	        public void checkDelete(String file){
	        	//System.out.println(jteOutputPath);
	        	//System.out.println(file);
	        	File f=new File(file);
	        	File jteop=new File(JTE.jteOutputPath);
	        	File tc=new File(JTE.testCasesPath);
	        	if(!(f.getAbsolutePath().startsWith(tc.getAbsolutePath()) || f.getAbsolutePath().startsWith(jteop.getAbsolutePath()))
	        			&& !f.getAbsolutePath().contains(File.separator+"tmp"+File.separator)
	        			&& !f.getAbsolutePath().contains(File.separator+"temp"+File.separator))
	        		throw new SecurityException("Delete a File is Not allowed."+file);
	        }
        };
        System.setSecurityManager(securityManager);
    }
 
    public static void enableSystemExitCall() {
        System.setSecurityManager(null);
    }
}

