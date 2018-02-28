package editorModulesDefinitions;

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.MainFrameEditor;

public class TransfereSolutionsFromVialToVial extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "Transfere Solutions";
	private JTextField indexVialSource = new JTextField("-1");
	private JTextField indexVialDest = new JTextField("-1");
	private JTextField volume = new JTextField("300");
	private JCheckBox useLS2 = new JCheckBox("Use LS2");
	
	public TransfereSolutionsFromVialToVial(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public TransfereSolutionsFromVialToVial(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(4, 2,60,15));
		retPanel.add(new JLabel("Vial Number From Rack 3 SOURCE:"));
		retPanel.add(indexVialSource);
		retPanel.add(new JLabel("Vial Number From Rack 3 DESTINATION:"));
		retPanel.add(indexVialDest);
		retPanel.add(new JLabel("Volume To Transfer [Microliter]:"));
		retPanel.add(volume);
		retPanel.add(new JLabel(""));
		retPanel.add(useLS2);
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new TransfereSolutionsFromVialToVial(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[4];
		tempString[0] = indexVialSource.getText();
		tempString[1] = indexVialDest.getText();
		tempString[2] = volume.getText();
		if (useLS2.isSelected()){
			tempString[3] = "selected";
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		indexVialSource.setText(tempString[0]);
		indexVialDest.setText(tempString[1]);
		volume.setText(tempString[2]);
		if (tempString[3].equals("selected")){
			useLS2.setSelected(true);
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof TransfereSolutionsFromVialToVial){
			TransfereSolutionsFromVialToVial returnObject = new TransfereSolutionsFromVialToVial(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
	
	private int getIndexSource() {
		int index = Integer.parseInt(indexVialSource.getText());
		if (index<1 || index>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return index;
		}
		
	}
	
	private int getIndexDest() {
		int index = Integer.parseInt(indexVialDest.getText());
		if (index<1 || index>54) {
			System.err.println("Vial Number is not within limits of 1 to 54!");
			return -1;
		} else {
			return index;
		}
		
	}
	
	private int getVolume() {
		int reps = Integer.parseInt(volume.getText());
		if (reps<1 || reps>1000) {
			System.err.println("Volume is not within limits of 1 to 1000!");
			return -1;
		} else {
			return reps;
		}
		
	}
	
	

	@Override
	public void perform() {
		// TODO Auto-generated method stub
		Utility.createSampleListForTransfereFromVialToVial(getIndexSource(), getIndexDest(), getVolume(), useLS2.isSelected(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
	}

}
