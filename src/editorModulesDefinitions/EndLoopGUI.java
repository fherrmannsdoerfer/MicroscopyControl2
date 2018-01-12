package editorModulesDefinitions;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import editor.EditorModules;
import editor.MainFrameEditor;

public class EndLoopGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	private EditorModules beginLoop;
	private static String name = "EndLoop";
	
	public EndLoopGUI(MainFrameEditor mfe) {
		super(mfe);
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorLoop());
		this.setOptionPanel(createOptionPanel());
		setProgressBarInvisible();
	}
	
	public EndLoopGUI(EditorModules beginLoop){
		this.beginLoop = beginLoop;
	}
	
	public EndLoopGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box horizontalBox = Box.createHorizontalBox();
		
		horizontalBox.add(new JLabel("End of loop, place functions to repeat between start and end module."));
				
		retPanel.add(horizontalBox);
		
		return retPanel;
	}

	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new EndLoopGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		return null;
	}

	@Override
	public void setSettings(String[] tempString) {
		
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof EndLoopGUI){
			EndLoopGUI returnObject = new EndLoopGUI(mfe);
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
