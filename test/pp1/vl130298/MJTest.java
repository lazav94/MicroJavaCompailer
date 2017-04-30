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

public class MJTest {

	static {
		//DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		//Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws IOException {
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
		s.log =  Logger.getLogger(MJTest.class);
		Reader br = null;
		try {
			
			File sourceCode = new File("test/lex_test_all.mj");	
			s.log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			br = new BufferedReader(new FileReader(sourceCode));
			
			Yylex lexer = new Yylex(br);
			Symbol currToken = null;
			while ((currToken = lexer.next_token()).sym != sym.EOF) {
				if (currToken != null)
					s.log.info(currToken.toString() + " " + currToken.value.toString());
			}
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { e1.printStackTrace(); }
		}
	}
	
}
