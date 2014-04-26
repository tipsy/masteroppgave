package no.ntnu.assignmentsystem.model;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import no.ntnu.assignmentsystem.model.eagle.EaglePackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class Main {

	public static void main(String[] args) {
	    EaglePackage.eINSTANCE.eClass();
//	    EagleFactory factory = EagleFactory.eINSTANCE;
	    
	    // Create a resource set.
	    ResourceSet resourceSet = new ResourceSetImpl();

	    // Register the default resource factory -- only needed for stand-alone!
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

	    // Get the URI of the model file.
	    URI fileURI = URI.createFileURI(new File("model/UoD.xmi").getAbsolutePath());

	    // Create a resource for this file.
	    Resource resource = resourceSet.createResource(fileURI);

	    try {
			resource.load(Collections.EMPTY_MAP);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	    System.out.println(resource.getContents());
	    
	    // Add the book and writer objects to the contents.
//	    resource.getContents().add(uod);
	    
	    // Save the contents of the resource to the file system.
//	    try
//	    {
//	    	resource.save(Collections.EMPTY_MAP);
//	    }
//	    catch (IOException e) {
//	    }
	    
//	    UoD uod = factory.createUoD();
//
//		Pet pet = factory.createPet();
//		pet.setName("Rocky");
//		
//	    Person person = factory.createPerson();
//	    person.setName("Christian");
//	    person.getPets().add(pet);
//		
//		uod.getPeople().add(person);
//		uod.getPets().add(pet);
//		
//		for (Person p : uod.getPeople()) {
//			System.out.println(p.getName() + " has the following pets: " + p.getPets());
//		}
	}

}
