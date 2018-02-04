package org.xtext.example.generator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe d'entrée d'une fonction pour la table des fonctions
 * @author groupe JS
 *
 */
public class funcEntry {
	//nb de paramètres et de valeurs retournées
	private int in,out;
	//liste des instructions en code 3@
	private ArrayList<Instruction> code;
	//table des variables de la fonction
	private HashMap<String,String> tabVar;
	//numéro de var
	private int numVar;
	private int numReg;
	
	public funcEntry(){
		this.in = 0;
		this.out = 0;
		this.numVar = 0;
		this.numReg = 0;
		this.code = new ArrayList<Instruction>();
		this.tabVar = new HashMap<String,String>();
	}
	
	public int getIn() {
		return in;
	}

	public int getOut() {
		return out;
	}

	public String getVar(String key){
		return this.tabVar.get(key);
	}
	
	public Instruction addCode(Instruction instr){
		this.code.add(instr);
		return instr;
	}
	
	public ArrayList<Instruction> getCode(){
		return this.code;
	}
	
	public void addVar(String key){
		if(!this.tabVar.containsKey(key)){
			this.tabVar.put(key, Integer.toString(numVar));
			numVar++;
		}
	}
	
	public String addReg(){
		this.tabVar.put("r"+Integer.toString(numReg), Integer.toString(numVar));
		String res = getVar("r"+numReg);
		numReg++;
		numVar++;
		return res;
	}
	
	public void addIn(){
		this.in++;
	}
	
	public void addOut(){
		this.out++;
	}
	
	public String toString(){
		String res="";
		res+=in +" entrées, "+out+" sorties ; ";
		res+="\nTable Var : { ";
		for (String key: tabVar.keySet()){
			res+="<"+key +", "+ tabVar.get(key) + ">";
		}
		
		res+=" }\nCode : {\n";
		for (Instruction ligne : code){
			res+=ligne.toString() + ",\n";			
		}	
		res+="}";
		return res;
	}
}
