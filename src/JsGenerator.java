package org.xtext.example.generator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

public class JsGenerator {
	
	private funcTab functions;
	private Writer writer;
	private String generatedCode;
	
	public JsGenerator(funcTab f) {
		this.functions = f;
		this.generatedCode = "";
		try {
			this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./src/org/xtext/example/gen/GENERATED.js"), ("utf-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Translates the functions contained in the functions attribute and write the generated JS code in the file.
	 */
	public String translate() {
		String mainFunction = "";
		this.generatedCode += ("var whileFunctions = { \n");
		for ( String f : this.functions.getTab().keySet() ) {
			funcEntry current = this.functions.getFunc(f);
			mainFunction = f;
			this.generatedCode += (f + " : { \n ");
			this.generatedCode += "code: function (params) { \n"  + this.translateFunc(current.getCode(), true);
			this.generatedCode += ("}, \n");
			this.generatedCode += ("metadata: { \n ");
			this.generatedCode += ("argsIn: " + current.getIn() + ",\n");
			this.generatedCode += ("argsOut: " + current.getOut() + "\n");
			this.generatedCode += ("} \n");
			this.generatedCode += ("}, \n");
		}
		this.generatedCode += ("}");
		this.write();
		return mainFunction;
	}
	
	/**
	 * Translates a function into JS
	 * @param liste the instruction list if the function
	 * @param princ : to know if we are in an imbricated code section or in the main code, to know when we write the "return" 
	 * @return the generated JS Code
	 */
	private String translateFunc(ArrayList<Instruction>  liste, boolean princ){
		int nbParam = 0;
		String res = "";
		ArrayList<String> write_params = new ArrayList<String>();
		for ( Instruction c : liste) {
			
			String[] left_right = this.formatLeftRight(c);
			
			switch (c.getClass().getSimpleName()) {
			case "Affect": 
				res += formatString(c.res) + " = " + formatString(c.left) + ";\n"; 
				break;
			case "Read":
				res+= "var "+formatString(c.left)+" = params["+nbParam+"]\n";
				nbParam++;
				break;
			case "Write":
				write_params.add(c.left);
				break;
			case "If":
				If i = (If) c;
				if ( formatString(i.getLeft()) == "nil") {
					res+= "if (evalT(nil)) {\n";
				}else{
					res+= "if (evalT("+formatString(i.getLeft())+")) {\n";
				}				
				res+= this.translateFunc(i.getCode(), false);
				res+= "} else { \n";
				res+= this.translateFunc(i.getCodeBis(), false);
				res+= "}\n";
				break;
			case "While":
				While w = (While) c;
				if ( formatString(w.getLeft()) == "nil") {
					res+= "while (evalT(nil)) {\n";
				}else{
					res+= "while (evalT("+formatString(w.getLeft())+")) {\n";
				}
				res+=this.translateFunc(w.getCode(), false);
				res+="}\n";
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
				res+= "var v"+c.res+" = list("+left[0]+",";
				if(left.length < 2){
					res+="nil);\n";
				}else if(left.length < 3){
					res+= left[1]+");\n";
				}else{
					int cpt;
					for(cpt = 1; cpt < left.length; cpt++){
						res+= "list("+ left[cpt] +",";
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
					ArrayList<String> params = new ArrayList<String>();
					for (String s : call.getRight()) {
						params.add(formatString(s));
					}
					res+= formatString(call.res) +" = whileFunctions['"+call.left+ "'].code("+params.toString()+"); \n";
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
	
	/**
	 * Format the parameters of an instruction, depending on if it is "nil" or not
	 * @param i the Instruction
	 * @return an array containing the formated parameters 
	 */
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
	
	/**
	 * Format an array of array of values ( ex: cons A B C D )
	 * @param l the array
	 * @return the formated objects
	 */
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
	
	/**
	 * Format a simple string
	 * @param s
	 * @return
	 */
	private String formatString(String s) {
		String res = s;
			if ( s != "nil") {
				res = "v"+res;
			}
		return res;
	}
	
	
	/**
	 * writes in the file and close the writer
	 */
	private void write() {
		try {
			this.writer.write(this.generatedCode);
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
