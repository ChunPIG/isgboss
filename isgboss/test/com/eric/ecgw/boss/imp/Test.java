package com.eric.ecgw.boss.imp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.eric.ecgw.boss.entity.TE0;
import com.eric.ecgw.boss.entity.TE1;
import com.eric.ecgw.boss.entity.TE22;

public class Test {
	static String NILL="";
	public static void main(String[] args) {
		TE0 t = new TE0();
		TE1 t1 = new TE1();
		TE22 t2 = new TE22();
		//generate(t2);
		
		long start = System.currentTimeMillis();
		Field[] fs = t.getClass().getFields();
		try {
			for (int i = 0; i < 1 * 10000; i++) {
				re(t, fs);
			}
		} catch (Exception e) {

		}
		System.out.println(System.currentTimeMillis() - start);

		start = System.currentTimeMillis();

		for (int i = 0; i < 1000 * 10000; i++) {
		
			if (t.e0==null) t.e0="";
			if (t.e1==null) t.e1="";
			if (t.e3==null) t.e3="";
			if (t.e4==null) t.e4="";
			if (t.e5==null) t.e5="";
			if (t.e6==null) t.e6="";
			if (t.e7==null) t.e7="";
			
			if (t.e15==null) t.e15="";
			if (t.e18==null) t.e18="";
			if (t.e21==null) t.e21="";
			if (t.e25==null) t.e25="";
			if (t.e26==null) t.e26="";
			if (t.e31==null) t.e31="";
			if (t.e38==null) t.e38="";
			if (t.e41==null) t.e41="";
			if (t.e42==null) t.e42="";
			if (t.e43==null) t.e43="";
			
			if (t.e82==null) t.e82="";
			if (t.e107==null) t.e107="";
			if (t.e108==null) t.e108="";
			if (t.e109==null) t.e109="";
			if (t.e110==null) t.e110="";
			if (t.e111==null) t.e111="";
			if (t.e112==null) t.e112="";
			if (t.e119==null) t.e119="";
			if (t.e120==null) t.e120="";
			if (t.e121==null) t.e121="";
			
			if (t.e143==null) t.e143="";
		}
		System.out.println(System.currentTimeMillis() - start);

	}

	public static void re(Object t19, Field[] fs) throws Exception {

		for (Field f : fs) {

			Object o = f.get(t19);
			if (o == null) {
				f.set(t19, "");
			}

		}

	}

	public static void generate(Object t){
		Field[] fs = t.getClass().getFields();
		for (Field f : fs) {

			System.out.println("if (t."+f.getName()+"==null) t."+f.getName()+"=NILL;");

		}
	}
	
	public static String getPropertyString(Object entityName) throws Exception {
		Class c = entityName.getClass();
		Field field[] = c.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		sb.append("------ " + " begin ------\n");
		for (Field f : field) {
			sb.append(f.getName());
			sb.append(" : ");
			sb.append(invokeMethod(entityName, f.getName(), null));
			sb.append("\n");
		}
		sb.append("------ " + " end ------\n");
		return sb.toString();
	}

	public static Object invokeMethod(Object owner, String methodName, Object[] args)
			throws Exception {

		Class ownerClass = owner.getClass();
		methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
		Method method = null;
		try {
			method = ownerClass.getMethod("get" + methodName);
		} catch (SecurityException e) { // TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (NoSuchMethodException e) {
			return " can't find 'get" + methodName + "' method";
		}
		return method.invoke(owner);
	}
}
