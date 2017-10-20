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

public class LoopIterableGUI extends LoopModules{
	
	private static final long serialVersionUID = 1L;
	JTextField iterationTag = new JTextField("%Tag?%");
	
	private static String name = "LoopIterable";
	EditorModules endLoop = new EndLoopGUI(this);
	
	public LoopIterableGUI(MainFrameEditor mfe) {
		super(mfe);
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorLoop());
		this.setOptionPanel(createOptionPanel());
	}
	
	public LoopIterableGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(1, 2,60,15));
		retPanel.add(new JLabel("Number of iterations:"));
		iterationTag = Utility.setFormatTextFields(iterationTag, 30, 20, 5);
		retPanel.add(iterationTag);
		return retPanel;
	}
	
	@Override
	public EditorModules getEndLoopModule(MainFrameEditor mfe){
		return new EndLoopGUI(mfe);
	}

	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new LoopIterableGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[1];
		tempString[0] = iterationTag.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		iterationTag.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof LoopIterableGUI){
			LoopIterableGUI returnObject = new LoopIterableGUI(mfe);
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
