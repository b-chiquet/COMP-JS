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
	
	funcEntry(int in, int out){
		this.in = in;
		this.out = out;
		this.code = new ArrayList<code3A>();
		this.tabVar = new HashMap<String,String>();
	}
	
	void addCode(code3A instruction){
		this.code.add(instruction);
	}
	
	void addVal(String id, String val){
		this.tabVar.put(id, val);
	}
}
