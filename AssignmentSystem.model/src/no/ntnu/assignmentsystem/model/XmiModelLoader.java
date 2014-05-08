package no.ntnu.assignmentsystem.model;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class XmiModelLoader implements ModelLoader {
	private File dataFile;
	private UoD uod;
	
	public XmiModelLoader(File dataFile) {
		this.dataFile = dataFile;
		
		ModelPackage.eINSTANCE.eClass();
	    
	    loadModel();
	}
	
	@Override
	public UoD getUoD() {
		return uod;
	}

	private void loadModel() {
	    Resource resource = getResource();
	    
	    // Load the contents of the resource from the file system.
	    try {
			resource.load(Collections.EMPTY_MAP);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	    uod = (UoD)resource.getContents().get(0);
	}
	
	private void saveModel() {
		Resource resource = getResource();
		
		resource.getContents().add(uod);
		
	    // Save the contents of the resource to the file system.
	    try
	    {
	    	resource.save(Collections.EMPTY_MAP);
	    }
	    catch (IOException e) {
	    }
	}
	
	private Resource getResource() {
		// Create a resource set.
	    ResourceSet resourceSet = new ResourceSetImpl();

	    // Register the default resource factory -- only needed for stand-alone!
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

	    // Get the URI of the model file.
	    URI fileURI = URI.createFileURI(dataFile.getAbsolutePath());

	    // Create a resource for this file.
	    Resource resource = resourceSet.createResource(fileURI);
	    
	    return resource;
	}
}
