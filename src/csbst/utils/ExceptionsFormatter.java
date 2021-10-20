package csbst.utils;

import java.util.ArrayList;
import java.util.List;



public class ExceptionsFormatter {
	public static final String EXCEPTION_PREAMBLE ="**startException**, ";
	public static final String EXCEPTION_ANALYSIS ="**ExceptionAnalysis**, ";

	public static void printException(Throwable exce, String projectPrefix, String CUT) {
		//"org.apache.hadoop"
		if(exce==null 
				||  exce.getStackTrace()==null 
				|| exce.getStackTrace().length<1 
				|| projectPrefix==null 
				|| projectPrefix.length()<1)
			return;

		ExceptionFormat ex=new ExceptionFormat(exce,projectPrefix);
		
		if(!ex.clazz.contains("JTETestCases"))
			System.err.println(EXCEPTION_PREAMBLE+ex.toString());
		//exce.printStackTrace();
		
	}
	
	public static void ExceptionAnalyzer(Throwable exce){
		ExceptionFormat ex=new ExceptionFormat(exce,"JTETestCases");
		if(ex.clazz.contains("JTETestCases"))
			System.err.println(EXCEPTION_ANALYSIS+ex.toString());		
	}
	
	public static class ExceptionFormat{
		private String exception="";
		private String clazz="";
		private String method="";
		private String line="";
		private String fileName="";
		private String level="";
		private String message="";
		private List<ExceptionFormat> causesList=new ArrayList();
		private ExceptionFormat tc;
		private boolean exist=false;
		private  Throwable causeException;
		
		
		public ExceptionFormat(Throwable exce, String projectPrefix){
			this( exce,  projectPrefix, true);
		}

		
		public ExceptionFormat(Throwable exce, String projectPrefix, boolean large){
//			if(exce==null || exce.getStackTrace()==null)
//				return;
			
			int e=0;
			boolean exist=false;
			while (e<exce.getStackTrace().length && !exist ) {
				if(exce.getStackTrace()[e].getClassName().contains("JTETestCases"))
					exist=true;
				else
					e++; 
			}
			
			if(!exist) {
				System.err.println("************************=============");
				exce.printStackTrace();
				return;
				
			}
			
			this.exist=exist;
			
			
			this.exception=exce.getClass().getName();
			
			setClazz(exce.getStackTrace()[e].getClassName());
			method=exce.getStackTrace()[e].getMethodName();
			setLine(""+exce.getStackTrace()[e].getLineNumber());
			fileName=exce.getStackTrace()[e].getFileName();
			level=""+e;
			if(large){
				tc=new ExceptionFormat(exce,"JTETestCases", false);
				
				if(!tc.exist){
					tc.clazz="???";
					tc.method="???";
					tc.line="000";
				}

				Throwable cause=exce.getCause();
				while(cause!=null){
					ExceptionFormat ef=new ExceptionFormat(cause,"JTETestCases",false); //projectPrefix
					if(!ef.exist)
						break;
					
					getCausesList().add(ef);
					cause=cause.getCause();
				}
			}
//				if(cause!=null)
//					causeException=cause;
//				else
//					causeException=new Throwable();
			
			
			message=exce.getMessage();
			if(message==null)
				message="";
			message=message.replace(",", "_COMMA_");
			message=message.replace(System.getProperty("line.separator"), "_EOL_");
			message=message.replace("\u001a", "_EOF_");	//EOF character
			final char EOF = (char)26;
			message=message.replace(EOF, '_');
		}
		
		public ExceptionFormat(String logLine){
			this(logLine,true);
		}
		
		public ExceptionFormat(String logLine, boolean large){
			String[]fields;
			if(large){
				logLine+=" END.";
				fields=logLine.split(", ");
				if(fields==null)
					return;
				
				this.exception=fields[1];
				setClazz(fields[2]);
				method=fields[3];
				setLine(fields[4]);
				fileName=fields[5];
				level=fields[6];
				tc=new ExceptionFormat(fields[7],false);
			}else{
				fields=logLine.split(": ");
				if(fields==null )
					return;
				
				setClazz(fields[0]);
				method=fields[1];
				setLine(fields[2]);			
			}
				
			

			int i=8;
			while(i<fields.length-1){
				getCausesList().add(new ExceptionFormat(fields[i],false));
				i++;
			}
			
			//if(large)
			message=fields[fields.length-1];
			
		}
		
		public String toString(){
			String st=exception 
					 + ", "+ getClazz()
					 + ", "+ method
					 + ", "+ getLine()
					 + ", "+ fileName
					 + ", "+ level;
			
			if(tc!=null)
				st+=", "+ tc.toSmallString();
			
			for(ExceptionFormat cause:getCausesList())
					st+=", "+cause.toSmallString();
			
			st+=", "+ message;
			return 	 st;
		}
		
		public String toSmallString(){		
			return  getClazz()
					 + ": "+ method
					 + ": "+ getLine();
		}
		
		public String getBugString(){
			String st=exception 
					 + ", "+ getClazz()
					 + ", "+ method
					 + ", "+ getLine()		
					 + ", "+ tc.toSmallString()
					 + ", "+ fileName
					 + ", "+ level;
			
//			for(ExceptionFormat cause:getCausesList())
//				st+=", "+cause.toSmallString();
			
			return 	 st;
		}
		
		public Throwable getCauseException(){
			return  causeException;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public String getLine() {
			return line;
		}

		public void setLine(String line) {
			this.line = line;
		}

		public List<ExceptionFormat> getCausesList() {
			return causesList;
		}

		public void setCausesList(List<ExceptionFormat> causesList) {
			this.causesList = causesList;
		}
		
	
}

	
}

