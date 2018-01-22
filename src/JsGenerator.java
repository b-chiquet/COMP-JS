package org.xtext.example.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class JsGenerator {
	
	private funcTab functions;
	private SymTab symbols;
	private PrintWriter pw;
	public JsGenerator(funcTab f, SymTab s) {
		this.functions = f;
		this.symbols = s;
		try {
			this.pw = new PrintWriter("the-file-name.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * return error code : 0 if all OK, -1 if something went wrong
	 */
	public int translate() {
		int res = 0;
		// TODO: Changer avec l'accesseur de funcTab
		for ( String f : this.functions.tab.keySet() ) {
			funcEntry current = this.functions.getFunc(f);
			this.writeInFile(f);
		}
		this.closeWriter();
		return res;
	}
	
	
	private void writeInFile(String code) {
		this.pw.println(code);
	}
	
	private void closeWriter(){
		this.pw.close();
	}
}
