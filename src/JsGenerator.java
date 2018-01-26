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
	private Writer writer;
	private String generatedCode;
	public JsGenerator(funcTab f) {
		this.functions = f;
		this.generatedCode = ""; // TODO: import lib ?
		try {
			this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./src/org/xtext/example/gen/GENERATED.js"), ("utf-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
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
			this.generatedCode += "code: function (" + this.getFuncParameters(current) + ") { \n"  + this.translateFunc(current.getCode(), true);
			this.generatedCode += ("}, \n");
			this.generatedCode += ("metadata: { \n ");
			this.generatedCode += ("argsIn: " + current.getIn() + ",\n");
			this.generatedCode += ("argsOut: " + current.getOut() + "\n");
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
	
	
	private String translateFunc(ArrayList<Instruction>  liste, boolean princ){
		String res = "";
		ArrayList<String> write_params = new ArrayList<String>();
		for ( Instruction c : liste) {
			
			String[] left_right = this.formatLeftRight(c);
			
			switch (c.getClass().getSimpleName()) {
			case "Affect": 
				res += "v" + c.res + " = v" + c.left + ";\n"; 
				break;
			case "Write":
				write_params.add(c.left);
				//res+= "return [v" + c.left + "]; \n";
				break;
			case "If":
				If i = (If) c;
				if ( formatString(i.getLeft()) == "nil") {
					res+= "if (nil) {\n";
				}else{
					res+= "if ("+formatString(i.getLeft())+") {\n";
				}				
				res+= this.translateFunc(i.getCode(), false);
				res+= "} else { \n";
				res+= this.translateFunc(i.getCodeBis(), false);
				res+= "}\n";
				break;
				
			case "Nil":
				res+= "var v"+c.res+" = nil;\n";
				break;
				
			case "Cons":
				Cons cons = (Cons) c;
				Object[] left_c = formatArray(cons.getLeft());
				res+= "var v"+c.res+" = cons("+left_c[0]+",";
				if(left_c.length < 2){
					res+="nil);\n";
				}else if(left_c.length < 3){
					res+= left_c[1]+");\n";
				}else{
					int cpt;
					for(cpt = 1; cpt < left_c.length-1; cpt++){
						res+= "cons("+ left_c[cpt] +",";
					}
					res+=left_c[cpt]+"));\n";
				}
				break;
			case "Liste":
				Liste list  = (Liste) c;
				Object[] left = formatArray(list.getLeft());
				res+= "var v"+c.res+" = cons("+left[0]+",";
				if(left.length < 2){
					res+="nil);\n";
				}else if(left.length < 3){
					res+= left[1]+");\n";
				}else{
					int cpt;
					for(cpt = 1; cpt < left.length; cpt++){
						res+= "cons("+ left[cpt] +",";
					}
					res+="nil));\n";
				}
				break;
			case "And":
				res += "var v" + c.res +" = and("+ left_right[0] + "," + left_right[1] + ");\n";
				break;
				
			case "Or":
				res += "var v" + c.res +" = or("+ left_right[0] + "," + left_right[1] + ");\n";
				break;
				
			case "Call":
				Call call = (Call) c;
				if ( call.left == null){
					throw new NullPointerException("Function not found.");
				} else {
					res += call.left+"(";
					for (String s : call.getRight()) {
						res += formatString(s)+',';
					}
					res = res.substring(0, res.length()-1);
					res+= "); \n" ;
				}
				break;
			case "Eq":
				res += "var v" + c.res +" = eq("+ left_right[0] + "," + left_right[1] + ");\n";
				break;
			case "For":
				For f =(For)c;
				res+="for (i = 0; i < countIt("+formatString(c.left)+",0); i++){\n";
				res+=this.translateFunc(f.getCode(), false);
				res+="}\n";
				break;
			default:
				break;
			}
		}
		
		if(princ) {
			res += "return [";
			for( String s : write_params){
				res += formatString(s)+",";
			}
			res = res.substring(0, res.length()-1);
			res += "];\n";
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
	private Object[] formatArray(Object[] l) {
		Object[] res = l;
		for(int i=0;i<res.length;i++){
			if ( res[i] == "nil") {
				res[i] = "nil";
			}else{
				res[i] = "v"+res[i];
			}
		}
		return res;
	}
	private String formatString(String s) {
		String res = s;
			if ( s != "nil") {
				res = "v"+res;
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
