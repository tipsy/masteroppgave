package no.ntnu.assignmentsystem.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.model.ModelFactory;
import no.ntnu.assignmentsystem.model.ModelPackage;
import no.ntnu.assignmentsystem.model.UoD;
import no.ntnu.assignmentsystem.services.AssignmentServices;
import no.ntnu.assignmentsystem.services.CourseServices;
import no.ntnu.assignmentsystem.services.ProblemServices;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.ServicesFactory;
import no.ntnu.assignmentsystem.services.ServicesPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class ServicesImpl extends Container implements Services {
	private ModelFactory modelFactory;
	private ServicesFactory servicesFactory;
	
	private UoD uod;
	
	public ServicesImpl() {
	    ModelPackage.eINSTANCE.eClass();
	    modelFactory = ModelFactory.eINSTANCE;
	    
	    ServicesPackage.eINSTANCE.eClass();
	    servicesFactory = ServicesFactory.eINSTANCE;
	    
	    loadModel();
	}
	
	@Override
	public CourseServices getCourseServices() {
		EList<Course> courses = getUoD().getCourses();
		return new CourseServicesImpl(servicesFactory, courses);
	}

	@Override
	public AssignmentServices getAssignmentServices(String courseId) {
		return null;
	}

	@Override
	public ProblemServices getProblemServices(String courseId, String assignmentId) {
		return null;
	}
	
	private UoD getUoD() {
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
	    URI fileURI = URI.createFileURI(new File("model/UoD.xmi").getAbsolutePath());

	    // Create a resource for this file.
	    Resource resource = resourceSet.createResource(fileURI);
	    
	    return resource;
	}
}
