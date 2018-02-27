package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

public class RemoveSolutionFromSampleGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	JTextField volumePerSpot = new JTextField("200");
	transient MainFrameEditor mfe;
	private static String name = "RemoveSolutionFromSample";
	
	public RemoveSolutionFromSampleGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public RemoveSolutionFromSampleGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(1, 2,60,15));
		retPanel.add(new JLabel("Volume Per Spot [Microliter]:"));
		retPanel.add(volumePerSpot);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new RemoveSolutionFromSampleGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[1];
		tempString[0] = volumePerSpot.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		volumePerSpot.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof RemoveSolutionFromSampleGUI){
			RemoveSolutionFromSampleGUI returnObject = new RemoveSolutionFromSampleGUI(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}

	private int getVolumePerSpot() {
		int volume = Integer.parseInt(volumePerSpot.getText());
		if (volume<1 || volume>250) {
			System.err.println("Volume per spot must be in range of 1 to 250!");
			return -1;
		} else {
			return volume;
		}
	}
	@Override
	public void perform() {
		Utility.createSampleListForSolutionRemoval(getVolumePerSpot(), mfe.getMainFrameReference().getXYStagePosition(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
	}

}
