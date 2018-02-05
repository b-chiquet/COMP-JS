=== PROJET COMPILATEUR WHILE VERS JAVASCRIPT ===
=== Compilation, ESIR2, 2017-2018 ===
=== GODBILLOT Faustine, GAUTRAIN Antoine, CHIQUET Basile, ROCHAT Nicolas, DIIARA Mame, LABRUE Gwendal, JEGO Emmanuel ===


=== CODE SOURCE ===
La grammaire est définie dans le fichier "Projet.xtext"
La classe "Launcher.java" permet de lancer le compilateur directement plutôt que via le serveur
Le fichier test.wh est le fichier contenant le programme WHILE lu par Launcher.java
Un répertoire gen/ est créé à la compilation lancée via Luncher.java et contient les différents fichiers créés

Les classes utiles à la compilation sont définies dans le package "generator"
	- les classes correspondant aux instructions possibles en code 3@
	- les classes utiles à la génération de code 3@ : funcEntry, funcTab, symTab
	- ProjectGenerator.xtend est la classe permettant de générer le fichier pretty-printé
	- CodeGenerator.java est la classe permettant de générer le code 3@
	- JsGenerator.java est la classe permettant de générer le code JavaScript
	- le fichier executable compilator.jar permet d'exécuter le compilateur sans interface (amélioration du jar du premier sprint)
Le package "server" contient la classe "SimpleHttpServer.java" qui défini notre serveur java pour l'interface web


=== MODE D'EMPLOI COMPILATEUR SANS INTERFACE ===
1. Ouvrir une invite de commande
2. Se rendre dans le répertoire "code source"
3. Lancer le compilateur via la commande
	java -jar compaltor.jar --src=test.wh
4. Le fichier pretty-printé est créé dans le repertoire "gen"
5. Le fichier JavaScript est créé dans le repertoire "code source"