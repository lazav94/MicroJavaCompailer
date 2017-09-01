package pp1.vl130298;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import pp1.vl130298.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.visitors.MySymbolTableVisitor;

public class MJParserTest {

	static boolean test = true; 
	
	public static void main(String[] args) throws Exception {
		
		String filename = "config/log4j.xml";
		
		DOMConfigurator.configure(filename);
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
		
		/** putanja do ulaznog fajla  */
		String  inputFilePath = args[0];
		
		  if(test == true)
				inputFilePath = new String("test/test302.mj");
			 
		 
		
		/** putanja do izlaznog fajla */
		String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf('.')) + ".obj";
		 
		 if(test == true)
			 outputFilePath = new String("test/program.obj");

		 
		
		Singleton s = Singleton.getInstance();
		Reader br = null;
		try {
			
			  File sourceCode = new File(inputFilePath);
		
			s.log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			s.initLexer(br);
			s.initParser();
			(s.parser).parse();
	        s.log.info("\n");
	        s.counterPrint();
	        
	        
	        if(s.parser.errorDectected == true){
	        	s.log.info("Parsiranje NIJE uspesno zavrseno!");
	        }else{
	        	System.out.println();
	        	Tab.dump(new MySymbolTableVisitor());
	        	
	        	
	        	
	        	File objFile = new File(outputFilePath);
	        	if(objFile.exists())
	        		objFile.delete();
	        	Code.write(new FileOutputStream(objFile));
	        	
	        	s.log.info("Parsiranje uspesno zavrseno!");
	        	
	        }

	        
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { s.log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
