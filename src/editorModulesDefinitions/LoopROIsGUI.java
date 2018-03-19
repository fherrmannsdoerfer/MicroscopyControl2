package editorModulesDefinitions;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.micromanager.api.PositionList;

import editor.EditorModules;
import editor.LoopModules;
import editor.MainFrameEditor;
import editor.LoopModules.ParameterTag;
//for detailed comments look at performMeasurementGUI
public class LoopROIsGUI extends LoopModules{
	
	private static final long serialVersionUID = 1L;
	JLabel numberRuns = new JLabel("");
	//JTextField parameterTagForThisLoop = new JTextField();
	JButton updateButton = new JButton("Update ROI List");
	JButton addParameterTag = new JButton("Add Parameter Tag");
	transient MainFrameEditor mfe;
	JPanel dispList;
	
	private static String name = "LoopROIs";
	EditorModules endLoop = new EndLoopGUI(this);
	JScrollPane scrollPane2;
	
	
	
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
		upperPart.setLayout(new GridLayout(3, 2,60,15));
		upperPart.add(new JLabel("Number of iterations:"));
		upperPart.add(numberRuns);
		upperPart.add(new JLabel(""));
		upperPart.add(updateButton);
		upperPart.add(new JLabel());
		upperPart.add(addParameterTag);
		updateButton.addActionListener(new updateButtonActionListener());
		
		dispList = new JPanel();
		//updatePositionList();
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
		
		ParameterTag pt = new ParameterTag(mfe);
		pt.setParameterTag("%Positions%");
		for (int i = 0;i<list.getNumberOfPositions(); i++){
			pt.addRow(String.format(Locale.US,"%.3f<->%.3f", list.getPosition(i).getX(),list.getPosition(i).getY()));
		}
		pt.fillScrollPane();
		replaceParameterTag(pt,0);
		
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
		logTimeStart();
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
		logTimeEnd();
	}

	@Override
	public void performIncrementalStep() {
	
	}

	/*@Override
	public ArrayList<String> getParameterTags() {
		ArrayList<String> al = new ArrayList<String>();
		al.add("roi iteration");
		return al;
	}*/

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
