package csbst.testing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jdt.core.dom.IVariableBinding;

public class ClassUnderTest {
	private Vector<DataMember> dataMembers;
	private Class clazz;
	
	public ClassUnderTest(Class cls){
		clazz=cls;

	}
	
	public void prepareDataMembers() throws IOException{
		dataMembers=new Vector<DataMember>();
		for(Map.Entry<IVariableBinding,Set<Integer>> dm :JTE.dataMemberUseAnalyser.getDataMemberBranchTransformersMap().entrySet()){
			if(dm.getKey()==null)
				continue;
			//TODO jump final data members
			//System.out.println(dm.getKey().getVariableDeclaration().getName()+ " ??? "+dm.getKey().getVariableDeclaration().getConstantValue());
			if(Modifier.isFinal(dm.getKey().getModifiers())&&dm.getKey().getVariableDeclaration().getConstantValue()!=null)
					continue;
			//System.out.println(dm.getKey().getDeclaringClass().getQualifiedName()+ " ??? "+clazz.getCanonicalName());
			if(!dm.getKey().getDeclaringClass().getQualifiedName().equals(clazz.getCanonicalName()))
				continue;
			
			//System.out.println(dm.getKey().getDeclaringClass().getQualifiedName()+ " ??? "+dm.getKey());
			
			DataMember tmpDM=new DataMember(dm.getKey());
			dataMembers.add(tmpDM);
		}
				
	}
	
	public  Vector<DataMember> getDataMembers(){
		return dataMembers;
	}
	
	public Class getClazz(){
		return clazz;
	}
	
//	private class DataMember {
//		private IVariableBinding key;
//		private boolean isAccessible;
//		private Vector<Method> methodsMayTransforme;
//		private Vector<Constructor> constructorsMayTransforme;
//		
//		public DataMember(IVariableBinding key){
//			this.key=key;
//			Set<Integer> branchTransformers=JTE.dataMemberUseAnalyser.getDataMemberBranchTransformersMap().get(key);
//			Vector<Path>dmPaths=new Vector<Path>();
//			for(Integer b:branchTransformers){
//				dmPaths.addAll(JTE.getPublicPaths(b));
//			}
//			
//			methodsMayTransforme =new Vector<Method>();
//			constructorsMayTransforme =new Vector<Constructor>();
//			for(Path p:dmPaths)
//				if(p.getEntryPoint() instanceof Method)
//					methodsMayTransforme.add((Method) p.getEntryPoint());
//				else
//					constructorsMayTransforme.add((Constructor) p.getEntryPoint());		
//			isAccessible=!(Modifier.isPrivate(key.getModifiers())||Modifier.isProtected(key.getModifiers()));
//		}
//		
//		public Vector<Method> getMethodTransformers(){
//			return methodsMayTransforme;
//		}
//		
//		public Vector<Constructor> getConstructorTransformers(){
//			return constructorsMayTransforme;
//		}
//	}
}

