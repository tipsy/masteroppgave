# Assignment System

Master's thesis by Christian Rasmussen and David Åse.

## Installation

1. Setting up Eclipse

	a. Start Eclipse
	b. Set workspace to the root of project (the folder containing README.md)
	c. Open File > Import...
	d. Select General > Existing Projects into Workspace
	e. Press "Next"
	f. In the "Select root directory" field, browse to AssignmentSystem.model
	g. Make sure the project is checked
	h. Click "Finish"

2. Generating model code

	a. Navigate to model/model.genmodel
	b. Right-click on Model
	c. Click on "Generate Model Code"

3. Create a Run configuration

	a. Open Run Configurations (Run > Run Configurations from the menu bar)
	b. Right-click Java Application and select New
	c. Set name to ```Main```
	d. Set project to ```AssignmentSystem.model```
	e. Set main class to ```Main```
	f. Click "Run" and confirm that it compiles

4. Exporting to JAR

	a. Right-click the project and select Export
	b. Select Java > Runnable JAR file
	c. Click "Next"
	d. Set launch configuration to ```Main```
	e. Set export destination to ```AssignmentModel/lib```
	f. Check ```Save an ANT script```
	g. Click "Finish"

5. Set up automatic building

	a. Open Preferences
	b. ...
	c. Right-click project and select Properties
	d. Go to Builds
	e. Click "New"
	f. Select "Ant Builder"
	h. "Click OK"
	i. Set name ```Model Builder```
	j. Set buildfile to the generated ANT-file
	k. Set base directory to the root folder (folder containing README.md)
	l. Click "OK"

6. Setting up IntelliJ

	a. Generate IDEA-files (```play idea```)
	b. Start IntelliJ
	c. Select "Open Project"
	d. Navigate to "AssignmentSystem"
