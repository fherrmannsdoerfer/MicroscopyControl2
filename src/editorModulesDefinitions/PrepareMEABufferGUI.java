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


public class PrepareMEABufferGUI extends EditorModules{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField volumePBSForStock = new JTextField("500");
	JCheckBox createStockSolution = new JCheckBox("Create Stock Solution");
	JTextField indexVialMeaPowder = new JTextField("-1");
	JTextField indexVialMeaFinal = new JTextField("-1");
	JTextField volumePBSForFinal = new JTextField("900");
	JTextField volumeMEAStockForFinal = new JTextField("100");
	JTextField volumeNaOHForFinal = new JTextField("20");
	JTextField indexVialNaOH = new JTextField("-1");
	JTextField nbrVortex = new JTextField("3");
	JTextField nbrWashingCycles = new JTextField("3");
	transient MainFrameEditor mfe;
	JTextField tagROILoop = new JTextField();
	private static String name = "Prepare MEA Buffer";
	
	public PrepareMEABufferGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorStainingRobot());
		this.setOptionPanel(createOptionPanel());
	}
	
	public PrepareMEABufferGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new GridLayout(10, 2,60,15));
		
		retPanel.add(new JLabel("Volume Of PBS For Stock Solution [Microliter]:"));
		retPanel.add(volumePBSForStock);
		retPanel.add(new JLabel(""));
		retPanel.add(createStockSolution);
		retPanel.add(new JLabel("Vial Number From Rack 3 (MEA Stock):"));
		retPanel.add(indexVialMeaPowder);
		createStockSolution.addActionListener(new chkBoxActionListener());
		createStockSolution.setSelected(true);
		retPanel.add(new JLabel("Vial Number From Rack 3 (Final MEA):"));
		retPanel.add(indexVialMeaFinal);
		retPanel.add(new JLabel("Vial Number From Rack 3 (NaOH):"));
		retPanel.add(indexVialNaOH);
		retPanel.add(new JLabel("Volume MEA Stock For Final MEA [Microliters]:"));
		retPanel.add(volumeMEAStockForFinal);
		retPanel.add(new JLabel("Volume PBS For Final MEA [Microliters]:"));
		retPanel.add(volumePBSForFinal);
		retPanel.add(new JLabel("Volume NaOH For Final MEA [Microliters]:"));
		retPanel.add(volumeNaOHForFinal);
		retPanel.add(new JLabel("Number Of Vortex-Cycles:"));
		retPanel.add(nbrVortex);
		retPanel.add(new JLabel("Number Of Washing Cycles (Syringe):"));
		retPanel.add(nbrWashingCycles);
		return retPanel;
	}
	
	class chkBoxActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			volumePBSForStock.setEnabled(createStockSolution.isSelected());
		}
		
	}
	
		
	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new PrepareMEABufferGUI(mfe);
	}

	public String[] getSettings(){
		String[] tempString = new String[10];
		tempString[0] = volumePBSForStock.getText();
		tempString[1] = indexVialMeaPowder.getText();
		tempString[2] = indexVialMeaFinal.getText();
		tempString[3] = indexVialNaOH.getText();
		tempString[4] = volumeMEAStockForFinal.getText();
		tempString[5] = volumePBSForFinal.getText();
		tempString[6] = volumeNaOHForFinal.getText();
		tempString[7] = nbrVortex.getText();
		tempString[8] = nbrWashingCycles.getText();
		if (createStockSolution.isSelected()){
			tempString[9] = "selected";
		}
		else {
			tempString[9] = "notSelected";
		}
		return tempString;
	}
	public void setSettings(String[] tempString){
		volumePBSForStock.setText(tempString[0]);
		indexVialMeaPowder.setText(tempString[1]);
		indexVialMeaFinal.setText(tempString[2]);
		indexVialNaOH.setText(tempString[3]);
		volumeMEAStockForFinal.setText(tempString[4]);
		volumePBSForFinal.setText(tempString[5]);
		volumeNaOHForFinal.setText(tempString[6]);
		nbrVortex.setText(tempString[7]);
		nbrWashingCycles.setText(tempString[8]);
		if (tempString[9].equals("selected")){
			createStockSolution.setSelected(true);
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof PrepareMEABufferGUI){
			PrepareMEABufferGUI returnObject = new PrepareMEABufferGUI(mfe);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
	
	private int getNbrVortexCycles() {
		int index = Integer.parseInt(nbrVortex.getText());
		if (index<1 || index>100) {
			System.err.println("Number vortex-cycles must be between 1 and 100!");
			return -1;
		} else {
			return index;
		}
	}
	private int getNbrWashingCycles() {
		int index = Integer.parseInt(nbrWashingCycles.getText());
		if (index<1 || index>100) {
			System.err.println("Number washing-cycles must be between 1 and 100!");
			return -1;
		} else {
			return index;
		}
	}


	@Override
	public void perform() {
		Utility.createSampleListForPreparationOfMEA(getVolume(volumePBSForStock,true), getVialNumber(indexVialMeaPowder), getVialNumber(indexVialMeaFinal), getVolume(volumePBSForFinal,true),getVolume(volumeMEAStockForFinal,true),getVolume(volumeNaOHForFinal,false),getVialNumber(indexVialNaOH),getNbrCycles(nbrVortex), getNbrCycles(nbrWashingCycles),mfe.getMainFrameReference().getPathToExchangeFolder(), createStockSolution.isSelected());
		setProgressbarValue(100);
	}

	@Override
	public boolean checkForValidity() {
		if (getVolume(volumePBSForStock,true)==-1||getVialNumber(indexVialMeaPowder)==-1||getVialNumber(indexVialMeaFinal)==-1|| getVolume(volumePBSForFinal,true)==-1|| getVolume(volumeMEAStockForFinal,true)==-1||getVolume(volumeNaOHForFinal,false)==-1||getVialNumber(indexVialNaOH)==-1||getNbrCycles(nbrVortex)==-1||getNbrCycles(nbrWashingCycles)==-1) {
			return false;
		}
		return true;
	}
}
