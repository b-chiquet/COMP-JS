package org.xtext.example.generator;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Classe de table des fonctions
 * @author groupe JS
 *
 */
public class funcTab {
	//table des fonctions : hash map de clés/entrées de fonction
	HashMap<String,funcEntry> tab;
	
	funcTab(){
		this.tab = new HashMap<String,funcEntry>();
	}
	
	void addFunc(String nom, funcEntry fonction){
		this.tab.put(nom, fonction);
	}
	
	funcEntry getFunc(String nom){
		return this.tab.get(nom);
	}
	
	public String toString(){
		String res ="";
		for (String key : this.tab.keySet()) {
			res+="\n\n"+key+":"+this.tab.get(key).toString();
		}
		return res;
	}
}
