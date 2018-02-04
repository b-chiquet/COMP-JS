package org.xtext.example.generator;

public class Write extends Instruction{
	public Write(String left){
		this.left = left;
	}
	
	public String toString(){
		return "<WRITE, _, "+this.left+", _>";
	}
}
