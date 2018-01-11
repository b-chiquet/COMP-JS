package org.xtext.example.generator;

import org.eclipse.emf.common.util.EList;
import org.xtext.example.projet.*;

public class CodeGenerator {
	
	//table des fonctions
	static private funcTab table;
	//numéro de fonction
	private int num;
	//funcEntry utilisé pour ajouter des fonctions à la funcTab
	private funcEntry nouvelleFunc;
	
	
	public CodeGenerator(){
		this.table = new funcTab();
		num=0;
	}
	
	public void generate(PROGRAM d){
		this.compile(d);
		System.out.println(this.table.toString());
	}

	
	// Pour le type "PROGRAM", qui représente tout le programme
	private void compile(PROGRAM d) {
		for (FUNCTION func : d.getFunctions()){
			this.compile(func);
		}
	}

	// Pour le type "FUNCTION"
	private void compile(FUNCTION f) {
		this.compile(f.getDef());
	}

	// Pour le type "DEFINTION"
	private void compile(DEFINITION d) {
		nouvelleFunc = new funcEntry();
		table.addFunc("f"+num,nouvelleFunc);
		num++;
		this.compile(d.getInputs());
		this.compile(d.getCode());
		this.compile(d.getOutputs());
	}

	// Pour le type INPUT
	private void compile(INPUTS i) {
		nouvelleFunc.addIn();
		nouvelleFunc.addVar(i.getInput());
		for(INPUTS in : i.getInputs()){
			this.compile(in);
		}
	}

	// Pour le type OUTPUT
	private void compile(OUTPUTS o) {
		nouvelleFunc.addOut();
		for(OUTPUTS out : o.getOutputs()){
			this.compile(out);
		}
	}

	// Pour le type "COMMANDS"
	private void compile(COMMANDS c) {
		this.compile(c.getCommand());
		for(COMMAND command : c.getCommands()){
			this.compile(command);
		}
	}

	// Pour le type "COMMAND", càd chaque commande 
	private void compile(COMMAND c) {
		if(c.eClass().getName() == "AFFECT"){
			this.compile((AFFECT)c);
		}
		if(c.eClass().getName() == "IF_THEN"){
			this.compile((IF_THEN)c);
		}
		if(c.eClass().getName() == "NOP"){
			this.compile((NOP)c);
		}
		if(c.eClass().getName() == "FOR_LOOP"){
			this.compile((FOR_LOOP)c);
		}
		if(c.eClass().getName() == "WHILE"){
			this.compile((WHILE)c);
		}
		if(c.eClass().getName() == "FOREACH"){
			this.compile((FOREACH)c);
		}
	}

	// Pour le type "AFFECT"
	private void compile(AFFECT a) {
		code3A instr = nouvelleFunc.addCode(Op.NONE,"_","_","_");
		nouvelleFunc.addVar(a.getVariable());
		//@ resultat = id(a.variable)
		instr.setRes(nouvelleFunc.getVar(a.getVariable()));
		instr.setLeft(this.compile(a.getValeur(),instr));
	}

	// Pour le type "IF_THEN"
	private void compile(IF_THEN if_then) {
		code3A instr = nouvelleFunc.addCode(Op.IF,"_","_","_");
		EXPRAND and = if_then.getCond().getExpand();
		//si la condition est une expression composée (AND / OR / =?)
		if(!and.getExpors().isEmpty() || !and.getExpor().getExpnots().isEmpty() || !and.getExpor().getExpnot().getExpeq().getExp2().isEmpty()){
			code3A tmp = nouvelleFunc.addCode(null, "x", "", "");
			tmp.setLeft(this.compile(if_then.getCond(), tmp));
			instr.setLeft("x");
		}else{
			instr.setLeft(this.compile(if_then.getCond(),instr));
		}
		for(COMMANDS comm : if_then.getCommands1()){
			this.compile(comm);
		}
		for(COMMANDS comm : if_then.getCommands2()){
			this.compile(comm);
		}
	}

	// Pour le type "NOP"
	private void compile(NOP n) {
		code3A instr = nouvelleFunc.addCode(Op.NOP,"_","_","_");
	}
	
	//Pour le type "FOR_LOOP"
	private void compile(FOR_LOOP fl){
		code3A instr = nouvelleFunc.addCode(Op.FOR,"_","_","_");
		instr.setLeft(this.compile(fl.getExp(),instr));
		for(COMMANDS comm : fl.getCommands()){
			this.compile(comm);
		}
	}
	
	//Pour le type "WHILE"
	private void compile(WHILE w){
		code3A instr = nouvelleFunc.addCode(Op.WHILE,"_","_","_");
		instr.setLeft(this.compile(w.getCond(),instr));
		for(COMMANDS comm : w.getCommands()){
			this.compile(comm);
		}
	}
	
	//Pour le type "FOREACH"
	private void compile(FOREACH fe){
		code3A instr = nouvelleFunc.addCode(Op.FOREACH,"_","_","_");
		instr.setLeft(this.compile(fe.getExp1(),instr));
		instr.setRight(this.compile(fe.getExp2(),instr));
		for(COMMANDS comm : fe.getCommands()){
			this.compile(comm);
		}
  	}


	//Pour le type "EXPRESSION"
	private String compile(EXPRESSION e,code3A instr){
		return this.compile(e.getExpand(),instr);
	}
	
	private String compile(EXPRAND e,code3A instr){
		String res="";
		if (!e.getExpors().isEmpty()){
			instr.setOp(Op.AND);
			res = this.compile(e.getExpor(),instr);
			for(EXPRAND exp : e.getExpors()){
				if(!exp.getExpors().isEmpty() || !exp.getExpor().getExpnots().isEmpty()){
					code3A tmp = nouvelleFunc.addCode(null, "x", "", "");
					tmp.setLeft(this.compile(exp, tmp));
					instr.setRight("x");
				}else{
					instr.setRight(this.compile(exp, instr));
				}
			}
		}else{
			res = this.compile(e.getExpor(),instr);
		}
		return res;
	}
	
	private String compile(EXPROR e,code3A instr){
		String res="";
		if (!e.getExpnots().isEmpty()){
			instr.setOp(Op.OR);
			res = this.compile(e.getExpnot(),instr);
			for(EXPROR exp :e.getExpnots()){
				if(!exp.getExpnots().isEmpty()){
					code3A tmp = nouvelleFunc.addCode(null, "x", "", "");
					tmp.setLeft(this.compile(exp, tmp));
					instr.setRight("x");
				}else{
					instr.setRight(this.compile(exp, instr));
				}
			}
		}else{
			res = this.compile(e.getExpnot(),instr);
		}
		return res;
	}
	
	private String compile(EXPRNOT e,code3A instr){
		String res="";
		if(e.getN() != null){
			res = this.compile(e.getExpeq(),instr);
			nouvelleFunc.addCode(Op.NOT,"_","_","_");
		}else{
			res = this.compile(e.getExpeq(),instr);
		}
		return res;
	}
	
	private String compile(EXPREQ e,code3A instr){
		String res="";
		if(e.getExp1() != null){
			if(!e.getExp2().isEmpty()){
				instr.setOp(Op.EQ);
				res = this.compile(e.getExp1(),instr);
				for(EXPRSIMPLE exp : e.getExp2()){
					instr.setRight(this.compile(exp,instr));
				}
			}else{
				res = this.compile(e.getExp1(),instr);
			}
		}else{
			res = this.compile(e.getExp(),instr);
		}
		return res;
	}
	
	private String compile(EXPRSIMPLE e,code3A instr){
		String res="";
		if(e.getValeur() != null){
			if(e.getValeur().equals("nil")){
				instr.setOp(Op.NIL);
				res = "_";
			}else{
				if (instr.getOp() == Op.NONE){
					//aucun opérateur placé auparavant donc affectation simple
					instr.setOp(Op.AFFECT);
					res = nouvelleFunc.getVar(e.getValeur());
				}else{
					res = nouvelleFunc.getVar(e.getValeur());
				}
			}
		}else if(e.getCons() != null){
			//to do
		}else if(e.getList() != null){
			//to do
		}else if(e.getHd() != null){
			res = this.compile(e.getExpr(),instr);
			instr.setOp(Op.HD);
		}else if(e.getTl() != null){
			res = this.compile(e.getExpr(), instr);
			instr.setOp(Op.TL);
		}else if(e.getSym() != null){
			//to do
			res = this.compile(e.getExpr(), instr);
			if(e.getLexpr() != null){
				this.compile(e.getLexpr(), instr);
			}
		}
		return res;
	}
	
	private String compile(LEXPR e,code3A instr){
		String res = this.compile(e.getExpr(),instr);
		if(e.getLexpr() != null){
			this.compile(e.getLexpr(), instr);
		}
		return res;
	}
}
