package csbst.utils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

//import sun.misc.Unsafe;

public class AJavaFailure {
	
	private static void TestCase1(){
		// this method available only from trusted code. (to make your code trusted add "-Xbootclasspath:JAVA_HOME/jre/lib/rt.jar:YOUR_PROJECT_BIN_DIRECTORY" to your JVM argument )
		//you can use the TestCase2
		//final Unsafe theUnsafe=Unsafe.getUnsafe(); 
		//long rw = theUnsafe.getLong(null,(long) 566);
	}
	
	public static void main(String[] args) {
		//TestCase2	();
	}
	
//	private static void TestCase2(){
//		final Unsafe theUnsafe=(Unsafe) AccessController.doPrivileged(
//	            new PrivilegedAction<Object>() {
//		              @Override
//		              public Object run() {
//		                try {
//		                  Field f = Unsafe.class.getDeclaredField("theUnsafe");
//		                  f.setAccessible(true);
//		                  return f.get(null);
//		                } catch (NoSuchFieldException e) {
//		                  // It doesn't matter what we throw;
//		                  // it's swallowed in getBestComparer().
//		                  throw new Error();
//		                } catch (IllegalAccessException e) {
//		                  throw new Error();
//		                }
//		              }
//		            });
//		 //long rw = theUnsafe.getInt(null, 6);//.getLong(null,(long) 566 );//(byte[]) offset2Adj + (long) 566
//		theUnsafe.getLongVolatile(null, 5555);
//		
//	}
}
