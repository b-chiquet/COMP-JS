package org.xtext.example.generator;

import java.util.ArrayList;

public class For extends Instruction{
	private ArrayList<Instruction> right;
	
	public For(String condition){
		this.left = condition;
		this.right = new ArrayList<Instruction>();
	}
	
	public ArrayList<Instruction> getCode(){
		return this.right;
	}

	public String toString(){
		res = "<FOR, "+res+", "+left+", [";
		for(Instruction instr : right){
			res+=instr.toString()+",";
		}
		res+="]>";
		return res;
	}
}
