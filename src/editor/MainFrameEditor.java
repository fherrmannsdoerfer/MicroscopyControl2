package editor;

import javax.swing.Box;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import java.awt.Font;

import microscopeControl.MainFrame;
import editorModulesDefinitions.AddSolutionFromVialToSampleGUI;
import editorModulesDefinitions.AddWashingSolutionToSampleGUI;
import editorModulesDefinitions.AddWashingSolutionToVialGUI;
import editorModulesDefinitions.AutoFocusGUI;
import editorModulesDefinitions.AutofocusBeadBasedGUI;
import editorModulesDefinitions.BreakPointGUI;
import editorModulesDefinitions.CameraParametersGUI;
import editorModulesDefinitions.CaptureWidefieldImageGUI;
import editorModulesDefinitions.CommentaryBarGUI;
import editorModulesDefinitions.DefineReferenceForAutoFocus;
import editorModulesDefinitions.EndLoopGUI;
import editorModulesDefinitions.FilterWheelGUI;
import editorModulesDefinitions.FocusLockStateGUI;
import editorModulesDefinitions.LaserControl;
import editorModulesDefinitions.LoopIterableGUI;
import editorModulesDefinitions.LoopROIsGUI;
import editorModulesDefinitions.MeasurementTagGUI;
import editorModulesDefinitions.MoveFocalPlaneAbsoluteGUI;
import editorModulesDefinitions.MoveFocalPlaneGUI;
import editorModulesDefinitions.MoveStageGUI;
import editorModulesDefinitions.PauseGUI;
import editorModulesDefinitions.PerformMeasurmentGUI;
import editorModulesDefinitions.PrepareMEABufferGUI;
import editorModulesDefinitions.RemoveSolutionFromSampleGUI;
import editorModulesDefinitions.RunPumpsGUI;
import editorModulesDefinitions.StainingRobotCommandGUI;
import editorModulesDefinitions.StartImageAcquisitionGUI;
import editorModulesDefinitions.TransfereSolutionsFromVialToVial;
import editorModulesDefinitions.VortexVialGUI;
import editorModulesDefinitions.Wash3TimesWithPBSLS2GUI;
import editorModulesDefinitions.WashSyringeGUI;
//Main class behind the experiment editor. The GUI is created here. Also the handling of all button callbacks are managed within this class
public class MainFrameEditor extends JFrame implements Serializable{
	
	transient private MainFrame mf;
	private ArrayList<EditorModules> listProcessingStepPanels = new ArrayList<EditorModules>();
	//controlerEditor takes care of the execution of the modules and find parameterTags
	ControlerEditor controlerReference;
	JPanel panel;
	//JPanel optionPanel;
	JPanel optionPanel;
	static MainFrameEditor mfe;
	private static DataFlavor dragAndDropPanelDataFlavor = null;
	private JComboBox preselectionComboBox;
	private JComboBox microscopeFunctionsComboBox;
	private JComboBox stainingRobotFunctionsComboBox;
	private JComboBox loopsComboBox;
	transient ActionListener outputActionListener;
	private final ArrayList<String> optionsPreselectedTasksComboBoxAuto = new ArrayList<String>();
	public StyleClass style = new StyleClass();
	ButtonGroup bg = new ButtonGroup();
	private boolean editorShouldBeRunning = true;

	JButton runButton;
	JButton stopButton;
	
	//Default folder for saving and loading of module lists
	File folder = new File(System.getProperty("user.home")+"//ExperimentEditor"); //Folder of savedPresettings
	private final ArrayList<EditorModules> stainingRobotComboBoxOptions = new ArrayList<EditorModules>();
	private final ArrayList<EditorModules> microscopeComboBoxOptions = new ArrayList<EditorModules>();
	private final ArrayList<EditorModules> loopsComboBoxOptions = new ArrayList<EditorModules>();
	
	JMenuBar menuBar;
	final JFileChooser settingsFileChooserLoad = new JFileChooser(folder);
	final JFileChooser settingsFileChooserSave = new JFileChooser(folder);

	public MainFrameEditor(final ControlerEditor controler, MainFrame mf) {
		this.mf = mf;
		this.controlerReference = controler;
		this.setTitle("Experiment Editor");
		mfe = this;
		//Create if necessary default folder in user.home directory
		folder.mkdir();
		setUpGUI();
	}
	
	private void setUpComboboxes() {
		outputActionListener = new OutputActionListener();
		///////////////////////////////// set options to choose from for drop-down menus; creates empty GUI class objects with member name
		
		//First Combobox contains all Files from the preselected tasks folder
		File[] listOfFiles = folder.listFiles();		
		for (int i = 0; i < listOfFiles.length; i++) {
		if (listOfFiles[i].isFile()) {
			optionsPreselectedTasksComboBoxAuto.add(listOfFiles[i].getName());
			} 
		}
	
		microscopeComboBoxOptions.add(new LaserControl());
		microscopeComboBoxOptions.add(new StartImageAcquisitionGUI());
		microscopeComboBoxOptions.add(new MoveStageGUI());
		microscopeComboBoxOptions.add(new CaptureWidefieldImageGUI());
		microscopeComboBoxOptions.add(new PauseGUI());
		microscopeComboBoxOptions.add(new FilterWheelGUI());
		microscopeComboBoxOptions.add(new CameraParametersGUI());
		microscopeComboBoxOptions.add(new MeasurementTagGUI());
		microscopeComboBoxOptions.add(new CommentaryBarGUI());
		microscopeComboBoxOptions.add(new FocusLockStateGUI());
		microscopeComboBoxOptions.add(new MoveFocalPlaneAbsoluteGUI());
		microscopeComboBoxOptions.add(new MoveFocalPlaneGUI());
		microscopeComboBoxOptions.add(new PerformMeasurmentGUI());
		microscopeComboBoxOptions.add(new BreakPointGUI());
		microscopeComboBoxOptions.add(new AutoFocusGUI());
		microscopeComboBoxOptions.add(new DefineReferenceForAutoFocus());
		microscopeComboBoxOptions.add(new AutofocusBeadBasedGUI());
		
		stainingRobotComboBoxOptions.add(new StainingRobotCommandGUI());
		stainingRobotComboBoxOptions.add(new RemoveSolutionFromSampleGUI());
		stainingRobotComboBoxOptions.add(new AddSolutionFromVialToSampleGUI());
		stainingRobotComboBoxOptions.add(new RunPumpsGUI());
		stainingRobotComboBoxOptions.add(new Wash3TimesWithPBSLS2GUI());
		stainingRobotComboBoxOptions.add(new AddWashingSolutionToSampleGUI());
		stainingRobotComboBoxOptions.add(new AddWashingSolutionToVialGUI());
		stainingRobotComboBoxOptions.add(new WashSyringeGUI());
		stainingRobotComboBoxOptions.add(new VortexVialGUI());
		stainingRobotComboBoxOptions.add(new TransfereSolutionsFromVialToVial());
		stainingRobotComboBoxOptions.add(new PrepareMEABufferGUI());
		
		//loopsComboBoxOptions.add(new LoopGUI());
		loopsComboBoxOptions.add(new LoopROIsGUI());
		loopsComboBoxOptions.add(new LoopIterableGUI());
		
		
		//Create Strings for the entries of the comboboxes
		String[] optionsMicroscopeComboBox = new String[microscopeComboBoxOptions.size()];		
		for (int i = 0; i < microscopeComboBoxOptions.size(); i++){
			optionsMicroscopeComboBox[i] = microscopeComboBoxOptions.get(i).getFunctionName();
		}	
		
		String[] optionsLoopsComboBox = new String[loopsComboBoxOptions.size()];		
		for (int i = 0; i < loopsComboBoxOptions.size(); i++){
			optionsLoopsComboBox[i] = loopsComboBoxOptions.get(i).getFunctionName();
		}		
		
		String[] optionsStainingRobotComboBox = new String[stainingRobotComboBoxOptions.size()];
		
		for (int i = 0; i < stainingRobotComboBoxOptions.size(); i++){
			optionsStainingRobotComboBox[i] = stainingRobotComboBoxOptions.get(i).getFunctionName();
		}
		
		
		preselectionComboBox = new JComboBox(optionsPreselectedTasksComboBoxAuto.toArray());
		preselectionComboBox.addActionListener(outputActionListener);
		preselectionComboBox.setMaximumSize(new Dimension(32767, 22));
		
		microscopeFunctionsComboBox = new JComboBox(optionsMicroscopeComboBox);
		microscopeFunctionsComboBox.addActionListener(outputActionListener);
		microscopeFunctionsComboBox.setMaximumSize(new Dimension(32767, 22));
		
		stainingRobotFunctionsComboBox = new JComboBox(optionsStainingRobotComboBox);
		stainingRobotFunctionsComboBox.addActionListener(outputActionListener);
		stainingRobotFunctionsComboBox.setMaximumSize(new Dimension(32767, 22));
		
		loopsComboBox = new JComboBox(optionsLoopsComboBox);
		loopsComboBox.addActionListener(outputActionListener);
		loopsComboBox.setMaximumSize(new Dimension(32767, 22));
	}
	
	
	
	private void setUpMenu() {
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		
		
		JMenuItem loadSettingsButton = new JMenuItem("Load Settings");
		JMenuItem loadDefaultSettingsButton = new JMenuItem("Load Default Settings");
		JMenuItem importModulesButton = new JMenuItem("Import Modules");
		JMenuItem saveSettingsButton = new JMenuItem("Save Settings");
		JMenuItem saveDefaultSettingsButton = new JMenuItem("Save Default Settings");
		
		fileMenu.add(loadSettingsButton);
		fileMenu.add(loadDefaultSettingsButton);
		fileMenu.add(importModulesButton);
		fileMenu.add(saveSettingsButton);
		fileMenu.add(saveDefaultSettingsButton);
		
		loadDefaultSettingsButton.addActionListener(new LoadDefaultSettingActionListener());
		loadSettingsButton.addActionListener(new LoadSettingActionListener());
		importModulesButton.addActionListener(new ImportModulesActionListener());
		saveDefaultSettingsButton.addActionListener(new SaveDefaultSettingActionListener());	
		saveSettingsButton.addActionListener(new SaveSettingsButton());
		
		runButton = new JButton("Start Processing");
		runButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		runButton.addActionListener(new RunButtonActionListener());
		
		stopButton = new JButton("Stop Processing");
		stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		stopButton.addActionListener(new StopButtonActionListener());
		
		JButton checkValidity = new JButton("Check Validity");
		checkValidity.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkValidity.addActionListener(new CheckValidityActionListener());
		
		menuBar.add(fileMenu);
		menuBar.add(runButton);
		menuBar.add(stopButton);
		menuBar.add(checkValidity);
	}
		
	private void setUpGUI() {
		setMinimumSize(style.getSizeEditor());
	
		setUpComboboxes();
		getContentPane().setPreferredSize(style.getSizeEditor());
		setUpMenu();

		JLabel titleAvailableModulesLabel = new JLabel("Available Modules");
		titleAvailableModulesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleAvailableModulesLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
				
		Box preselectedTaskTagBox = Box.createHorizontalBox();
		preselectedTaskTagBox.add( new JLabel("Preselected Tasks"));
		preselectedTaskTagBox.add(Box.createHorizontalGlue());
				
		Box microscopeFunctionsTagBox = Box.createHorizontalBox();
		microscopeFunctionsTagBox.add( new JLabel("Microscope Functions"));
		microscopeFunctionsTagBox.add(Box.createHorizontalGlue());
		
		Box stainingRobotFunctionsTagBox = Box.createHorizontalBox();	
		stainingRobotFunctionsTagBox.add(new JLabel("Staining Robot Functions"));		
		stainingRobotFunctionsTagBox.add(Box.createHorizontalGlue());
		
		Box loopsTagBox = Box.createHorizontalBox();
		loopsTagBox.add(new JLabel("Loops"));
		loopsTagBox.add(Box.createHorizontalGlue());
		
		Box modulesSelectionBox = Box.createVerticalBox();
		modulesSelectionBox.setBorder(new TitledBorder(null, "Modules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		modulesSelectionBox.add(microscopeFunctionsTagBox);
		modulesSelectionBox.add(microscopeFunctionsComboBox);
		modulesSelectionBox.add(Box.createVerticalStrut(20));
		modulesSelectionBox.add(stainingRobotFunctionsTagBox);
		modulesSelectionBox.add(stainingRobotFunctionsComboBox);
		modulesSelectionBox.add(Box.createVerticalStrut(20));
		modulesSelectionBox.add(loopsTagBox);
		modulesSelectionBox.add(loopsComboBox);

		
		Box blockOfComboboxes = Box.createVerticalBox();
		
		blockOfComboboxes.setMaximumSize(new Dimension(400, 99990));
		blockOfComboboxes.add(preselectedTaskTagBox);
		blockOfComboboxes.add(preselectionComboBox);
		blockOfComboboxes.add(Box.createVerticalStrut(20));
		blockOfComboboxes.add(modulesSelectionBox);
		blockOfComboboxes.add(Box.createVerticalGlue());
		
		Box availableModulesBox = Box.createVerticalBox();
		availableModulesBox.setMaximumSize(style.getDimensionAvailableModules());
		availableModulesBox.setMinimumSize(style.getDimensionAvailableModules());
		
		availableModulesBox.add(titleAvailableModulesLabel);
		availableModulesBox.add(Box.createVerticalStrut(20));
		availableModulesBox.add(blockOfComboboxes);
		
		////create middle Column
		Box selectedModulesBox = createSelecteModuleColumn();
				
		//create right column
		Box parameterBox = createParameterSelection();
		
		//wholeEditorBox contains all elements of the editor GUI
		Box wholeEditorBox = Box.createHorizontalBox();
	
		wholeEditorBox.add(Box.createHorizontalStrut(20));
		wholeEditorBox.add(availableModulesBox);
		wholeEditorBox.add(Box.createHorizontalStrut(20));
		wholeEditorBox.add(selectedModulesBox);
		wholeEditorBox.add(Box.createHorizontalStrut(20));
		wholeEditorBox.add(parameterBox);
				
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(wholeEditorBox);
	}
	
	Box createSelecteModuleColumn() {
		Box selectedModulesBox = Box.createVerticalBox();
		selectedModulesBox.setMaximumSize(style.getDimensionSelectedModules());
		selectedModulesBox.setMinimumSize(style.getDimensionSelectedModules());
		
		JLabel titelSelectedModulesLabel = new JLabel("Selected Modules");
		titelSelectedModulesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titelSelectedModulesLabel.setFont(new Font("Tahoma", Font.BOLD, 20));

		Component verticalStrut_4 = Box.createVerticalStrut(20);
		
		panel = new RootPanel(this);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(panel);
		
		scrollPane.setPreferredSize(new Dimension(style.getWidthEditorModules()+60,1200));
		scrollPane.setMinimumSize(new Dimension(style.getWidthEditorModules()+30,500));
		scrollPane.setMaximumSize(new Dimension(style.getWidthEditorModules()+900,33200));
		
		selectedModulesBox.add(titelSelectedModulesLabel);
		selectedModulesBox.add(verticalStrut_4);
		selectedModulesBox.add(scrollPane);
		
		return selectedModulesBox;
	}
	
	Box createParameterSelection() {
		/// Right Column
		optionPanel = new JPanel();
		Box parameterBox = Box.createVerticalBox();
		parameterBox.setMaximumSize(style.getDimensionParameters());
		parameterBox.setMinimumSize(style.getDimensionParameters());
		
		
		JLabel titelParameterSelectionLabel = new JLabel("Parameter Selection");
		titelParameterSelectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titelParameterSelectionLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		Component verticalStrut_5 = Box.createVerticalStrut(20);
		
		parameterBox.add(titelParameterSelectionLabel);
		parameterBox.add(verticalStrut_5);
		parameterBox.add(optionPanel);
		optionPanel.setMinimumSize(mfe.style.getDimensionParameters());
		///Right Column
		return parameterBox;
	}

	protected void checkValidity() {
		boolean valid = true;
		ArrayList<Integer> positionList = new ArrayList<Integer>();
		ArrayList<String> nameList = new ArrayList<String>();
		int counter = 0; 
		for (EditorModules em:listProcessingStepPanels) {
			counter = counter +1;
			if (em.checkForValidity()==false) {
				valid =false;
				positionList.add(counter);
				nameList.add(em.getFunctionName());
			}
		}
		if (valid) {
			JOptionPane.showMessageDialog(null, "No obvious error detected!");
		} else {
			String message;
			message = "Errors in modulenbr: ";
			for (int i=0;i<positionList.size();i++) {
				message = message +positionList.get(i)+", ";
			}
			message = message+"\n\nFunction Names:\n";
			for (int i=0;i<nameList.size();i++) {
				message = message +nameList.get(i)+"\n";
			}
			JOptionPane.showMessageDialog(null, message);
		}
	}

	public ControlerEditor getControlerEditorReference(){
		return controlerReference;
	}
	
	public void stopEditor() {
		editorShouldBeRunning = false;
		runButton.setEnabled(true);
	}
	
	public boolean getEditorShouldBeRunning() {
		return editorShouldBeRunning;
	}
	
	private void setupPreselectedTasks() {
		File[] listOfFiles = folder.listFiles();	
		preselectionComboBox.removeAllItems();
	    for (int i = 0; i < listOfFiles.length; i++) {
		if (listOfFiles[i].isFile()) {
				optionsPreselectedTasksComboBoxAuto.add(listOfFiles[i].getName());
				preselectionComboBox.addItem(listOfFiles[i].getName());
			} 
	    }
	}

	public static DataFlavor getDragAndDropPanelDataFlavor() throws Exception {
        // Lazy load/create the flavor
        if (dragAndDropPanelDataFlavor == null) {
            dragAndDropPanelDataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class="+EditorModules.class.getName());
        }

        return dragAndDropPanelDataFlavor;
    }
	
	
	protected ArrayList<EditorModules> getListProcessingStepPanels() {
        return listProcessingStepPanels;
    }

	public void updatePanels() {
		panel.removeAll();
		int indentCounter = 0;
		for (EditorModules p : listProcessingStepPanels){
			if (p instanceof LoopModules || p instanceof EndLoopGUI){
				if (p instanceof EndLoopGUI){
					p.setIndentation(indentCounter);
					indentCounter -= 1;
				}
				else{
					indentCounter += 1;
					p.setIndentation(indentCounter);
				}
			}
			else{
				p.setIndentation(indentCounter+1);
			}
			Component verticalStrut = Box.createVerticalStrut(10);
    		panel.add(verticalStrut);
    		
    		p.setAlignmentX(0);
			panel.add(p);
		}
		panel.repaint();
		panel.revalidate();
	}
	
	public void repaintOptionPanel(){
		optionPanel.repaint();
		optionPanel.revalidate();
	}
	

	public void removePanel(EditorModules thisPanel) {
		//if the deleted module is any kind of LoopModules then the according endLoop module is deleted as well
		if (thisPanel instanceof LoopModules){
			int LoopModuleCounter = 0;
			EditorModules endLoopModuleToDelete = null;
			for (EditorModules p : listProcessingStepPanels){
				//reset counter to identify the endLoop module of the same level
				if (p == thisPanel){LoopModuleCounter = 0;}
				if (p instanceof EndLoopGUI){LoopModuleCounter -= 1;}
				if (p instanceof LoopModules){LoopModuleCounter += 1;}
				if (p instanceof EndLoopGUI && LoopModuleCounter == 0){
					endLoopModuleToDelete = p;
					break;
				}
			}
			this.optionPanel.remove(endLoopModuleToDelete.getOptionPanel());
			listProcessingStepPanels.remove(endLoopModuleToDelete);
		}
		
		if (thisPanel instanceof EndLoopGUI){
			int loopModuleCounter = 0;
			EditorModules loopModuleToDelete = null;
			for (int i = listProcessingStepPanels.size(); i>0; i--){
				EditorModules p = listProcessingStepPanels.get(i-1);
				//reset counter to identify the endLoop module of the same level
				if (p == thisPanel){loopModuleCounter = 0;}
				if (p instanceof EndLoopGUI){loopModuleCounter -= 1;}
				if (p instanceof LoopModules){loopModuleCounter += 1;}
				if (p instanceof LoopModules && loopModuleCounter == 0){
					loopModuleToDelete = p;
					break;
				}
			}
			listProcessingStepPanels.remove(loopModuleToDelete);
		}
		
		listProcessingStepPanels.remove(thisPanel);
		updatePanels();
	}

	public JComboBox getPreselectionComboBox() {
		return preselectionComboBox;
	}
	public JComboBox getInputComboBox() {
		return microscopeFunctionsComboBox;
	}
	public JComboBox getOutputComboBox() {
		return stainingRobotFunctionsComboBox;
	}
	public JComboBox getProcessingComboBox() {
		return loopsComboBox;
	}
	
	public void moduleAdded(ActionEvent e){
		JComboBox thisBox = (JComboBox)e.getSource();
		EditorModules panelToAdd = null;
		
		if (thisBox == stainingRobotFunctionsComboBox){
//			System.out.println(outputComboBoxOptions.get(thisBox.getSelectedIndex()).getFunctionName());
			panelToAdd = stainingRobotComboBoxOptions.get(thisBox.getSelectedIndex()).getFunction(mfe);
		}
		if (thisBox == microscopeFunctionsComboBox){
//			System.out.println(inputComboBoxOptions.get(thisBox.getSelectedIndex()).getFunctionName());
			panelToAdd = microscopeComboBoxOptions.get(thisBox.getSelectedIndex()).getFunction(mfe);
		}
		if (thisBox == loopsComboBox){
//			System.out.println(processingComboBoxOptions.get(thisBox.getSelectedIndex()).getFunctionName());
			panelToAdd = loopsComboBoxOptions.get(thisBox.getSelectedIndex()).getFunction(mfe);
			listProcessingStepPanels.add(panelToAdd);
			listProcessingStepPanels.add(((LoopModules)panelToAdd).getEndLoopModule(mfe));
			panelToAdd = null;
		}
		
		if (thisBox == preselectionComboBox&&thisBox.getSelectedIndex()>=0){			
			ArrayList<EditorModules> tempOrderListSettings = new ArrayList<EditorModules>();
			String settingsPath = folder + "\\"+ optionsPreselectedTasksComboBoxAuto.toArray()[thisBox.getSelectedIndex()];
			try {
				FileInputStream fileInDefault = new FileInputStream(settingsPath);
				ObjectInputStream inPreselection = new ObjectInputStream(fileInDefault);
				tempOrderListSettings = (ArrayList<EditorModules>) inPreselection.readObject();
				inPreselection.close();
				fileInDefault.close();
				listProcessingStepPanels.clear();
				panel.removeAll();
				optionPanel.removeAll();				
				for (int i = 0; i < tempOrderListSettings.size(); i++){						
					EditorModules tempObject = tempOrderListSettings.get(i);
					listProcessingStepPanels.add(tempObject.getEditorModulesObject(tempObject, mfe));
					listProcessingStepPanels.get(i).setSettings(tempOrderListSettings.get(i).getSettings());
				}				
				updatePanels();
				invalidate();
				validate();
//				revalidate();
				repaint();					
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
//				case 3:
//					panelToAdd = new CropGUI(mf);
//					break;
			}
		}
		
		if (panelToAdd !=null){
			listProcessingStepPanels.add(panelToAdd);
		}
		updatePanels();
	}
	
	void savePanels(String file){
		String settingsPath = file;
		try {
//			if (!settingsPath.endsWith(".ser")){return;}
			FileOutputStream fileOut = new FileOutputStream(settingsPath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(listProcessingStepPanels);
			out.close();
			fileOut.close();
			for (EditorModules p:listProcessingStepPanels){
				p.removeButton.setBorder(null);
			}
			//setupPreselectedTasks();
			System.out.println("saved");	
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	void importPanels(File file) {
		ArrayList<EditorModules> tempOrderList = new ArrayList<EditorModules>();
		String settingsPath = null;
		//File file = settingsFileChooserLoad.getSelectedFile();
		settingsPath = file.getAbsolutePath();
		try {
			FileInputStream fileIn = new FileInputStream(settingsPath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			tempOrderList = (ArrayList<EditorModules>) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("loaded");	
			
			for (int i = 0; i < tempOrderList.size(); i++){						
				EditorModules tempObject = tempOrderList.get(i);
				EditorModules tmp = tempObject.getEditorModulesObject(tempObject, mfe);
				//tmp.setSettings(tempOrderList.get(i).getSettings());
				listProcessingStepPanels.add(tmp);
				listProcessingStepPanels.get(listProcessingStepPanels.size()-1).setSettings(tempOrderList.get(i).getSettings());
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		updatePanels();
		invalidate();
		validate();
//		revalidate();
		repaint();
	}
	
	void loadPanels(File file){
		ArrayList<EditorModules> tempOrderList = new ArrayList<EditorModules>();
		String settingsPath = null;
		//File file = settingsFileChooserLoad.getSelectedFile();
		settingsPath = file.getAbsolutePath();
		try {
			FileInputStream fileIn = new FileInputStream(settingsPath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			tempOrderList = (ArrayList<EditorModules>) in.readObject();
			in.close();
			fileIn.close();
			System.out.println("loaded");	
			listProcessingStepPanels.clear();
			panel.removeAll();
			optionPanel.removeAll();				
			for (int i = 0; i < tempOrderList.size(); i++){						
				EditorModules tempObject = tempOrderList.get(i);
				listProcessingStepPanels.add(tempObject.getEditorModulesObject(tempObject, mfe));
				listProcessingStepPanels.get(i).setSettings(tempOrderList.get(i).getSettings());
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		updatePanels();
		invalidate();
		validate();
//		revalidate();
		repaint();
	}
	
	public MainFrame getMainFrameReference(){
		return mf;
	}
	
	//////////////////////////////////////////Action Listener
	
	//Action listener to add modules once they are selected from the combobox
	class OutputActionListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			mfe.moduleAdded(e);
		}
	}

	public void hideAllOptionPanels() {
		for (EditorModules psp: listProcessingStepPanels){
			psp.setVisibilityOptionPanel(false);
		}
	}
	
	class LoadDefaultSettingActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {				
			ArrayList<EditorModules> tempOrderList = new ArrayList<EditorModules>();
			String settingsPath = "settings.default";
			try {
				FileInputStream fileInDefault = new FileInputStream(settingsPath);
				ObjectInputStream inDefault = new ObjectInputStream(fileInDefault);
				tempOrderList = (ArrayList<EditorModules>) inDefault.readObject();
				inDefault.close();
				fileInDefault.close();
				System.out.println("loaded default");
				listProcessingStepPanels.clear();
				panel.removeAll();
				optionPanel.removeAll();				
				for (int i = 0; i < tempOrderList.size(); i++){						
					EditorModules tempObject = tempOrderList.get(i);
					listProcessingStepPanels.add(tempObject.getEditorModulesObject(tempObject, mfe));
					listProcessingStepPanels.get(i).setSettings(tempOrderList.get(i).getSettings());
				}				
				updatePanels();
				invalidate();
				validate();
	//					revalidate();
				repaint();					
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}			
	}
	
	class LoadSettingActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("before loaded");
			int returnVal = settingsFileChooserLoad.showOpenDialog(null);
			System.out.println("loadSettingsButton");
			if (returnVal == JFileChooser.APPROVE_OPTION){
				loadPanels(settingsFileChooserLoad.getSelectedFile());
			}
		}
	}
	
	
	class ImportModulesActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int returnVal = settingsFileChooserLoad.showOpenDialog(null);
			System.out.println("loadSettingsButton");
			if (returnVal == JFileChooser.APPROVE_OPTION){
				importPanels(settingsFileChooserLoad.getSelectedFile());
			}		
		}
	}
	class SaveDefaultSettingActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			savePanels("settings.default");
		}
	}
	
	class SaveSettingsButton implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("before saved");
			int saveVal = settingsFileChooserSave.showSaveDialog(null);
			System.out.println("saveSettingsButton");
			if (saveVal == JFileChooser.APPROVE_OPTION){
				File file = settingsFileChooserSave.getSelectedFile();
				savePanels(file.getAbsolutePath());
			}				
		}
	}
	
	class RunButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (mf.checkSpace(mf.getMinimalFreeSpaceForEditor())) {
				Thread t = new Thread(){
					@Override
					public 
					void run(){
						editorShouldBeRunning = true;
						controlerReference.resetData();
						controlerReference.resetProgressBar(getListProcessingStepPanels());
						String content = "-------------------------------------------------------------------------------------------------\n";
						content += new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
						content = content + ":\nStart of processing.\n\n";
						runButton.setEnabled(false);
						getMainFrameReference().writeToEditorLogfile(content);
						controlerReference.startProcessing(getListProcessingStepPanels());
						content = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
						content = content + ":\nEnd of processing.\n-------------------------------------------------------------------------------------------------\n";
						getMainFrameReference().writeToEditorLogfile(content);
						runButton.setEnabled(true);
					}
				};
				t.start();
			}
		}		
	}
	
	class StopButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			stopEditor();
		}
	}
	
	class CheckValidityActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			checkValidity();
		}
	}
}



