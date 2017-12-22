package editorModulesDefinitions;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	JCheckBox useVariableFromLoop = new JCheckBox("Use ROI From Loop");
	transient MainFrameEditor mfe;
	JTextField tagROILoop = new JTextField();
	private static String name = "MoveXYStage";
	
	public MoveStageGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public MoveStageGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		xPos = Utility.setFormatTextFields(xPos,30,20,3);
		yPos = Utility.setFormatTextFields(yPos,30,20,3);
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(5, 2,60,15));
		
		retPanel.add(new JLabel("X Position in nm:"));
		retPanel.add(xPos);
		retPanel.add(new JLabel("Y Position in nm:"));
		retPanel.add(yPos);
		retPanel.add(new JLabel(""));
		retPanel.add(useVariableFromLoop);
		useVariableFromLoop.addActionListener(new chkBoxActionListener());
		retPanel.add(new JLabel("Tag for ROI Loop:"));
		retPanel.add(tagROILoop);

		return retPanel;
	}
	
	class chkBoxActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			xPos.setEnabled(!useVariableFromLoop.isSelected());
			yPos.setEnabled(!useVariableFromLoop.isSelected());
		}
		
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new MoveStageGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[4];
		tempString[0] = xPos.getText();
		tempString[1] = yPos.getText();
		if (useVariableFromLoop.isSelected()){
			tempString[2] = "selected";
		}
		else {
			tempString[2] = "notSelected";
		}
		return tempString;
	}
	public void setSettings(String[] tempString){
		xPos.setText(tempString[0]);
		yPos.setText(tempString[1]);
		if (tempString[2].equals("selected")){
			useVariableFromLoop.setSelected(true);
		}
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
		if (useVariableFromLoop.isSelected()){
			mfe.getMainFrameReference().moveXYStage(Double.valueOf(Utility.parseParameter(tagROILoop.getText()+"X", mfe)), Double.valueOf(Utility.parseParameter(tagROILoop.getText()+"Y", mfe)));
		}
		else{
			mfe.getMainFrameReference().moveXYStage(Double.valueOf(Utility.parseParameter(xPos.getText(),mfe)), Double.valueOf(Utility.parseParameter(yPos.getText(),mfe)));
		}
	}
}
