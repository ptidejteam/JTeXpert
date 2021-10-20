package csbst.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import csbst.generators.dynamic.InstanceGenerator;
import csbst.testing.JTE;

public class ClassFinder {
	private static Collection<String> packages;
	
	public static List<Class> getFQNs(String simpleName) {
	    if (packages == null) {
	        packages = getPackages();
	    }
	    
	    Set<Class<?>> allClasses = InstanceGenerator.reflections.getSubTypesOf(Object.class);
	    List<Class> fqns = new ArrayList<Class>();
	    for(Class<?> c : allClasses) {
	    	System.out.println(c);
	    	System.out.println(c.getSimpleName().toString()+"="+simpleName+"*");
	    	System.out.println(c.getSimpleName().toString().equals(simpleName));
	    	if(c.getSimpleName().toString().equals(simpleName))
	    		fqns.add(c);         
	    }
	    
	    if(fqns.size()<1){
		    Reflections reflections1=InstanceGenerator.package2Reflections.get("java.lang");
			if(reflections1==null){
				File java_home = new File(System.getProperty("java.home"));
				File java_classes=new File(java_home.getParent()+"/Classes/classes.jar");
				//System.out.println(java_classes);
				URL[] urls = new URL[1];
				for(int i =0;i<1;i++){
					try {
						urls[i]=java_classes.toURL();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				reflections1 = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false /* don't exclude Object.class */))
				.filterInputsBy(new FilterBuilder().includePackage("java.lang"))
				.setUrls(urls));
				InstanceGenerator.package2Reflections.put("java.lang", reflections1);
			}
		    allClasses = reflections1.getSubTypesOf(Object.class);
		    //fqns = new ArrayList<Class>();
		    for(Class<?> c : allClasses) {
		    	if(c.getSimpleName().toString().equals(simpleName))
		    		fqns.add(c);         
		    }
	    }
	    return fqns;
	}
	
	public static Collection<String> getPackages() {
	    Set<String> packages = new HashSet<String>();
	    for (Package aPackage : Package.getPackages()) {
	        packages.add(aPackage.getName());
	    }
	    return packages;
	}
}
