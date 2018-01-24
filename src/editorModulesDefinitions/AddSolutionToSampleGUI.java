package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

public class AddSolutionToSampleGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	MainFrameEditor mfe;
	private static String name = "AddSolutionToSample";
	
	public AddSolutionToSampleGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public AddSolutionToSampleGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AddSolutionToSampleGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[0];
		
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof AddSolutionToSampleGUI){
			AddSolutionToSampleGUI returnObject = new AddSolutionToSampleGUI(mfe);
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
		// TODO Auto-generated method stub
		String fname = Utility.ChooseSampleListBasedOnStagePositionForAddingSolution(mfe.getMainFrameReference().getXYStagePosition());
		System.out.println(fname);
		setProgressbarValue(100);
	}

}
