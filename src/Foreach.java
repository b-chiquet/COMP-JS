package org.xtext.example.generator;

import java.util.ArrayList;

public class Foreach extends Instruction{
	private String left,leftBis;
	private ArrayList<Instruction> right;
	
	public Foreach(String condition){
		this.left = condition;
		this.right = new ArrayList<Instruction>();
	}
	
	public void setLeftBis(String leftBis){
		this.leftBis = leftBis;
	}
	
	public ArrayList<Instruction> getCode(){
		return this.right;
	}

	public String toString(){
		String ret = "<FOREACH, "+this.res+", "+left+", "+leftBis+", [";
		for(Instruction instr : right){
			ret+=instr.toString()+",";
		}
		ret+="]>";
		return ret;
	}
}
