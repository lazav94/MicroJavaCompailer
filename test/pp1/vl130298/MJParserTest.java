package pp1.vl130298;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import pp1.vl130298.util.Log4JUtils;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.visitors.MySymbolTableVisitor;

public class MJParserTest {

	static {
		//DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		//Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception {
		String filename = "";
		if("-f".equals(args[1]))
			filename = "config/log4j_file.xml";
		else if("-c".equals(args[1]))
			filename = "config/log4j_console.xml";
		else if("-cf".equals(args[1]) || "-fc".equals(args[1]) || "".equals(args[1]))
			filename = "config/log4j.xml";
		else {
			System.err.println("Invalid argument for output option\n Choose: \n -c - for consol \n -f - for file \n -cf or -fc or blank for consol and file");
			return;
		}
		
		DOMConfigurator.configure(filename);
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
		
		Singleton s = Singleton.getInstance();
		Reader br = null;
		try {
			File sourceCode = new File("test/v2_test1.mj");
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
	        	s.log.info("Parsiranje uspesno zavrseno!");
	        	Tab.dump(new MySymbolTableVisitor());
	        }
	      
	        
	       s.log.info("Succesfull!!!");
	        
	        
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { s.log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
