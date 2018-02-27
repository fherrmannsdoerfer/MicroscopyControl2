
package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

public class AddSolutionFromVialToSampleGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "AddSolutionToSample";
	private JTextField vialNumber = new JTextField("-1");
	private JTextField volume = new JTextField("300");
	
	public AddSolutionFromVialToSampleGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public AddSolutionFromVialToSampleGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(2, 2,60,15));
		retPanel.add(new JLabel("Vial Number From Rack 3:"));
		retPanel.add(vialNumber);
		retPanel.add(new JLabel("Volume [Microliter]:"));
		retPanel.add(volume);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AddSolutionFromVialToSampleGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[2];
		tempString[0] = vialNumber.getText();
		tempString[1] = volume.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		vialNumber.setText(tempString[0]);
		volume.setText(tempString[1]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof AddSolutionFromVialToSampleGUI){
			AddSolutionFromVialToSampleGUI returnObject = new AddSolutionFromVialToSampleGUI(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
	
	private int getVialNumber() {
		int intVialNumber = Integer.parseInt(vialNumber.getText());
		if (intVialNumber<1 || intVialNumber>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return intVialNumber;
		}
		
	}
	
	private int getVolume() {
		int intVolume = Integer.parseInt(volume.getText());
		if (intVolume<1 || intVolume>1000) {
			System.err.println("Volume is not within limits of 1 to 1000!");
			return -1;
		} else {
			return intVolume;
		}
		
	}
	
	

	@Override
	public void perform() {
		// TODO Auto-generated method stub
		Utility.createSampleListForSolutionAdding(getVialNumber(), getVolume(),mfe.getMainFrameReference().getXYStagePosition(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
	}

}
