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
import editor.LoopModules;
import editor.MainFrameEditor;

public class LoopGUI extends LoopModules{
	
	private static final long serialVersionUID = 1L;
	JTextField numberRuns = new JTextField("1");
	
	private static String name = "Loop";
	EditorModules endLoop = new EndLoopGUI(this);
	
	public LoopGUI(MainFrameEditor mfe) {
		super(mfe);
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorLoop());
		this.setOptionPanel(createOptionPanel());
	}
	
	public LoopGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(1, 2,60,15));
		retPanel.add(new JLabel("Number of iterations:"));
		numberRuns = Utility.setFormatTextFields(numberRuns, 30, 20, 5);
		retPanel.add(numberRuns);

		
		return retPanel;
	}
	
	@Override
	public EditorModules getEndLoopModule(MainFrameEditor mfe){
		return new EndLoopGUI(mfe);
	}

	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new LoopGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[1];
		tempString[0] = numberRuns.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		numberRuns.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof LoopGUI){
			LoopGUI returnObject = new LoopGUI(mfe);
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
		
	}

}
