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


public class PerformMeasurmentGUI extends EditorModules{
	/**
	 * 
	 */
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
	
	transient MainFrameEditor mfe;
	private static String name = "Perform Measurement";
	
	public PerformMeasurmentGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public PerformMeasurmentGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		
		pathToCalib.setText(mfe.getMainFrameReference().getPathTo3DCalibrationFileRapidStorm());
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(19,2,10,15));
		
		retPanel.add(new JLabel("Path:"));
		Utility.setFormatTextFields(measurementPath, 120, 20, 5);
		retPanel.add(measurementPath);
		retPanel.add(new JLabel("Measurement Tag:"));
		retPanel.add(measurementTag);
		retPanel.add(new JLabel(""));
		retPanel.add(movePosition);
		retPanel.add(new JLabel("Target Position:"));
		retPanel.add(targetPosition);
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

		return retPanel;
	}
	
			
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new PerformMeasurmentGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[19];
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
		return tempString;
	}
	public void setSettings(String[] tempString){
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
		if (tempString[13].equals("selected")){
			bleachSampleBeforeMeasurement.setSelected(true);
		}else {
			bleachSampleBeforeMeasurement.setSelected(false);
		}
		if (tempString[14].equals("selected")){
			doSimultaneousReconstruction.setSelected(true);
		}else {
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
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof PerformMeasurmentGUI){
			PerformMeasurmentGUI returnObject = new PerformMeasurmentGUI(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}

	private double parseStageCoordinates(String coords, int index) {
		String[] parts =coords.split("<->");
		return Double.valueOf(parts[index]);
	}

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
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		
		if(measurementPath.getText().isEmpty()||measurementTag.getText().isEmpty()||targetPosition.getText().isEmpty()||laserIndex.getText().isEmpty()||laserPowerWf.getText().isEmpty()||laserPowerMeasurement.getText().isEmpty()||filterWheelIndex.getText().isEmpty()||exposureTimeWF.getText().isEmpty()||bleachTime.getText().isEmpty()||exposureTimeMeasurement.getText().isEmpty()||nbrFrames.getText().isEmpty()||pathToCalib.getText().isEmpty()||cameraGain.getText().isEmpty()) {
			return false;
		}
		else {return true;}

	}
}
