package editorModulesDefinitions;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

public class StainingRobotCommandGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	JTextField pathInstructionList = new JTextField("");
	
	private static String name = "StainingRobotCommand";
	
	public StainingRobotCommandGUI(MainFrameEditor mfe) {
		super(mfe);
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public StainingRobotCommandGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box verticalBox = Box.createVerticalBox();
		
		Box horizontalBox2 = Box.createHorizontalBox();
		horizontalBox2.add(new JLabel("Path To Staining-Robot Instruction List:"));
		horizontalBox2.add(Box.createHorizontalGlue());
		verticalBox.add(horizontalBox2);
		verticalBox.add(Box.createVerticalStrut(20));
		Box horizontalBox = Box.createHorizontalBox();
		//horizontalBox.add(Box.createHorizontalStrut(20));
		horizontalBox.add(Utility.setFormatTextFields(pathInstructionList,400,20,30));
		verticalBox.add(horizontalBox);
		retPanel.add(verticalBox);
		
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new StainingRobotCommandGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[1];
		tempString[0] = pathInstructionList.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		pathInstructionList.setText(tempString[0]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof StainingRobotCommandGUI){
			StainingRobotCommandGUI returnObject = new StainingRobotCommandGUI(mfe);
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
