package editorModulesDefinitions;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataTypes.CameraParameters;
import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

//find more detailed comments in this class
public class PerformMeasurmentGUI extends EditorModules{
	/**
	 * 
	 */
	//In this part all member variables are defined
	//JTextFields are objects where the user can type in numbers or chars
	//In general all components have a type (JTextField, JCheckBox, etc) and
	//a name (measurementPath, measurementTag, laserIndex, etc)
	//A variable is created with the expression "type variablename" (e.g. JTextField measurementPath)
	//on the right side of the equal sign the variable gets its content.
	//e.g. new JTextField("test") creates an object of type JTextField with
	//test written in the text field. All lines end with a semicolon.
	private static final long serialVersionUID = 1L;
	JTextField measurementPath = new JTextField("D:\\Measurements\\Default\\");
	JTextField measurementTag = new JTextField("MessungX");
	JTextField targetPosition = new JTextField("0<->0");
	JTextField laserIndex = new JTextField("4");
	JTextField laserPowerWf = new JTextField("1.6");
	JTextField laserPowerMeasurement = new JTextField("100");
	JTextField filterWheelIndex = new JTextField("3");
	JTextField exposureTimeWF = new JTextField("200");
	JCheckBox bleachSampleBeforeMeasurement = new JCheckBox("Bleach Before Measurement");
	JTextField bleachTime = new JTextField("10000");
	JTextField exposureTimeMeasurement = new JTextField("30");
	JTextField nbrFrames = new JTextField("20000");
	JCheckBox doSimultaneousReconstruction = new JCheckBox("Simulatneous Reconstruction");
	JCheckBox recon3D = new JCheckBox("3D");
	JTextField pathToCalib = new JTextField("PathToCalibFile3D");
	JTextField cameraGain = new JTextField("10");
	JCheckBox movePosition = new JCheckBox("Change Position");
	JCheckBox moveFocus = new JCheckBox("Move Focus");
	JTextField focusMirrorPos = new JTextField("0");
	JCheckBox enableUVLaserControl = new JCheckBox("Enable UV Laser Control");
	JTextField minimalNbrBlinkingEvents = new JTextField("10");
	//MainFrameEditor reference to communicate with the rest of the editor and with 
	//the MainFrame class from the microscopeControl software
	//the transient property is needed to indicate that this part of the module is 
	//not saved.
	transient MainFrameEditor mfe;
	//Name of the module which will be shown in the combobox and on the option button
	private static String name = "Perform Measurement";
	
	//Constructor of this class. This code is executed whenever a new object of the type PerformMeasurementGUI is created
	public PerformMeasurmentGUI(MainFrameEditor mfe) {
		//first line in the constructor of a subclass must be the call for the constructor of the parent class
		super(mfe);
		//copy the MainFrameEditor reference to the member variable to be used within the module
		this.mfe = mfe;
		//set the text for the parameter button to be the same as the modules name
		this.setParameterButtonsName(name);
		//set the color for the module, get the information from the style class
		this.setColor(mfe.style.getColorMicroscope());
		//this sets the option panel which is created by the createOptionPanel() method
		this.setOptionPanel(createOptionPanel());
	}
	
	public PerformMeasurmentGUI(){
		
	}
	
	//in this method the option panel is created. The option panel is what is shown in the right
	//column of the experiment editor once the parameter button of a certain module is pressed.
	//each module has its own option panel but in some cases the option panel is empty or does only
	//contain text. All components have to be added in order to be displayed. 
	private JPanel createOptionPanel(){
		
		//get the path of the calibration file for 3D astigmatism measurements from the MainFrame class
		//of the microscope control software.
		pathToCalib.setText(mfe.getMainFrameReference().getPathTo3DCalibrationFileRapidStorm());
		//the local name of the variable of the option panel is retPanel which is created here 
		JPanel retPanel = new JPanel();
		//the layout is set to  GridLayout, which organizes the components in a table like fashion.
		//the first parameter defines the number of rows, the second the number of columns, the third
		//the horizontal gap between the individual components and the forth the vertical gap
		//All components are filled in from left to right and from top to bottom.
		//the number of added components should match the number of cells exactly
		//All columns have the same width.
		retPanel.setLayout(new GridLayout(21,2,10,15));
		
		//the first entry of the first row is added, in general the left column is used
		//for labels the right column is used for the textfields and checkboxes
		retPanel.add(new JLabel("Path:"));
		//The width of the textfield is limited otherwise the column would expand with longer
		//paths
		Utility.setFormatTextFields(measurementPath, 120, 20, 5);
		//The first textfield is added on the second column
		retPanel.add(measurementPath);
		retPanel.add(new JLabel("Measurement Tag:"));
		retPanel.add(measurementTag);
		retPanel.add(new JLabel(""));
		retPanel.add(movePosition);
		retPanel.add(new JLabel("Target Position:"));
		retPanel.add(targetPosition);
		//Checkboxes have their own description therefore and empty label is added in order to 
		//keep the structure of the table 
		retPanel.add(new JLabel(""));
		retPanel.add(moveFocus);
		retPanel.add(new JLabel("Focus Mirror Position:"));
		retPanel.add(focusMirrorPos);
		retPanel.add(new JLabel("Laser Index:"));
		retPanel.add(laserIndex);
		retPanel.add(new JLabel("Laser Power Widefield [Milliwatt]:"));
		retPanel.add(laserPowerWf);
		retPanel.add(new JLabel("Laser Power Measuremnt [Milliwatt]:"));
		retPanel.add(laserPowerMeasurement);
		retPanel.add(new JLabel("Filter Wheel Position:"));
		retPanel.add(filterWheelIndex);
		retPanel.add(new JLabel("Exp. Time Widefield Image [Milliseconds]:"));
		retPanel.add(exposureTimeWF);
		retPanel.add(new JLabel(""));
		retPanel.add(bleachSampleBeforeMeasurement);
		retPanel.add(new JLabel("Bleach Time [Milliseconds]:"));
		retPanel.add(bleachTime);
		retPanel.add(new JLabel("Exp. Time Measurement Image [Milliseconds]:"));
		retPanel.add(exposureTimeMeasurement);
		retPanel.add(new JLabel("EM Gain:"));
		retPanel.add(cameraGain);
		retPanel.add(new JLabel("Number Frames Measurement:"));
		retPanel.add(nbrFrames);
		retPanel.add(new JLabel(""));
		retPanel.add(doSimultaneousReconstruction);
		retPanel.add(new JLabel(""));
		retPanel.add(recon3D);
		retPanel.add(new JLabel("Path To 3D Calibration:"));
		Utility.setFormatTextFields(pathToCalib, 120, 20, 5);
		retPanel.add(pathToCalib);
		retPanel.add(new JLabel(""));
		retPanel.add(enableUVLaserControl);
		retPanel.add(new JLabel("Minimal Nbr. Of BlinkingEvents:"));
		retPanel.add(minimalNbrBlinkingEvents);
		
		//whenever the move focus checkbox is selected the corresponding textfield should be active
		//if the checkbox is not selected the focusMirrorPos textfield should be grey and disabled
		//this behaviour is realized by adding ActionListener to the checkboxes. Whenever the checkbox
		//is selected or deselected the actionPerformed method of the ActionListener is executed.
		//in this case it simply switches the focusMirrorPos is enabled or disabled according to the state of the checkbox
		moveFocus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				focusMirrorPos.setEnabled(moveFocus.isSelected());
			}
			
		});
		
		movePosition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				targetPosition.setEnabled(movePosition.isSelected());
			}
		});
		//this line selects the checkbox by default
		movePosition.setSelected(true);
		bleachSampleBeforeMeasurement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bleachTime.setEnabled(bleachSampleBeforeMeasurement.isSelected());
			}
		});
		bleachSampleBeforeMeasurement.setSelected(true);
		doSimultaneousReconstruction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recon3D.setEnabled(doSimultaneousReconstruction.isSelected());
				pathToCalib.setEnabled(doSimultaneousReconstruction.isSelected());
			}
		});
		doSimultaneousReconstruction.setSelected(true);
		recon3D.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pathToCalib.setEnabled(recon3D.isSelected());
			}
		});
		recon3D.setSelected(true);
		
		enableUVLaserControl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				minimalNbrBlinkingEvents.setEnabled(enableUVLaserControl.isSelected());
			}
		});
		enableUVLaserControl.setSelected(false);
		minimalNbrBlinkingEvents.setEnabled(false);
		
		//in the end the option panel gets returned and will be set as option panel
		return retPanel;
	}
	
	//This method can be copied for new modules only the class name has to be altered (PerformMeasurementGUI must be changed in the name of the new class)	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new PerformMeasurmentGUI(mfe);
	}

	//This method is used to store the user set settings as an array of Strings
	//all settings are expressed as Strings, the text fields in the obvious fashion
	//for checkboxes the String selected is saved in the case of the selected checkbox
	public String[] getSettings(){
		//if the number of parameters is altered also the number of elements in the
		//String array has to be adjusted. Each parameter and each checkbox counts as one
		String[] tempString = new String[21];
		//indexing starts at 0 so an array with 21 entries is composed of 
		//tempString[0], tempString[1], ... , tempString[20]!
		tempString[0] = measurementPath.getText();
		tempString[1] = measurementTag.getText();
		tempString[2] = targetPosition.getText();
		tempString[3] = laserIndex.getText();
		tempString[4] = laserPowerWf.getText();
		tempString[5] = laserPowerMeasurement.getText();
		tempString[6] = filterWheelIndex.getText();
		tempString[7] = exposureTimeWF.getText();
		tempString[8] = bleachTime.getText();
		tempString[9] = exposureTimeMeasurement.getText();
		tempString[10] = nbrFrames.getText();
		tempString[11] = pathToCalib.getText();
		tempString[12] = cameraGain.getText();
		if (bleachSampleBeforeMeasurement.isSelected()){
			tempString[13] = "selected";
		}
		else {
			tempString[13] = "notSelected";
		}
		if (doSimultaneousReconstruction.isSelected()){
			tempString[14] = "selected";
		}
		else {
			tempString[14] = "notSelected";
		}
		if (recon3D.isSelected()){
			tempString[15] = "selected";
		}
		else {
			tempString[15] = "notSelected";
		}
		if (movePosition.isSelected()){
			tempString[16] = "selected";
		}
		else {
			tempString[16] = "notSelected";
		}
		if (moveFocus.isSelected()){
			tempString[17] = "selected";
		}
		else {
			tempString[17] = "notSelected";
		}
		tempString[18] = focusMirrorPos.getText();
		if (enableUVLaserControl.isSelected()){
			tempString[19] = "selected";
		}
		else {
			tempString[19] = "notSelected";
		}
		tempString[20] = minimalNbrBlinkingEvents.getText();
		return tempString;
	}
	
	//This function is used to recreate the option panel from the saved values
	//all the parameters have to be filled in in the same variable as they were saved from
	//otherwise parameters will get mixed up
	public void setSettings(String[] tempString){
		//For textfield the text is just overwritten with the saved text
		measurementPath.setText(tempString[0]);
		measurementTag.setText(tempString[1]);
		targetPosition.setText(tempString[2]);
		laserIndex.setText(tempString[3]);
		laserPowerWf.setText(tempString[4]);
		laserPowerMeasurement.setText(tempString[5]);
		filterWheelIndex.setText(tempString[6]);
		exposureTimeWF.setText(tempString[7]);
		bleachTime.setText(tempString[8]);
		exposureTimeMeasurement.setText(tempString[9]);
		nbrFrames.setText(tempString[10]);
		pathToCalib.setText(tempString[11]);
		cameraGain.setText(tempString[12]);
		//for checkboxes there is a if-else block to either select or deselect the
		//checkbox based on the stored state
		if (tempString[13].equals("selected")){
			bleachSampleBeforeMeasurement.setSelected(true);
		}else {
			bleachSampleBeforeMeasurement.setSelected(false);
		}
		if (tempString[14].equals("selected")){
			doSimultaneousReconstruction.setSelected(true);
		}else {
			//if action listener are connected to a checkbox and additional fields or
			//checkboxes should be dissabled this is the place to to that
			doSimultaneousReconstruction.setSelected(false);
			recon3D.setEnabled(false);
			movePosition.setEnabled(false);
		}
		if (tempString[15].equals("selected")){
			recon3D.setSelected(true);
		}else {
			recon3D.setSelected(false);
			movePosition.setEnabled(false);
		}
		if (tempString[16].equals("selected")){
			movePosition.setSelected(true);
		}else {
			movePosition.setSelected(false);
			targetPosition.setEnabled(false);
		}
		if (tempString[17].equals("selected")){
			moveFocus.setSelected(true);
		}else {
			moveFocus.setSelected(false);
			focusMirrorPos.setEnabled(false);
		}
		focusMirrorPos.setText(tempString[18]);
		if (tempString[19].equals("selected")){
			enableUVLaserControl.setSelected(true);
			minimalNbrBlinkingEvents.setEnabled(true);
		}else {
			minimalNbrBlinkingEvents.setEnabled(false);
		}
		minimalNbrBlinkingEvents.setText(tempString[20]);
	}

	//this method can be copied when new modules are created but the classname has to be replaced with the new one
	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof PerformMeasurmentGUI){
			PerformMeasurmentGUI returnObject = new PerformMeasurmentGUI(mfe);
			return returnObject;
		}
		return null;
	}

	//this method can directly be copied, it will return the name of the module
	@Override
	public String getFunctionName() {
		return name;
	}

	//special function only needed for the handling of coordinates, this does not have to be copied unless
	//coordinates shall be processed
	private double parseStageCoordinates(String coords, int index) {
		String[] parts =coords.split("<->");
		return Double.valueOf(parts[index]);
	}

	//this method initiates all the action once the module gets executed.
	//in case of modules that control the microscope hardware only all instructions
	//will be written in here. In this example there are many different tasks to perform
	//for an easier example have a look at other modules from the "Microscope Functions" selection
	@Override
	public void perform() {
		logTimeStart();
		//move if necessary
		if (movePosition.isSelected()) {
			mfe.getMainFrameReference().moveXYStage(parseStageCoordinates(Utility.parseParameter(targetPosition.getText(), mfe),0), parseStageCoordinates(Utility.parseParameter(targetPosition.getText(), mfe),1));
		}
		if (moveFocus.isSelected()) {
			mfe.getMainFrameReference().setMirrorPosition(Double.parseDouble(Utility.parseParameter(focusMirrorPos.getText(), mfe)));
		}
		if (enableUVLaserControl.isSelected()) {
			mfe.getMainFrameReference().setEnableUVControlState(true);
			mfe.getMainFrameReference().setMinimalBlinkingNbr(Integer.parseInt(Utility.parseParameter(minimalNbrBlinkingEvents.getText(), mfe)));
		}
		//set output Paths
		mfe.getMainFrameReference().setPathForMeasurment(Utility.parseParameter(measurementPath.getText(), mfe));
		mfe.getMainFrameReference().setMeasurementTag(Utility.parseParameter(measurementTag.getText(), mfe));
		//set Filterwheel
		mfe.getMainFrameReference().setFilterWheelPosition(Integer.parseInt(Utility.parseParameter(filterWheelIndex.getText(), mfe))-1);
		mfe.getMainFrameReference().pauseThread(5000);
		//open shutter
		mfe.getMainFrameReference().openShutter();
		mfe.getMainFrameReference().pauseThread(5000);
		//turn on laser for WF image
		mfe.getMainFrameReference().setLaserIntensity(Integer.parseInt(Utility.parseParameter(laserIndex.getText(), mfe))-1, Double.parseDouble(Utility.parseParameter(laserPowerWf.getText(),mfe)));
		//capture wf image
		mfe.getMainFrameReference().captureAndStoreWidefieldImage(Double.parseDouble(Utility.parseParameter(exposureTimeWF.getText(), mfe)));
		CameraParameters camParam = new CameraParameters(Integer.parseInt(Utility.parseParameter(exposureTimeMeasurement.getText(), mfe)), Integer.parseInt(Utility.parseParameter(cameraGain.getText(), mfe)), Integer.parseInt(Utility.parseParameter(nbrFrames.getText(), mfe)), true);
		mfe.getMainFrameReference().setStateDo3DReconstruction(recon3D.isSelected());
		mfe.getMainFrameReference().setStateDoSimulatneousReconstruction(doSimultaneousReconstruction.isSelected());
		mfe.getMainFrameReference().setCameraParameter(camParam);
		//close shutter
		mfe.getMainFrameReference().setPathTo3DCalibrationFileRapidStorm(pathToCalib.getText());
		if (bleachSampleBeforeMeasurement.isSelected()) {
			mfe.getMainFrameReference().closeShutter();
			mfe.getMainFrameReference().pauseThread(5000);
			mfe.getMainFrameReference().setLaserIntensity(Integer.parseInt(Utility.parseParameter(laserIndex.getText(), mfe))-1, Double.parseDouble(Utility.parseParameter(laserPowerMeasurement.getText(),mfe)));
			mfe.getMainFrameReference().pauseThread(Integer.parseInt(Utility.parseParameter(bleachTime.getText(), mfe)));
			mfe.getMainFrameReference().openShutter();
			mfe.getMainFrameReference().pauseThread(5000);
		} else {
			mfe.getMainFrameReference().setLaserIntensity(Integer.parseInt(Utility.parseParameter(laserIndex.getText(), mfe))-1, Double.parseDouble(Utility.parseParameter(laserPowerMeasurement.getText(),mfe)));
		}
		mfe.getMainFrameReference().startSequenceAcquisition(false);
		while (mfe.getMainFrameReference().isAcquisitionRunning()){
			mfe.getMainFrameReference().pauseThread(100);
			setProgressbarValue((int) (mfe.getMainFrameReference().getCurrentFrame()/(1.*mfe.getMainFrameReference().getNumberFramesForCurrentAcquisition())*100));
		}
		//turn off laser
		mfe.getMainFrameReference().setLaserIntensity(Integer.parseInt(Utility.parseParameter(laserIndex.getText(), mfe))-1, 0.1);
		//and UV Laser
		mfe.getMainFrameReference().setLaserIntensity(0, 0.1);
		mfe.getMainFrameReference().setEnableUVControlState(false);
		setProgressbarValue(100);
		logTimeEnd();
	}

	//This method is used to do a coarse check for missing parameters 
	@Override
	public boolean checkForValidity() {
		
		if(measurementPath.getText().isEmpty()||measurementTag.getText().isEmpty()||targetPosition.getText().isEmpty()||laserIndex.getText().isEmpty()||laserPowerWf.getText().isEmpty()||laserPowerMeasurement.getText().isEmpty()||filterWheelIndex.getText().isEmpty()||exposureTimeWF.getText().isEmpty()||bleachTime.getText().isEmpty()||exposureTimeMeasurement.getText().isEmpty()||nbrFrames.getText().isEmpty()||pathToCalib.getText().isEmpty()||cameraGain.getText().isEmpty()||minimalNbrBlinkingEvents.getText().isEmpty()) {
			return false;
		}
		else {return true;}

	}
}
