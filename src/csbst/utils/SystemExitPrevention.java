package csbst.utils;

import csbst.testing.SystemExitControl;

public class SystemExitPrevention {
 
    public static void main(String[] args) {
        System.out.println("Preventing System.exit");
        SystemExitControl.forbidSystemExitCall();
 
        try {
            System.out.println("Running a program which calls System.exit");
            ProgramWithExitCall.main(args);
        } catch (SystemExitControl.ExitTrappedException e) {
            System.out.println("Forbidding call to System.exit");
        }
        
        System.out.println("Allowing System.exit");
        SystemExitControl.enableSystemExitCall();
 
        System.out.println("Running the same program without System.exit prevention");
        ProgramWithExitCall.main(args);
 
        System.out.println("This code never executes because the JVM has exited");
    }
}
 
class ProgramWithExitCall {
 
    public static void main(String[] args) {
    	
        System.exit(2);
    }
}
 

//if (perm.getName().contains("exitVM")) {
//if (!perm.getName().contains(".5555")){
//	 //
//	 throw new SecurityException("System.exit attempted and blocked."+perm.getName());
//}else
//	 super.checkExit(5555);
//}


