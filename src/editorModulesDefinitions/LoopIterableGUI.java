package editorModulesDefinitions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import utility.Utility;
import editor.EditorModules;
import editor.LoopModules;
import editor.MainFrameEditor;

public class LoopIterableGUI extends LoopModules{
	
	private static final long serialVersionUID = 1L;
	JTextField numberRuns = new JTextField("1");
	JButton addParameterTag = new JButton("Add Parameter Tag");
	
	private static String name = "LoopIterable";
	EditorModules endLoop = new EndLoopGUI(this);
	transient MainFrameEditor mfe;
	JScrollPane scrollPane;
	
	public LoopIterableGUI(MainFrameEditor mfe) {
		super(mfe);
		this.mfe = mfe;
		this.setParameterButtonsName(name);
		this.setColor(mfe.style.getColorLoop());
		this.setOptionPanel(createOptionPanel());
	}
	
	public LoopIterableGUI(){
		
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		JPanel upperPart = new JPanel();
		upperPart.setLayout(new GridLayout(2, 2,60,15));
		upperPart.add(new JLabel("Number Runs:"));
		numberRuns = Utility.setFormatTextFields(numberRuns, 30, 20, 5);
		upperPart.add(numberRuns);
		upperPart.add(addParameterTag);
		upperPart.add(new JLabel());

		scrollPane = new JScrollPane(verticalBox2);
		scrollPane.setPreferredSize(new Dimension(400+60,800));
		scrollPane.setMinimumSize(new Dimension(400+30,700));
		scrollPane.setMaximumSize(new Dimension(400+900,800));
		
		Box verticalBoxAlles = Box.createVerticalBox();
		verticalBoxAlles.add(upperPart);
		verticalBoxAlles.add(Box.createVerticalStrut(30));
		verticalBoxAlles.add(scrollPane);
		
		addParameterTag.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addParameterTag();
			}
		});
		
		retPanel.add(verticalBoxAlles);
		return retPanel;
	}
	
	@Override
	public EditorModules getEndLoopModule(MainFrameEditor mfe){
		return new EndLoopGUI(mfe);
	}

	@Override
	public EditorModules getFunction(MainFrameEditor mfe) {
		return new LoopIterableGUI(mfe);
	}

	@Override
	public String[] getSettings() {
		//The first entry is reserved to the number of runs, then comes the parameterTag and the parameters for each ParameterTag object.
		int nbrElements =1+parameterTags.size()*(Integer.parseInt(numberRuns.getText())+1);
		String[] tempString = new String[nbrElements];
		tempString[0] = numberRuns.getText();
		int counter = 1;
		while (counter < nbrElements){
			for (ParameterTag pt:parameterTags){
				tempString[counter] = pt.getParameterTag();
				counter +=1;
				for (String parameterVal:pt.getParameterList()){
					tempString[counter] = parameterVal;
					counter +=1;
				}
			}
		}
		return tempString;
	}

	@Override
	public void setSettings(String[] tempString) {
		numberRuns.setText(tempString[0]);
		int nbrRuns = Integer.parseInt(tempString[0]);
		//tempString.length -1 is the number of elements for the individual parameterTags,
		//Integer.parseInt(tempString[0])+1 is the number of field that is occupied by each 
		//parameterTag, 1 for the tag itself and numberRuns (tempString[0]) for the parameters
		int nbrParameterTags = (tempString.length-1)/(nbrRuns+1);
		for (int i = 0; i< nbrParameterTags; i++){
			addParameterTag();
			parameterTags.get(i).setParameterTag(tempString[1+i*(nbrRuns+1)]);
			ArrayList<String> params = new ArrayList<String>();
			for (int j = 0;j<nbrRuns; j++){
				params.add(tempString[1+i*(nbrRuns+1)+j+1]);
			}
			parameterTags.get(i).setParameterList(params);
			parameterTags.get(i).fillScrollPane();
		}
	}

	@Override
	public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe) {
		if (processingStepsPanelObject instanceof LoopIterableGUI){
			LoopIterableGUI returnObject = new LoopIterableGUI(mfe);
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
		setNbrIterations(Integer.parseInt(numberRuns.getText()));
		logTimeEnd();
	}

	@Override
	public void performIncrementalStep() {
		// TODO Auto-generated method stub
		logTimeStart();
	}


	@Override
	public boolean checkForValidity() {
		for (int i= 0;i<parameterTags.size();i++) {
			if (parameterTags.get(i).getNumberOfColumns()!= Integer.parseInt(numberRuns.getText())) {
				return false;
			}
		}
		return true;
	}
	
	


}
