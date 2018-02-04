=== PROJET COMPILATEUR WHILE VERS JAVASCRIPT ===
=== Compilation, ESIR2, 2017-2018 ===
=== GODBILLOT Faustine, GAUTRAIN Antoine, CHIQUET Basile, ROCHAT Nicolas, DIIARA Mame, LABRUE Gwendal, JEGO Emmanuel ===


=== MODE D'EMPLOI ===
1. Ouvrir une invite de commande
2. Se rendre dans le répertoire où se trouve ce fichier README.txt
3. Lancer le serveur via la commande
	java -jar ./server.jar
4. Ouvrir l'interface d'utilisation (index.html)
5. Modifier le code WHILE en cliquant sur "MODIFIER" (un programme simple est saisi par défaut)
6. Cliquer sur "VALIDER" pour confirmer les modifications apportées au programme
7. Cliquer sur "COMPILER" : le code JavaScript est affiché après le chargement
8. Cliquer sur le bouton dans la fenêtre de test ("fX") pour tester le programme
	-> les paramètres à saisir doivent correspondre au langage JavaScript
		-> (cons nil nil) : cons(nil,nil)
		-> (list nil nil) : list(nil,nil)
		-> (hd X) : hd(X)
		-> (tl X) : tl(X)
		-> nil : nil
	-> les résultats renvoyés sont affichés en langage JavaScript	
		-> "left" correspond au sous-arbre gauche
		-> "right" correspond au sous-arbre droit
		-> {"left":null,"rigth":null} correspond à nil
	-> la fonction appelée est la dernière déclarée dans le programme WHILE

Le bouton "? Aide" donne certaines indications supplémentaires sur l'interface.
Des indications concernant la compilation sont affichées dans l'invite de commande.


=== FONCTIONNALITES ===
Le compilateur WHILE ver JavaScript permet de traduire du code écrit en langage WHILE vers le langage JavaScript.
L'interface d'utilisation permet d'exécuter le compilateur et de tester le code JavaScript créé.
Le compilateur offre 3 principales fonctionnalités :
	- pretty-printer le code WHILE saisi
	- généré et visualiser du code JavaScript correspondant au code WHILE saisi
	- exécuter le code de manière intéractive et visualiser le résultat
L'interface comprend une gestion des erreurs. Un message d'erreur s'affiche si :
	- le code WHILE ne respecte pas la grammaire
	- la génération du code JavaScript a échoué
	- l'exécution du code JavaScript a échoué
