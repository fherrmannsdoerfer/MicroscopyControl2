package editorModulesDefinitions;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.micromanager.api.PositionList;

import microscopeControl.MainFrame;
import utility.Utility;
import editor.EditorModules;
import editor.LoopModules;
import editor.MainFrameEditor;

public class LoopROIsGUI extends LoopModules{
	
	private static final long serialVersionUID = 1L;
	JLabel numberRuns = new JLabel("");
	JButton updateButton = new JButton("Update ROI List");
	MainFrameEditor mfe;
	MainFrame mf;
	
	private static String name = "LoopROIs";
	EditorModules endLoop = new EndLoopGUI(this);
	
	public LoopROIsGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.mf = mfe.getMainFrameReference();
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorLoop());
		this.setOptionPanel(createOptionPanel());
	}
	
	public LoopROIsGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(2, 2,60,15));
		retPanel.add(new JLabel("Number of iterations:"));
		PositionList list = mf.getPositionList();
		numberRuns.setText(String.valueOf(list.getNumberOfPositions()));
		retPanel.add(numberRuns);
		retPanel.add(new JLabel(""));
		retPanel.add(updateButton);
		JPanel dispList = new JPanel();
		dispList.setLayout(new GridLayout(list.getNumberOfPositions()+1,4));
		dispList.add(new JLabel("Position:"));
		dispList.add(new JLabel("X:"));
		dispList.add(new JLabel("Y:"));
		dispList.add(new JLabel("Z:"));
		for (int i = 0;i<list.getNumberOfPositions(); i++){
			dispList.add(new JLabel(String.valueOf(i)));
			dispList.add(new JLabel(String.valueOf(list.getPosition(i).getX())));
			dispList.add(new JLabel(String.valueOf(list.getPosition(i).getY())));
			dispList.add(new JLabel(String.valueOf(list.getPosition(i).getZ())));
		}
		retPanel.add(dispList);
		return retPanel;
	}
	
	@Override
	public EditorModules getEndLoopModule(MainFrameEditor mfe){
		return new EndLoopGUI(mfe);
	}

	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new LoopROIsGUI(mfe);
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
		if (processingStepsPanelObject instanceof LoopROIsGUI){
			LoopROIsGUI returnObject = new LoopROIsGUI(mfe);
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
