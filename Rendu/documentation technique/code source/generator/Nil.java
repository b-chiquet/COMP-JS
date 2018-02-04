package org.xtext.example.generator;

public class Nil extends Instruction{
	public Nil(String res){
		this.res = res;
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public String toString(){
		return "<NIL, "+res+", _, _>";
	}
}
