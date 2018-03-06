package editorModulesDefinitions;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JCheckBox vortex = new JCheckBox("Vortex");
	private JTextField vortexVolume = new JTextField("200");
	private JTextField nbrVortexCycles = new JTextField("3");
	
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
		retPanel.setLayout(new GridLayout(7, 2,60,15));
		retPanel.add(new JLabel("Vial Number From Rack 3 SOURCE:"));
		retPanel.add(indexVialSource);
		retPanel.add(new JLabel("Vial Number From Rack 3 DESTINATION:"));
		retPanel.add(indexVialDest);
		retPanel.add(new JLabel("Volume To Transfer [Microliter]:"));
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
		return new TransfereSolutionsFromVialToVial(mfe);
	}

	@Override
	public String[] getSettings() {
		String[] tempString = new String[7];
		tempString[0] = indexVialSource.getText();
		tempString[1] = indexVialDest.getText();
		tempString[2] = volume.getText();
		if (useLS2.isSelected()){
			tempString[3] = "selected";
		}
		if (vortex.isSelected()){
			tempString[4] = "selected";
		}
		tempString[5] = vortexVolume.getText();
		tempString[6] = nbrVortexCycles.getText();
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
		if (tempString[4].equals("selected")){
			vortex.setSelected(true);
		}
		vortexVolume.setText(tempString[5]);
		nbrVortexCycles.setText(tempString[6]);
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
	
	
	
	
	

	@Override
	public void perform() {
		logTimeStart();
		// TODO Auto-generated method stub
		Utility.createSampleListForTransfereFromVialToVial(getVialNumber(indexVialSource), getVialNumber(indexVialDest), getVolume(volume, useLS2.isSelected()), useLS2.isSelected(), vortex.isSelected(), getVolume(vortexVolume,useLS2.isSelected()), getNbrCycles(nbrVortexCycles),mfe.getMainFrameReference().getPathToExchangeFolder());
		setProgressbarValue(100);
		logTimeEnd();
	}

	@Override
	public boolean checkForValidity() {
		if (getVialNumber(indexVialSource)==-1 || getVialNumber(indexVialDest)==-1|| getVolume(volume, useLS2.isSelected())==-1|| getVolume(vortexVolume,useLS2.isSelected())==-1|| getNbrCycles(nbrVortexCycles)==-1) {
			return false;
		}
		return true;
	}

}
