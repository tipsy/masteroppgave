package no.ntnu.assignmentsystem.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import no.ntnu.assignmentsystem.model.ModelFactory;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.ModelPackage;
import no.ntnu.assignmentsystem.model.UoD;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class XmiModelLoaderImpl implements ModelLoader {
	private File dataFile;
	private Resource resource;
	private UoD uod;
	
	private ModelPackage modelPackage;
	
	public XmiModelLoaderImpl(File dataFile) {
		this.dataFile = dataFile;
		
		// Initialize model package
		modelPackage = ModelPackage.eINSTANCE;
		modelPackage.eClass();
		modelPackage.setEFactoryInstance(new GlobalIdModelFactoryImpl());
	    
	    loadModel();
	}
	
	@Override
	public UoD getUoD() {
		return uod;
	}
	
	@Override
	public Resource getResource() {
		if (resource == null) {
		    ResourceSet resourceSet = new ResourceSetImpl();
	
		    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
	
		    URI fileURI = URI.createFileURI(dataFile.getAbsolutePath());
	
		    resource = resourceSet.createResource(fileURI);
		}
	    
	    return resource;
	}
	
	@Override
	public ModelFactory getFactory() {
		return (ModelFactory)modelPackage.getEFactoryInstance();
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
}
