package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;


public class FilterWheelGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JComboBox filterSelection;
	MainFrameEditor mfe;
	private static String name = "Filter Wheel";
	
	public FilterWheelGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorMicroscope());
		this.setOptionPanel(createOptionPanel());
	}
	
	public FilterWheelGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(1, 2,60,15));
		filterSelection = new JComboBox(mfe.getMainFrameReference().getFilterNames());
		//laserSelection = new JComboBox(dummyLaserNames);
		retPanel.add(new JLabel("Filter Selection:"));
		retPanel.add(filterSelection);
		
		return retPanel;
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new FilterWheelGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[1];
		tempString[0] = String.valueOf(filterSelection.getSelectedIndex());
		return tempString;
	}
	public void setSettings(String[] tempString){
		filterSelection.setSelectedIndex(Integer.parseInt(tempString[0]));
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof FilterWheelGUI){
			FilterWheelGUI returnObject = new FilterWheelGUI(mfe);
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
		mfe.getMainFrameReference().setFilterWheelPosition(filterSelection.getSelectedIndex());
	}
}
