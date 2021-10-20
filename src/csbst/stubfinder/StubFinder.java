package csbst.stubfinder;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

public class StubFinder {

	public static Class getStub(Class abstractClass){
		Class inheritClass=null;
		Set<Class> allAbstractInheritClasses=new HashSet();
		//get classes from the same package
		 Reflections reflections = new Reflections(abstractClass.getPackage());

		 Set<Class<? extends Object>> allClasses =  reflections.getSubTypesOf(Object.class);
		
		for(Class cls:allAbstractInheritClasses)
			if(cls.isAssignableFrom(inheritClass))
				if(Modifier.isAbstract(cls.getModifiers()))
					allAbstractInheritClasses.add(cls);
				else
					return cls;
			
		return null;
	}
}
