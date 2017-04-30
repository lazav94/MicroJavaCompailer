package pp1.vl130298;

import org.apache.log4j.*;
import java.io.*;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;


public class Singleton {
	private static final Singleton instance = new Singleton();
	private Singleton(){}
	public static Singleton getInstance(){ return instance; }
	
	// Message source 
	public static final int LEXER    = 0;
	public static final int SINTAX   = 1;
	public static final int SEMANTIC = 2;
	public static final int CODE     = 3;
	
	// Message type
	public static final int error    = 0;
	public static final int info	 = 1;
	public static final int debug    = 2;
	
	// Message Id
	public static final int noMessage=-1;
	// Type 
	public static final int None     = 0;
	public static final int Int 	 = 1;
	public static final int Char 	 = 2;
	public static final int Array	 = 3;
	public static final int Class 	 = 4;
	public static final int Bool 	 = 5;
	
	public int lexerErrorsCounter     = 0;
	public int sintaxErrorsCounter    = 0;
	public int semanticErrorsCounter  = 0;
	
	public static final Struct boolType = new Struct(Struct.Bool);
	public static final Obj boolObj = new Obj(Obj.Type, "bool" , boolType);
	
	public Yylex    lexer  = null;;
	CUP$MJParser$actions action = null;
	public MJParser parser = null;
	
	public Yylex initLexer(Reader br){
		lexer = new Yylex(br);
		return lexer;
	}
	public MJParser initParser(){
		parser = new MJParser(lexer);
		action = parser.action_obj;
		return parser;		
	}
	public void initSymbolTable(){
		action = parser.action_obj;
		Tab.init();
		
		Tab.currentScope.addToLocals(boolObj);      // dodavanje BOOL kao tip	
		
		// insertConstant(0, "true", new Obj(Obj.Con, "CONSTANT", parser.boolType));
		// insertConstant(0, "false", new Obj(Obj.Con, "CONSTANT", parser.boolType));
	}
	public void counterErrors(){
		log.info("LEXER SINTAX SEMATINC\n\t\t\t " + lexerErrorsCounter + "\t" + sintaxErrorsCounter + "\t" + semanticErrorsCounter );
	}
	
	
	public Logger log = Logger.getLogger(getClass());
	
	private String [] Type = {
			"void",
			"int",
			"char",
			"array",
			"class",
			"bool"
	};
	
	private String [] msgSource = {
			"LEKSICKA: ",
			"SINTAKSNA: ",
			"SEMANTICKA:"
	};
	private String [] errorMsgs = {
		
		"Ubaceni simbol ne postoji u tabeli simbola"
	};
	
	private String [] infoMsgs = {
			"Deleracija polja klase",
			"Deleracija globalne promeljive",
			"Deleracija lokalne promeljive",
			
			"Ubacena konstanta",
			"Ubacena promeljiva",
			"Ubacena metoda",
			"Ubacena konstana",
			
			"Obradjuje se funkcija",
			"Obradjuje se klasa"
	};
	
	private String [] debugMsgs = {
			
	};
	
	
	public String getTypeName(int type){
		return this.Type[type];
	}
	
	
	public void lexerError(int line, String symbol, int column){
		StringBuilder msg = new StringBuilder();
		msg.append(column == -1 ? "\t" : "[" + column + "] ");
		msg.append("Symbol: " + symbol);
		message(error, LEXER, noMessage, line, msg);
		lexerErrorsCounter++;
	}
	
	public void sintaxError(int line, String message){
		StringBuilder msg = new StringBuilder(message);
		message(error, SINTAX, noMessage ,line, msg);
		sintaxErrorsCounter++;
	}
	public void sintaxInfo(String message){
		StringBuilder msg = new StringBuilder(message);
		message(info, SINTAX, noMessage ,-1, msg);
	}
	
	public void sintaxInfo(int line, String message){
		StringBuilder msg = new StringBuilder(message);
		message(info, SINTAX, noMessage ,line, msg);
	}
	
	
	
	public void semanticError(int line, String message){
		StringBuilder msg = new StringBuilder(message);
		message(error, SEMANTIC, noMessage ,line, msg);
		semanticErrorsCounter++;
	}
	public void semanticError(String message){
		StringBuilder msg = new StringBuilder(message);
		message(error, SINTAX, noMessage ,-1, msg);
		semanticErrorsCounter++;
	}
	
	public void semanticInfo(String message){
		StringBuilder msg = new StringBuilder(message);
		message(info, SEMANTIC, noMessage ,-1, msg);
	}
	
	public void semanticInfo(int line, String message){
		StringBuilder msg = new StringBuilder(message);
		message(info, SEMANTIC, noMessage ,line, msg);
	}
	
	public void semanticDebug(String message){
		StringBuilder msg = new StringBuilder(message);
		message(debug, SEMANTIC, noMessage ,-1, msg);
	}
	
	public void semanticDebug(int line, String message){
		StringBuilder msg = new StringBuilder(message);
		message(debug, SEMANTIC, noMessage ,line, msg);
	}
	
	public void counterPrint(){
	
        log.info("\n\n==================SINTAKSNA ANALIZA====================\n");
        log.info("Broj definicijea globalnih promenljivih = "                			+ parser.globalVariables);
        log.info("Broj definicija lokalnih promeljivih (u main funkciji) = " 			+ parser.localMainVariables);
        log.info("Broj definicije globalnih konstanti = "                   			+ parser.globalConstants);
        log.info("Broj dekleracije globalnih nizova = "                      			+ parser.globalArrays);
        
        log.info("Broj definicija globalniih i statickih funkicja unutrasnjih klasa = " + parser.glAndStMethInClass);
        log.info("Broj blokovi naredbi = "                                              + parser.blocks);
        log.info("Broj poziv funkicje u telu metode main = "                            + parser.calledFuncMain);
        log.info("Broj delaracija formalnih argumenata funkcije  = "                    + parser.fomalArgMeth);
        
        log.info("Broj definicjije unutrasnjih klasa = "       							+ parser.defInnerClass);
        log.info("Broj defiicije metoda unutrasnjih klasa = "  							+ parser.defInnerClassMeth);
        log.info("Broj deklaracija polja unutrasnjih klasa = " 							+ parser.dekFieldsInnerClass);
        
	}
	
	public void message(int errorType, int msgSource, int msgId, int line, StringBuilder message){
		StringBuilder msg = new StringBuilder();
		

		if(errorType == error)
			  parser.errorDectected = true;
		if(line != -1)
			msg.append("(" + line + ") ");
		msg.append(message);
		
		switch (errorType) {
			case error:
				log.error(msg);
				break;
			case info:
				log.info(msg);
				break;
			case debug:
				log.debug(msg);
				break;
	
			default:
				break;
			}
	}
	
	public void showMessage(){

	}
	
	//=====================================================================================================//
	//---------------------------------------------- Program ----------------------------------------------//
	//=====================================================================================================//
	
	public void program_action(Obj programObj){
		Tab.chainLocalSymbols(programObj); // Povezi sve simbole sa objektom program
		Tab.closeScope();				   // Zatvori program scope
		
		
	}
	
	public void checkMain(){
		if(!action.haveMain)
			semanticError(0, "Ne postoji main funkcija u programu!");
	}
	
//=====================================================================================================//
//---------------------------------------------- CONSTANT ---------------------------------------------//
//=====================================================================================================//
	public void checkContantType(int line){
		if(parser.lastConstType != Tab.noType)
			if(parser.lastConstType != Tab.intType && parser.lastConstType != Tab.charType && parser.lastConstType.equals(new Struct(Struct.Bool))){
				semanticError(line, "Tip konstante moze biti samo int, char ili bool");
				parser.lastConstType = Tab.noType;
			}
	}
	
	public void constantCount(){
		if(parser.isGlobal)
	    	parser.globalConstants++;
	}
	
	public void insertConstant(int line, String constName, Obj constValue){
		 Obj temp;
		 int kind = None;
		    if(parser.lastConstType != Tab.noType){
		       temp = Tab.currentScope.findSymbol(constName);
		       if(temp == null){
		       		if(parser.lastConstType.equals(constValue.getType())){
				   		temp = Tab.insert(Obj.Con, constName, parser.lastConstType);
				   		temp.setAdr(constValue.getAdr());	
				   		sintaxInfo("Ubacena nova konstanta: '" + constName + "' vrednosti = " + temp.getAdr());
			   		}else
			   			sintaxError(line, "Tip \"" + getTypeName(parser.lastConstType.getKind()) + "\" konstante '"+  constName + "' nije kompatibilna sa tipom \""  + getTypeName(constValue.getType().getKind()) + "\" vrednost koja joj se dodeljuje" );	
		       		
			   }
			   else 
			     sintaxError(line, "Ubacena kontanta: '" + constName + "' vec postoji u ovom scope");
		   } 
	}
	
//=====================================================================================================//
//---------------------------------------------- Variable ---------------------------------------------//
//=====================================================================================================//
	
	public void variableError(int line, char safeChar, boolean isSemi){
		String msg = "Oporavak (" +  safeChar + ") ";
		
		if(isSemi)
			parser.lastVarType = Tab.noType; // for semantic
		
		if(parser.isClass == true){
    		sintaxInfo(line, msg + "Dekleracija polja (unutrasnje) klase." );
    	}
    	else{
    		if(parser.isClass == false){
				if(parser.isGlobal)
					sintaxInfo(line, msg + "Definicija globalne promenljive.");
				else
					sintaxInfo(line, msg + "Definicija lokalne promenljive.");
				}
			else 
				sintaxInfo(line, msg + "Polje klase (ne trazi se u zadatku).");
		}
	}
	
	public void variableCount(){
		
		if(parser.isClass == true && parser.isFunct == false)
    		parser.dekFieldsInnerClass++;
    	if(parser.isMain == true && parser.isGlobal == false)
    		parser.localMainVariables++; 
		if(parser.isGlobal && parser.isClass == false && parser.isClass == false && parser.isArray)
			parser.globalArrays++; 
		if(parser.isGlobal && parser.isClass == false && parser.isArray == false)
    		parser.globalVariables++;
	}
	
	public void insertVariable(int line, String name, boolean isArray){

	   	Obj temp = Tab.currentScope.findSymbol(name);
	   	if(temp == null  && !action.methodErrorDetected){
	   	
	   		if(parser.lastVarType != Tab.noType){
	   			
		   		if(parser.isClass  && !parser.isFunct)
		  		 	temp = Tab.insert(Obj.Fld, name, isArray ? new Struct(Struct.Array, parser.lastVarType) : parser.lastVarType);
		  		else
					temp = Tab.insert(Obj.Var, name, isArray ? new Struct(Struct.Array, parser.lastVarType) : parser.lastVarType);
								
				StringBuilder msg = new StringBuilder(isArray ? "Ubacen novi niz: " : "Ubacena: ");
				msg.append(parser.isClass ? "polje klase: " + action.currentClass.getName() :"" );
				msg.append("(" + (parser.isGlobal ? "globalna" : "lokalna") + ") promenljiva"); 
				msg.append(parser.isFunct ? " za funkciju " +action.currentMethod.getName() + ( parser.isClass ? "klase" : "") : "");
				msg.append(" sa imenom '" + name + "'");
				
				sintaxInfo(line,msg.toString());
	   		}
	   		else 
	   			sintaxError(line, "Ubaceni simbol: '" + name + "' nema dobar tip (proveriti ovaj ispis)" );
	   	}else 
	   		sintaxError(line, "Ubaceni simbol: '" + name + "' vec postoji u ovom scope-u" );
	   		
	}
	
	
	//=====================================================================================================//
	//----------------------------------------------- Method ----------------------------------------------//
	//=====================================================================================================//
	
	public void insertMethodAndCount(int line,String name, Struct retType){
		parser.isFunct = true;
		Obj temp = Tab.currentScope.findSymbol(name);
	 	action.currentMethod = Tab.insert(Obj.Meth, name, retType); 
	 	
	  	if(temp == null){
	  		Tab.openScope();
	  		sintaxInfo(line,"Obradjuje se funcija '" + name + "'");
	  		if(parser.isClass && !parser.isStatic) // ako je fja unutar neke klase i nije staticka dodaj this
    			Tab.insert(Obj.Var, "this", action.currentClass.getType());
	  	    
	  		// obrada main fje 	
		  	if(name.equals("main")){ 
		  		if(retType.getKind() != Struct.None)
		  			semanticError(line, "Main funkcija treba ne treba da ima povratu vrednost");
		  		action.haveMain = true;
		  		parser.isMain = true;
		  	}
		  	else parser.isMain = false;  
	 	
		  	// counting
			if(parser.isClass == true ){
				parser.defInnerClassMeth++;
				if(parser.isStatic == true)
					parser.glAndStMethInClass++;
			}

	  	}else
	  		 sintaxError(line, "Simbol sa imenom: '" + name + "' vec postoji u ovom scope-u, ime funkcije mora biti drugacije" );
	}
	
	public void methodError(int line, char safeChar, String message){
		String msg = "Oporavak (" +  safeChar + ") ";
		sintaxError(line, message);
	}
	
	public void methodReturn(int line){
		
		if(!action.returnFound && !action.currentMethod.getType().equals(Tab.noType))
			semanticError(line, "Funckija ocekuje return (povratnu vrednost)");
	}
	public void methodEnd(){
		
		parser.returnType = null;
		if(!action.methodErrorDetected){
			Tab.chainLocalSymbols(action.currentMethod);
			Tab.closeScope();
			
		}
		
		parser.isMain = false;
		parser.isStatic = false;
		parser.isFunct = false;

		action.methodErrorDetected = false;
		action.currentMethod = null; 
	}
	
	public void methodStaticClassCheck(int line){
		if(!parser.isClass)
			semanticError(line, "Kljucna rec static moze da se koristi samo za metode unutrasnje klase");
		parser.isStatic = true;
	}
	
	public void formalParams(Struct type){
		parser.lastVarType = type;
		action.currMethodFormParams++;
		parser.fomalArgMeth++;
	}
	
	//=====================================================================================================//
	//----------------------------------------------- Class -----------------------------------------------//
	//=====================================================================================================//
	
	
}
