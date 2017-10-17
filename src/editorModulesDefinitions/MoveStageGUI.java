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


public class MoveStageGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField xPos = new JTextField("0");
	JTextField yPos = new JTextField("0");
	JTextField zPos = new JTextField("0");
	
	private static String name = "MoveStage";
	
	public MoveStageGUI(MainFrameEditor mfe) {
		super(mfe);
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public MoveStageGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		xPos = Utility.setFormatTextFields(xPos,30,20,3);
		yPos = Utility.setFormatTextFields(yPos,30,20,3);
		zPos = Utility.setFormatTextFields(zPos,30,20,3);
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(3, 2,60,15));
		
		retPanel.add(new JLabel("X Position in nm:"));
		retPanel.add(xPos);
		retPanel.add(new JLabel("Y Position in nm:"));
		retPanel.add(yPos);
		retPanel.add(new JLabel("Z Position in nm:"));
		retPanel.add(zPos);

		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new MoveStageGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[3];
		tempString[0] = xPos.getText();
		tempString[1] = yPos.getText();
		tempString[2] = zPos.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		xPos.setText(tempString[1]);
		yPos.setText(tempString[2]);
		zPos.setText(tempString[3]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof MoveStageGUI){
			MoveStageGUI returnObject = new MoveStageGUI(mfe);
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
