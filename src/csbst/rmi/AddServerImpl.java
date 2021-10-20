package csbst.rmi;

import java.rmi.*;
import java.rmi.server.*;

import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.MethodGenerator;

public class AddServerImpl extends UnicastRemoteObject
  implements AddServerIntf {

	protected AddServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void MethodExecutorBasedRMI(Object obj, MethodGenerator meth)
			throws RemoteException {
    	if(obj==null)
    		meth.execute();
    	else
    		meth.execute(obj,meth.getClazz());
		
	}
	@Override
	public void ObjectGeneratorBasedRMI(AbsractGenerator ge) throws RemoteException {
		ge.generateRandom();
	}
}
