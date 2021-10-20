package csbst.testing;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Vector;

public class Target {
	private int branch;
	private Vector<Path> paths;// the set of path reaching the branch target and starting at a public method. Satisfying one path is enough to satisfy a test target
	//private Vector<Path> unAssisiblePaths;
	private Vector<Method> methodsMayReach;
	private Vector<Constructor> constructorsMayReach;
	private Vector<Method> externalMethodsMayReach;
	
	public Target(int b) throws IOException{	
		
		branch=b;
		methodsMayReach=new Vector<Method>();
		constructorsMayReach=new Vector<Constructor>();
		externalMethodsMayReach=new Vector<Method>();
		
		// generate all paths that reach the branch target
		paths=JTE.getAccessiblePaths(b);

		//System.err.println("Branch number: "+b);
		//System.err.println("paths number: "+paths.size());
		// generate a chromosome as prototype
		generateReachability();
	}
	
	public int getBranch(){
		return branch;
	}
	
	public Vector<Method> getMethodsMayReach(){
		return methodsMayReach;
	}

	public Vector<Method> getExternalMethodsMayReach(){
		return externalMethodsMayReach;
	}
	
	public Vector<Constructor> getconstructorsMayReach(){
		return constructorsMayReach;
	}
	
	private void generateReachability() throws IOException {
		//no way to reach the test target
		if(paths==null ||paths.size()==0  ){ //||paths.get(0).getEntryPoint()==null
			return;
		}
		
		if(paths.get(0).getEntryPoint() instanceof Constructor){
			if(!((Constructor)paths.get(0).getEntryPoint()).getDeclaringClass().equals(JTE.currentClassUnderTest.getClazz())){
				JTE.setClassUnderTest(((Constructor)paths.get(0).getEntryPoint()).getDeclaringClass());
			}
		}

		if(paths.get(0).getEntryPoint() instanceof Method){
			if(!((Method)paths.get(0).getEntryPoint()).getDeclaringClass().equals(JTE.currentClassUnderTest.getClazz()))
				JTE.setClassUnderTest(((Method)paths.get(0).getEntryPoint()).getDeclaringClass());
		}

		for(Path p:paths){
			if(p.getEntryPoint() instanceof Constructor){
				if(!Modifier.isProtected(((Constructor)p.getEntryPoint()).getModifiers()))
					constructorsMayReach.add((Constructor) p.getEntryPoint());
				else{
//				//look for external calls
//				//externalMethodsMayReach;
//					MethodCallingFinder mcf=new MethodCallingFinder();
//				      String tgJar="/Users/abdelilahsakti/PHD2011/Projet/EclipseWS/JTestExpert/CUT/Joda-time23/joda-time-2.3.jar";
//				      String tgCls=((Constructor) p.getEntryPoint()).getDeclaringClass().getCanonicalName();
//				      tgCls=tgCls.replace('.', '/');
//				      if(((Constructor) p.getEntryPoint()).getDeclaringClass().isLocalClass()||((Constructor) p.getEntryPoint()).getDeclaringClass().isMemberClass())
//				    	  tgCls=tgCls.substring(0, tgCls.lastIndexOf('/')) +"$" +tgCls.substring(tgCls.lastIndexOf('/')+1, tgCls.length());
//	
//				      String tgFct=getSugnature((Constructor) p.getEntryPoint());//. "void println(String)";
//					try {
//						mcf.findCallingMethodsInJar(tgJar, tgCls, tgFct);					
//						externalMethodsMayReach.addAll(mcf.getCalleesMethodsSet());
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}
			else{	
				if(p.getEntryPoint()!=null)
					methodsMayReach.add((Method) p.getEntryPoint());
				else{	
					;
				}
			}
		}
		//if(methodsMayReach.size()<1 && constructorsMayReach.size()<1 && paths.size()>1)
	}
//	
//	private String getCanonicalName(Class){
//		
//	}
	private String getSugnature(Constructor md){
		String nm=md.getName().replace('$', '.');//("\\.");
		String name[]=nm.split("\\.");
		//name=name[name.length-1].split("$");
		return signature(name[name.length-1], "",md.getParameterTypes());
	}
	private String getSugnature(Method md){
		return signature(md.getName(), md.getReturnType().getName(),md.getParameterTypes());
//		String signature="";
//		signature+=md.getReturnType().getName()+" ";
//		signature+=md.getName()+" (";
//		for(int i=0; i<md.getParameterTypes().length ;i++){
//			Class p=md.getParameterTypes()[i];
//			signature+=p.getName();
//			if(i<md.getParameterTypes().length-1)
//				signature+=", ";
//			//else
//				
//		}
//		signature+=")";
//		
//		return signature;	
	}
	
	private String signature(String name, String returnType,Class[]parametersType ){
		String signature="";
		signature+=returnType+" ";
		signature+=name+" (";
		for(int i=0; i<parametersType.length ;i++){
			Class p=parametersType[i];
			signature+=p.getName();
			if(i<parametersType.length-1)
				signature+=", ";
			//else
				
		}
		signature+=")";
		
		return signature;
	}
	
	public Iterator<Path> iterator(){
		return paths.iterator();
	}
	
	public int size(){
		return paths.size();
	}
}
