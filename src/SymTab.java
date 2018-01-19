package org.xtext.example.generator;

import java.util.ArrayList;
import java.util.HashMap;

public class SymTab {
	private HashMap<String,String> table;
	private int num;
	
	public SymTab(){
		this.table = new HashMap<String,String>();
		this.num = 0;
	}
	
	public void addSym(String key){
		this.table.put(key, "s"+num);
	}
	
	public String getSym(String key){
		return this.table.get(key);
	}
}
