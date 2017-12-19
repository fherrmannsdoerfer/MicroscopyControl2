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
import editorModulesDefinitions.IterableInputGUI.ParameterColumn;

public class LoopIterableGUI extends LoopModules{
	
	private static final long serialVersionUID = 1L;
	JTextField numberRuns = new JTextField("1");
	JButton addParameterTag = new JButton("Add Parameter Tag");
	
	private static String name = "LoopIterable";
	EditorModules endLoop = new EndLoopGUI(this);
	MainFrameEditor mfe;
	Box verticalBox;
	JScrollPane scrollPane;
	ArrayList<ParameterTag> parameterTags = new ArrayList<ParameterTag>();
	
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

		verticalBox = Box.createVerticalBox();
		scrollPane = new JScrollPane(verticalBox);
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
	
	public void addParameterTag(){
		ParameterTag pt = new ParameterTag(mfe); 
		parameterTags.add(pt);
		verticalBox.add(pt);
		verticalBox.add(Box.createVerticalStrut(15));
		mfe.repaintOptionPanel();
	}
	
	public ArrayList<ParameterTag> getParameters(){
		return this.parameterTags;
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
		setNbrIterations(Integer.parseInt(numberRuns.getText()));
	}

	@Override
	public void performIncrementalStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> getParameterTags() {
		ArrayList<String> al = new ArrayList<String>();
		for (ParameterTag pt:parameterTags){
			al.add(pt.getParameterTag());
		}
		return al;
		//return iterationTag.getText();
	}
	
	public class ParameterTag extends JPanel{
		JButton addParameterColumn = new JButton("Add New Param");
		private ArrayList<ParameterColumn> parameterList = new ArrayList<ParameterColumn>();
		JScrollPane scrollPane;
		JTextField iterationTagParameterFields = new JTextField();
		JPanel newLinePanel = new JPanel();
		JPanel middlePart;
		MainFrameEditor mfe;
		
		public ParameterTag(MainFrameEditor mfe){
			this.mfe = mfe;
			this.setBorder(BorderFactory.createLineBorder(Color.black));

			JPanel upperPart = new JPanel();
			upperPart.setLayout(new GridLayout(1, 2,60,15));

			upperPart.add(new JLabel("Tag For The Parameter Fields:"));
			upperPart.add(iterationTagParameterFields);
			
			Box verticalBox = Box.createVerticalBox();
			this.add(verticalBox);
			verticalBox.add(upperPart);
			
			middlePart = new JPanel();
			
			verticalBox.add(middlePart);
			middlePart.setLayout(new BoxLayout(middlePart,BoxLayout.Y_AXIS));
			scrollPane = new JScrollPane(middlePart);
			scrollPane.setPreferredSize(new Dimension(200+60,400));
			scrollPane.setMinimumSize(new Dimension(200+30,300));
			scrollPane.setMaximumSize(new Dimension(200+900,800));
			
			Box horizontalBox = Box.createHorizontalBox();
			horizontalBox.add(Box.createVerticalGlue());
			JButton buttonAddLine = new JButton("Add New Parameter");
			buttonAddLine.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ParameterColumn pc = new ParameterColumn(""); 
					parameterList.add(pc);
					fillScrollPane();
				}
			});
			horizontalBox.add(buttonAddLine);
			newLinePanel.add(horizontalBox);
			verticalBox.add(Box.createVerticalStrut(20));
			verticalBox.add(scrollPane);
			middlePart.add(newLinePanel);
		}
		
		private void fillScrollPane(){
			middlePart.removeAll();
			for (ParameterColumn pc:parameterList){
				middlePart.add(pc);
				middlePart.add(Box.createVerticalStrut(10));
			}
			middlePart.add(newLinePanel);
			middlePart.add(Box.createVerticalGlue());
			mfe.repaintOptionPanel();
		}
		
		public void setParameterList(ArrayList<String> parameters){
			for (int i =0; i<parameters.size(); i++){
				ParameterColumn pc = new ParameterColumn(parameters.get(i)); 
				parameterList.add(pc);
			}
		}
		
		public ArrayList<String> getParameterList(){
			ArrayList<String> parameters = new ArrayList<String>();
			for (ParameterColumn pc:parameterList){
				parameters.add(pc.getParameter());
			}
			return parameters;
		}
		
		public String getParameterTag(){
			return iterationTagParameterFields.getText();
		}
		
		public void setParameterTag(String text){
			iterationTagParameterFields.setText(text);
		}
		
		class ParameterColumn extends JPanel{
			ParameterColumn selfReference;
			JTextField parameter;
			
			public ParameterColumn(String string) {
				selfReference = this;
				//this.setBorder(BorderFactory.createLineBorder(Color.black));
				Box horizontalBox = Box.createHorizontalBox();
				parameter = new JTextField(string);
				parameter.setMinimumSize(new Dimension(250,20));
				parameter.setColumns(20);
				horizontalBox.add(parameter);
				horizontalBox.add(Box.createHorizontalStrut(20));
				JButton removeButton = new JButton("X");
				removeButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						removeColumn(selfReference);
					}
				});
				horizontalBox.add(removeButton);
				this.add(horizontalBox);
				this.setMaximumSize(new Dimension(300,40));
			}
			public String getParameter(){
				return parameter.getText();
			}
			public void setParameter(String parameter){
				this.parameter.setText(parameter);
			}
			
			
		}
		
		
		private void removeColumn(ParameterColumn column){
			parameterList.remove(column);
			fillScrollPane();
			mfe.repaintOptionPanel();
		}
	}
	
	


}
