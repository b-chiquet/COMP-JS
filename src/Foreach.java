package org.xtext.example.generator;

import java.util.ArrayList;

public class Foreach extends Instruction{
	private String left,leftBis;
	private ArrayList<Instruction> right;
	
	public Foreach(String condition){
		this.left = condition;
		this.right = new ArrayList<Instruction>();
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public void setLeft(String left){
		this.left = left;
	}
	
	public void setLeftBis(String leftBis){
		this.leftBis = leftBis;
	}
	
	public Instruction addCode(Instruction instr){
		this.right.add(instr);
		return instr;
	}
	
	public ArrayList<Instruction> getCode(){
		return this.right;
	}

	public String toString(){
		res = "<FOREACH, "+res+", "+left+", "+leftBis+", [";
		for(Instruction instr : right){
			res+=instr.toString()+",";
		}
		res+="]>";
		return res;
	}
}
