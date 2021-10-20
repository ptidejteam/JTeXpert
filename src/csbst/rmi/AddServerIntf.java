package csbst.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import csbst.generators.AbsractGenerator;
import csbst.generators.dynamic.MethodGenerator;

public interface AddServerIntf extends Remote {
	public void MethodExecutorBasedRMI(Object obj, MethodGenerator meth) throws RemoteException;
	public void ObjectGeneratorBasedRMI(AbsractGenerator ge) throws RemoteException;
}
