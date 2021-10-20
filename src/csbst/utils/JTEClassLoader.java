package csbst.utils;

//import java.io.IOException;
//import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.ClassWriter;
//import org.objectweb.asm.commons.Remapper;
//import org.objectweb.asm.commons.RemappingClassAdapter;

public class JTEClassLoader extends URLClassLoader {

    //private final String defaultPackageName;
	//public static Map<String,byte[]> class2Binary=new HashMap<String,byte[]>();
	public static Map<String,String> class2Binary=new HashMap<String,String>();
	
    public JTEClassLoader(Map<String,String> c2b, URL[] urls) {
        super(urls);
        if(class2Binary==null)
        	class2Binary=c2b;
        if(class2Binary==null)
        	class2Binary=new HashMap<String,String>();
        //this.defaultPackageName = defaultPackageName;
    }

//    public MagicClassLoader(Map<String,byte[]> c2b,ClassLoader parent) {
//        super(parent);
//        class2Binary=c2b;
//        if(class2Binary==null)
//        	class2Binary=new HashMap<String,byte[]>();
//        //this.defaultPackageName = defaultPackageName;
//    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        //Byte[] bytecode = class2Binary.get(name); 
    	//if(name.equals("org.apache.commons.lang3.CharRange"))
    	//	name=name;	
    	
//       String classFile= class2Binary.get(name);
//        if(classFile!=null){
//        	//System.out.print("loading class under test "+name+ "**********************************************"); 
//        	byte[] remappedBytecode;
//			try {
//				class2Binary.clear();
//				remappedBytecode = IOUtil.readFile(classFile);
//				return defineClass(name, remappedBytecode, 0, remappedBytecode.length);
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	
//        	
//        }
//        else
    	//try{
		if( name.equals("I"))
			return int.class;
		else if( name.equals("Z"))
			return boolean.class;
		else if( name.equals("F"))
			return float.class;
		else if( name.equals("J"))
			return long.class;
		else if( name.equals("S"))
			return short.class;
		else if( name.equals("B"))
			return byte.class;
		else if( name.equals("D"))
			return double.class;
		else if( name.equals("C"))
			return char.class;
		else
			//return  ClassLoaderUtil.getClass(name);
        	return super.loadClass(name);
	    //}catch(ClassFormatError e){}
		//catch(Exception e){}
        
    	//return null;
        //return null;
    }  	
//        	remappedBytecode=Arrays.copyOf(bytecode, bytecode.length);// (byte[])bytecode;
//        try {
//            remappedBytecode = rewriteDefaultPackageClassNames(bytecode);
//        } catch (IOException e) {
//            throw new RuntimeException("Could not rewrite class " + name);
//        }     
    

//    public byte[] rewriteDefaultPackageClassNames(byte[] bytecode) throws IOException {
//        ClassReader classReader = new ClassReader(bytecode);
//        ClassWriter classWriter = new ClassWriter(classReader, 0);
//
//        Remapper remapper = new DefaultPackageClassNameRemapper();
//        classReader.accept(
//                new RemappingClassAdapter(classWriter, remapper),
//                0
//            );
//
//        return classWriter.toByteArray();
//    }

//    class DefaultPackageClassNameRemapper extends Remapper {
//
//        @Override
//        public String map(String typeName) {
//            boolean hasPackageName = typeName.indexOf('.') != -1;
//            if (hasPackageName) {
//                return typeName;
//            } else {
//                return defaultPackageName + "." + typeName;
//            }
//        }
//
//    }

}
