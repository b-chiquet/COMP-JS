package org.xtext.example.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.xtext.example.ProjetStandaloneSetupGenerated;
import org.xtext.example.generator.CodeGenerator;
import org.xtext.example.generator.JsGenerator;
import org.xtext.example.utils.MalformedHttpRequest;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class SimpleHttpServer {
	
	@Inject
	private Provider<ResourceSet> resourceSetProvider;
	@Inject 
	private JavaIoFileSystemAccess fileAccess;
	@Inject
	private GeneratorDelegate generator;
	
	public static void main(String[] args) throws MalformedHttpRequest, ClassNotFoundException {
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
					Map<String, String> formdata = new HashMap<String, String>();
					String headerKey = "", headerValue = "";
					
					
					boolean bodyReceived = false, lineCounts = false;
					String requestBody = "";
					boolean headersReceived = false;
					
					int i = 0, hyphenCounter = 0;
					
					
					while ((lineReceived = in.readLine()) != null) {
			            System.out.println("[n°" + i + "]" + lineReceived);
			            i++;			            
			            
			            if (lineReceived.isEmpty()) {
			                if(!headersReceived) headersReceived = true;
			                
			                // si on attend des donnees egalement
			                if((headers.get("Content-Type") == null) 
			                		|| (headers.get("Content-Type").equals("multipart/form-data") && bodyReceived)
			                		|| (headers.get("Content-Type").equals("application/json") && bodyReceived)
			                		|| (headers.get("Content-Type").equals("text/plain") && bodyReceived)) {
			                	
			                	break;
			                }
			                
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

			            	if(!headersReceived) {
			            		
				            	// Pour chaque ligne recue on en extrait l'en-tete et sa valeur
				            	int delimiter = lineReceived.indexOf(':');
				            	if(delimiter > 0) {
				            		headerKey = lineReceived.substring(0, delimiter);
				            		headerValue = lineReceived.substring(delimiter + 1, lineReceived.length()).trim();
				            		headers.put(headerKey, headerValue);
				            		// System.out.println("Matching the key [" + headerKey + "] and value [" + headerValue + "]");
				            	}
			            	} else {
			            		
			            		lineCounts = true;
			            		
			            		if(lineReceived.startsWith("--")) {
			            			hyphenCounter++;
			            			lineCounts = false;
			            		}
			            		
			            		if(hyphenCounter == 2) {
			            			// Debut et fin des donnees
			            			bodyReceived = true;
			            			lineCounts = false;
			            			break;
			            		}
			            		
			            		if(lineReceived.startsWith("Content-Disposition")) {
			            			formdata.put("code", "");
			            			lineCounts = false;
			            		}
			            		
			            		if(lineCounts) requestBody += (lineReceived + "\n");
			            		
			            	}
			            	
			            }
			        }
					
					formdata.put("code", requestBody.trim());
					
					System.out.println("We received the following object : " + formdata.get("code"));
					
					//ecriture d'un fichier .wh à partir du code reçu
					Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./GENERATED.wh"), ("utf-8")));
					writer.write(formdata.get("code"));
					writer.close();
					
					Injector injector = new ProjetStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
					SimpleHttpServer shs = injector.getInstance(SimpleHttpServer.class);
					
					String mainFunction="";
					String responseContent="{";
					//appel des générations de .whc, code 3@ et .js
					try{
						mainFunction = shs.func();
						responseContent += "\"main\": \"";
						responseContent += mainFunction;
						responseContent += "\",";
					}catch (Exception e){
						System.err.println(e.toString());
						responseContent += "\"err\": \"";
						responseContent += e.getMessage();
						responseContent += "\",";
					}

					//lecture du fichier JS produit
					String resultat="";
					try{
						InputStream flux=new FileInputStream("./GENERATED.js"); 
						InputStreamReader lecture=new InputStreamReader(flux);
						BufferedReader buff=new BufferedReader(lecture);
						String ligne;
						
						while ((ligne=buff.readLine())!=null){
							resultat+= (ligne + "\n");
						}
						buff.close(); 
					}		
					catch (Exception e){
						System.err.println(e.toString());
						responseContent += "\"err\": \"";
						responseContent += e.getMessage();
						responseContent += "\",";
					}
					
					
					// A ce stade on a collecte les informations contenues dans la requete
					// Il faut desormais y repondre
					String responseCode = "200";
					String responseMessage = "OK";
										
					System.out.println("\n\n" + resultat + "\n\n");
					
					resultat = resultat.trim().replace("\t", "\\t").replace("\n", "\\n");
					
					// Constructing JSON
					//attribut code JS
					responseContent += "\"code\": \"";
					responseContent += resultat;
					responseContent += "\"";
					
					//lecture du fichier WHC produit
					resultat="";
					try{
						InputStream flux=new FileInputStream("./GENERATED.whp"); 
						InputStreamReader lecture=new InputStreamReader(flux);
						BufferedReader buff=new BufferedReader(lecture);
						String ligne;
						
						while ((ligne=buff.readLine())!=null){
							resultat+= (ligne + "\n");
						}
						buff.close(); 
						}		
					catch (Exception e){
						System.err.println(e.toString());
						responseContent += "\"err\": \"";
						responseContent += e.getMessage();
						responseContent += "\",";
					}
					
					System.out.println("\n\n" + resultat + "\n\n");
					resultat = resultat.trim().replace("\t", "\\t").replace("\n", "\\n");
					
					//attribut code whp
					responseContent += ",\"while\": \"";
					responseContent += resultat;
					responseContent += "\"";
					responseContent += "}";
					
					System.out.println("\n\n" + responseContent + "\n\n");
						
					out.write("HTTP/1.0 " + responseCode + " " + responseMessage + "\r\n");
					Date now = new Date();
					DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("EN","en"));
					out.write("Date: " + formatter.format(now) + "\r\n");
					out.write("Content-Type: application/json\r\n");
					out.write("Access-Control-Allow-Origin: *\r\n");
					out.write("\r\n");
					
					out.write(responseContent);
					
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
	
	protected String func() throws Exception{
		ResourceSet set = this.resourceSetProvider.get();
		Resource resource = set.getResource(URI.createFileURI("./GENERATED.wh"), true);
		
		//pretty printer
		fileAccess.setOutputPath(".");
		GeneratorContext context = new GeneratorContext();
		context.setCancelIndicator(CancelIndicator.NullImpl);
		try{
			generator.generate(resource, fileAccess, context);
		}catch (Exception e){
			throw new Exception("Error : incorrect WHILE code !!");
		}
		
		//3@ generator
		CodeGenerator gen = new CodeGenerator();
		try{
			gen.generate(resource);
		}catch (Exception e){
			throw new Exception("Error : 3@ code generation failed !!");
		}
		
		//js generator
		JsGenerator jsGen = new JsGenerator(gen.getFuncTab());
		String mainFunction = "";
		try{
			mainFunction = jsGen.translate();
		}catch (Exception e){
			throw new Exception("Error : JavaScript generation failed !!");
		}
		return mainFunction;
	}
}
