package csbst.testing;

import java.security.Permission;


public  class NoExitSecurityManager extends SecurityManager 
{
	private  class ExitTrappedException extends SecurityException {
    }
    
    @Override
    public void checkPermission(Permission permission) {
        if (permission.getName().contains("exitVM")) {
            throw new ExitTrappedException();
        }
    }
    
//  @Override
//  public void checkPermission(Permission permission) {
//      if (permission.getName().contains("exitVM")) {
//          throw new ExitTrappedException();
//      }
    

}
