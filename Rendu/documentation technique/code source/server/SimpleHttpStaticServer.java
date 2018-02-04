package org.xtext.example.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.xtext.example.utils.MalformedHttpRequest;

public class SimpleHttpStaticServer {

	public static void main(String[] args) throws MalformedHttpRequest {
		int port = 3333;
		boolean serverUp = true;
		String lineReceived = "";
		String workingDirectory = new File("").getAbsolutePath();
		ServerSocket server;
		try {
			server = new ServerSocket(port);
			server.setReuseAddress(true);

			while (serverUp) {

				try {

					// On met le serveur en attente de connexion
					// L'acceptation de la connexion renvoie une socket
					Socket client = server.accept();
					
					
					System.out.println("[MSG] Debut de transmission");
					System.out.println("[MSG] Client " + client.getInetAddress() + ":" + client.getPort() + "\n");
					
					// C'est par le biais de celle ci qu'on recevra les requetes
					// du client et qu'on lui repondra
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					
					String[] requestParts;
					String requestVerb = "", requestResource = "", requestVersion = "";
					
					Map<String, String> headers = new HashMap<String, String>();
					String headerKey = "", headerValue = "";
					
					
					int i = 0;
					while ((lineReceived = in.readLine()) != null) {
			            System.out.println("[n°" + i + "]" + lineReceived);
			            i++;			            
			            
			            if (lineReceived.isEmpty()) {
			                break;
			            } else if (i == 1) {
			            	
			            	// La premiere ligne sera de la forme GET / HTTP/1.0 par exemple
			            	requestParts = lineReceived.split(" ");
			            	
			            	if(requestParts.length == 3){
				            	requestVerb = requestParts[0];
				            	requestResource = requestParts[1].replace('/', '\\');
				            	requestVersion = requestParts[2];
			            	} else {
			            		// throw new MalformedHttpRequest("Request does not contain verb, resource or http version used.");
			            		System.out.println("[ERROR] Request has not the right shape (VERB RESOURCE VERSION)");
			            	}
			            	
			            } else {
			            	
			            	// Pour chaque ligne recue on en extrait l'en-tete et sa valeur
			            	int delimiter = lineReceived.indexOf(':');
			            	if(delimiter > 0) {
			            		headerKey = lineReceived.substring(0, delimiter);
			            		headerValue = lineReceived.substring(delimiter + 1, lineReceived.length()).trim();
			            		headers.put(headerKey, headerValue);
			            		System.out.println("Matching the key [" + headerKey + "] and value [" + headerValue + "]");
			            	}
			            	
			            }
			        }
					
					
					// A ce stade on a collecte les informations contenues dans la requete
					// Il faut desormais y repondre
					String responseCode = "200";
					String responseMessage = "OK";
					String responseContent = "";
					
					// Cas particulier de l'index
					if(requestResource.equals("\\")){
						requestResource = "\\index.html";
					}
					
					// ATTENTION, pas teste avec linux, peut etre que les backslashs sont des slashs sous linux :/
					System.out.println("[MSG] On essaie d'acceder au fichier : " + workingDirectory + "\\src\\www" + requestResource);
					
					
					try {
						BufferedReader fileContent = new BufferedReader(new FileReader(workingDirectory + "\\src\\www" + requestResource));
						
						while ((lineReceived = fileContent.readLine()) != null) {
							responseContent += lineReceived + "\n";
						}
						
						System.out.println("\n\n" + responseContent + "\n\n");
						
						fileContent.close();
						
					} catch (Exception e) {
						System.out.println("[ERROR] Impossible to access requested file");
						
						responseCode = "404";
						responseMessage = "File Not Found";
						responseContent = "<h1>Error 404</h1><b>Resource requested is not available on the server.</b>";
					}
					
					
					out.write("HTTP/1.0 " + responseCode + " " + responseMessage + "\r\n");
					Date now = new Date();
					DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("EN","en"));
					out.write("Date: " + formatter.format(now) + "\r\n");
					out.write("Content-Type: text/html\r\n");
					out.write("\r\n");
					
					out.write(responseContent + "\n");
					
					System.out.println("[MSG] Fin de transmission\n\n");
					out.close();
					in.close();
					client.close(); // la fermeture implique que le client devra rouvrir une nouvelle connexion pour effectuer une autre requete

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			server.close();

		} catch (IOException e1) {
			// error while creating server socket
			System.out.println("Impossible de lancer le serveur (port deja utilise ? permission necessaire ?)");
		}
	}
}
