package csbst.testing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jdt.core.dom.IVariableBinding;

public class DataMember {
	private IVariableBinding key;
	private boolean isAccessible;
	private Vector<Method> methodTransformes;
	private Vector<Constructor> constructorsMayTransforme;
	private Vector<Method> methodOthers;
	private Vector<Method> methodReporters;
	
	public DataMember(IVariableBinding key) throws IOException{
		this.key=key;
		//-----------Reporters
		Set<Integer> branchGetters=JTE.dataMemberUseAnalyser.getDataMemberBranchReportersMap().get(key);	
		//System.out.println(" Repporters : "+key +" reporter branch " + branchGetters);
		Vector<Path>dmPathsGetters=new Vector<Path>();
		for(Integer b:branchGetters){
			dmPathsGetters.addAll(JTE.getAccessiblePaths(b));
		}
		  
		methodReporters =new Vector<Method>();
		//constructorsMayTransforme =new Vector<Constructor>();
		for(Path p:dmPathsGetters){			
			if(p.getEntryPoint() instanceof Method){
				if(((Method)p.getEntryPoint()).getDeclaringClass().getCanonicalName().equals(key.getDeclaringClass().getBinaryName())){
					//you can ignor static methods
					Method m=(Method) p.getEntryPoint();
					if(!Modifier.isStatic(m.getModifiers()))
							methodReporters.add((Method) p.getEntryPoint());
				}
			}
		}
		
		Method toString=null;
		try {
			toString=JTE.currentClassUnderTest.getClazz().getMethod("toString", null);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if(toString!=null)
			methodReporters.add(toString);
		
		Method hashcode=null;
		try {
			hashcode=JTE.currentClassUnderTest.getClazz().getMethod("hashcode", null);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if(hashcode!=null)
			methodReporters.add(hashcode);

		
		//-----------Others
		Set<Integer> branchReporters=JTE.dataMemberUseAnalyser.getDataMemberBranchOthersMap().get(key);	
		//System.out.println(" Others : "+key +" Others branch " + branchReporters);
		Vector<Path>dmPathsReporters=new Vector<Path>();
		for(Integer b:branchReporters){
			dmPathsReporters.addAll(JTE.getAccessiblePaths(b));
		}
		  
		methodOthers =new Vector<Method>();
		//constructorsMayTransforme =new Vector<Constructor>();
		for(Path p:dmPathsReporters){			
			if(p.getEntryPoint() instanceof Method){
				if(((Method)p.getEntryPoint()).getDeclaringClass().getCanonicalName().equals(key.getDeclaringClass().getBinaryName()))
					methodOthers.add((Method) p.getEntryPoint());
			}
		}
		
		//----------transformers
		Set<Integer> branchTransformers=JTE.dataMemberUseAnalyser.getDataMemberBranchTransformersMap().get(key);	
		//System.out.println(" Transformers : "+key +" Transformers branch " + branchTransformers);
		Vector<Path>dmPaths=new Vector<Path>();
		for(Integer b:branchTransformers){
			dmPaths.addAll(JTE.getAccessiblePaths(b));
		}
		  
		methodTransformes =new Vector<Method>();
		constructorsMayTransforme =new Vector<Constructor>();
		for(Path p:dmPaths){			
			if(p.getEntryPoint() instanceof Method){
				if(((Method)p.getEntryPoint()).getDeclaringClass().getCanonicalName().equals(key.getDeclaringClass().getBinaryName()))
					methodTransformes.add((Method) p.getEntryPoint());
			}
			else{
				if(p.getEntryPoint()!=null && p.getEntryPoint() instanceof Constructor)
					if(((Constructor)p.getEntryPoint()).getDeclaringClass().equals(key.getDeclaringClass()))
						constructorsMayTransforme.add((Constructor) p.getEntryPoint());	
			}
		}
		

		//isAccessible=!(Modifier.isPrivate(key.getModifiers())||Modifier.isProtected(key.getModifiers()));
	}
	
	public IVariableBinding getKey(){
		return key;
	}
	public Vector<Method> getMethodTransformers(){
		return methodTransformes;
	}
	
	public Vector<Method> getMethodOthers(){
		return methodOthers;
	}
	
	public Vector<Method> getMethodReporters(){
		return methodReporters;
	}
	
	public Vector<Constructor> getConstructorTransformers(){
		return constructorsMayTransforme;
	}
	
	public String toString(){
		return key.getName();
	}
}
