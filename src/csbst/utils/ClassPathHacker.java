package csbst.utils;
import java.io.IOException;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;

public class ClassPathHacker {

private static final Class[] parameters = new Class[]{URL.class};

public static void addFile(String s) throws IOException {
   File f = new File(s);
   addFile(f);
}//end method

public static void addFile(File f) throws IOException {
   addURL(f.toURL());
}//end method


public static List<URL> loadClassPath(String classPath) throws Exception{
	List<URL> pathList=new ArrayList<URL>();
	pathList.clear();
	
	String paths[]=classPath.split(File.pathSeparator);
	
	for(int i=0;i<paths.length;i++){
		File bin=new File( paths[i]);
		pathList.add(bin.toURL());
	}
	
	URL[] classPathURL=new URL[pathList.size()];
	classPathURL=pathList.toArray(classPathURL);
	
	for(int i1=0;i1<pathList.size();i1++)
		addFile(pathList.get(i1).getPath());
	
	return pathList;
}

public static void addURL(URL u) throws IOException {

  URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
  Class sysclass = URLClassLoader.class;

  try {
     Method method = sysclass.getDeclaredMethod("addURL", parameters);
     method.setAccessible(true);
     method.invoke(sysloader, new Object[]{u});
  } catch (Throwable t) {
     t.printStackTrace();
     throw new IOException("Error, could not add URL to system classloader");
  }//end try catch

   }//end method

}//end class
