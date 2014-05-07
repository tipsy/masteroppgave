# Assignment System

Master's thesis by Christian Rasmussen and David Åse.

## Installation

### Setting up Eclipse

1. Start Eclipse
2. Set workspace to the root of project (the folder containing README.md)
3. Open File > Import...
4. Select General > Existing Projects into Workspace
5. Press "Next"
6. In the "Select root directory" field, browse to AssignmentSystem.model
7. Make sure the project is checked
8. Click "Finish"

### Installing Java 8 plugin

1. Go to Help
2. Open "Eclipse Marketplace"
3. Search for ```java 8 kepler```
4. Install "Java 8 support for Eclipse Kepler SR2"
5. Complete the wizard.

### Installing Maven plugin

1. Go to Help
2. Open "Eclipse Marketplace"
3. Search for ```maven 1.4```
4. Install "Maven Integration for Eclipse (Juno or newer) 1.4"
5. Complete the wizard.

### Generating model code

1. Navigate to model/model.genmodel
2. Right-click on Model
3. Click on "Generate Model Code"

### Create a Run configuration

1. Open Run Configurations (Run > Run Configurations from the menu bar)
2. Right-click Java Application and select New
3. Set name to ```Main```
4. Set project to ```AssignmentSystem.model```
5. Set main class to ```Main```
6. Click "Run" and confirm that it compiles

### Exporting to JAR

1. Right-click the project and select Export
2. Select Java > Runnable JAR file
3. Click "Next"
4. Set launch configuration to ```Main```
5. Set export destination to ```AssignmentModel/lib```
6. Check ```Save an ANT script```
7. Click "Finish"

### Set up automatic building

1. Open Preferences
2. <...>
3. Right-click project and select Properties
4. Go to Builds
5. Click "New"
6. Select "Ant Builder"
7. "Click OK"
8. Set name ```Model Builder```
9. Set buildfile to the generated ANT-file
10. Set base directory to the root folder (folder containing README.md)
11. Click "OK"

### Setting up IntelliJ

1. Generate IDEA-files (```play idea```)
2. Start IntelliJ
3. Select "Open Project"
4. Navigate to "AssignmentSystem"
