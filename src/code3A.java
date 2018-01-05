package org.xtext.example.generator;

/**
 * Classe de représentation d'instructions en code 3 adresses
 * @author groupe JS
 *
 */
public class code3A {
	String op;
	String res;
	String left,right;
	
	public code3A(String op, String res, String left, String right){
		this.op = op;
		this.res = res;
		this.left = left;
		this.right = right;
	}
	
	public void setOp(String op){
		this.op = op;
	}
	
	public void setRes(String res){
		this.res = res;
	}
	
	public void setLeft(String left){
		this.left = left;
	}
	
	public void setRight(String right){
		this.right = right;
	}

	public String toString(){
		return "<"+op+", "+res+", "+left+", "+right+">";
	}
}
