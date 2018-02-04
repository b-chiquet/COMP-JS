package org.xtext.example.generator;

import java.util.ArrayList;
import java.util.HashMap;

public class SymTab {
	private HashMap<String,String> table;
	private int num_sym;
	private int num_fun;

	public SymTab(){
		this.table = new HashMap<String,String>();
		this.num_sym = 0;
		this.num_fun = 0;
	}
	
	public void addSym(String key){
		this.table.put(key, "s"+num_sym);
		num_sym++;
	}
	public void addFun(String key){
		this.table.put(key, "f"+num_fun);
		num_fun++;
	}
	
	public String getSym(String key){
		return table.get(key);
	}
	
	public String toString(){
		String res = "{ ";
		for (String key: table.keySet()){
			res+="<"+key +", "+ table.get(key) + ">";
		}
		res+= " }";
		return res;
	}
}
