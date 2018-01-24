package org.xtext.example.generator;

import java.util.ArrayList;

public class For extends Instruction{
	private ArrayList<Instruction> right;
	
	public For(String condition){
		this.left = condition;
		this.right = new ArrayList<Instruction>();
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public void setLeft(String left){
		this.left = left;
	}
	
	public ArrayList<Instruction> getCode(){
		return this.right;
	}

	public String toString(){
		String ret = "<FOR, "+this.res+", "+left+", [";
		for(Instruction instr : right){
			ret+=instr.toString()+",";
		}
		ret+="]>";
		return ret;
	}
}
