package org.xtext.example.generator;

import java.util.ArrayList;

public class SymTab {
	private ArrayList<String> table;
	
	public SymTab(){
		this.table = new ArrayList<String>();
	}
	
	public void addSym(String nom){
		this.table.add(nom);
	}
	
	public boolean contains(String nom){
		return this.table.contains(nom);
	}
}
