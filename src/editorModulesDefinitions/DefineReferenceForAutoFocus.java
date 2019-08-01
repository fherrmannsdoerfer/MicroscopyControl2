package editorModulesDefinitions;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataTypes.CameraParameters;
import dataTypes.XYStagePosition;
import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;
import microscopeControl.MainFrame;

//for detailed comments look at performMeasurementGUI
public class DefineReferenceForAutoFocus extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "Set Reference for Autofocus";
	JButton getCurrentPosition = new JButton("Define Current Position As Reference:");
	JLabel currentPositionLabel = new JLabel("position not yet set");
		
	public DefineReferenceForAutoFocus(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorAutoFocus());
		this.setOptionPanel(createOptionPanel());
	}
	
	public DefineReferenceForAutoFocus(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(1,2,60,15));
		retPanel.add(getCurrentPosition);
		retPanel.add(currentPositionLabel);
		getCurrentPosition.addActionListener(new GetCurrentPosition_ActionListener());
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new DefineReferenceForAutoFocus(mfe);
	}

	@Override
	public String[] getSettings() {
		//tempString[1] and tempString[2] are used to save and restore the xy position
		String[] tempString = new String[3];
		tempString[0] = currentPositionLabel.getText();
		try {
		tempString[1] = String.valueOf(mfe.getMainFrameReference().getReferencePosition().getxPos());
		tempString[2] = String.valueOf(mfe.getMainFrameReference().getReferencePosition().getyPos());
		} catch (NullPointerException e) {
			tempString[1] = "not set yet";
			tempString[2] = "not set yet";
		}
		return tempString;
	}
	

	@Override
	public void setSettings(String[] tempString) {
		currentPositionLabel.setText(tempString[0]);
		try {
			XYStagePosition xyPos = new XYStagePosition(Double.valueOf(tempString[1]),Double.valueOf(tempString[2]));
			mfe.getMainFrameReference().setReferencePosition(xyPos);
		} catch (NumberFormatException e) {
			System.err.println("No reference point set so far.");
		}
	}

	
	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof DefineReferenceForAutoFocus){
			DefineReferenceForAutoFocus returnObject = new DefineReferenceForAutoFocus(mfe);
			return returnObject;
		}
		return null;
	}
	

	@Override
	public String getFunctionName() {
		return name;
	}
			

	@Override
	public void perform() {
		logTimeStart();
		MainFrame mf = mfe.getMainFrameReference();
		XYStagePosition refPoint = mf.getReferencePosition();
		mf.moveXYStage(refPoint.getxPos(), refPoint.getyPos());
		mf.setPathForMeasurment(mf.getReferencePositionOutputPath());
		mf.setMeasurementTag(mf.getReferencePositionOutputTag());
		CameraParameters camParam = new CameraParameters(mf.getExposureTimeForAutofocus(), mf.getGainForAutofocus(), mf.getNbrFramesForAutofocus(), true);
		mf.setCameraParameter(camParam);
		mf.setFocusLockState(true);
		mf.sleep(1000);
		mf.setLastMirrorPosition(mf.getMirrorPosition());
		
		Utility.deleteFolder(new File(mf.getReferencePositionOutputPath()+"\\"+mf.getReferencePositionOutputTag()));
		
		mf.setAction("Capture Reference Image");

		mf.setFilterWheelPosition(mf.getFilterWheelPositionForAutoFocus());
		mf.setLaserIntensity(mf.getLaserIndexForAutoFocus(), mf.getLaserPowerForAutoFocus());
		mf.sleep(200);
		mf.startSequenceAcquisition(false);
		while (mf.isAcquisitionRunning()){
			mf.sleep(200);
		}
		
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (currentPositionLabel.getText().isEmpty()) {
			return false;
		}
		return true;
	}

	
	class GetCurrentPosition_ActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try {
				mfe.getMainFrameReference().setReferencePosition(mfe.getMainFrameReference().getXYStagePosition());
				currentPositionLabel.setText(mfe.getMainFrameReference().getReferencePosition().toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	};
}
