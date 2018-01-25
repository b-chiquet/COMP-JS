package org.xtext.example.generator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

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
			this.generatedCode += "code: function (" + this.getFuncParameters(current) + ") { \n"  + this.translateFunc(current.getCode());
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
	
	
	private String translateFunc(ArrayList<Instruction>  liste){
		String res = "";
		for ( Instruction c : liste) {
			
			String[] left_right = this.formatLeftRight(c);
			
			switch (c.getClass().getSimpleName()) {
			case "Affect": 
				res += "v" + c.res + " = v" + c.left + ";\n"; 
				break;
			case "Write": 
				res+= "return [v" + c.left + "]; \n";
				break;
			case "If":
				If i = (If) c;
				res+= "if (v" + i.getLeft()+") {\n";
				res+= this.translateFunc(i.getCode());
				res+= "} else { \n";
				res+= this.translateFunc(i.getCodeBis());
				res+= "}\n";
				break;
				
			case "Nil":
				res+= "var v"+c.res+" = nil;\n";
				break;
				
			case "Cons":
				
				
				res+= "var v"+c.res+" = new Tree("+left_right[0]+","+left_right[1]+");\n";
				break;
				
			case "And":
				res += "var v"+c.res+"= "+ left_right[0] +" && " + left_right[1] +";\n";
				break;
				
			case "Or":
				res += "var v"+c.res+"= "+ left_right[0] +" || " + left_right[1] +";\n";
				break;
			default:
				break;
			}
		}
		return res;
	}
	
	private String[] formatLeftRight(Instruction i) {
		String[] res = {"v"+i.left,"v"+i.right};
		if ( i.left == "nil") {
			res[0] = "nil";
		}
		if ( i.right == "nil") {
			res[1] = "nil";
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
