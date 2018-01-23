package org.xtext.example.generator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.xtext.example.projet.AFFECT;

public class JsGenerator {
	
	private funcTab functions;
	private SymTab symbols;
	private Writer writer;
	private String generatedCode;
	public JsGenerator(funcTab f, SymTab s) {
		this.functions = f;
		this.symbols = s;
		this.generatedCode = ""; // TODO: import lib ?
		try {
			this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./src/org/xtext/example/gen/GENERATED.js"), ("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * return error code : 0 if all OK, -1 if something went wrong
	 */
	public int translate() {
		int res = 0;
		this.generatedCode += ("var whileFunctions = { \n");
		for ( String f : this.functions.getTab().keySet() ) {
			funcEntry current = this.functions.getFunc(f);
			this.generatedCode += (f + " : { \n ");
			this.generatedCode += "code: function (" + this.getFuncParameters(current) + ") { \n"  + this.translateFunc(current);
			this.generatedCode += ("}, \n");
			this.generatedCode += ("metadata: { \n ");
			this.generatedCode += ("in: " + current.getIn() + ",\n");
			this.generatedCode += ("out: " + current.getOut() + "\n");
			this.generatedCode += ("} \n");
			this.generatedCode += ("}, \n");
		}
		this.generatedCode += ("}");
		this.write();
		return res;
	}
	
	private String getFuncParameters( funcEntry f) {
		String res = "";
		int i = 0;
		for ( Instruction c : f.getCode() ) {
			if ( c.getClass().getSimpleName().equals("Read") ) {
				res+="v"+i+",";
				i++;
			}
		}
		if (res.length()>0) {
			res = res.substring(0, res.length()-1);
		}
		return res;
	}
	
	
	private String translateFunc(funcEntry func ){
		String res = "";
		for ( Instruction c : func.getCode() ) {
			switch (c.getClass().getSimpleName()) {
			case "Affect": 
				
				break;
			case "Write": 
				res+= "return [v" + c.left + "]; \n";
			default:
				break;
			}
		}
		return res;
	}
	
	
	private void write() {
		try {
			this.writer.write(this.generatedCode);
			this.writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
