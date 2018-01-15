package org.xtext.example.generator;

import java.util.ArrayList;

public class While extends Instruction{
	private String left;
	private ArrayList<Instruction> right;
	
	public While(String condition){
		this.left = condition;
		this.right = new ArrayList<Instruction>();
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public void setLeft(String left){
		this.left = left;
	}
	
	public Instruction addCode(Instruction instr){
		this.right.add(instr);
		return instr;
	}
	
	public ArrayList<Instruction> getCode(){
		return this.right;
	}

	public String toString(){
		res = "<WHILE, "+res+", "+left+", [";
		for(Instruction instr : right){
			res+=instr.toString()+",";
		}
		res+="]>";
		return res;
	}
}
