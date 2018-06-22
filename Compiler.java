package compiler;

import java.util.*;
import java.io.*;

/**
 * @author John Hunsaker
 * CPSC 323 Term Project
 */

public class Compiler {
	
	private static class CurrentIndex { //Inner class to track token being analyzed.

		int curr;

		public CurrentIndex() {
			curr = 0;
		}

		public int curr() {
			return curr;
		}

		public int inc() {
			curr++;
			return curr;
		}

		public int reset() {
			curr = 0;
			return curr;
		}
	}
		
	private static class SymbolTable {
		
		int times = 0;
		String[][] tb;
		String[][] symTbl;
		String[][] syTb;
		String[][] stb;

		public SymbolTable(String[][] table) {
			
			tb = new String[table.length][3];
			 
			for (int a = 0; a < table.length; a++) {
				if ((table[a][0]).equals("Identifier")) {
					tb[a][0] = table[a][1];
					tb[a][1] = Integer.toString(6000 + a);
					tb[a][2] = "Integer";
				}
			}
			
			//Eliminate null entries.
			int times = 0;
			int c = 0;
			while (c < tb.length) {
				if (tb[c][0] != null) {
					times++;
				}
				c++;
			}
			symTbl = new String[times][3];
			int i = 0;
			int g = 0;
			while (g < tb.length) {
				if (tb[g][0] != null) {
					symTbl[i][0] = tb[g][0];
					symTbl[i][1] = Integer.toString(6000 + i);
					symTbl[i][2] = tb[g][2];
					i++;
				}
				g++;
			}
			
			//Elimnate duplicates.
			syTb = new String[symTbl.length][3];
			for (int a = 0; a < symTbl.length; a++) {
				boolean bo = true;
				for (int k = a+1; k < symTbl.length; k++) {
					if (symTbl[a][0].equals(symTbl[k][0])) {
						bo = false;
					}
				}
				if (bo) {
					syTb[a][0] = symTbl[a][0];
					syTb[a][2] = symTbl[a][2];
				}
			}
			
			//Eliminate null entries again.
			times = 0;
			c = 0;
			while (c < syTb.length) {
				if (syTb[c][0] != null) {
					times++;
				}
				c++;
			}
			
			stb = new String[times][3];
			i = 0;
			g = 0;
			while (g < syTb.length) {
				if (syTb[g][0] != null) {
					stb[i][0] = syTb[g][0];
					stb[i][1] = Integer.toString(6000 + i);
					stb[i][2] = syTb[g][2];
					i++;
				}
				g++;
			}
		}
		
		public boolean isId(String id) {
			int a = 0;
			
			while (a < stb.length) {
				if (stb[a][0].equals(id)) {
					return true;
				}
				else {
					a++;
				}
			}
			return false;
		}
		
		public int getAdr(String id) {
			int a = 0;
			
			while (a < stb.length) {
				if (stb[a][0].equals(id)) {
					return Integer.parseInt(stb[a][1]);
				}
				else {
					a++;
				}
			}
			return -1;
		}
		
		public void print(PrintWriter outputFile) {
			for (int a = 0; a < stb.length; a++) {
				outputFile.println(stb[a][0] + "\t\t\t" + stb[a][1] + "\t\t\t" + 
				 stb[a][2]);
			}
		}
	}
		
	private static class AssemblyListing {

		String[][] assmTbl = new String[600][3];
		int ln = 0;

		public AssemblyListing() {
			for (int a = 0; a < 600; a++) {
				assmTbl[a][0] = Integer.toString(a + 1);
				assmTbl[a][1] = "";
				assmTbl[a][2] = "";
			}
		}
		
		public int getLn() {
			return ln;
		}
		
		public void incLn() {
			ln++;
		}
		
		public void setInstr(int i, String str) {
			assmTbl[i][1] = str;
		}
		
		public void setOprnd(int i, String str) {
			assmTbl[i][2] = str;
		}
		
		public int getLnNum(String id) {
			int a = 0;
			
			while (a < assmTbl.length) {
				if (assmTbl[a][1].equals(id)) {
					return Integer.parseInt(assmTbl[a][0]);
				}
				else {
					a++;
				}
			}
			return -1;
		}
		
		public void print(PrintWriter outputFile) {
			for (int a = 0; a < 600; a++) {
				if (assmTbl[a][1].equals("")) {
					break;
				}
				else {
					outputFile.println(assmTbl[a][0] + "\t\t" + assmTbl[a][1] + 
					 "\t\t" + assmTbl[a][2]);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		
		//Input File: Get name from user.
		System.out.println("   ");
		System.out.println("Welcome, this application will take a source code file "
		 + "and tokenize then parse it's syntax.");
		System.out.println("  ");
		Scanner scanner = new Scanner(System.in);
		System.out.println("To begin, please copy and paste the filepath "
		 + "ending in NAME.txt of the source code file, "
		 + "(i.e. \"C:\\Users\\john\\Documents\\testFile.txt\"), then press enter: ");
		String inputFileName = scanner.nextLine();

		//Input File: Attempt to open file.
		File myFile = new File(inputFileName);
		Scanner inputFile = new Scanner(myFile);
		System.out.println("File was found.");

		//Output File: Enter name to create new output file, and open it.
		System.out.println("Type the filepath and name "
		 + "(i.e. \"C:\\Users\\john\\Documents\\outputFile.txt\")"
		 + " of the file you wish the application to create to write output to, then press enter: ");
		String outputFileName = scanner.nextLine();
		PrintWriter outputFile = new PrintWriter(outputFileName);

		//ArrayLists to store information.
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> lexemes = new ArrayList<String>();
		ArrayList<String> lineNums = new ArrayList<String>();

		//Call the lexer to tokenize the input file contents.
		lexer(inputFile, tokens, lexemes, lineNums);
		inputFile.close();
		
		//Copy arraylists into a multidimensional array.
		String[][] table = new String[tokens.size()][3];
		for (int i = 0; i < tokens.size(); i++) {
			table[i][0] = tokens.get(i);
			table[i][1] = lexemes.get(i);
			table[i][2] = lineNums.get(i);
			
			//Error out if "Real" or "real".
			if ((table[i][0]).equals("Real") || (table[i][1]).equals("real")) {
				outputFile.println("");
				outputFile.println("Error detected.");
				outputFile.println("'Real' type found in source code.");
				outputFile.println("'Real' is not permitted in simplified Rat16F.");
				outputFile.println("Ending process.");
				outputFile.close();
				System.out.println("Output to new file is complete. Goodbye!");
				System.exit(0);
			}
		}
		
		
		//Parser
		CurrentIndex i = new CurrentIndex();
		if(rat16F(table, outputFile, i)){
			int placeholder = 0;
		}
		i.reset();

		//Symbol Table Handling
		SymbolTable smTb = new SymbolTable(table);
		outputFile.println("\t\tSymbol Table");
		outputFile.println("");
		outputFile.println("Identifier\t\t" + "MemoryLocation\t\t" + "Type");
		smTb.print(outputFile);
		
		//Assembly Code Listing
		AssemblyListing asTb = new AssemblyListing();
		outputFile.println("");
		outputFile.println("");
		outputFile.println("");
		outputFile.println("\t  Assembly Code Listing");
		outputFile.println("");
		
		//Second Parser
		if(rat16F(table, outputFile, i, smTb, asTb)){
			int placeholder = 0;
		}

		//Print Assembly Code Listing
		asTb.print(outputFile);
		
		//Close Program
		outputFile.println("");
		outputFile.println("");
		outputFile.println("Source code compilation complete.");
		outputFile.close();
		System.out.println("Output to new file is complete. Goodbye!");
	}

	public static void lexer(Scanner inpt, ArrayList tkn, 
	 ArrayList lxm, ArrayList lne) throws IOException {
		
		//Current line number.
		int currLine = 0;
		
		//Lexer to tokenize input file contents and write to output file.
		while (inpt.hasNext()) {
			currLine++;
			String s = inpt.nextLine();
			String lexeme = "";

			while (s.length() > 0) {

				//Eliminate Blank Spaces
				if (s.charAt(0) == 9 || s.charAt(0) == 32) { //ASCII 9=TAB, 32=SPACE
					s = s.substring(1, s.length());
				}

				//Separator Test
				else if (s.charAt(0) == '{' || s.charAt(0) == '}' || s.charAt(0) == ';' || 
				 s.charAt(0) == ',' || s.charAt(0) == '[' || s.charAt(0) == ']' || 
				 s.charAt(0) == '(' || s.charAt(0) == ')' || s.charAt(0) == '$') {
					if (s.length() > 1 && s.charAt(0) == '$' && s.charAt(1) == '$') {
						lexeme = s.substring(0, 2);
						s = s.substring(2, s.length());
					}

					else {
						lexeme = s.substring(0, 1);
						s = s.substring(1, s.length());
					}
					
					tkn.add("Separator");
					lxm.add(lexeme);
					lne.add("" + currLine);
					lexeme = "";
				}

				//Operator Test
				else if (s.charAt(0) == ':' || s.charAt(0) == '+' || s.charAt(0) == '-' || 
				 s.charAt(0) == '*' || s.charAt(0) == '/' || s.charAt(0) == '%' || 
				 s.charAt(0) == '=' || s.charAt(0) == '!' || s.charAt(0)== '&' || 
				 s.charAt(0) == '|' || s.charAt(0) == '>' || s.charAt(0) == '<'){

					if (s.length() > 1 && s.charAt(0) == '/' && s.charAt(1) == '/') {
						lexeme = s.substring(0, s.length());
						s = s.substring(s.length(), s.length());
						
						lexeme = "";
					}

					else {
						if (s.length() > 1 && s.charAt(0) == ':' && s.charAt(1) == '=') {
							lexeme = s.substring(0, 2);
							s = s.substring(2, s.length());
						}

						else if (s.length() > 1 && s.charAt(0) == '!' && s.charAt(1) == '=') {
							lexeme = s.substring(0, 2);
							s = s.substring(2, s.length());
						}

						else if (s.length() > 1 && s.charAt(0) == '/' && s.charAt(1) == '=') {
							lexeme = s.substring(0, 2);
							s = s.substring(2, s.length());
						}

						else if (s.length() > 1 && s.charAt(0) == '>' && s.charAt(1) == '=') {
							lexeme = s.substring(0, 2);
							s = s.substring(2, s.length());
						}

						else if (s.length() > 1 && s.charAt(0) == '<' && s.charAt(1) == '=') {
							lexeme = s.substring(0, 2);
							s = s.substring(2, s.length());
						}

						else {
							lexeme = s.substring(0, 1);
							s = s.substring(1, s.length());
						}

						tkn.add("Operator");
						lxm.add(lexeme);
						lne.add("" + currLine);
						lexeme = "";
					}
				}

				//Keyword Test and Identifier Test
				else if (Character.isLetter(s.charAt(0))) {
					int i = 0;
					while (i + 1 < s.length() && (Character.isLetter(s.charAt(i+1)) || 
					 (Character.isDigit(s.charAt(i+1))) || (s.charAt(i+1) == '_'))) {
						i++;
					}

					while (s.charAt(i) == '_') {
						i = i - 1;
					}

					lexeme = s.substring(0, i+1);
					s = s.substring(i+1, s.length());

					if (lexeme.contentEquals("integer") || lexeme.contentEquals("real") || 
					 lexeme.contentEquals("boolean") || lexeme.contentEquals("if") ||
					 lexeme.contentEquals("endif") || lexeme.contentEquals("else") ||
					 lexeme.contentEquals("return") || lexeme.contentEquals("write") || 
					 lexeme.contentEquals("read") || lexeme.contentEquals("while") || 
					 lexeme.contentEquals("true") || lexeme.contentEquals("false") ||
					 lexeme.contentEquals("print") || lexeme.contentEquals("function")){
						tkn.add("Keyword");
						lxm.add(lexeme);
						lne.add("" + currLine);
						lexeme = "";
					}

					else {
						tkn.add("Identifier");
						lxm.add(lexeme);
						lne.add("" + currLine);
						lexeme = "";
					}
				}

				//Real Test then Integer Test
				else if (Character.isDigit(s.charAt(0)) || 
				 (s.length() > 1 && s.charAt(0) == '.' && Character.isDigit(s.charAt(1)))) {
					int i = 0;
					while (i + 1 < s.length() && (Character.isDigit(s.charAt(i+1)) || 
					 (s.charAt(i+1) == '.'))) {
						i++;
					}

					lexeme = s.substring(0, i+1);
					s = s.substring(i+1, s.length());

					if (lexeme.contains(".")) {
						tkn.add("Real");
						lxm.add(lexeme);
						lne.add("" + currLine);
						lexeme = "";
					}
					else {
						tkn.add("Integer");
						lxm.add(lexeme);
						lne.add("" + currLine);
						lexeme = "";
					}
				}

				//Unknown
				else {
					lexeme = s.substring(0, 1);
					s = s.substring(1, s.length());
					tkn.add("Unknown");
					lxm.add(lexeme);
					lne.add("" + currLine);
					lexeme = "";
				}
			}
		}
	}
	
	public static void error(String expected, String[][] tbl, PrintWriter out, CurrentIndex i) {
		out.println("");
		out.println("Syntax Error!");
		out.println("Line #" + tbl[i.curr()][2] + " found token " + tbl[i.curr()][0] 
		 + " named " + tbl[i.curr()][1] + ", should be a " + expected);
		out.println("");
		
		//Close Program.
		out.close();
		System.out.println("Output to new file is complete. Goodbye!");
		System.exit(0);
	}
	
	public static void prtAndInc(String[][] tbl, PrintWriter out, CurrentIndex i) {
			
		//Increment i
		i.inc();
	}
	
	public static boolean compareToken(String expected, String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (expected.equals(tbl[i.curr()][0])) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean compareLexeme(String expected, String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (expected.equals(tbl[i.curr()][1])) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean rat16F(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("$$", tbl, out, i)) {
			prtAndInc(tbl, out, i);
		}
		else{
			error("$$", tbl, out, i);
		}
		
		if(compareLexeme("$$", tbl, out, i)) {
			prtAndInc(tbl, out, i);
		}
		else{
			error("$$", tbl, out, i);
		}
		
		if(optDeclarationList(tbl, out, i)) {
			int placeholder = 0;
		}
		
		if(statementList(tbl, out, i)) {
			int placeholder = 0;
		}
		
		if(compareLexeme("$$", tbl, out, i)) {
			prtAndInc(tbl, out, i);
		}
		else{
			error("$$", tbl, out, i);
		}
		return true;
	}
	
	public static boolean qualifier(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("integer", tbl, out, i)){
			prtAndInc(tbl, out, i);
			return true;
		}
		else if(compareLexeme("boolean", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			return true;
		}
		else if(compareLexeme("real", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean body(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("{", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			if(statementList(tbl, out, i)){
				if(compareLexeme("}", tbl, out, i)){
					prtAndInc(tbl, out, i);
					return true;
				}
				else{
					error("}", tbl, out, i);
					return false;
				}
			}
			else {
				error("StatementList", tbl, out, i);
				return false;
			}
		}
		else {
			error("Body", tbl, out, i);
			return false;
		}
	}
	
	public static boolean optDeclarationList(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (declarationList(tbl, out, i)) {
			return true;
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	public static boolean declarationList(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(declaration(tbl, out, i)) {
			if(compareLexeme(";", tbl, out, i)){
				prtAndInc(tbl, out, i);
				if(declarationPrime(tbl, out, i)) {
					return true;
				}
				else{
					error("DecarationPrime", tbl, out, i);
					return false;
				}
			}
			else{
				error(";", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean declaration(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(qualifier(tbl, out, i)) {
			if(ids(tbl, out, i)){
				return true;
			}
			else{
				error("ID's", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean declarationPrime(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (declarationList(tbl, out, i)) {
			return true;
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	
	public static boolean ids(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(identifier(tbl, out, i)){
			if(identifierPrime(tbl, out, i)) {
				return true;
			}
			else{
				error("IdentifierPrime", tbl, out, i);
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean identifier(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareToken("Identifier", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean identifierPrime(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (compareLexeme(",", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(ids(tbl, out,  i)){
				return true;
			}
			else{
				error("IDs", tbl, out, i);
				return false;
			}
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	public static boolean statementList(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(statement(tbl, out, i)) {
			if(statementPrime(tbl, out, i)) {
				return true;
			}
			else{
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean statement(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (compound(tbl, out, i)) {
			return true;
		}
		
		else if (write(tbl, out, i)) {
			return true;
		}
		
		else if (assign(tbl, out, i)) {
			return true;
		}
		
		else if (if_(tbl, out, i)) {
			return true;
		}
		
		else if (return_(tbl, out, i)) {
			return true;
		}
		
		else if (read(tbl, out, i)) {
			return true;
		}
		
		else if(while_(tbl, out, i)) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean statementPrime(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (statementList(tbl, out,  i)) {
			return true;
		}
		
		else {
			return true;
		}
	}
	
	public static boolean compound(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("{", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			if(statementList(tbl, out, i)){
				if(compareLexeme("}", tbl, out, i)){
					prtAndInc(tbl, out, i);
					return true;
				}
				else{
					error("}", tbl, out, i);
					return false;
				}
			}
			else {
				error("StatementList", tbl, out, i);
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean assign(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(identifier(tbl, out, i)){
			if(compareLexeme(":=", tbl, out, i)){
				prtAndInc(tbl, out, i);
				if(expression(tbl, out, i)){
					if(compareLexeme(";", tbl, out, i)){
						prtAndInc(tbl, out, i);
						return true;
					}
					else{
						error(";", tbl, out, i);
						return false;
					}
				}
				else{
					error("Expression", tbl, out, i);
					return false;
				}
			}
			else{
				error(":=", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean if_(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("if", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(compareLexeme("(", tbl, out, i)){
				prtAndInc(tbl, out, i);
				if(condition(tbl, out, i)){
					if(compareLexeme(")", tbl, out, i)){
						prtAndInc(tbl, out, i);
						if(statement(tbl, out, i)){
							if (compareLexeme("endif", tbl, out, i)) {
								prtAndInc(tbl, out, i);
								return true;
							}
							else if (compareLexeme("else", tbl, out, i)){
								prtAndInc(tbl, out, i);
								if(statement(tbl, out, i)){
									if(compareLexeme("endif", tbl, out, i)){
										prtAndInc(tbl, out, i);
										return true;
									}
									else{
										error("endif", tbl, out, i);
										return false;
									}
								}
								else{
									error("statement", tbl, out, i);
									return false;
								}
							}
							else{
								error("endif or else", tbl, out, i);
								return false;
							}
						}
						else{
							error("Statement", tbl, out, i);
							return false;
						}
					}
					else{
						error(")", tbl, out, i);
						return false;
					}
				}
				else{
					error("Condition", tbl, out, i);
					return false;
				}
			}
			else{
				error("(", tbl, out, i);
				return false;
			}
		}
		
		else{
			return false;
		}
	}
	
	public static boolean return_(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (compareLexeme("return", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(compareLexeme(";", tbl, out, i)){
				prtAndInc(tbl, out, i);
				return true;
			}
			else if(expression(tbl, out, i)){
				if(compareLexeme(";", tbl, out, i)){
					prtAndInc(tbl, out, i);
					return true;
				}
				else{
					error(";", tbl, out, i);
					return false;
				}
			}
			else{
				error("; or Expression;", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean write(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("print", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(compareLexeme("(", tbl, out, i)){
				prtAndInc(tbl, out, i);
				if(expression(tbl, out, i)){
					if(compareLexeme(")", tbl, out, i)){
						prtAndInc(tbl, out, i);
						if(compareLexeme(";", tbl, out, i)){
							prtAndInc(tbl, out, i);
							return true;
						}
						else{
							error(";", tbl, out, i);
							return false;
						}
					}
					else{
						error(")", tbl, out, i);
						return false;
					}
				}
				else{
					error("Expression", tbl, out, i);
					return false;
				}
			}
			else{
				error("(", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean read(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("read", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(compareLexeme("(", tbl, out, i)){
				prtAndInc(tbl, out, i);
				if(ids(tbl, out, i)){
					if(compareLexeme(")", tbl, out, i)){
						prtAndInc(tbl, out, i);
						if(compareLexeme(";", tbl, out, i)){
							prtAndInc(tbl, out, i);
							return true;
						}
						else{
							error(";", tbl, out, i);
							return false;
						}
					}
					else{
						error(")", tbl, out, i);
						return false;
					}
				}
				else{
					error("IDs", tbl, out, i);
					return false;
				}
			}
			else{
				error("(", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean while_(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("while", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(compareLexeme("(", tbl, out, i)){
				prtAndInc(tbl, out, i);
				if(condition(tbl, out, i)){
					if(compareLexeme(")", tbl, out, i)){
						prtAndInc(tbl, out, i);
						if(statement(tbl, out, i)){
							return true;
						}
						else{
							error("Statement", tbl, out, i);
							return false;
						}
					}
					else{
						error(")", tbl, out, i);
						return false;
					}
				}
				else{
					error("Condition", tbl, out, i);
					return false;
				}
			}
			else{
				error("(", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean condition(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(expression(tbl, out, i)){
			if(relop(tbl, out, i)){
				if(expression(tbl, out, i)){
					return true;
				}
				else{
					error("Expression", tbl, out, i);
					return false;
				}
			}
			else{
				error("Relop", tbl, out, i);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean relop(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("=", tbl, out, i)){
			prtAndInc(tbl, out, i);
			return true;
		}
		else if(compareLexeme("/=", tbl, out, i)){
			prtAndInc(tbl, out, i);
			return true;
		}
		else if(compareLexeme(">", tbl, out, i)){
			prtAndInc(tbl, out, i);
			return true;
		}
		else if(compareLexeme("<", tbl, out, i)){
			prtAndInc(tbl, out, i);
			return true;
		}
		else if(compareLexeme("=>", tbl, out, i)){
			prtAndInc(tbl, out, i);
			return true;
		}
		else if(compareLexeme("<=", tbl, out, i)){
			prtAndInc(tbl, out, i);
			return true;
		}
		else {
			error("Relop", tbl, out, i);
			return false;
		}
	}
	
	public static boolean expression(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(term(tbl, out, i)) {
			if(expressionPrime(tbl, out, i)) {
				return true;
			}
			else{
				return false;
			}
		}
		
		else {
			return false;
		}
	}
	
	public static boolean expressionPrime(String[][] tbl, PrintWriter out, CurrentIndex i){
		if(compareLexeme("+", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			if(term(tbl, out, i)) {
				if(expressionPrime(tbl, out, i)) {
					return true;
				}
				else {
					error("ExpressionPrime", tbl, out, i);
					return false;
				}
			}
			else{
				error("Term", tbl, out, i);
				return false;
			}
		}
		
		else if(compareLexeme("-", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			if(term(tbl, out, i)) {
				if(expressionPrime(tbl, out, i)) {
					return true;
				}
				else {
					error("ExpressionPrime", tbl, out, i);
					return false;
				}
			}
			else{
				error("Term", tbl, out, i);
				return false;
			}
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	public static boolean term(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(factor(tbl, out, i)) {
			if(termPrime(tbl, out, i)) {
				return true;
			}
			else{
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean termPrime(String[][] tbl, PrintWriter out, CurrentIndex i){
		if(compareLexeme("*", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(factor(tbl, out, i)){
				if(termPrime(tbl, out, i)){
					return true;
				}
				else{
					error("TermPrime", tbl, out, i);
					return false;
				}
			}
			else{
				error("Factor", tbl, out, i);
				return false;
			}		
		}
		
		else if(compareLexeme("/", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(factor(tbl, out, i)){
				if(termPrime(tbl, out, i)){
					return true;
				}
				else{
					error("TermPrime", tbl, out, i);
					return false;
				}
			}
			else{
				error("Factor", tbl, out, i);
				return false;
			}
		}
		
		//Empty
		else{
			return true;
		}
	}
	
	public static boolean factor(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareLexeme("-", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(primary(tbl, out, i)){
				return true;
			}
			else{
				error("Primary", tbl, out, i);
				return false;
			}
		}
		
		else if(primary(tbl, out, i)) {
			return true;
		}
		
		else {
			error("Factor", tbl, out, i);
			return false;
		}
	}
	
	public static boolean primary(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(integer(tbl, out, i)) {
			return true;
		}
		
		else if(identifier(tbl, out, i)) {
			if(compareLexeme("[", tbl, out, i)){
				prtAndInc(tbl, out, i);
				if(ids(tbl, out, i)){
					if(compareLexeme("]", tbl, out, i)){
						prtAndInc(tbl, out, i);
						return true;
					}
					else{
						error("]", tbl, out, i);
						return false;
					}
				}
				else{
					error("IDs", tbl, out, i);
					return false;
				}
			}
			else{
				return true;
			}
		}
		
		else if(compareLexeme("(", tbl, out, i)){
			prtAndInc(tbl, out, i);
			if(expression(tbl, out, i)){
				if(compareLexeme(")", tbl, out, i)){
					prtAndInc(tbl, out, i);
					return true;
				}
				else{
					error(")", tbl, out, i);
					return false;
				}
			}
			else{
				error("Expression", tbl, out, i);
				return false;
			}
		}
		
		else if(real(tbl, out, i)) {
			return true;
		}
		
		else if(compareLexeme("true", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			return true;
		}
		
		else if(compareLexeme("false", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean integer(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if (compareToken("Integer", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			return true;
		}
		
		else {
			return false;
		}
	}

	public static boolean real(String[][] tbl, PrintWriter out, CurrentIndex i) {
		if(compareToken("Real", tbl, out, i)) {
			prtAndInc(tbl, out, i);
			return true;
		}
		
		else {
			return false;
		}
	}

	public static void error(String expected, String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		out.println("");
		out.println("Syntax Error!");
		out.println("Line #" + tbl[i.curr()][2] + " found token " + tbl[i.curr()][0] 
		 + " named " + tbl[i.curr()][1] + ", should be a " + expected);
		out.println("");
		
		//Close Program.
		out.close();
		System.out.println("Output to new file is complete. Goodbye!");
		System.exit(0);
	}
	
	public static void prtAndInc(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
			
		//Increment i
		i.inc();
	}
	
	public static boolean compareToken(String expected, String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (expected.equals(tbl[i.curr()][0])) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean compareLexeme(String expected, String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (expected.equals(tbl[i.curr()][1])) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean rat16F(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		
		if(compareLexeme("$$", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
		}
		else{
			error("$$", tbl, out, i, s, a);
		}
		
		if(compareLexeme("$$", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
		}
		else{
			error("$$", tbl, out, i, s, a);
		}
		
		if(optDeclarationList(tbl, out, i, s, a)) {
			int placeholder = 0;
		}
		
		if(statementList(tbl, out, i, s, a)) {
			int placeholder = 0;
		}
		
		if(compareLexeme("$$", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
		}
		else{
			error("$$", tbl, out, i, s, a);
		}
		return true;
	}
	
	public static boolean qualifier(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("integer", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else if(compareLexeme("boolean", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else if(compareLexeme("real", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean body(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("{", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			if(statementList(tbl, out, i, s, a)){
				if(compareLexeme("}", tbl, out, i, s, a)){
					prtAndInc(tbl, out, i, s, a);
					return true;
				}
				else{
					error("}", tbl, out, i, s, a);
					return false;
				}
			}
			else {
				error("StatementList", tbl, out, i, s, a);
				return false;
			}
		}
		else {
			error("Body", tbl, out, i, s, a);
			return false;
		}
	}
	
	public static boolean optDeclarationList(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (declarationList(tbl, out, i, s, a)) {
			return true;
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	public static boolean declarationList(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(declaration(tbl, out, i, s, a)) {
			if(compareLexeme(";", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				if(declarationPrime(tbl, out, i, s, a)) {
					return true;
				}
				else{
					error("DecarationPrime", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error(";", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean declaration(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(qualifier(tbl, out, i, s, a)) {
			if(ids(tbl, out, i, s, a)){
				return true;
			}
			else{
				error("ID's", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean declarationPrime(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (declarationList(tbl, out, i, s, a)) {
			return true;
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	
	public static boolean ids(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(identifier(tbl, out, i, s, a)){
			if(identifierPrime(tbl, out, i, s, a)) {
				return true;
			}
			else{
				error("IdentifierPrime", tbl, out, i, s, a);
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean identifier(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareToken("Identifier", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean identifierPrime(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (compareLexeme(",", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(ids(tbl, out, i, s, a)){
				return true;
			}
			else{
				error("IDs", tbl, out, i, s, a);
				return false;
			}
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	public static boolean statementList(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(statement(tbl, out, i, s, a)) {
			if(statementPrime(tbl, out, i, s, a)) {
				return true;
			}
			else{
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean statement(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (compound(tbl, out, i, s, a)) {
			return true;
		}
		
		else if (write(tbl, out, i, s, a)) {
			return true;
		}
		
		else if (assign(tbl, out, i, s, a)) {
			return true;
		}
		
		else if (if_(tbl, out, i, s, a)) {
			return true;
		}
		
		else if (return_(tbl, out, i, s, a)) {
			return true;
		}
		
		else if (read(tbl, out, i, s, a)) {
			return true;
		}
		
		else if(while_(tbl, out, i, s, a)) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean statementPrime(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (statementList(tbl, out,  i, s, a)) {
			return true;
		}
		
		else {
			return true;
		}
	}
	
	public static boolean compound(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("{", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			if(statementList(tbl, out, i, s, a)){
				if(compareLexeme("}", tbl, out, i, s, a)){
					prtAndInc(tbl, out, i, s, a);
					return true;
				}
				else{
					error("}", tbl, out, i, s, a);
					return false;
				}
			}
			else {
				error("StatementList", tbl, out, i, s, a);
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean assign(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(identifier(tbl, out, i, s, a)){
			int addr = s.getAdr(tbl[i.curr()-1][1]);
			if(compareLexeme(":=", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				if(expression(tbl, out, i, s, a)){
					if(compareLexeme(";", tbl, out, i, s, a)){
						prtAndInc(tbl, out, i, s, a);
						a.setInstr(a.getLn(), "POPM");
						a.setOprnd(a.getLn(), Integer.toString(addr));
						a.incLn();
						return true;
					}
					else{
						error(";", tbl, out, i, s, a);
						return false;
					}
				}
				else{
					error("Expression", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error(":=", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean if_(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("if", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(compareLexeme("(", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				if(condition(tbl, out, i, s, a)){
					if(compareLexeme(")", tbl, out, i, s, a)){
						prtAndInc(tbl, out, i, s, a);
						if(statement(tbl, out, i, s, a)){
							if (compareLexeme("endif", tbl, out, i, s, a)) {
								prtAndInc(tbl, out, i, s, a);
								return true;
							}
							else if (compareLexeme("else", tbl, out, i, s, a)){
								prtAndInc(tbl, out, i, s, a);
								if(statement(tbl, out, i, s, a)){
									if(compareLexeme("endif", tbl, out, i, s, a)){
										prtAndInc(tbl, out, i, s, a);
										return true;
									}
									else{
										error("endif", tbl, out, i, s, a);
										return false;
									}
								}
								else{
									error("statement", tbl, out, i, s, a);
									return false;
								}
							}
							else{
								error("endif or else", tbl, out, i, s, a);
								return false;
							}
						}
						else{
							error("Statement", tbl, out, i, s, a);
							return false;
						}
					}
					else{
						error(")", tbl, out, i, s, a);
						return false;
					}
				}
				else{
					error("Condition", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("(", tbl, out, i, s, a);
				return false;
			}
		}
		
		else{
			return false;
		}
	}
	
	public static boolean return_(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (compareLexeme("return", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(compareLexeme(";", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				return true;
			}
			else if(expression(tbl, out, i, s, a)){
				if(compareLexeme(";", tbl, out, i, s, a)){
					prtAndInc(tbl, out, i, s, a);
					return true;
				}
				else{
					error(";", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("; or Expression;", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean write(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("print", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(compareLexeme("(", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				if(expression(tbl, out, i, s, a)){
					a.setInstr(a.getLn(), "STDOUT");
					a.setOprnd(a.getLn(), "");
					a.incLn();
					if(compareLexeme(")", tbl, out, i, s, a)){
						prtAndInc(tbl, out, i, s, a);
						if(compareLexeme(";", tbl, out, i, s, a)){
							prtAndInc(tbl, out, i, s, a);
							return true;
						}
						else{
							error(";", tbl, out, i, s, a);
							return false;
						}
					}
					else{
						error(")", tbl, out, i, s, a);
						return false;
					}
				}
				else{
					error("Expression", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("(", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean read(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("read", tbl, out, i, s, a)){
			a.setInstr(a.getLn(), "STDIN");
			a.setOprnd(a.getLn(), "");
			a.incLn();
			prtAndInc(tbl, out, i, s, a);
			if(compareLexeme("(", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				if(ids(tbl, out, i, s, a)){
					a.setInstr(a.getLn(), "POPM");
					a.setOprnd(a.getLn(), Integer.toString(s.getAdr(tbl[i.curr()-1][1])));
					a.incLn();
					if(compareLexeme(")", tbl, out, i, s, a)){
						prtAndInc(tbl, out, i, s, a);
						if(compareLexeme(";", tbl, out, i, s, a)){
							prtAndInc(tbl, out, i, s, a);
							return true;
						}
						else{
							error(";", tbl, out, i, s, a);
							return false;
						}
					}
					else{
						error(")", tbl, out, i, s, a);
						return false;
					}
				}
				else{
					error("IDs", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("(", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean while_(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("while", tbl, out, i, s, a)){
			int assmLine = a.getLn() + 1;
			a.setInstr(a.getLn(), "LABEL");
			a.setOprnd(a.getLn(), "");
			a.incLn();
			prtAndInc(tbl, out, i, s, a);
			if(compareLexeme("(", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				if(condition(tbl, out, i, s, a)){
					if(compareLexeme(")", tbl, out, i, s, a)){
						prtAndInc(tbl, out, i, s, a);
						if(statement(tbl, out, i, s, a)){
							a.setInstr(a.getLn(), "JUMP");
							a.setOprnd(a.getLn(), Integer.toString(assmLine));
							a.setOprnd(a.getLnNum("JUMPZ") - 1, Integer.toString(a.getLnNum("JUMP") + 1)); //For JUMPZ line num retrospect.
							a.incLn();
							return true;
						}
						else{
							error("Statement", tbl, out, i, s, a);
							return false;
						}
					}
					else{
						error(")", tbl, out, i, s, a);
						return false;
					}
				}
				else{
					error("Condition", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("(", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean condition(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(expression(tbl, out, i, s, a)){
			if(relop(tbl, out, i, s, a)){
				String rlop = tbl[i.curr()-1][1];
				if(expression(tbl, out, i, s, a)){
					if (rlop.equals("<")) {
						a.setInstr(a.getLn(), "LES");
						a.setOprnd(a.getLn(), "");
					}
					else if(rlop.equals(">")) {
						a.setInstr(a.getLn(), "GRT");
						a.setOprnd(a.getLn(), "");
					}
					else {
						a.setInstr(a.getLn(), "EQU");
						a.setOprnd(a.getLn(), "");
					}
					a.incLn();
					a.setInstr(a.getLn(), "JUMPZ");
					a.setOprnd(a.getLn(), "");
					a.incLn();
					return true;
				}
				else{
					error("Expression", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("Relop", tbl, out, i, s, a);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public static boolean relop(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("=", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else if(compareLexeme("/=", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else if(compareLexeme(">", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else if(compareLexeme("<", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else if(compareLexeme("=>", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else if(compareLexeme("<=", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		else {
			error("Relop", tbl, out, i, s, a);
			return false;
		}
	}
	
	public static boolean expression(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(term(tbl, out, i, s, a)) {
			if(expressionPrime(tbl, out, i, s, a)) {
				return true;
			}
			else{
				return false;
			}
		}
		
		else {
			return false;
		}
	}
	
	public static boolean expressionPrime(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a){
		if(compareLexeme("+", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			if(term(tbl, out, i, s, a)) {
				if(expressionPrime(tbl, out, i, s, a)) {
					a.setInstr(a.getLn(), "ADD");
					a.setOprnd(a.getLn(), "");
					a.incLn();
					return true;
				}
				else {
					error("ExpressionPrime", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("Term", tbl, out, i, s, a);
				return false;
			}
		}
		
		else if(compareLexeme("-", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			if(term(tbl, out, i, s, a)) {
				if(expressionPrime(tbl, out, i, s, a)) {
					a.setInstr(a.getLn(), "SUB");
					a.setOprnd(a.getLn(), "");
					a.incLn();
					return true;
				}
				else {
					error("ExpressionPrime", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("Term", tbl, out, i, s, a);
				return false;
			}
		}
		
		//Empty
		else {
			return true;
		}
	}
	
	public static boolean term(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(factor(tbl, out, i, s, a)) {
			if(termPrime(tbl, out, i, s, a)) {
				return true;
			}
			else{
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public static boolean termPrime(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a){
		if(compareLexeme("*", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(factor(tbl, out, i, s, a)){
				if(termPrime(tbl, out, i, s, a)){
					a.setInstr(a.getLn(), "MUL");
					a.setOprnd(a.getLn(), "");
					a.incLn();
					return true;
				}
				else{
					error("TermPrime", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("Factor", tbl, out, i, s, a);
				return false;
			}		
		}
		
		else if(compareLexeme("/", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(factor(tbl, out, i, s, a)){
				if(termPrime(tbl, out, i, s, a)){
					a.setInstr(a.getLn(), "DIV");
					a.setOprnd(a.getLn(), "");
					a.incLn();
					return true;
				}
				else{
					error("TermPrime", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("Factor", tbl, out, i, s, a);
				return false;
			}
		}
		
		//Empty
		else{
			return true;
		}
	}
	
	public static boolean factor(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareLexeme("-", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(primary(tbl, out, i, s, a)){
				return true;
			}
			else{
				error("Primary", tbl, out, i, s, a);
				return false;
			}
		}
		
		else if(primary(tbl, out, i, s, a)) {
			return true;
		}
		
		else {
			error("Factor", tbl, out, i, s, a);
			return false;
		}
	}
	
	public static boolean primary(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(integer(tbl, out, i, s, a)) {
			return true;
		}
		
		else if(identifier(tbl, out, i, s, a)) {
			if(compareLexeme("[", tbl, out, i, s, a)){
				prtAndInc(tbl, out, i, s, a);
				if(ids(tbl, out, i, s, a)){
					if(compareLexeme("]", tbl, out, i, s, a)){
						prtAndInc(tbl, out, i, s, a);
						return true;
					}
					else{
						error("]", tbl, out, i, s, a);
						return false;
					}
				}
				else{
					error("IDs", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				a.setInstr(a.getLn(), "PUSHM");
				a.setOprnd(a.getLn(),(Integer.toString(s.getAdr(tbl[i.curr()-1][1]))));
				a.incLn();
				return true;
			}
		}
		
		else if(compareLexeme("(", tbl, out, i, s, a)){
			prtAndInc(tbl, out, i, s, a);
			if(expression(tbl, out, i, s, a)){
				if(compareLexeme(")", tbl, out, i, s, a)){
					prtAndInc(tbl, out, i, s, a);
					return true;
				}
				else{
					error(")", tbl, out, i, s, a);
					return false;
				}
			}
			else{
				error("Expression", tbl, out, i, s, a);
				return false;
			}
		}
		
		else if(real(tbl, out, i, s, a)) {
			return true;
		}
		
		else if(compareLexeme("true", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		
		else if(compareLexeme("false", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public static boolean integer(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if (compareToken("Integer", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			a.setInstr(a.getLn(), "PUSHI");
			a.setOprnd(a.getLn(), (tbl[i.curr()-1][1]));
			a.incLn();
			return true;
		}
		
		else {
			return false;
		}
	}

	public static boolean real(String[][] tbl, PrintWriter out, CurrentIndex i, SymbolTable s, AssemblyListing a) {
		if(compareToken("Real", tbl, out, i, s, a)) {
			prtAndInc(tbl, out, i, s, a);
			return true;
		}
		
		else {
			return false;
		}
	}
}