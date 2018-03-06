
package editorModulesDefinitions;

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

public class AddSolutionFromVialToSampleGUI extends EditorModules{
	
	private static final long serialVersionUID = 1L;
	transient MainFrameEditor mfe;
	private static String name = "AddSolutionToSample";
	private JTextField vialNumber = new JTextField("-1");
	private JTextField volume = new JTextField("300");
	private JCheckBox useLS2 = new JCheckBox("Use LS 2");
	private JCheckBox vortex = new JCheckBox("Vortex");
	private JTextField vortexVolume = new JTextField("200");
	private JTextField nbrVortexCycles = new JTextField("3");
	
	public AddSolutionFromVialToSampleGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public AddSolutionFromVialToSampleGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(6, 2,60,15));
		retPanel.add(new JLabel("Vial Number From Rack 3:"));
		retPanel.add(vialNumber);
		retPanel.add(new JLabel("Volume [Microliter]:"));
		retPanel.add(volume);
		retPanel.add(new JLabel(""));
		retPanel.add(useLS2);
		useLS2.setSelected(true);
		retPanel.add(new JLabel(""));
		retPanel.add(vortex);
		vortex.setSelected(true);
		retPanel.add(new JLabel("Volume For Vortexing:"));
		retPanel.add(vortexVolume);
		retPanel.add(new JLabel("Number Of Vortex Cycles:"));
		retPanel.add(nbrVortexCycles);
		vortex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vortexVolume.setEnabled(vortex.isSelected());
				nbrVortexCycles.setEnabled(vortex.isSelected());
			}
		});
		return retPanel;
	}
	
	
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new AddSolutionFromVialToSampleGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[6];
		tempString[0] = vialNumber.getText();
		tempString[1] = volume.getText();
		if (useLS2.isSelected()){
			tempString[2] = "selected";
		}
		if (vortex.isSelected()){
			tempString[3] = "selected";
		}
		tempString[4] = vortexVolume.getText();
		tempString[5] = nbrVortexCycles.getText();
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		vialNumber.setText(tempString[0]);
		volume.setText(tempString[1]);
		if (tempString[2].equals("selected")){
			useLS2.setSelected(true);
		}
		if (tempString[3].equals("selected")){
			vortex.setSelected(true);
		}
		vortexVolume.setText(tempString[4]);
		nbrVortexCycles.setText(tempString[5]);
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof AddSolutionFromVialToSampleGUI){
			AddSolutionFromVialToSampleGUI returnObject = new AddSolutionFromVialToSampleGUI(mfe);
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
		logTimeStart();
		Utility.createSampleListForSolutionAdding(getVialNumber(vialNumber), getVolume(volume,useLS2.isSelected()), useLS2.isSelected() ,vortex.isSelected(), getVolume(vortexVolume,useLS2.isSelected()), getNbrCycles(nbrVortexCycles),mfe.getMainFrameReference().getXYStagePosition(),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (getVialNumber(vialNumber) == -1 || getVolume(volume, useLS2.isSelected()) == -1|| getVolume(vortexVolume,useLS2.isSelected())==-1|| getNbrCycles(nbrVortexCycles)==-1) {
			return false;
		}
		return true;
	}

}
