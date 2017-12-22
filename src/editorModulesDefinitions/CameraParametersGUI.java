package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataTypes.CameraParameters;
import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;



public class CameraParametersGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox shutterPosition;
	JTextField emGain = new JTextField("10");
	JTextField exposureTime = new JTextField("100");
	JTextField nbrFrames = new JTextField("20000");
	transient MainFrameEditor mfe;
	private static String name = "CameraParametersGUI";
	String[] dummyLaserNames = {"laser1","laser2","laser3","laser4"};
	
	public CameraParametersGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public CameraParametersGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		emGain = Utility.setFormatTextFields(emGain,30,20,3);
		exposureTime = Utility.setFormatTextFields(exposureTime,30,20,3);
		nbrFrames = Utility.setFormatTextFields(nbrFrames,30,20,3);

		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(4, 2,60,15));
		shutterPosition = new JComboBox();
		shutterPosition.addItem("open");
		shutterPosition.addItem("close");
		//laserSelection = new JComboBox(dummyLaserNames);
		retPanel.add(new JLabel("Exposure Time:"));
		retPanel.add(exposureTime);
		retPanel.add(new JLabel("EM Gain:"));
		retPanel.add(emGain);
		retPanel.add(new JLabel("Shutter State:"));
		retPanel.add(shutterPosition);
		retPanel.add(new JLabel("Number Frames:"));
		retPanel.add(nbrFrames);
		
		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new CameraParametersGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[4];
		tempString[0] = String.valueOf(shutterPosition.getSelectedIndex());
		tempString[1] = exposureTime.getText();
		tempString[2] = emGain.getText();
		tempString[3] = nbrFrames.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		shutterPosition.setSelectedIndex(Integer.parseInt(tempString[0]));
		exposureTime.setText(tempString[1]);
		emGain.setText(tempString[2]);
		nbrFrames.setText(tempString[3]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof CameraParametersGUI){
			CameraParametersGUI returnObject = new CameraParametersGUI(mfe);
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
		CameraParameters camParam;
		if (shutterPosition.getSelectedIndex() == 0){
			camParam = new CameraParameters(Double.parseDouble(Utility.parseParameter(exposureTime.getText(), mfe)),Integer.parseInt(Utility.parseParameter(emGain.getText(), mfe)), Integer.parseInt(Utility.parseParameter(nbrFrames.getText(), mfe)),true);
		}
		else{
			camParam = new CameraParameters(Double.parseDouble(Utility.parseParameter(exposureTime.getText(), mfe)),Integer.parseInt(Utility.parseParameter(emGain.getText(), mfe)), Integer.parseInt(Utility.parseParameter(nbrFrames.getText(), mfe)),false);
		}
		mfe.getMainFrameReference().setCameraParameter(camParam);
	}
}
