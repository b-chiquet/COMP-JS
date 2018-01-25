package org.xtext.example.generator;

import java.util.ArrayList;

public class If extends Instruction{
	private String left;
	private ArrayList<Instruction> right, rightBis;
	
	public If(String condition){
		this.setLeft(condition);
		this.setRight(new ArrayList<Instruction>());
		this.rightBis = new ArrayList<Instruction>();
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public void setLeft(String left){
		this.left = left;
	}
	
	public ArrayList<Instruction> getCode(){
		return this.getRight();
	}
	
	public ArrayList<Instruction> getCodeBis(){
		return this.rightBis;
	}

	public String toString(){
		String ret = "<IF, "+this.res+", "+getLeft()+", [";
		for(Instruction instr : getRight()){
			ret+=instr.toString()+",";
		}
		ret+="], [";
		for(Instruction instr : rightBis){
			ret+=instr.toString()+",";
		}
		ret+= "]>";
		return ret;
	}

	public ArrayList<Instruction> getRight() {
		return right;
	}

	public void setRight(ArrayList<Instruction> right) {
		this.right = right;
	}

	public String getLeft() {
		return left;
	}
}
