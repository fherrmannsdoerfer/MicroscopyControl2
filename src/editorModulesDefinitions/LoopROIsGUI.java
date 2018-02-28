package editorModulesDefinitions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.micromanager.api.MultiStagePosition;
import org.micromanager.api.PositionList;

import dataTypes.XYStagePosition;
import microscopeControl.MainFrame;
import utility.Utility;
import editor.EditorModules;
import editor.LoopModules;
import editor.MainFrameEditor;

public class LoopROIsGUI extends LoopModules{
	
	private static final long serialVersionUID = 1L;
	JLabel numberRuns = new JLabel("");
	JTextField parameterTagForThisLoop = new JTextField();
	JButton updateButton = new JButton("Update ROI List");
	JButton addParameterTag = new JButton("Add Parameter Tag");
	transient MainFrameEditor mfe;
	JPanel dispList;
	
	private static String name = "LoopROIs";
	EditorModules endLoop = new EndLoopGUI(this);
	JScrollPane scrollPane2;
	ArrayList<ParameterTag> parameterTags = new ArrayList<ParameterTag>();
	Box verticalBox2;
	
	public LoopROIsGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorLoop());
		this.setOptionPanel(createOptionPanel());
	}
	
	public LoopROIsGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		
		PositionList list = mfe.getMainFrameReference().getPositionList();

		JPanel retPanel = new JPanel();
		JPanel upperPart = new JPanel();
		upperPart.setLayout(new GridLayout(4, 2,60,15));
		upperPart.add(new JLabel("Number of iterations:"));
		upperPart.add(numberRuns);
		upperPart.add(new JLabel("Parameter tag for this loop:"));
		upperPart.add(parameterTagForThisLoop);
		upperPart.add(new JLabel(""));
		upperPart.add(updateButton);
		upperPart.add(new JLabel());
		upperPart.add(addParameterTag);
		updateButton.addActionListener(new updateButtonActionListener());
		
		dispList = new JPanel();
		updatePositionList();
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(upperPart);
		verticalBox.add(Box.createVerticalStrut(30));
		verticalBox.add(dispList);
		
		verticalBox2 = Box.createVerticalBox();
		scrollPane2 = new JScrollPane(verticalBox2);
		scrollPane2.setPreferredSize(new Dimension(400+60,800));
		scrollPane2.setMinimumSize(new Dimension(400+30,700));
		scrollPane2.setMaximumSize(new Dimension(400+900,800));
		
		verticalBox.add(scrollPane2);
		
		
		addParameterTag.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addParameterTag();
			}
		});
		
		
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public void addParameterTag(){
		ParameterTag pt = new ParameterTag(mfe); 
		parameterTags.add(pt);
		verticalBox2.add(pt);
		verticalBox2.add(Box.createVerticalStrut(15));
		mfe.repaintOptionPanel();
	}
	
	public ArrayList<ParameterTag> getParameters(){
		return this.parameterTags;
	}
	
	
	class updateButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			updatePositionList();
		}
	}
	
	private void updatePositionList(){
		dispList.removeAll();
		PositionList list = mfe.getMainFrameReference().getPositionList();
		numberRuns.setText(String.valueOf(list.getNumberOfPositions()));
		dispList.setLayout(new GridLayout(list.getNumberOfPositions()+1,4,10,10));
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
		setNbrIterations(Integer.parseInt(numberRuns.getText()));
		ParameterTag xVals = new ParameterTag(mfe);
		ParameterTag yVals = new ParameterTag(mfe);
		PositionList list = mfe.getMainFrameReference().getPositionList();
		ArrayList<String> paramsX = new ArrayList<String>();
		ArrayList<String> paramsY = new ArrayList<String>();
		for (int i = 0;i<list.getNumberOfPositions(); i++){
			paramsX.add(String.valueOf(list.getPosition(i).getX()));
			paramsY.add(String.valueOf(list.getPosition(i).getY()));
		}
		xVals.setParameterList(paramsX);
		yVals.setParameterList(paramsY);

	}

	@Override
	public void performIncrementalStep() {
	
	}

	@Override
	public ArrayList<String> getParameterTags() {
		ArrayList<String> al = new ArrayList<String>();
		al.add("roi iteration");
		return al;
	}

	@Override
	public boolean checkForValidity() {
		// TODO Auto-generated method stub
		return true;
	}

}
