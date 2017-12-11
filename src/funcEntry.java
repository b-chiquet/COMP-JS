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
	
	funcEntry(){
		this.in = 0;
		this.out = 0;
		this.code = new ArrayList<code3A>();
		this.tabVar = new HashMap<String,String>();
	}
	
	public void addCode(code3A instruction){
		this.code.add(instruction);
	}
	
	public void addVal(String id, String val){
		this.tabVar.put(id, val);
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
		res+="Table Var : { ";
		for (String key: tabVar.keySet()){
			res+="<"+key +", "+ tabVar.get(key) + ">";
		}
		
		res+=" } Code : { ";
		for (code3A ligne : code){
			res+=ligne.toString() + ", ";			
		}	
		res+=" }";
		return res;
	}
}
