package pp1.vl130298;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.apache.log4j.Logger;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Singleton {
	private static final Singleton instance = new Singleton();

	private Singleton() {
	}

	public static Singleton getInstance() {
		return instance;
	}

	// Message source
	public static final int LEXER = 0;
	public static final int SINTAX = 1;
	public static final int SEMANTIC = 2;
	public static final int CODE = 3;

	// Message type
	public static final int error = 0;
	public static final int info = 1;
	public static final int debug = 2;

	// Message Id
	public static final int noMessage = -1;
	// Type
	public static final int None = 0;
	public static final int Int = 1;
	public static final int Char = 2;
	public static final int Array = 3;
	public static final int Class = 4;
	public static final int Bool = 5;

	public int lexerErrorsCounter = 0;
	public int sintaxErrorsCounter = 0;
	public int semanticErrorsCounter = 0;

	public static final Struct boolType = new Struct(Struct.Bool);
	public static final Obj boolObj = new Obj(Obj.Type, "bool", boolType);

	public Yylex lexer = null;;
	CUP$MJParser$actions action = null;
	public MJParser parser = initParser();

	public Yylex initLexer(Reader br) {
		lexer = new Yylex(br);
		return lexer;
	}

	public MJParser initParser() {
		parser = new MJParser(lexer);
		action = parser.action_obj;
		return parser;
	}

	public void initSymbolTable() {
		action = parser.action_obj;
		Tab.init();

		Tab.currentScope.addToLocals(boolObj); // dodavanje BOOL kao tip

		// insertConstant(0, "true", new Obj(Obj.Con, "CONSTANT",
		// parser.boolType));
		// insertConstant(0, "false", new Obj(Obj.Con, "CONSTANT",
		// parser.boolType));
	}

	public void counterErrors() {
		log.info("LEXER SINTAX SEMATINC\n\t\t\t " + lexerErrorsCounter + "\t" + sintaxErrorsCounter + "\t"
				+ semanticErrorsCounter);
	}

	public Logger log = Logger.getLogger(getClass());

	private String[] Type = { "void", "int", "char", "array", "class", "bool" };

	// private String[] msgSource = { "LEKSICKA: ", "SINTAKSNA: ", "SEMANTICKA:"
	// };
	// private String[] errorMsgs = {
	//
	// "Ubaceni simbol ne postoji u tabeli simbola" };
	//
	// private String[] infoMsgs = { "Deleracija polja klase", "Deleracija
	// globalne promeljive",
	// "Deleracija lokalne promeljive",
	//
	// "Ubacena konstanta", "Ubacena promeljiva", "Ubacena metoda", "Ubacena
	// konstana",
	//
	// "Obradjuje se funkcija", "Obradjuje se klasa" };
	//
	// private String[] debugMsgs = {
	//
	// };

	public String getTypeName(int type) {
		return this.Type[type];
	}

	public String getObjName(Obj obj) {
		if (obj == null)
			return "getObjName: null!!!";
		switch (obj.getKind()) {
		case Obj.Con:
			return "konstanta";
		case Obj.Elem:
			return "element niza";
		case Obj.Fld:
			return "polje klase";
		case Obj.Meth:
			return "funkcija";
		case Obj.Prog:
			return "PROGRAM";
		case Obj.Type:
			return "Tip!";
		case Obj.Var:
			break;
		case Obj.NO_VALUE:
			return "bez Obj name";

		}
		return "Greska: funkcija getObjName";
	}

	public void lexerError(int line, String symbol, int column) {
		StringBuilder msg = new StringBuilder("Leksicka greska: ");
		msg.append(column == -1 ? "\t" : "[" + column + "] ");
		msg.append("Symbol: " + symbol);
		message(error, LEXER, noMessage, line, msg);
		lexerErrorsCounter++;
	}

	public void sintaxError(int line, String message) {
		StringBuilder msg = new StringBuilder(message);
		message(error, SINTAX, noMessage, line, msg);
		sintaxErrorsCounter++;
	}

	public void sintaxInfo(String message) {
		StringBuilder msg = new StringBuilder(message);
		message(info, SINTAX, noMessage, -1, msg);
	}

	public void sintaxInfo(int line, String message) {
		StringBuilder msg = new StringBuilder(message);
		message(info, SINTAX, noMessage, line, msg);
	}

	public void semanticError(int line, String message) {
		StringBuilder msg = new StringBuilder(message);
		message(error, SEMANTIC, noMessage, line, msg);
		semanticErrorsCounter++;
	}

	public void semanticError(String message) {
		StringBuilder msg = new StringBuilder(message);
		message(error, SINTAX, noMessage, -1, msg);
		semanticErrorsCounter++;
	}

	public void semanticInfo(String message) {
		StringBuilder msg = new StringBuilder(message);
		message(info, SEMANTIC, noMessage, -1, msg);
	}

	public void semanticInfo(int line, String message) {
		StringBuilder msg = new StringBuilder(message);
		message(info, SEMANTIC, noMessage, line, msg);
	}

	public void semanticDebug(String message) {
		StringBuilder msg = new StringBuilder(message);
		message(debug, SEMANTIC, noMessage, -1, msg);
	}

	public void semanticDebug(int line, String message) {
		StringBuilder msg = new StringBuilder(message);
		message(debug, SEMANTIC, noMessage, line, msg);
	}

	public void counterPrint() {

		log.info("\n\n==========================SINTAKSNA ANALIZA============================\n");
		log.info("\n--------------------------------- A -----------------------------------");
		log.info("Broj definicija globalnih promenljivih = " + parser.globalVariables);
		log.info("Broj definicija lokalnih promeljivih (u main funkciji) = " + parser.localMainVariables);
		log.info("Broj definicije globalnih konstanti = " + parser.globalConstants);
		log.info("\n--------------------------------- B -----------------------------------");
		log.info("Broj definicija globalniih i statickih funkicja unutrasnjih klasa = " + parser.methods);
		log.info("Broj poziv funkicje u telu metode main = " + parser.calledFuncMain);
		log.info("Broj delaracija formalnih argumenata funkcije  = " + parser.fomalArgMeth);
		log.info("\n--------------------------------- C -----------------------------------");
		log.info("Broj definicjije unutrasnjih klasa = " + parser.defInnerClass);
		log.info("Broj defiicije metoda unutrasnjih klasa = " + parser.defInnerClassMeth);
		log.info("Broj deklaracija polja unutrasnjih klasa = " + parser.dekFieldsInnerClass);

	}

	public void message(int errorType, int msgSource, int msgId, int line, StringBuilder message) {
		StringBuilder msg = new StringBuilder();

		if (errorType == error)
			parser.errorDectected = true;
		if (line != -1)
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

	public void showMessage() {

	}

	// =====================================================================================================//
	// ------------------------------------------------ Util
	// -----------------------------------------------//
	// =====================================================================================================//

	public boolean isConst(Obj o) {
		if (o != null)
			return o.getKind() == Obj.Con;
		return false;
	}

	public boolean isInt(Obj o) {
		if (o != null)
			return o.getType().getKind() == Struct.Int;

		return false;
	}

	public boolean isChar(Obj o) {
		if (o != null)
			return o.getType().getKind() == Struct.Char;
		return false;
	}

	public boolean isBool(Obj o) {
		if (o != null)
			return o.getType().getKind() == Struct.Bool;
		return false;
	}

	public boolean isIntCharBool(Obj o) {
		return isInt(o) | isChar(o) | isBool(o);
	}

	public boolean isInt(Struct s) {
		if (s != null)
			return s.getKind() == Struct.Int;

		return false;
	}

	public boolean isChar(Struct s) {
		if (s != null)
			return s.getKind() == Struct.Char;
		return false;
	}

	public boolean isBool(Struct s) {
		if (s != null)
			return s.getKind() == Struct.Bool;
		return false;
	}

	public boolean isIntCharBool(Struct s) {
		return isInt(s) | isChar(s) | isBool(s);
	}

	public boolean isArray(Obj o) {
		if (o != null)
			return o.getType().getKind() == Struct.Array;
		return false;
	}

	public boolean isArray(Struct s) {
		if (s != null)
			return s.getKind() == Struct.Array;
		return false;
	}

	public boolean isMethod(Obj o) {
		if (o != null)
			return o.getKind() == Obj.Meth;
		return false;
	}

	public boolean isVariable(Obj o) {
		if (o != null)
			return o.getKind() == Obj.Var;
		return false;
	}

	public boolean isClassField(Obj o) {
		if (o != null)
			return o.getKind() == Obj.Fld;
		return false;
	}

	public boolean isArrayElem(Obj o) {
		if (o != null)
			return o.getKind() == Obj.Elem;
		return false;
	}

	public boolean isVariablClassFieldArrayElem(Obj o) {
		return isVariable(o) | isClassField(o) | isArrayElem(o);
	}

	public boolean isClassType(Obj o) {
		if (o != null)
			return o.getType().getKind() == Struct.Class;
		return false;
	}

	public boolean isClassType(Struct s) {
		if (s != null)
			return s.getKind() == Struct.Class;
		return false;
	}

	/*********************************************/
	/******************* PROGRAM *****************/
	/*********************************************/

	/**
	 * Dodaje objektni cvor u tabelu simbola, i otvara scope programa
	 * 
	 * @return Objektni cvor programa
	 */
	public Obj programStart(String programName) {

		Obj programNode = Tab.insert(Obj.Prog, programName, Tab.noType);
		Tab.openScope();
		Tab.currentScope();
		return programNode;

	}

	/**
	 * Popunjava <code>Code.dataSize</code>, povezuje sve simbole sa objetom
	 * 'Program'. I zatvara program scope
	 */
	public void programEnd(Obj programObj) {

		Code.dataSize = Tab.currentScope().getnVars();
		Tab.chainLocalSymbols(programObj);
		Tab.closeScope();

	}

	/**
	 * (0) U programu mora postojati metoda sa imenom <i>main</i>. Ona mora biti
	 * deklarisana kao vid metoda bez arguemnata.
	 * 
	 * @see insertMethodAndCount
	 */
	public void checkMain() {
		if (!action.haveMain)
			semanticError(0, "Ne postoji main funkcija u programu!");
	}

	/**
	 * (9) typeName mora oznacavati tip podataka
	 */
	public Struct insertType(String typeName, int line) {

		Obj typeNode = Tab.find(typeName);
		if (typeNode == Tab.noObj) {
			semanticError(line, " Nije pronadjen tip : '" + typeName + "' u tabeli simbola ");
			return Tab.noType;
		}

		if (Obj.Type == typeNode.getKind()) {
			return typeNode.getType();
		} else {
			semanticError(line, " Ime: '" + typeName + "' ne predstavlja tip");
			return Tab.noType;
		}

	}

	/*********************************************/
	/****************** CONSTANT *****************/
	/*********************************************/
	/**
	 * Proverava da li je tip konstante dobar sa vrednost koja se dodeljuje
	 */
	public void checkContantType(int line) {
		if (parser.lastConstType != Tab.noType) {
			if (isIntCharBool(parser.lastConstType)) {
				semanticError(line, "Tip konstante moze biti samo int, char ili bool");
				parser.lastConstType = Tab.noType;
			}
		}
	}

	/**
	 * Inkrementira broj konstanti (counting function)
	 */
	public void constantCount() {
		if (parser.isGlobal)
			parser.globalConstants++;
	}

	/**
	 * (2) Tip terminala <i> constName </i> mora biti <u>ekvivalentan</u> tipu
	 * <i> constValue <i> <br>
	 * Konstana moze da bude samo int, char ili bool.
	 */
	public void insertConstant(int line, String constName, Obj constValue) {
		Obj temp;
		if (parser.lastConstType != Tab.noType) {
			temp = Tab.currentScope.findSymbol(constName);
			if (temp == null) {
				if (parser.lastConstType.equals(constValue.getType())) {
					temp = Tab.insert(Obj.Con, constName, parser.lastConstType);
					temp.setAdr(constValue.getAdr());
					sintaxInfo("Ubacena nova konstanta: '" + constName + "' vrednosti = " + temp.getAdr());

				} else
					sintaxError(line,
							"Tip \"" + getTypeName(parser.lastConstType.getKind()) + "\" konstante '" + constName
									+ "' nije kompatibilna sa tipom \"" + getTypeName(constValue.getType().getKind())
									+ "\" vrednost koja joj se dodeljuje");

			} else
				sintaxError(line, "Ubacena kontanta: '" + constName + "' vec postoji u ovom scope");
		}
	}

	/*********************************************/
	/****************** VARIABLE *****************/
	/*********************************************/

	/** Oporavak od greske kod variabli */
	public void variableError(int line, char safeChar, boolean isSemi) {
		String msg = "Oporavak (" + safeChar + ") ";

		if (isSemi)
			parser.lastVarType = Tab.noType; // for semantic

		if (parser.isClass == true) {
			sintaxInfo(line, msg + "Dekleracija polja (unutrasnje) klase.");
		} else {
			if (parser.isClass == false) {
				if (parser.isGlobal)
					sintaxInfo(line, msg + "Definicija globalne promenljive.");
				else
					sintaxInfo(line, msg + "Definicija lokalne promenljive.");
			} else
				sintaxInfo(line, msg + "Polje klase (ne trazi se u zadatku).");
		}
	}

	/**
	 * Inkrementira broj promeljivih (counting function)
	 */
	public void variableCount() {

		if (parser.isClass == true && parser.isMethod == false)
			parser.dekFieldsInnerClass++;
		if (parser.isMain == true && parser.isGlobal == false)
			parser.localMainVariables++;

		if (parser.isGlobal == true && parser.isClass == false)
			parser.globalVariables++;
	}

	public Obj insertVariable(int line, String name) {

		Obj temp = Tab.currentScope.findSymbol(name);

		if (temp == null && !action.methodErrorDetected) {

			if (parser.lastVarType != Tab.noType) {
				Struct s;
				if (parser.isArray)
					s = new Struct(Struct.Array, parser.lastVarType);
				else
					s = parser.lastVarType;

				if (parser.isClass) {
					if (action.staticFldMap.get(action.currentClass.getType()) == null
							|| action.nonStaticFldMap.get(action.currentClass.getType()) == null)
						return Tab.noObj;

					if (action.currentClass == null)
						return Tab.noObj;
					if (action.staticFldMap == null || action.nonStaticFldMap == null)
						return Tab.noObj;

					if (!parser.isMethod) {
						temp = Tab.insert(Obj.Fld, name, s);
						action.thisFld.add(temp);

						if (!action.isActPar) {
							if (parser.isStaticFld)
								(action.staticFldMap.get(action.currentClass.getType())).add(temp);
							else
								(action.nonStaticFldMap.get(action.currentClass.getType())).add(temp);
						}
					} else {

						temp = Tab.insert(Obj.Var, name, s);
						if (!action.isActPar) {
							if (parser.isStatic)
								(action.staticFldMap.get(action.currentClass.getType())).add(temp);
							else
								(action.nonStaticFldMap.get(action.currentClass.getType())).add(temp);
						}
					}

				} else
					Tab.insert(Obj.Var, name, s);

				StringBuilder msg = new StringBuilder(parser.isArray ? "Ubacen novi niz: " : "Ubacena: ");
				msg.append(parser.isClass ? "polje klase: " + action.currentClass.getName() : "");
				msg.append("(" + (parser.isGlobal ? "globalna" : "lokalna") + ") promenljiva");
				msg.append(parser.isMethod
						? " za funkciju " + action.currentMethod.getName() + (parser.isClass ? "klase" : "") : "");
				msg.append(" sa imenom '" + name + "'");

				sintaxInfo(line, msg.toString());
			} else
				sintaxError(line, "Ubaceni simbol: '" + name + "' nema dobar tip (proveriti ovaj ispis)");
		} else
			sintaxError(line, "Ubaceni simbol: '" + name + "' vec postoji u ovom scope-u");
		return temp;

	}

	/*********************************************/
	/******************* METHOD ******************/
	/*********************************************/

	/**
	 * (13.1 & 31.1) <i>designator</i> mora oznacavati staticku ili nestaticku
	 * metodu unutrasnje klase ili globalnu funkciju glavnog programa.
	 */
	public Obj checkMethodCallDesignator(Obj func, boolean factor, int line) {

		if (func.getKind() != Obj.Meth)
			semanticError(line,
					"Designator mora oznacavati staticku ili nestaticku metodu unutrasnje klase ili globalnu funkciju glavnog programa! ' "
							+ func.getName());

		if (parser.isMain == true)
			parser.calledFuncMain++;

		if (factor)
			if (func.getType() == Tab.noType)
				semanticError(line, "Funkcija ne vraca vrednost pa ne moze da bude unutar izraza");

		action.isActPar = true;

		if (Obj.Meth == func.getKind()) {

			sintaxInfo("Pronadjen poziv funkcije: '" + func.getName() + "' na liniji " + line);
			return func;

		} else {
			sintaxError(line, "Ime :" + func.getName() + " nije funkcija");
			return Tab.noObj;

		}

	}

	public Obj afterActParsMethod(Obj func, boolean factor, int line) {

		// System.out.println("factor?FAC:DS " + func.getName());

		if (func.getName().equals("len"))
			Code.put(Code.arraylength);
		else if (!(func.getName().equals("chr") || func.getName().equals("ord"))) {
			if (action.currentMethodStack.isEmpty())
				return Tab.noObj;
			if (isMethodWithVarArgs(action.currentMethodStack.peek())) {

				patchVarArgs();
				action.haveVarArgs = false;
				if (parser.isMain)
					action.FormalParamStack.remove(0);

			}

			int destAdr = func.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(destAdr);
		}

		if (!factor) {
			if (func.getType() != Tab.noType)
				Code.put(Code.pop);
		}
		action.currentMethodStack.pop();

		// if (action.currentMethodStack != null &&
		// !action.currentMethodStack.isEmpty())
		// System.out.println("current method stack pop, new : " +
		// action.currentMethodStack.peek().getName());
		// else
		// System.out.println("current method stack pop, EMPTY : ");

		if (func.getName().equals("len")) {
			if (!action.FormalParamStack.isEmpty())
				action.FormalParamStack.remove(0);
		}
		return func;

	}

	/**
	 * (6)
	 */
	public void insertMethodAndCount(int line, String name, Struct retType) {
		parser.isMethod = true;
		Obj temp = Tab.currentScope.findSymbol(name);
		action.currentMethod = Tab.insert(Obj.Meth, name, retType);

		if (temp == null) {
			Tab.openScope();
			String s = "";
			if (parser.isGlobal && parser.isClass == false)
				s = "globalna funkcija ";
			else if (parser.isClass)
				s = " funkcija unutar klase ";
			else
				s = " lokalna funkcija ";

			sintaxInfo(line, "Obradjuje se " + s + "'" + name + "'");
			if (parser.isClass) {
				if (!parser.isStatic)
					Tab.insert(Obj.Var, "this", action.currentClass.getType());

				if (action.parentClass != null) {
					for (Obj staticFld : action.staticFldMap.get(action.parentClass)) {
						if (staticFld != null && staticFld.getKind() == Obj.Meth
								&& staticFld.getName().equals(action.currentMethod.getName()))
							semanticError("Metoda sa imenom: " + staticFld.getName()
									+ " se ne moze redefinisati jer je ona staticna u nadklasi date klase!");
					}
				}

			}

			// obrada main fje
			if (name.equals("main")) {
				if (retType.getKind() != Struct.None)
					semanticError(line, "Main funkcija ne treba da ima povratnu vrednost");
				action.haveMain = true;
				parser.isMain = true;
			} else
				parser.isMain = false;

			// counting
			parser.methods++;
			if (parser.isClass == true) {

				if (action.staticFldMap.get(action.currentClass.getType()) == null
						|| action.nonStaticFldMap.get(action.currentClass.getType()) == null)
					return;

				if (parser.isStatic) {

					action.nonStaticFldMap.get(action.currentClass.getType()).add(temp);
				} else {
					action.staticFldMap.get(action.currentClass.getType()).add(temp);
				}
				parser.defInnerClassMeth++;
			}

		} else
			sintaxError(line, "Simbol sa imenom: '" + name
					+ "' vec postoji u ovom scope-u, ime funkcije mora imati drugacije ime");
	}

	public void methodError(int line, char safeChar, String message) {
		String msg = "Oporavak (" + safeChar + ") ";
		sintaxError(line, msg + message);
	}

	/** Proverava da li je povratna vrednost metode odgovarajuca */
	public void methodReturnCheck(int line) {

		if (!action.returnFound && !action.currentMethod.getType().equals(Tab.noType))
			semanticError(line, "Funckija ocekuje return (povratnu vrednost) tipa: "
					+ getTypeName(action.currentMethod.getType().getKind()));

	}

	/**
	 * Dohvatamo formalne parametre odrene funcije
	 * 
	 * @param Objektni
	 *            cvor funckije cije formalne parametre dohvatamo
	 */
	private void addFormalParam(Obj method) {

		int numberOfFormalParam = method.getLevel();

		List<Obj> list = new ArrayList<Obj>(method.getLocalSymbols());
		if (list != null && !list.isEmpty()) {
			if (list.get(0).getName().equals("this"))
				action.FormalParamStack.addAll(0, list.subList(1, numberOfFormalParam + 1));
			else
				action.FormalParamStack.addAll(0, list.subList(0, numberOfFormalParam));

		}

	}

	private List<Obj> getFormalParamList(Obj method) {

		int numberOfFormalParam = method.getLevel();
		List<Obj> list = new ArrayList<Obj>(method.getLocalSymbols());
		if (list != null && !list.isEmpty()) {
			if (list.get(0).getName().equals("this"))
				list = list.subList(1, numberOfFormalParam + 1);

			else
				list = list.subList(0, numberOfFormalParam);

		}
		return list;

	}

	public void printFormList() {
		System.out.print("Formal arguments: \n");
		printList(action.FormalParamStack);
	}

	/**
	 * Restartuje sve flagove i pomocne promenljive koriscenje za detekciju
	 * metode
	 */
	public void methodEnd() {

		if (action.currentClass != null && (action.staticFldMap.get(action.currentClass.getType()) == null
				|| action.nonStaticFldMap.get(action.currentClass.getType()) == null))
			return;

		if (!action.methodErrorDetected) {
			if (parser.isClass) {
				if (parser.isStatic)
					action.staticFldMap.get(action.currentClass.getType()).add(action.currentMethod);
				else
					action.nonStaticFldMap.get(action.currentClass.getType()).add(action.currentMethod);
			}
			Tab.closeScope();

		}
		parser.returnType = Tab.noType;

		// if (action.currentMethod.getName().equals("main"))
		// ;
		parser.isMain = false;
		parser.isStatic = false;
		parser.isMethod = false;

		action.methodErrorDetected = false;
		action.currentMethod = null;
		action.returnFound = false;

		resetFormAndActualList();

	}

	public void resetFormAndActualList() {
		// action.FormalParamStack = new ArrayList<>();
		// action.ActualParamStack = new ArrayList<>();
		action.varArgsType = Tab.noType;
	}

	/**
	 * @return int - number of var args
	 * 
	 */
	public int varArgsSize = 0;
	public int actualParamSize = 0;
	public List<Obj> varArgs = new ArrayList<Obj>();
	public Obj currentVarArgsMethod = Tab.noObj;

	public int checkParam(Obj actualParam, int line) {

		if (actualParam == null) {

			semanticError(line, "Actual param is null! (checkParam)");
			// action.FormalParamStack.remove(0);
			return 0;
		}

		// ulazak u if ako poslednja pozvana metoda nije metoda sa promenljivim
		// brojem argumenata
		if (!isMethodWithVarArgs(action.currentMethodStack.peek())) {
			Obj formalParam = Tab.noObj;

			// semanticInfo("FP: " +
			// getTypeName(action.FormalParamStack.get(0).getType().getKind()) +
			// " AP: "
			// + getTypeName(actualParam.getType().getKind()));

			if (!action.FormalParamStack.isEmpty())
				formalParam = action.FormalParamStack.remove(0);

			if (isArray(formalParam)) {
				if (!isArray(actualParam)) {
					semanticError(line,
							"Formalni parametar tipa: " + getTypeName(formalParam.getType().getKind())
									+ " ne slaze se sa stvarnim parametrom tipa: "
									+ getTypeName(actualParam.getType().getKind()));
					return 0;
				}
			} else {
				if (!formalParam.getType().equals(actualParam.getType())) {
					semanticError(line,
							"Formalni parametar tipa: " + getTypeName(formalParam.getType().getKind())
									+ " ne slaze se sa stvarnim parametrom tipa: "
									+ getTypeName(actualParam.getType().getKind()));
					return 0;
				}

			}

		} else {
			/**
			 * 1 Ako je formalni parametar isto se ponasa kao da nema var args
			 */

			// System.out.println("FP var args");
			// printList(action.FormalParamStack);
			Obj formalParam = Tab.noObj;
			List<Obj> list = new ArrayList<Obj>(action.currentMethodStack.peek().getLocalSymbols());

			// last - objekat poslednjeg formalnog argumenta kod funkiije sa
			// promeljviim brojem argumenata
			// array
			Obj last;
			if (!list.isEmpty())
				last = list.get(action.currentMethodStack.peek().getLevel() - 1);
			else {
				// Za poziv funkcije pre main(a) , pre nego sto se zavrsila
				// dekleracija (telo petlje)!!!
				// Rekurzija
				last = null;
				for (Obj m : action.MehtodWithVarArgs) {
					if (m.getName() == action.currentMethodStack.peek().getName()) {
						list = new ArrayList<Obj>(m.getLocalSymbols());
						last = list.get(m.getLevel() - 1);
						break;
					}
				}
				if (last == null) {
					semanticError(line, "FATALNA GRESKA, last == null");
					return 0;
				}
				if (isArray(last)) {
					semanticError(line,
							"FATALNA GRESKA, last treba da bude array a ne: " + getTypeName(last.getType().getKind()));
					return 0;
				}

			}

			// Dok ne dodjemo do poslednjeg argumenta (var arg argument, upari
			// formalne i stvarne argumente
			if (!action.FormalParamStack.get(0).getName().equals(last.getName())) {
				formalParam = action.FormalParamStack.remove(0);

				if (isArray(formalParam) && !isArray(actualParam)) {
					semanticError(line,
							"Formalni parametar tipa: " + getTypeName(formalParam.getType().getKind())
									+ " ne slaze se sa stvarnim parametrom tipa "
									+ getTypeName(actualParam.getType().getKind()));
					return 0;
				} else if (!formalParam.getType().equals(actualParam.getType())) {
					semanticError(line,
							"Formalni parametar tipa: " + getTypeName(formalParam.getType().getKind())
									+ " ne slaze se sa stvarnim parametrom tipa "
									+ getTypeName(actualParam.getType().getKind()));
					return 0;
				}
			} else {

				// System.out.println("OKI " +
				// action.currentMethodStack.peek().getName());
				/** 1 Ako je var args actual param */
				// Proveri actual param sa tipom var args
				// Dodaj u hash za ovu funkciju ovaj parametar

				// Dovhati VAR ARGs tip
				action.varArgsType = getVarArgsType(action.currentMethodStack.peek());
				// System.out.print("Var args type : " +
				// getTypeName(action.varArgsType.getKind()));

				// uporedi tip od var args sa tipom actual param
				Struct actType = isArray(actualParam) ? actualParam.getType().getElemType() : actualParam.getType();
				if (!action.varArgsType.equals(actType)) {
					semanticError(line, "VarArg acutal param tip: " + getTypeName(action.varArgsType.getKind())
							+ " se ne poklapa sa tipom formalnog var args " + getTypeName(actType.getKind()));
					return 0;

				}

				action.varArgsStack.peek().add(actualParam);
				// System.out.println("add in varargs stack " +
				// actualParam.getName() + " size : "
				// + action.varArgsStack.peek().size());

			}

		}
		return 0;
	}

	/**
	 * 
	 * Ako se radi o funkciji sa promenljivim brojem argumenata
	 * <code>(VarArgs)</code>, sve argumente koji nisu klasicni formalni
	 * argumenti ubacuje u niz i taj niz se prosledjuje kao parametar funckije.
	 */
	public void patchVarArgs() {

		List<Obj> varArgsList = action.varArgsStack.peek();
		Struct varArgsType = getVarArgsType(action.currentMethodStack.peek());

		int size = varArgsList.size();
		List<Obj> list = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			Obj o = new Obj(Obj.Var, UUID.randomUUID().toString(), varArgsType);

			// CHECK Can this work?!
			o.setAdr(Code.pc % 128);
			Code.store(o);
			list.add(o);
		}

		// Code.put(Code.pop);

		// Make array
		Code.loadConst(size);
		Code.put(Code.newarray);

		if (isChar(varArgsType))
			Code.put(0);
		else
			Code.put(1);

		for (int i = 0; i < size; i++) {
			Code.put(Code.dup); // ARRAY addres
			Code.loadConst(i); // INDEX

			Code.load(list.get(size - i - 1)); // VALUE

			if (isChar(varArgsType))
				Code.put(Code.bastore);
			else
				Code.put(Code.astore);
		}

		for (int i = 0; i < size; i++) {
			varArgsList.remove(0);
			list.remove(0);
		}

		// System.out.println("\n !Var Args list: ");
		// for (Obj o : action.varArgsStack.peek())
		// System.out.println(o.getAdr());

		action.varArgsStack.pop();

	}

	void printList(List<Obj> list) {
		System.out.println("size: " + list.size());
		for (Obj o : list)
			System.out.println("  " + o.getName() + "  " + getTypeName(o.getType().getKind()));
		System.out.println();
	}

	public void addActualParam(Obj param) {

		if (param != null) {
			if (param.getKind() == Obj.Meth) {
				System.out.println("Brate zasto?");
				addFormalParam(param);
			}

		}

	}

	public void staticCheck(int line) {
		if (!parser.isClass)
			semanticError(line, "Kljucna rec static moze da se koristi samo za metode unutrasnje klase");
		else
			parser.isStatic = true;
	}

	public void formalParams(Obj formParm) {

		action.currMethodFormParams++;
		parser.fomalArgMeth++;

	}

	/*********************************************/
	/******************** CLASS ******************/
	/*********************************************/

	public String classStart(String className, int line) {
		parser.defInnerClass++;
		parser.isClass = true;
		sintaxInfo("Obradjivanje nove klase '" + className + "' na liniji " + line);
		return className;
	}

	/** (5) Tip <i> type </i> mora biti unutrasnja klasa glavnog programa. */
	public void classExtendCheck(String className, Struct extendClassType, int line) {
		Obj temp = Tab.currentScope.findSymbol(className);

		if (temp == null) {

			if (extendClassType.getKind() == Struct.Class) {
				action.parentClass = extendClassType;

				action.currentClass = Tab.insert(Obj.Type, className, new Struct(Struct.Class));
				Tab.openScope();
				Tab.insert(Obj.Fld, "super", extendClassType);

				action.staticFldMap.put(action.currentClass.getType(), new ArrayList<Obj>());
				action.nonStaticFldMap.put(action.currentClass.getType(), new ArrayList<Obj>());

			} else {
				sintaxError(line, "Tip iz koje se izvodi klasa " + className + " nije klasnog tipa.");
				action.currentClass = Tab.noObj;
			}
		} else
			sintaxError(line,
					"Ne moze da se ubaci klasa sa ovim imenom: '" + className + "' vec postoji u ovom scope ");

	}

	public void endClass() {
		Tab.chainLocalSymbols(action.currentClass.getType());
		Tab.closeScope();
		parser.isClass = false;

		// printStaticField(action.currentClass.getType());
		action.thisFld = new ArrayList<Obj>();
		action.currentClass = Tab.noObj;
		action.parentClass = null;

	}

	public void printStaticField(Struct c) {
		if (c != null)
			for (Obj o : action.staticFldMap.get(c)) {
				System.out.println(o.getName() + " " + getTypeName(o.getType().getKind()));
			}
	}

	public void addClass(String className, int line) {

		Obj temp = Tab.currentScope.findSymbol(className);
		if (temp != null)
			semanticError(line, "Ne moze da se ubaci klasa sa imenom: '" + className + "' vec postoji u ovom scope");
		else {
			action.currentClass = Tab.insert(Obj.Type, className, new Struct(Struct.Class));
			Tab.openScope();
			classScope.put(action.currentClass.getType(), Tab.currentScope());

			action.staticFldMap.put(action.currentClass.getType(), new ArrayList<Obj>());
			action.nonStaticFldMap.put(action.currentClass.getType(), new ArrayList<Obj>());
		}

	}

	public void classError() {
		parser.isClass = false;
		action.currentClass = Tab.noObj;
		sintaxInfo("GRESKA ({): Deklaracija prosirenja nadklase  (extends).  ");
	}

	// =====================================================================================================//
	// ----------------------------------------------- Designator
	// -----------------------------------------------//
	// =====================================================================================================//

	public static Scope temp = null;

	public static HashMap<Struct, Scope> classScope = new HashMap<Struct, Scope>();
	public static boolean changeScope = false;
	public static boolean first = false;

	public void changeToGlobalScope(Obj classObj) {

		//if (!"this".equals(classObj.getName())) {
			System.out.println(classObj.getName());
			temp = Tab.currentScope;
			Tab.currentScope = classScope.get(classObj.getType());
			// System.out.println("Scope changed");
		//}
	}

	public void resetScope(Obj classObj) {
		//if (!"this".equals(classObj.getName())) {
			Tab.currentScope = temp;
			temp = null;
		//}
	}

	public boolean isMethodWithVarArgs(Obj method) {
		// if(method == null) return false;
		return action.MehtodWithVarArgs.contains(method);
	}

	public Struct getVarArgsType(Obj method) {
		if (isMethodWithVarArgs(method)) {
			List<Obj> list = getFormalParamList(method);
			if (list == null || list.isEmpty())
				return Tab.noType;
			return list.get(list.size() - 1).getType().getElemType();
		}
		return Tab.noType;
	}

	public Obj designatorName(String designatorName, int line) {

		Obj symbol = Tab.find(designatorName);

		StringBuilder msg = new StringBuilder("Koriscenje ");
		if (symbol != Tab.noObj) {

			switch (symbol.getKind()) {
			case Obj.Con:
				msg.append("konstante '");
				break;
			case Obj.Var:
				msg.append((symbol.getLevel() == 0) ? "(globalna) " : "(lokalna) ");
				if (isArray(symbol))
					msg.append("niza '");
				else
					msg.append("promenljive '");

				break;
			case Obj.Type:

				msg.append("tipa!!! '");
				break;
			case Obj.Meth:
				msg.append("metode '");

				action.currentMethodStack.push(symbol);
				// System.out.println("\nCurrent method stack: ");
				// printList(action.currentMethodStack);

				addFormalParam(symbol);

				if (isMethodWithVarArgs(symbol)) {

					action.varArgsType = getVarArgsType(symbol);
					action.varArgsStack.push(new ArrayList<Obj>());
					// System.out.println("Var Args stack push " +
					// action.varArgsStack.size());
				}

				break;
			case Obj.Fld:
				msg.append("polja klasa '");
				break;
			case Obj.Elem:
				msg.append("elementa niza '");
				break;
			case Obj.Prog:
				msg.append("programa!!! '");
				break;
			}
			msg.append(designatorName + "' " + " tipa: " + getTypeName(symbol.getType().getKind()));
			sintaxInfo(line, msg.toString());

		} else {
			if (!parser.isClass) {
				if (!parser.lastDesignator.isEmpty()) {
					
					if (isClassType(parser.lastDesignator.get(0))) {
						List<Obj> list = getAllClassFld(parser.lastDesignator.get(0));
						symbol = Tab.noObj;
						for (Obj o : list) {
							if (o != null && o.getName().equals(designatorName)) {
								symbol = o;
								if (isMethod(o)) {
									action.currentMethodStack.push(o);
									addFormalParam(o);
									if (isMethodWithVarArgs(o)) {
										action.varArgsType = getVarArgsType(o);
										action.varArgsStack.push(new ArrayList<Obj>());
									}
								}
								return symbol;
							}
						}
					}
				}
				sintaxError(line, "Simbol '" + designatorName + "' ne postoji u tabeli simbola!");
			} else { // ako je u klasi
				if ("this".equals(designatorName) || "super".equals(designatorName)) {
					return new Obj(Obj.Fld, designatorName, Tab.nullType);
				} else {

					List<Obj> list = getAllClassFld(action.currentClass);

					symbol = Tab.noObj;
					for (Obj o : list) {
						if (o != null && o.getName().equals(designatorName)) {
							symbol = o;
							if (isMethod(o)) {
								action.currentMethodStack.push(o);
								addFormalParam(o);
								if (isMethodWithVarArgs(o)) {
									action.varArgsType = getVarArgsType(o);
									action.varArgsStack.push(new ArrayList<Obj>());
								}
							}
							return symbol;
						}
					}
					if (symbol == Tab.noObj)
						semanticError(line, "Ne postoji simbol u tabeli simbola");
				}

			}
			return symbol;
		}
		return symbol;
	}

	/**
	 * (11) <i> Designator mora oznacavati promenljivu element niza ili polje
	 * unutar klase. <br>
	 * Tip nerterminala <i>expr</i> mora biti <u>kompatibline pri dodeli</u> sa
	 * tipom neterminala <i>designator</i>
	 */
	public Obj designatorAssignop(Obj designator, Obj expr, int line) {

		if (!isVariablClassFieldArrayElem(designator)) {
			semanticError(line,
					"Designator  mora oznacavati promenljivu element niza ili polje unutar klase. (dodela vrednosti, assignop)");
			return Tab.noObj;
		}

		if (isClassType(designator) && isClassType(expr)) {

			Obj superObj = Tab.noObj;
			List<Obj> list = new ArrayList<Obj>();
			list.addAll(new ArrayList<Obj>(expr.getType().getMembers()));
			for (Obj o : list) {
				if ("super".equals(o.getName())) {
					superObj = o;
					break;
				}
			}

			if (superObj != Tab.noObj) {

				if (superObj.getType() != designator.getType() && designator.getType() != expr.getType())
					semanticError(line, "Leva i desna nisu kompatibilne pri dodeli vrednost klasa2 (assignop)");

			} else {
				if (designator.getType() != expr.getType()) {
					semanticError(line, "Leva i desna nisu kompatibilne pri dodeli vrednost klasa (assignop)");
				}
			}

		} else {
			Struct designtatorType = ((isArray(designator)) ? designator.getType().getElemType()
					: designator.getType());
			Struct exprType = ((isArray(expr)) ? expr.getType().getElemType() : expr.getType());

			if (isArray(exprType)) {
				exprType = exprType.getElemType();
			}

			if (!designtatorType.equals(exprType)) {
				semanticError(line,
						"Leva (" + getTypeName(designtatorType.getKind()) + ") i desna ("
								+ getTypeName(exprType.getKind())
								+ ") strana nisu kompatibilne pri dodeli vrednost (assignop)");
				return Tab.noObj;
			}
		}

		sintaxInfo("Izvrena operacija = (assignop)");

		return designator;
	}

	/**
	 * (12) Designator mora oznacavati promeljivu, element niza ili polje
	 * objekta unutrasnje klase. Designator mora biti tipa int.
	 */
	public Obj designatorInc(Obj designator, int line) {

		if (!isInt(designator)) {
			semanticError(line, "Kod operacije inkremetiranja designator mora biti tipa int, a ne "
					+ getTypeName(designator.getType().getKind()));
			return Tab.noObj;
		}
		if (!isVariablClassFieldArrayElem(designator)) {
			semanticError(line,
					"Kod operacije inkremetiranja designator mora biti varabla, polje klase ili element niza a ne: "
							+ getObjName(designator));
			return Tab.noObj;
		}

		return designator;

	}

	/**
	 * (12) Designator mora oznacavati promeljivu, element niza ili polje
	 * objekta unutrasnje klase. Designator mora biti tipa int.
	 */
	public Obj designatorDec(Obj designator, int line) {

		if (!isInt(designator)) {
			semanticError(line, "Kod operacije dekrementiranja designator mora biti tipa int, a ne "
					+ getTypeName(designator.getType().getKind()));
			return Tab.noObj;
		}
		if (!isVariablClassFieldArrayElem(designator)) {
			semanticError(line,
					"Kod operacije dekrementiranja designator mora biti varabla, polje klase ili element niza a ne: "
							+ getObjName(designator));
			return Tab.noObj;
		}

		return designator;

	}

	/**
	 * (35.1) Tip neterminala <i> desingaotor </i> mora biti niz.
	 */
	public boolean designatorArray(Obj designator, int line) {

		if (!isArray(designator)) {
			semanticError(line, "Designator mora biti niz! (desingator array) " + designator.getName());
			return false;
		}

		return true;
	}

	/**
	 * (35.2) Tip neterminala <i> expr </i> mora biti <code>int</code>.
	 */
	public boolean exprArray(Obj expr, int line) {

		if (!isInt(expr)) {
			semanticError(line, "Tip neterminala expr " + expr.getName()
					+ " mora biti tipa int (desingator array), a ne :" + getTypeName(expr.getType().getKind()));
			return false;
		}
		return true;
	}

	public List<Obj> getAllClassFld(Obj classObj) {

		List<Obj> list = new ArrayList<Obj>();

		if (parser.isClass) {
			list = new ArrayList<Obj>(action.thisFld);
			if (action.parentClass != null) {
				list.addAll(action.nonStaticFldMap.get(action.parentClass));
				list.addAll(action.staticFldMap.get(action.parentClass));
			}
		} else {

			list.addAll(new ArrayList<Obj>(classObj.getType().getMembers()));

			Obj superObj = Tab.noObj;
			for (Obj o : list) {
				if ("super".equals(o.getName())) {
					superObj = o;
					break;
				}
			}
			if (superObj != Tab.noObj)
				list.addAll(getAllClassFld(superObj));
		}

		// Obrisi sve null
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) == null)
				list.remove(i);

		return list;
	}

	public List<Obj> getAllStaticField(Obj classObj) {

		List<Obj> staticList = action.staticFldMap.get(classObj.getType());

		Obj superObj = Tab.noObj;
		for (Obj o : classObj.getType().getMembers()) {
			if ("super".equals(o.getName())) {
				superObj = o;
				break;
			}
		}

		if (superObj != Tab.noObj)
			staticList.addAll(getAllStaticField(superObj));

		// Obrisi sve null
		for (int i = 0; i < staticList.size(); i++)
			if (staticList.get(i) == null)
				staticList.remove(i);

		return staticList;
	}

	/**
	 * (34) Tip nererminala <i> designator1 </i> mora biti unutrasnja klasa.
	 * <br>
	 * <i>designator2</i> mora biti ili polje ili metoda objekta oznacenog
	 * netereminalom <i> desingator 1 </i>
	 */
	public Obj designatorDot(Obj designator1, Obj designator2, int line) {

		// Ako je designator Klasa polje mora da bude staticko
		if (designator1.getKind() == Obj.Type && isClassType(designator1)) {
			boolean found = false;

			for (Obj o : getAllStaticField(designator1)) {
				if (o != null && designator2.getName().equals(o.getName())) {
					found = true;
					return o;
				}
			}
			semanticError(line, "Kod tacke . (DOT) nije pronadjen staticki desingaor2: " + designator2.getName());
			return Tab.noObj;

		} else if (isClassType(designator1)) {

			if (parser.isClass) {
				List<Obj> list = getAllClassFld(action.currentClass);

				if ("this".equals(designator1.getName()))
					designator1 = action.currentClass;

				for (Obj o : list) {
					if (o != null && designator2.getName().equals(o.getName())) {

						List<Obj> staticFldlist = action.staticFldMap.get(designator1.getType());
						if (action.parentClass != null)
							staticFldlist.addAll(action.staticFldMap.get(action.parentClass));

						if (staticFldlist.contains(o)) {
							semanticError(line, "Polje: " + o.getName()
									+ " je staticko. Ne moze mu se pristupiti preko instance klase. Vec preko imena klase.");
							return Tab.noObj;
						}
						return o;
					}
				}
				semanticError(line, "Kod . (DOT) nije pronadjeno ovo polje: " + designator2.getName() + " u klasi "
						+ designator1.getName());
				return Tab.noObj;
			} else {
				semanticInfo("DOT " + designator2.getName());
				return designator2;
			}
		} else {
			semanticError(line,
					"Designator 1 " + designator1.getName() + " kod . (DOT) mora da bude klasa ili klasnog tipa a ne "
							+ getTypeName(designator1.getType().getKind()));
			return Tab.noObj;
		}
	}

	/**
	 * (26.2)Term mora biti tipa <code>int</code>
	 */
	public Obj exprMinus(Obj term, int line) {

		if (!isInt(term)) {
			semanticError(line, "U minus Expr term mora biti tipa int, a ne: "
					+ (term != null ? getTypeName(term.getType().getKind()) : "minus null!!!"));
			return Tab.noObj;
		}
		return term;

	}

	/**
	 * (27) <br>
	 * <i>term</i> i <i>addopTerm</i> moraju biti tipa <code>int</code>.<br>
	 * U svakom slucaju tipovi za <i>term</i> i <i>addopTerm</i> moraju biti
	 * <u>kompatiblni</u>.<br>
	 * Ako je <i>Addop</i> kombinovani aritmeticki operator
	 * <code>(+=,-=)</code>, <i>addopTerm</i> mora oznacavati promeljivu,
	 * element niza ili polje unutar objeka.
	 */
	public Obj expr(Obj addopTerm, Obj term, Integer addop, int line) {

		if (addopTerm == null) {
			semanticError(line, "Tip (term) je null!");
			return Tab.noObj;
		}

		if (!isInt(term)) {
			semanticError(line,
					"U Expr (addop) term mora biti tipa int, a ne : " + getTypeName(term.getType().getKind()));
			return Tab.noObj;
		}
		if (!isInt(addopTerm)) {
			semanticError(line,
					"U Expr (addop) addopTerm mora biti tipa int, a ne: " + getTypeName(addopTerm.getType().getKind()));
			return Tab.noObj;
		}

		if (!addopTerm.getType().compatibleWith(term.getType())) {

			semanticError(line, "U Expr (addop) addopTerm nije tipa int");
			return Tab.noObj;

		}

		if (addop.intValue() > 101) {
			if (!isVariablClassFieldArrayElem(addopTerm)) {
				semanticError(line,
						"Ako je Addop kombinovani aritmeticki operator (+=,-=), addopTerm mora oznacavati promeljivu, element niza ili polje unutar objeka. A ne "
								+ getObjName(addopTerm) + " " + addopTerm.getName());
				return Tab.noObj;
			}
		}
		// term i addop su int ako dodje dovde

		return term; // expr = term + addopTerm

	}

	/**
	 * (15) Designator mora biti tipa int, char ili bool.
	 */
	public Obj statementPrint(Obj expr, int line) {
		if (!isIntCharBool(expr))
			semanticError(line, "U print funkciji argument mora biti int, bool ili char, a ne: "
					+ (expr != null ? getTypeName(expr.getType().getKind()) : "NULL!!!"));

		return Tab.noObj;
	}

	/**
	 * (16) Designator mora oznacavati promenlivu, element niza ili polje unutar
	 * klase. Designator mora biti int, char ili bool.
	 */
	public Obj statementRead(Obj designator, int line) {

		if (!isIntCharBool(designator)) {
			semanticError(line, "U read funkciji argument mora biti int, bool ili char, a ne: "
					+ (designator != null ? getTypeName(designator.getType().getKind()) : "NULL!!!"));
			return Tab.noObj;
		}

		if (!isVariablClassFieldArrayElem(designator)) {
			semanticError(line, "U read expr mora biti promenljiva, element niza ili polje unutar objekta");
			return Tab.noObj;
		}
		return designator;
	}

	public void loadDelayedObj(Obj obj) {
		if (isArrayElem(obj) && action.onStack == false) {
			Code.load(obj);
			action.onStack = true;
		}
	}

	public void addToStack(Integer op, Obj obj) {
		if (op.intValue() > 101) {
			if (isArrayElem(obj)) {
				Code.put(Code.dup2);
				Code.load(obj);
				action.onStack = true;
			}

			if (action.opStack.isEmpty())
				action.opStack.push(new Stack<>());

			action.opStack.peek().add(op);
			// System.out.println("DOAO NA STEK: " + obj.getName());
			action.objStack.push(obj);

		} else
			loadDelayedObj(obj);
	}

	public void calculateExpr() {

		if (!action.opStack.isEmpty()) {
			while (!action.opStack.peek().isEmpty()) {

				Obj obj = null;
				Integer op = action.opStack.peek().pop();
				if (!action.objStack.isEmpty()) {
					obj = action.objStack.pop();
				}

//				for (Obj o : action.objStack)
//					System.out.println(o.getName());

				calculateOp(op, null, null);

				if (obj != null && op.intValue() > 101 && isArrayElem(obj)) {
					Obj o = new Obj(Obj.Var, "temp calc op", obj.getType());
					o.setAdr(Code.pc % 128);
					Code.store(o);
					Code.put(Code.dup2);
					Code.load(o);

				}
				if (obj != null) {
					Code.store(obj);
					if (op.intValue() > 101) {
						Code.load(obj);

						if (isArray(obj))
							action.onStack = true;

					}
				}

			}
			action.opStack.pop();
		}
	}

	public Obj exprEnd(Obj addopTerm) {
		Obj result = addopTerm;
		if (action.isLonlyTerm)
			loadDelayedObj(addopTerm);
		else
			result = new Obj(Obj.Con, "complex expr", Tab.intType);

		calculateExpr();
		return result;
	}

	/**
	 * (24) Tipovi oba izraza moraju biti <u>kompatibilini</u>.<br>
	 * Uz promeljive tipa klase ili niza, od relacionih opreatora mogu da se
	 * korsite samo <code>!=</code> ili <code>==</code>
	 */
	public Obj conditionFact(Obj expr1, Obj expr2, int relop, int line) {
		if (expr1 == null) {
			semanticError(line, "Expr1 = null (condition fact)");
			return Tab.noObj;
		}
		if (expr2 == null) {
			// If condition factor is one (without relop): must be bool type
			if (!isBool(expr1)) {
				semanticError(line, "Expression mora da bude tipa bool");

				return Tab.noObj;
			}
			action.currentRelop = Code.gt;
			return expr1;

		}

		action.currentRelop = relop;
		if (isArray(expr1) || isArray(expr2) || isClassField(expr1) || isClassField(expr2)) {
			if (relop != Code.eq || relop != Code.ne) {
				semanticError(line,
						"Uz promenljive tipa klase ili niza od relacionh operatora mogu da se koriste samo != i == a ne: "
								+ relop);
				return Tab.noObj;
			}
		}

		Struct expr1Type = isArray(expr1) ? expr1.getType().getElemType() : expr1.getType();
		Struct expr2Type = isArray(expr2) ? expr2.getType().getElemType() : expr2.getType();

		if (!expr1Type.compatibleWith(expr2Type)) {
			semanticError(line, "Expr1: " + getTypeName(expr1Type.getKind()) + " nije kompatiblian sa Expr2: "
					+ getTypeName(expr2Type.getKind()) + " (condition fact)");
			return Tab.noObj;
		}

		return new Obj(Obj.Var, "condtion fact var", boolType);
	}

	/**
	 * (29) <i>factor</i> i <i>mfl</i> moraju biti tipa <code>int</code>. Ako je
	 * <i>mulop</i> kombinovani aritmeticki operator <code>(*=, *=, %=)</code>,
	 * <i>mfl</i> mora oznacavati promeljivu, element niza ili polje unutar
	 * objekta.
	 */
	public Obj factor(Obj factor, Obj mfl, int mulop, int line) {
		if (!isInt(factor)) {
			semanticError(line, "Factor mora biti tipa int a ne tipa: " + getTypeName(factor.getType().getKind()));
			return Tab.noObj;
		}
		if (!isInt(mfl)) {
			semanticError(line, "MFL mora biti tipa int a ne tipa: " + getTypeName(mfl.getType().getKind()));
			return Tab.noObj;
		}
		if (mulop >= 101) {
			if (!isVariablClassFieldArrayElem(mfl)) {
				semanticError(line,
						"Ako je mulop kombinovani aritmeticki operator (*=, *=, %=), mfl mora oznacavati promeljivu, element niza ili polje unutar objekta. A ne "
								+ getObjName(mfl));
				return Tab.noObj;
			}
		}

		return factor; // return factor * mfl
	}

	/**
	 * (33) Neterminal <code>type</code> mora da oznacava unutrasnju klasu
	 * (korisnicki definisan tip)
	 */
	public Obj factorNew(Struct type, int line) {
		if (!isClassType(type)) {
			semanticError(line, "Type kod new Type mora da bude klasnog tipa");
			return Tab.noObj;
		}
		return new Obj(Obj.Con, "New class", type);
	}

	/**
	 * (32) Tip neterminala <code>expr</code> mora biti tipa <code>int</code>
	 */
	public Obj factorArray(Obj expr, Struct type, int line) {
		if (!isInt(expr)) {
			semanticError(line, "Expr kod new[expr] mora da bude tipa int");
			return Tab.noObj;
		}
		if (!type.equals(parser.lastDesignator.peek().getType().getElemType())) {
			if (parser.lastDesignator.peek().getType().getElemType() == null)
				semanticError(line, "Kod new array tip niza:  se ne slaze sa tipom: " + getTypeName(type.getKind()));
			else
				semanticError(line,
						"Kod new array tip niza: "
								+ getTypeName(parser.lastDesignator.peek().getType().getElemType().getKind())
								+ " se ne slaze sa tipom: " + getTypeName(type.getKind()));
			return Tab.noObj;
		}

		return newArray(parser.lastDesignator.peek());

	}

	public void staticAccess(Obj designator, int line) {
		if (parser.isStatic && parser.isClass) {

			List<Obj> list = new ArrayList<Obj>(action.staticFldMap.get(action.currentClass.getType()));
			if (isClassField(designator) && (designator.getLevel() != 0 && !list.contains(designator)))
				semanticError(line, "Static funkija ne moze da koristi nestaticka polja!");
			else if (designator.getKind() == Obj.Meth
					&& action.currentMethod.getName().equals(designator.getName()) == false
					&& (designator.getLevel() != 0 && !list.contains(designator)))
				semanticError(line, "Static funkija ne moze da koristi nestaticke funkcije!");
		} else {

		}
	}

	public static boolean LEFT = false;
	public static boolean RIGHT = true;

	public void calculateOp(Integer OP, Obj object, Boolean inc) {
		if (OP.intValue() == 101) {
			semanticError("Fatal error, calculate op, check singleton");
			return;
		}

		if (OP > 101)
			OP -= 100;
		switch (OP) {
		case Code.add:
			Code.put(Code.add);
			break;
		case Code.sub:
			Code.put(Code.sub);
			break;
		case Code.mul:
			Code.put(Code.mul);
			break;
		case Code.div:
			Code.put(Code.div);
			break;
		case Code.rem:
			Code.put(Code.rem);
			break;
		case Code.neg:
			Code.put(Code.neg);
			break;
		case Code.shl:
			Code.put(Code.shl);
			break;
		case Code.shr:
			Code.put(Code.shr);
			break;
		case Code.inc:

			if (isArrayElem(object))
				Code.put(Code.dup2);

			Code.load(object);

			Code.loadConst((inc == false) ? 1 : -1);
			Code.put(Code.add);

			Code.store(object);
			break;

		default:
			semanticError("Greska kod Calculate op! " + OP);
			break;
		}

		// Obj returnObj = new Obj(Obj.Var, "calculate op result", Tab.intType);
		// return returnObj;

	}

	/********** GEN CODE ********/

	public void methodEnter() {
		action.currentMethod.setAdr(Code.pc);

		if (parser.isMain == true)
			Code.mainPc = action.currentMethod.getAdr(); // Code.pc

		Code.put(Code.enter);

		Code.put(action.currentMethod.getLevel());
		Code.put(Tab.currentScope().getnVars());

		Tab.chainLocalSymbols(action.currentMethod);

	}

	/**
	 * Generisanje koda kada se zavrsi neka funckija, ili prilikom
	 * <code>return-a</code>.
	 */
	public void methodExit() {
		Code.put(Code.exit);
		Code.put(Code.return_);

	}

	/**
	 * Generisanje koda za print
	 */
	public void print(Obj expr, int num) {

		if (expr.getType() == Tab.intType) {
			if (num != -1)
				Code.loadConst(num);
			else
				Code.loadConst(5);
			Code.put(Code.print);
		} else if (expr.getType() == Tab.charType) {
			Code.loadConst(1);
			Code.put(Code.bprint);
		} else if (expr.getType() == boolType) {
			Code.loadConst(1);
			Code.put(Code.print);
		}

	}

	/**
	 * <code> mod(sgn(x)) </code> <br>
	 * <br>
	 * Puts on stack: <br>
	 * <code> 0 for x == 0  <br> 1 for x != 0 </code>
	 */
	private void modsignum() {
		// x

		Code.put(Code.dup);
		// x
		// x

		Code.loadConst(2);
		// x
		// x
		// 2

		Code.put(Code.mul);
		// 2x

		Code.put(Code.mul);
		// 4|x|

		Code.loadConst(2);
		// 4|x|
		// 2

		Code.put(Code.div);
		// 2|x|

		Code.put(Code.dup);
		// 2|x|
		// 2|x|

		Code.loadConst(2);
		// 2|x|
		// 2|x|
		// 2

		Code.put(Code.div);
		// 2|x|
		// |x|

		Code.loadConst(1);
		// 2|x|
		// |x|
		// 1

		Code.put(Code.add);
		// 2|x|
		// |x| + 1

		Code.put(Code.div);
		// mod(sig(x)) = 2|x| / (|x|+1)

	}

	/**
	 * Generisanje koda za read
	 */
	public void read(Obj designator) {

		if (isChar(designator))
			Code.put(Code.bread);
		else
			Code.put(Code.read);

		if (isBool(designator))
			modsignum();

		Code.store(designator);
	}

	public void assignop(Integer OP, Obj designator) {

		if (OP.intValue() != 101) {
			if (isArrayElem(designator))
				Code.put(Code.dup2);

			Code.load(designator);
			calculateOp(OP.intValue(), null, null);
		}
		Code.store(designator);

	}

	/**
	 * Dodeljivanje vrednosti elementu niza. Na steku se ocekuju 3 vrednosti:
	 * adresa niza, indeks kojem se pristupa i nova vrednost
	 */
	public Obj arrayAcces(Obj designator) {

		Code.load(designator);
		Obj o;
		

		o = new Obj(Obj.Elem, designator.getName() + "[]", designator.getType().getElemType());
		parser.lastDesignator.pop();
		parser.lastDesignator.push(o);
		
		
		return o;

	}

	/**
	 * Generisanje koda za novi niz Na steku se nalazi objektni cvor niza i broj
	 * elementa niza
	 */
	public Obj newArray(Obj designator) {

		Code.put(Code.newarray);
		if (isChar(designator.getType().getElemType()))
			Code.put(0);
		else
			Code.put(1);

		return new Obj(Obj.Var, "new array", new Struct(Struct.Array, designator.getType()));
	}

	// CONDITIONS
	public void condFact() {
		Code.putFalseJump(action.currentRelop, 0);
		action.condFactJumps.addFirst(new Integer(Code.pc - 2));
	}

	public Obj condTerm(Obj conditionTerm, boolean alone) {
		if (alone) {

			Code.putFalseJump(action.currentRelop, 0);
			action.condFactJumps.addFirst(new Integer(Code.pc - 2));
			action.currentRelop = Code.gt;
			return conditionTerm;
		} else {

			Code.putFalseJump(Code.inverse[action.currentRelop], 0);
			action.condTermJumps.addFirst(new Integer(Code.pc - 2));
			for (Integer i : action.condFactJumps)
				Code.fixup(i);

			action.condFactJumps = new LinkedList<Integer>();
			return new Obj(Obj.Var, "Condition Var", boolType);
		}
	}

	public void ifCondition() {
		for (Integer i : action.condTermJumps)
			Code.fixup(i);
		action.condTermJumps = new LinkedList<Integer>();

		action.elseAdr.addLast(action.condFactJumps);
		action.condFactJumps = new LinkedList<Integer>();

		action.currBreakJump = new LinkedList<Integer>();

	}

	public void ifAfterStatement() {
		Code.putJump(0);
		action.afterElseAdr.push(new Integer(Code.pc - 2));
	}

	public void ifElse() {
		if (action.elseAdr != null)
			if (action.elseAdr.peekLast() != null) {
				LinkedList<Integer> list = action.elseAdr.removeLast();
				if (list != null)
					if (list.peekFirst() != null) {
						for (Integer i : list)
							Code.fixup(i);
					}
			}
	}

	public void ifAfterElseStatement() {
		Code.fixup(action.afterElseAdr.pop());

		// if(parser.forLoopLevel > 0 || parser.ifLevel == 1)
		if (action.elseAdr != null) {
			if (action.elseAdr.peekLast() != null) {
				LinkedList<Integer> l = action.elseAdr.removeLast();
				if (l != null)
					if (l.peekFirst() != null) {
						for (Integer i : l)
							Code.fixup(i);
					}
			}
		}
	}

	public void forAfterCondition() {
		Code.putJump(0);
		action.forStepAdr = Code.pc - 2;
		action.continueJump.push(new Integer(Code.pc));
	}

	public void forAfterStep() {
		Code.putJump(action.forConditionStartAdr);
		Code.fixup(action.forStepAdr);
		for (Integer i : action.condTermJumps)
			Code.fixup(i);
		action.condTermJumps = new LinkedList<Integer>();
		action.elseAdr.addLast(action.condFactJumps);
		action.condFactJumps = new LinkedList<Integer>();
		action.currBreakJump = new LinkedList<Integer>();
		action.breakJump.push(action.currBreakJump);
	}

	public void forBodyEnd() {
		Code.putJump(action.continueJump.pop());

		if (action.elseAdr != null)
			if (action.elseAdr.peekLast() != null) {
				LinkedList<Integer> l = action.elseAdr.removeLast();
				if (l != null)
					if (l.peekFirst() != null) {
						for (Integer i : l)
							Code.fixup(i);
					}
			}
		if (action.breakJump != null)
			if (action.breakJump.peekLast() != null) {
				LinkedList<Integer> l = action.breakJump.pop();
				if (l != null)
					if (l.peekFirst() != null) {
						for (Integer i : l)
							Code.fixup(i);
					}
			}

	}

}
