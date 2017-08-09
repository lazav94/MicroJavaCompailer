package pp1.vl130298;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import pp1.vl130298.util.Log4JUtils;

public class MJTest {

	static {
		// DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		// Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}

	public static void main(String[] args) throws IOException {
		PrintWriter outputFile;
		String filename = "";
		if ("-f".equals(args[0]))
			filename = "config/log4j_file.xml";
		else if ("-c".equals(args[0]))
			filename = "config/log4j_console.xml";
		else if ("-cf".equals(args[0]) || "-fc".equals(args[0]) || "".equals(args[0]))
			filename = "config/log4j.xml";
		else {
			System.err.println(
					"Invalid argument for output option\n Choose: \n -c - for consol \n -f - for file \n -cf or -fc or blank for consol and file");
			return;
		}

		DOMConfigurator.configure(filename);
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());

		String inputFilePath = "test/LEXER_TEST.mj";
		String outputFilePath = "output/lexer.lex";
		
		if (args.length > 2) {

			inputFilePath = args[1] != null ? args[1] : "test/LEXER_TEST.mj";
			outputFilePath = args[2] != null ? args[2] : "output/lexer.lex";
		}

		outputFile = new PrintWriter(outputFilePath, "UTF-8");
		

		Singleton s = Singleton.getInstance();
		s.log = Logger.getLogger(MJTest.class);
		Reader br = null;
		try {

			File sourceCode = new File(inputFilePath);
			s.log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			br = new BufferedReader(new FileReader(sourceCode));

			Yylex lexer = new Yylex(br);
			Symbol currToken = null;
			while ((currToken = lexer.next_token()).sym != sym.EOF) {
				if (currToken != null) {
					
					s.log.info(currToken.toString() + " " + currToken.value.toString());
					outputFile.println(currToken.toString() + " " + currToken.value.toString());
				}
			}
		} finally {
			outputFile.close();
			if (br != null)
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
	}

}
