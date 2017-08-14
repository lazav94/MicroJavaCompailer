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

	static {
		//DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		//Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static int x(int a){
		
			a++;
			a += 3;
			return a+3;
			
	}
	
	public static void main(String[] args) throws Exception {
		
		String filename = "";
		if("-f".equals(args[0]))
			filename = "config/log4j_file.xml";
		else if("-c".equals(args[0]))
			filename = "config/log4j_console.xml";
		else if("-cf".equals(args[0]) || "-fc".equals(args[0]) || "".equals(args[0]))
			filename = "config/log4j.xml";
		else {
			System.err.println("Invalid argument for output option\n Choose: \n -c - for consol \n -f - for file \n -cf or -fc or blank for consol and file");
			return;
		}
		
	
		
		DOMConfigurator.configure(filename);
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
		
		/** putanja do ulaznog fajla  */
		//String inputFilePath = args[1];
		/** putanja do izlaznog fajla */
		//String outputFilePath = args[2];
		
		
		Singleton s = Singleton.getInstance();
		Reader br = null;
		try {
			//File sourceCode = new File(inputFilePath);
			File sourceCode = new File("test/semantic/ARRAYS.mj");
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
	        	
	        	//File objFile = new File(outputFilePath);
	        	File objFile = new File("test/program.obj");
	        	if(objFile.exists())
	        		objFile.delete();
	        	Code.write(new FileOutputStream(objFile));
	        	
	        	s.log.info("Parsiranje uspesno zavrseno!");
	        	
	        }
	        
	        /* Asocijativnos test*/
        	int a, b, c,d ;
        	
        	a = 32;
        	b = 4;
        	c = 15;
        	d = 2;
        	System.out.println("a: " + a + '\t' + "b: "+ b + '\t' + "c: "+ c);
        	a = 2;
//    		a += a *= 5;
        	
        	a += ((5 * 1 - 1) * 2 - (3 % 2 + 3 * 2 - 3) - 1 * 0); 
        	
      
    		
        	//  a +=   b  /= 2 *  (c -= 15 / 3 + 2 * 3 );
        	// a +=   b  /= 2 *  c -= 15 / 3 + 2 * 3 ;
        	 
	        System.out.println("a: " + a + '\t' + "b: "+ b + '\t' + "c: "+ c + '\t' + "d: "+ d);
        /**/
	      
	        
	      
	        
	        
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { s.log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
