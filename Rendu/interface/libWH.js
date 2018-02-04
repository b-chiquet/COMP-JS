/*
* libWH.js
* Bibliothèque permettant de traduire du WHILE en JavaScript
* Défini le type arbre binaire, les opérations possibles, une fonction d'évaluation, ...
*/


//type Tree correspondant à l'arbre binaire WHILE
function Tree (l,r){
	this.left = l;
	this.right = r;

	Tree.prototype.setLeft = function(t){
		this.left=t;
	}

	Tree.prototype.setRight = function(t){
		this.right=t;
	}

	Tree.prototype.getLeft = function(){
		return this.left;
	}

	Tree.prototype.getRight = function(){
		return this.right;
	}
}

//constante nil
//arbre ne contenant ni de sous-arbre gauche ni de sous-arbre droit
const nil = new Tree(null,null);

//opérateur and
function and (t1, t2){
	if (eval(t1) && eval(t2)){
		return cons(nil,nil);
	}
	return nil;
}

//opérateur or
function or (t1, t2){
	if (eval(t2) || eval(t2)){
		return cons(nil,nil);
	}
	return nil;
}

//créé un arbre binaire
//t1 est le sous-arbre gauche, t2 est celui de droite
function cons (t1, t2){
	return new Tree(t1,t2);
}

//créé un arbre binaire
//t1 est le sous-arbre gauche
//le sous-arbre droit est constitué de t2 en sous-arbre gauche et nil en sous-arbre droit
function list (t1, t2){
	return cons(t1,cons(t2,nil));
}

//comparateur
function eq (t1, t2){
	if(hd(t1)==hd(t2) && tl(t1)==tl(t2)){
		return cons(nil,nil);
	}else{
		return nil;
	}
}

//accesseur au sous-arbre gauche
function hd (t){
	if(t == nil){
		return nil;
	}
	return t.left;
}

//accesseur au sous-arbre droit
function tl (t){
	if(t == nil){
		return nil;
	}
	return t.right;
}

//evalue un arbre binaire (true ou false)
function evalT(t){
	if(t == nil){
		return false;
	}
	return true;
}

//compte le nombre d'itération pour un for
function countIt (t, cpt){
	if(t==nil){
		return cpt;
	}
	return countIt(tl(t), cpt+1);
}
