package pp1.vl130298;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import pp1.vl130298.util.Log4JUtils;

public class MJTest {
	static boolean test = false;

	public static void main(String[] args) throws IOException {
		PrintWriter outputFile;
		String filename = "config/log4j.xml";
		
		DOMConfigurator.configure(filename);
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());

		String inputFilePath = args[0];
		String outputFilePath = args[1];
		
		
		if (test == true) {
			inputFilePath = "test/LEXER_TEST.mj";
			outputFilePath = "output/lexer.lex";
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
			s.log.info("jflex end");
		}
	}

}
