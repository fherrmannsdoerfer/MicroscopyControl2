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
	JTextField pathInstructionListUpperLeft = new JTextField("");
	JTextField pathInstructionListUp = new JTextField("");
	JTextField pathInstructionListUpperRight = new JTextField("");
	JTextField pathInstructionListLeft = new JTextField("");
	JTextField pathInstructionListMiddle = new JTextField("");
	JTextField pathInstructionListRight = new JTextField("");
	JTextField pathInstructionListLowerLeft = new JTextField("");
	JTextField pathInstructionListLow = new JTextField("");
	JTextField pathInstructionListLowerRight = new JTextField("");
	JTextField[] textFields= {pathInstructionListUpperLeft,pathInstructionListUp,pathInstructionListUpperRight,pathInstructionListLeft,pathInstructionListMiddle,pathInstructionListRight,pathInstructionListLowerLeft,pathInstructionListLow,pathInstructionListLowerRight};
	JLabel lblUl = new JLabel("Upper Left:");
	JLabel lblU = new JLabel("Up:");
	JLabel lblUr = new JLabel("Upper Right:");
	JLabel lblL = new JLabel("Left:");
	JLabel lblM = new JLabel("Middle:");
	JLabel lblR = new JLabel("Right:");
	JLabel lblLl = new JLabel("Lower Left:");
	JLabel lblLow = new JLabel("Down:");
	JLabel lblLR = new JLabel("Lower Right:");
	JLabel[] labels = {lblUl,lblU,lblUr,lblL,lblM,lblR,lblLl,lblLow,lblLR};
	MainFrameEditor mfe;
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
		retPanel.setLayout(new GridLayout(10, 2,60,15));
		retPanel.add(new JLabel("Path to Samplelists:"));
		retPanel.add(new JLabel(""));
		for (int i =0; i<labels.length; i++) {
			retPanel.add(labels[i]);
			retPanel.add(textFields[i]);
		}
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new RemoveSolutionFromSampleGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[labels.length];
		for (int i=0;i<labels.length;i++) {
			tempString[i] = textFields[i].getText();
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		for (int i=0;i<labels.length;i++) {
			textFields[i].setText(tempString[i]);
		}
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

	@Override
	public void perform() {
		// TODO Auto-generated method stub
		String fname = Utility.ChooseSampleListBasedOnStagePositionForRemovalOfSolution(mfe.getMainFrameReference().getXYStagePosition());
		System.out.println(fname);
		setProgressbarValue(100);
	}

}
