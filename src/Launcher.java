package org.xtext.example;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.IResourceValidator;
import org.xtext.example.ProjetStandaloneSetupGenerated;
import org.xtext.example.generator.CodeGenerator;
import org.xtext.example.generator.JsGenerator;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class Launcher {
	
	@Inject
	private Provider<ResourceSet> resourceSetProvider;

	@Inject
	private IResourceValidator validator;

	@Inject
	private GeneratorDelegate generator;

	@Inject 
	private JavaIoFileSystemAccess fileAccess;
	
	public static void main(String[] args) {	
		Injector injector = new ProjetStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
		Launcher main = injector.getInstance(Launcher.class);
		main.run();
	}
	
	/**
	 * Run the generation of a pretty printed .whc file, a 3@ code and a JS file
	 * .whc file and .js file are generated in /gen directory
	 * 3@ code is printed in the console
	 */
	protected void run(){
		// Load the resource
		ResourceSet set = resourceSetProvider.get();
		System.out.println("Reading WHILE file /src/org/xtext/example/test.wh");
		Resource resource = set.getResource(URI.createFileURI("./src/org/xtext/example/test.wh"), true);
		
		// Configure and start the generator
		String path = "./src/org/xtext/example/gen";
		System.out.println("File is being pretty printed in repository " + path);
		fileAccess.setOutputPath(path);
		GeneratorContext context = new GeneratorContext();
		context.setCancelIndicator(CancelIndicator.NullImpl);
		generator.generate(resource, fileAccess, context);
		System.out.println("Pretty-printing finished.\n");
		
		// Start 3@ code generation
		System.out.println("3@ code is being generated");
		CodeGenerator gen = new CodeGenerator();
		gen.generate(resource);
		System.out.println("3@ code generation finished.\n");
		
		// generation JS
		System.out.println("JS code is being generated");
		JsGenerator jsGen = new JsGenerator(gen.getFuncTab());
		jsGen.translate();
		System.out.println("JS code generation finished.\n");
		
		//interface web
		/*File htmlFile = new File("src/org/xtext/example/web_interface/index.html");
		try {
			Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

}
