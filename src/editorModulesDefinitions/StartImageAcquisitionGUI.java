package editorModulesDefinitions;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;


public class StartImageAcquisitionGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField emGain = new JTextField("10");
	JTextField exposureTime = new JTextField("30");
	JTextField numberOfFrames = new JTextField("20000");
	JCheckBox frameTransfer = new JCheckBox("Frame Transfer");
	transient MainFrameEditor mfe;
	
	private static String name = "StartImageAcquisition";
	
	public StartImageAcquisitionGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public StartImageAcquisitionGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		emGain = Utility.setFormatTextFields(emGain,30,20,3);
		exposureTime = Utility.setFormatTextFields(exposureTime,30,20,3);
		numberOfFrames = Utility.setFormatTextFields(numberOfFrames,30,20,3);
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(4, 2,60,15));
		
		retPanel.add(new JLabel("EM Gain:"));
		retPanel.add(emGain);
		retPanel.add(new JLabel("Exposure Time:"));
		retPanel.add(exposureTime);
		retPanel.add(new JLabel("Number Of Frames:"));
		retPanel.add(numberOfFrames);
		retPanel.add(new JLabel(""));
		retPanel.add(frameTransfer);
		
		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new StartImageAcquisitionGUI(mfe);
	}

	public String[] getSettings(){
		String statusChkBox = "";
		String statusChkBox2 = "";
		if (frameTransfer.isSelected()){
			statusChkBox = "selected";
		}
		else{
			statusChkBox = "notSelected";
		}
		
		String[] tempString = new String[4];
		tempString[0] = statusChkBox;
		tempString[1] = emGain.getText();
		tempString[2] = exposureTime.getText();
		tempString[3] = numberOfFrames.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		if (tempString[0].equals("selected")){
			frameTransfer.setSelected(true);
		}
		else{
			frameTransfer.setSelected(false);
		}
		emGain.setText(tempString[1]);
		exposureTime.setText(tempString[2]);
		numberOfFrames.setText(tempString[3]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof StartImageAcquisitionGUI){
			StartImageAcquisitionGUI returnObject = new StartImageAcquisitionGUI(mfe);
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
		mfe.getMainFrameReference().startSequenceAcquisition();
		System.out.println("Acquisition has started!");
		while (mfe.getMainFrameReference().isAcquisitionRunning()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Acquisition has stopped!");
	}
}
