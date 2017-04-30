package pp1.vl130298;

import java_cup.runtime.Symbol;
import pp1.vl130298.util.Singleton;

%%

%{
  Singleton s = Singleton.getInstance();
  public boolean printColumn = true; 
  
  private Symbol new_symbol(int type){
  		return new Symbol(type, yyline + 1, yycolumn);
  }
  private Symbol new_symbol(int type, Object value){
  		return new Symbol(type, yyline + 1, yycolumn, value);
  }
%}

%cup
%line
%column
%char // da analizator broji karaktere

%xstate COMMENT 

%eofval{
	return new_symbol(sym.EOF);
%eofval}


%%
" "    { }
"\b"   { }
"\t"   { }
"\r\n" { printColumn = true; }
"\f"   { }

"//"             { yybegin(COMMENT);   } 
<COMMENT> .      { yybegin(COMMENT);   }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }

"program"  { return new_symbol(sym.PROGRAM , yytext()); }
"break"    { return new_symbol(sym.BREAK   , yytext()); }
"class"    { return new_symbol(sym.CLASS   , yytext()); }
"else"     { return new_symbol(sym.ELSE    , yytext()); }
"const"    { return new_symbol(sym.CONST   , yytext()); }
"if"       { return new_symbol(sym.IF      , yytext()); }
"new"      { return new_symbol(sym.NEW     , yytext()); }
"print"    { return new_symbol(sym.PRINT   , yytext()); }
"read"     { return new_symbol(sym.READ    , yytext()); }
"return"   { return new_symbol(sym.RETURN  , yytext()); }
"void"     { return new_symbol(sym.VOID    , yytext()); }
"for"      { return new_symbol(sym.FOR     , yytext()); }
"extends"  { return new_symbol(sym.EXTENDS , yytext()); }
"continue" { return new_symbol(sym.CONTINIUE, yytext());}
"static"   { return new_symbol(sym.STATIC  , yytext()); }

"+" 	   { return new_symbol(sym.PLUS    , yytext()); }
"-" 	   { return new_symbol(sym.MINUS   , yytext()); }
"*" 	   { return new_symbol(sym.MUL     , yytext()); }
"/" 	   { return new_symbol(sym.DIV     , yytext()); }
"%" 	   { return new_symbol(sym.MOD     , yytext()); }
"==" 	   { return new_symbol(sym.EQU     , yytext()); }
"!=" 	   { return new_symbol(sym.NEQ     , yytext()); }
">" 	   { return new_symbol(sym.LSS     , yytext()); }
">=" 	   { return new_symbol(sym.LEQ     , yytext()); }
"<" 	   { return new_symbol(sym.GTR     , yytext()); }
"<=" 	   { return new_symbol(sym.GEQ     , yytext()); }
"&&" 	   { return new_symbol(sym.AND     , yytext()); }
"||" 	   { return new_symbol(sym.OR      , yytext()); }
"=" 	   { return new_symbol(sym.EQUAL   , yytext()); }
"+=" 	   { return new_symbol(sym.PEQ     , yytext()); }
"-=" 	   { return new_symbol(sym.MEQ     , yytext()); }
"*=" 	   { return new_symbol(sym.MUEQ    , yytext()); }
"/=" 	   { return new_symbol(sym.DEQ     , yytext()); }
"%=" 	   { return new_symbol(sym.MOEQ    , yytext()); }
"++" 	   { return new_symbol(sym.INC     , yytext()); }
"--" 	   { return new_symbol(sym.DEC     , yytext()); }
";" 	   { return new_symbol(sym.SEMI    , yytext()); }
"," 	   { return new_symbol(sym.COMMA   , yytext()); }
"."        { return new_symbol(sym.DOT     , yytext()); }
"(" 	   { return new_symbol(sym.LPAREN  , yytext()); }
")" 	   { return new_symbol(sym.RPAREN  , yytext()); }
"{"        { return new_symbol(sym.LBRACE  , yytext()); }
"}"		   { return new_symbol(sym.RBRACE  , yytext()); }
"["        { return new_symbol(sym.QLBRACE , yytext()); }
"]"		   { return new_symbol(sym.QRBRACE , yytext()); }



[0-9]+                          { return new_symbol (sym.NUMBER , new Integer(yytext())); }
"true" | "false"                { return new_symbol (sym.BOOL   ,             yytext());  }
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{ return new_symbol (sym.IDENT  ,             yytext());  }
\'[\d32-\d126]\'                { return new_symbol (sym.ASCII  ,             yytext());  }  


. {	
	s.lexerError(yyline + 1 ,yytext() , printColumn ? yycolumn  + 1: -1);
    if(printColumn)
        printColumn = false;
   }












