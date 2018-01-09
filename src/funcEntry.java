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
	int in,out;
	//liste des instructions en code 3@
	ArrayList<code3A> code;
	//table des variables de la fonction
	HashMap<String,String> tabVar;
	//numéro de var
	int num;
	
	public funcEntry(){
		this.in = 0;
		this.out = 0;
		this.code = new ArrayList<code3A>();
		this.tabVar = new HashMap<String,String>();
	}
	
	public String getVar(String key){
		return this.tabVar.get(key);
	}
	
	public code3A addCode(Op op, String res, String left, String right){
		code3A c = new code3A(op,res,left,right);
		this.code.add(c);
		return c;
	}
	
	public void addVar(String key){
		if(!this.tabVar.containsKey(key)){
			this.tabVar.put(key, Integer.toString(num));
			num++;
		}
	}
	
	public String addReg(){
		this.tabVar.put("r"+Integer.toString(num), Integer.toString(num));
		num++;
		return getVar("r"+num);
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
		for (code3A ligne : code){
			res+=ligne.toString() + ",\n";			
		}	
		res+="}";
		return res;
	}
}
