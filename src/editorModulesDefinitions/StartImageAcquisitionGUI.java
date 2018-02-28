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
	transient MainFrameEditor mfe;
	JCheckBox applyChecks = new JCheckBox("Apply Checks For Overwriting, ROI, etc..");
	
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
		
		JPanel retPanel = new JPanel();
		retPanel.add(applyChecks);
		
		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new StartImageAcquisitionGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[1];
		if (applyChecks.isSelected()) {
			tempString[0] = "selected";
		}
		else {
			tempString[0]="42";
		}
		return tempString;
	}
	public void setSettings(String[] tempString){
		if (tempString[0].contains("selected")) {
			applyChecks.setSelected(true);
		} else {
			applyChecks.setSelected(false);
		}
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
		mfe.getMainFrameReference().startSequenceAcquisition(applyChecks.isSelected());
		System.out.println("Acquisition has started!");
		while (mfe.getMainFrameReference().isAcquisitionRunning()){
			try {
				Thread.sleep(100);
				setProgressbarValue((int) (mfe.getMainFrameReference().getCurrentFrame()/(1.*mfe.getMainFrameReference().getNumberFramesForCurrentAcquisition())*100));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setProgressbarValue(100);
		System.out.println("Acquisition has stopped!");
	}

	@Override
	public boolean checkForValidity() {
		// TODO Auto-generated method stub
		return true;
	}
}
