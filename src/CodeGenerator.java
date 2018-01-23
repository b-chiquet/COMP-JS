package org.xtext.example.generator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.emf.ecore.resource.Resource;
import org.xtext.example.projet.AFFECT;
import org.xtext.example.projet.COMMAND;
import org.xtext.example.projet.COMMANDS;
import org.xtext.example.projet.DEFINITION;
import org.xtext.example.projet.EXPRAND;
import org.xtext.example.projet.EXPREQ;
import org.xtext.example.projet.EXPRESSION;
import org.xtext.example.projet.EXPRNOT;
import org.xtext.example.projet.EXPROR;
import org.xtext.example.projet.EXPRSIMPLE;
import org.xtext.example.projet.FOREACH;
import org.xtext.example.projet.FOR_LOOP;
import org.xtext.example.projet.FUNCTION;
import org.xtext.example.projet.IF_THEN;
import org.xtext.example.projet.INPUTS;
import org.xtext.example.projet.LEXPR;
import org.xtext.example.projet.NOP;
import org.xtext.example.projet.OUTPUTS;
import org.xtext.example.projet.PROGRAM;
import org.xtext.example.projet.WHILE;

public class CodeGenerator {
	
	//table des fonctions
	private funcTab table;
	//numéro de fonction
	private int num;
	//funcEntry utilisé pour ajouter des fonctions à la funcTab
	private funcEntry nouvelleFunc;
	
	//table des symboles
	private SymTab symtab;
	
	
	public CodeGenerator(){
		this.table = new funcTab();
		this.symtab = new SymTab();
		num=0;
	}
	
	public void generate(Resource resource){

		//generation du code 3@
		this.compile((PROGRAM)resource.getAllContents().next());
		
		//affichage table des symboles
		System.out.println("Table symboles : "+this.symtab.toString());
		//affichage de la table des fonctions obtenue
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
		//on ajoute le nom de la fonction à la table des symboles
		this.symtab.addFun(f.getName());
		this.compile(f.getDef());
	}

	// Pour le type "DEFINTION"
	private void compile(DEFINITION d) {
		//ajout d'une nouvelle fonction à la table des fonctions
		nouvelleFunc = new funcEntry();
		table.addFunc("f"+num,nouvelleFunc);
		num++;
		
		this.compile(d.getInputs());
		this.compile(d.getCode(), nouvelleFunc.getCode());
		this.compile(d.getOutputs());
	}

	// Pour le type INPUT
	private void compile(INPUTS i) {
		nouvelleFunc.addIn();
		nouvelleFunc.addVar(i.getInput());
		nouvelleFunc.addCode(new Read(nouvelleFunc.getVar(i.getInput())));
		for(INPUTS in : i.getInputs()){
			this.compile(in);
		}
	}

	// Pour le type OUTPUT
	private void compile(OUTPUTS o) {
		nouvelleFunc.addOut();
		nouvelleFunc.addCode(new Write(nouvelleFunc.getVar(o.getOutput())));
		for(OUTPUTS out : o.getOutputs()){
			this.compile(out);
		}
	}

	// Pour le type "COMMANDS"
	private void compile(COMMANDS c, ArrayList<Instruction> listDest) {
		this.compile(c.getCommand(), listDest);
		for(COMMAND command : c.getCommands()){
			this.compile(command, listDest);
		}
	}

	// Pour le type "COMMAND", càd chaque commande 
	private void compile(COMMAND c,ArrayList<Instruction> listDest) {
		if(c.eClass().getName() == "AFFECT"){
			this.compile((AFFECT)c,listDest);
		}
		if(c.eClass().getName() == "IF_THEN"){
			this.compile((IF_THEN)c,listDest);
		}
		if(c.eClass().getName() == "NOP"){
			this.compile((NOP)c,listDest);
		}
		if(c.eClass().getName() == "FOR_LOOP"){
			this.compile((FOR_LOOP)c,listDest);
		}
		if(c.eClass().getName() == "WHILE"){
			this.compile((WHILE)c,listDest);
		}
		if(c.eClass().getName() == "FOREACH"){
			this.compile((FOREACH)c,listDest);
		}
	}

	// Pour le type "AFFECT"
	private void compile(AFFECT a, ArrayList<Instruction> listDest) {
		//ajout de la variable à la table des variables
		nouvelleFunc.addVar(a.getVariable());
		//compilation de l'expression
		this.compile(a.getValeur(),nouvelleFunc.getVar(a.getVariable()),listDest);
		
		//si affectation multiple
		int index = 0;
		for(String var : a.getVars()){
			nouvelleFunc.addVar(var);
			this.compile(a.getVals().get(index),nouvelleFunc.getVar(var),listDest);
			index++;
		}
	}

	// Pour le type "IF_THEN"
	private void compile(IF_THEN if_then, ArrayList<Instruction> listDest) {
		String tmp = this.compile(if_then.getCond(),null,listDest);
		
		If instr = new If("");
		listDest.add(instr);
		
		instr.setLeft(tmp);
		for(COMMANDS comm : if_then.getCommands1()){
			//ajout des commandes dans le "then"
			this.compile(comm, instr.getCode());
		}
		for(COMMANDS comm : if_then.getCommands2()){
			//ajout des commandes dans le "else"
			this.compile(comm, instr.getCodeBis());
		}
	}

	// Pour le type "NOP"
	private void compile(NOP n, ArrayList<Instruction> listDest) {
		Nop instr = new Nop();
		listDest.add(instr);
	}
	
	//Pour le type "FOR_LOOP"
	private void compile(FOR_LOOP fl, ArrayList<Instruction> listDest){
		String tmp = this.compile(fl.getExp(),null,listDest);
		
		For instr = new For("");
		listDest.add(instr);
		
		instr.setLeft(tmp);
		
		for(COMMANDS comm : fl.getCommands()){
			//ajout des commandes dans le "do"
			this.compile(comm, instr.getCode());
		}
	}
	
	//Pour le type "WHILE"
	private void compile(WHILE w, ArrayList<Instruction> listDest){
		String tmp = this.compile(w.getCond(),null,listDest);
		
		While instr = new While("");
		listDest.add(instr);
		
		instr.setLeft(tmp);
		
		for(COMMANDS comm : w.getCommands()){
			//ajout des commandes dans le "do"
			this.compile(comm, instr.getCode());
		}
	}
	
	//Pour le type "FOREACH"
	private void compile(FOREACH fe, ArrayList<Instruction> listDest){
		//compilation du terme de gauche de la condition
		String tmp = this.compile(fe.getExp1(), null, listDest);
		//idem pour terme de droite
		String tmpbis = this.compile(fe.getExp2(), null, listDest);
		
		Foreach instr = new Foreach("");
		listDest.add(instr);
		
		instr.setLeft(tmp);
		instr.setLeftBis(tmpbis);
		
		for(COMMANDS comm : fe.getCommands()){
			//ajout des commandes dans le "do"
			this.compile(comm, instr.getCode());
		}
  	}


	//Pour le type "EXPRESSION"
	private String compile(EXPRESSION e,String addRes, ArrayList<Instruction> listDest){
		return this.compile(e.getExpand(),addRes,listDest);
	}
	
	private String compile(EXPRAND e,String addRes, ArrayList<Instruction> listDest){
		String res="";
		//si c'est une expression AND
		if (!e.getExpors().isEmpty()){
			//compilation terme de droite
			String right="";
			for(EXPRAND exp : e.getExpors()){
				right = this.compile(exp, null, listDest);
			}
			//compilation terme de gauche
			String left = this.compile(e.getExpor(),null, listDest);
			
			And and = new And("","","");
			listDest.add(and);
			
			and.setRight(right);
			and.setLeft(left);
			
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			
			and.setRes(res);
		//si c'est une expression OR	
		}else{
			if(addRes == null){
				res = this.compile(e.getExpor(),null, listDest);
			}else{
				res = this.compile(e.getExpor(),addRes, listDest);
			}
		}
		return res;
	}
	
	private String compile(EXPROR e,String addRes, ArrayList<Instruction> listDest){
		String res="";
		//si c'est une expression OR
		if (!e.getExpnots().isEmpty()){
			//compilation terme de droite
			String right="";
			for(EXPRAND exp :e.getExpnots()){
				right = this.compile(exp, null, listDest);
			}
			//compilation terme de gauche
			String left = this.compile(e.getExpnot(),null, listDest);
			
			Or or = new Or("","","");
			listDest.add(or);
			
			or.setRight(right);
			or.setLeft(left);
			
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			
			or.setRes(res);
		}else{
			if(addRes == null){
				res = this.compile(e.getExpnot(),null, listDest);
			}else{
				res = this.compile(e.getExpnot(),addRes, listDest);
			}
		}
		return res;
	}
	
	private String compile(EXPRNOT e,String addRes, ArrayList<Instruction> listDest){
		String res="";
		//si on a une instruction not
		if(e.getN() != null){
			String tmp = this.compile(e.getExpeq(),null, listDest);
			Not not = new Not("","");
			listDest.add(not);
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			not.setRes(res);
			not.setLeft(tmp);
		}else{
			res = this.compile(e.getExpeq(),addRes, listDest);
		}
		return res;
	}
	
	private String compile(EXPREQ e,String addRes, ArrayList<Instruction> listDest){
		String res="";
		//si c'est une exression simple ou eq
		if(e.getExp1() != null){
			//si c'est une expression eq
			if(!e.getExp2().isEmpty()){
				//res = this.compile(e.getExp1(),instr, listDest);
				String right="";
				for(EXPRAND exp : e.getExp2()){
					right = this.compile(exp,null, listDest);
				}
				//compilation terme de gauche
				String left = this.compile(e.getExp1(),null, listDest);
				
				Eq eq = new Eq("","","");
				listDest.add(eq);
				
				eq.setRight(right);
				eq.setLeft(left);
				
				if(addRes == null){
					res = nouvelleFunc.addReg();
				}else{
					res = addRes;
				}
				
				eq.setRes(res);
			//si c'est une expression simple
			}else{
				if(addRes == null){
					res = this.compile(e.getExp1(),null, listDest);
				//si l'adresse de resultat n'etait pas vide, on la fait transferer
				}else{
					res = this.compile(e.getExp1(),addRes, listDest);
				}
			}
		//si c'est une expression entre parenthèses
		}else{
			if(addRes == null){
				res = this.compile(e.getExp(),null, listDest);
			}else{
				res = this.compile(e.getExp(),addRes, listDest);			
			}
		}
		return res;
	}
	
	private String compile(EXPRSIMPLE e,String addRes, ArrayList<Instruction> listDest){
		String res="";
		//nil
		if(e.getNil() != null){
			if(addRes == null){
				res = "nil";
			}else{
				Nil instr = new Nil("");
				listDest.add(instr);
				instr.setRes(addRes);
				res = "_";
			}
			
		//variable
		}else if(e.getVariable() != null){
			//variable simple
			if(addRes == null){
				res = nouvelleFunc.getVar(e.getVariable());
			//affectation simple
			}else{
				Affect instr = new Affect("","");
				listDest.add(instr);
				instr.setRes(addRes);
				instr.setLeft(nouvelleFunc.getVar(e.getVariable()));
			}
		//symbole
		}else if(e.getSymbole() != null){
			//symbole simple
			if(addRes == null){
				res = symtab.getSym(e.getSymbole());
			//affectation simple
			}else{
				Affect instr = new Affect("","");
				listDest.add(instr);
				instr.setRes(addRes);
				instr.setLeft(symtab.getSym(e.getSymbole()));
			}
		//cons
		}else if (e.getCons() != null){			
			ArrayList<String> x = this.compile(e.getLexpr(), null, listDest);
			
			Cons cons = new Cons("","");
			listDest.add(cons);
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			cons.setRes(res);
			cons.setLeft(x.get(0));
			if(x.size() > 0){
				cons.setRight(x.get(1));
			}
		//list
		}else if (e.getList() != null){
			ArrayList<String> exps = this.compile(e.getLexpr(), null, listDest);
			
			Liste list = new Liste("","");
			listDest.add(list);
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			list.setRes(res);
			list.setLeft(exps.get(0));
			if(exps.size() > 0){
				list.setRight(exps.get(1));
			}
		//hd
		}else if (e.getHd() != null){
			String tmp = this.compile(e.getExpr(), null, listDest);
			Hd hd = new Hd("","");
			listDest.add(hd);
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			hd.setRes(res);
			hd.setLeft(tmp);
		//hd
		}else if (e.getTl() != null){
			String tmp = this.compile(e.getExpr(), null, listDest);
			Tl tl = new Tl("","");
			listDest.add(tl);
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			tl.setRes(res);
			tl.setLeft(tmp);
		//sym + left expression (appel de fonction)
		}else if (e.getSym() != null){
			ArrayList<String> exps = this.compile(e.getLexpr(), null, listDest);
			Call call = new Call("","");
			listDest.add(call);
			if(addRes == null){
				res = nouvelleFunc.addReg();
			}else{
				res = addRes;
			}
			call.setRes(res);
			call.setLeft(symtab.getSym(e.getSym()));
			call.setRight(exps);
		}
		return res;
	}
	
	//Left expression
	private ArrayList<String> compile(LEXPR e,String addRes, ArrayList<Instruction> listDest){
		ArrayList<String> res = new ArrayList<String>();
		res.add(this.compile(e.getExpr(),addRes, listDest));
		if(e.getLexpr() != null){
			res.addAll(this.compile(e.getLexpr(), null, listDest));
		}
		return res;
	}
}
