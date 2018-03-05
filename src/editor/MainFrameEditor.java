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
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import editorModulesDefinitions.CameraParametersGUI;
import editorModulesDefinitions.CaptureWidefieldImageGUI;
import editorModulesDefinitions.CommentaryBarGUI;
import editorModulesDefinitions.EndLoopGUI;
import editorModulesDefinitions.FilterWheelGUI;
import editorModulesDefinitions.FocusLockStateGUI;
import editorModulesDefinitions.LaserControl;
import editorModulesDefinitions.LoopIterableGUI;
import editorModulesDefinitions.LoopROIsGUI;
import editorModulesDefinitions.MeasurementTagGUI;
import editorModulesDefinitions.MoveFocalPlaneGUI;
import editorModulesDefinitions.MoveStageGUI;
import editorModulesDefinitions.PauseGUI;
import editorModulesDefinitions.PerformMeasurmentGUI;
import editorModulesDefinitions.PrepareMEABufferGUI;
import editorModulesDefinitions.RemoveSolutionFromSampleGUI;
import editorModulesDefinitions.StainingRobotCommandGUI;
import editorModulesDefinitions.StartImageAcquisitionGUI;
import editorModulesDefinitions.TransfereSolutionsFromVialToVial;
import editorModulesDefinitions.VortexVialGUI;
import editorModulesDefinitions.Wash3TimesWithPBSLS2GUI;
import editorModulesDefinitions.WashSyringeGUI;

public class MainFrameEditor extends JDialog implements Serializable{
	
	transient private MainFrame mf;
	private ArrayList<EditorModules> listProcessingStepPanels = new ArrayList<EditorModules>();
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
		
	File folder = new File(System.getProperty("user.home")+"//ExperimentEditor"); //Folder of savedPresettings
	private final ArrayList<EditorModules> stainingRobotComboBoxOptions = new ArrayList<EditorModules>();
	private final ArrayList<EditorModules> microscopeComboBoxOptions = new ArrayList<EditorModules>();
	private final ArrayList<EditorModules> loopsComboBoxOptions = new ArrayList<EditorModules>();

	public MainFrameEditor(final ControlerEditor controler, MainFrame mf) {
		this.mf = mf;
		setMinimumSize(style.getSizeEditor());
		folder.mkdir();
		optionPanel = new JPanel();
		final JFileChooser settingsFileChooserLoad = new JFileChooser(folder);
		final JFileChooser settingsFileChooserSave = new JFileChooser(folder);
	
		folder.mkdir();
		this.controlerReference = controler;
		outputActionListener = new OutputActionListener();
		
		//this.setBounds(0,0,1300,800);
		getContentPane().setPreferredSize(style.getSizeEditor());
		mfe = this;
		
		
		//////////////////////////////////////////////////////////////////// set options to choose from for drop-down menus; creates empty GUI class objects with member name

		microscopeComboBoxOptions.add(new LaserControl());
		microscopeComboBoxOptions.add(new StartImageAcquisitionGUI());
		microscopeComboBoxOptions.add(new MoveStageGUI());
		//microscopeComboBoxOptions.add(new IterableInputGUI());
		microscopeComboBoxOptions.add(new CaptureWidefieldImageGUI());
		microscopeComboBoxOptions.add(new PauseGUI());
		microscopeComboBoxOptions.add(new FilterWheelGUI());
		microscopeComboBoxOptions.add(new CameraParametersGUI());
		microscopeComboBoxOptions.add(new MeasurementTagGUI());
		microscopeComboBoxOptions.add(new CommentaryBarGUI());
		microscopeComboBoxOptions.add(new FocusLockStateGUI());
		microscopeComboBoxOptions.add(new MoveFocalPlaneGUI());
		microscopeComboBoxOptions.add(new PerformMeasurmentGUI());
		
		
		stainingRobotComboBoxOptions.add(new StainingRobotCommandGUI());
		stainingRobotComboBoxOptions.add(new RemoveSolutionFromSampleGUI());
		stainingRobotComboBoxOptions.add(new AddSolutionFromVialToSampleGUI());
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
		
		File[] listOfFiles = folder.listFiles();		
	    for (int i = 0; i < listOfFiles.length; i++) {
		if (listOfFiles[i].isFile()) {
			optionsPreselectedTasksComboBoxAuto.add(listOfFiles[i].getName());
			} 
	    }
		
		
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
		
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem loadSettingsButton = new JMenuItem("Load Settings");
		fileMenu.add(loadSettingsButton);
		
		JMenuItem loadDefaultSettingsButton = new JMenuItem("Load Default Settings");
		fileMenu.add(loadDefaultSettingsButton);
		
		loadDefaultSettingsButton.addActionListener(new ActionListener(){
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
		});
		
		loadSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("before loaded");
				int returnVal = settingsFileChooserLoad.showOpenDialog(null);
				System.out.println("loadSettingsButton");
				if (returnVal == JFileChooser.APPROVE_OPTION){
					loadPanels(settingsFileChooserLoad.getSelectedFile());
				}
			}
		});
		
		
		JMenuItem saveSettingsButton = new JMenuItem("Save Settings");
		fileMenu.add(saveSettingsButton);
		
		JMenuItem saveDefaultSettingsButton = new JMenuItem("Save Default Settings");
		fileMenu.add(saveDefaultSettingsButton);
		
		JButton runButton = new JButton("Start Processing");
		runButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		menuBar.add(runButton);
		
		runButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(){
					@Override
					public 
					void run(){
						editorShouldBeRunning = true;
						controler.resetData();
						controler.resetProgressBar(getListProcessingStepPanels());
						controler.startProcessing(getListProcessingStepPanels());
					}
				};
				t.start();
			}			
		});
		
		JButton stopButton = new JButton("Stop Processing");
		stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		menuBar.add(stopButton);
		
		stopButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				stopEditor();
			}			
		});
		
		JButton checkValidity = new JButton("Check Validity");
		checkValidity.setAlignmentX(Component.CENTER_ALIGNMENT);
		menuBar.add(checkValidity);
		
		checkValidity.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				checkValidity();
			}			
		});
		
		saveDefaultSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				savePanels("settings.default");
			}
		});
		
		saveSettingsButton.addActionListener(new ActionListener(){
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
		});
		
		JMenuItem quit = new JMenuItem("Exit");
		fileMenu.add(quit);
		quit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		Box wholeEditorBox = Box.createHorizontalBox();
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(wholeEditorBox);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		wholeEditorBox.add(horizontalStrut_1);
		
		Box availableModulesBox = Box.createVerticalBox();
		availableModulesBox.setMaximumSize(style.getDimensionAvailableModules());
		availableModulesBox.setMinimumSize(style.getDimensionAvailableModules());
		wholeEditorBox.add(availableModulesBox);
		
		JLabel lblNewLabel_6 = new JLabel("Available Modules");
		lblNewLabel_6.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 20));
		availableModulesBox.add(lblNewLabel_6);
		
		Component verticalStrut_3 = Box.createVerticalStrut(20);
		availableModulesBox.add(verticalStrut_3);
		
		///////////////////////////////////////////////////////////////////
		
		
		Box verticalBox_1 = Box.createVerticalBox();
		availableModulesBox.add(verticalBox_1);
		verticalBox_1.setMaximumSize(new Dimension(400, 99990));
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_5);
		
		JLabel lblNewLabel_3 = new JLabel("Preselected Tasks");
		horizontalBox_5.add(lblNewLabel_3);
		
		Component horizontalGlue_4 = Box.createHorizontalGlue();
		horizontalBox_5.add(horizontalGlue_4);
		
		preselectionComboBox = new JComboBox(optionsPreselectedTasksComboBoxAuto.toArray());
		preselectionComboBox.addActionListener(outputActionListener);
		preselectionComboBox.setMaximumSize(new Dimension(32767, 22));
		verticalBox_1.add(preselectionComboBox);		
		
		Component verticalStrut_2 = Box.createVerticalStrut(20);
		verticalBox_1.add(verticalStrut_2);
		
		Box verticalBox_2 = Box.createVerticalBox();
		verticalBox_2.setBorder(new TitledBorder(null, "Modules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox_1.add(verticalBox_2);
		
				
		Box horizontalBox_3 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_3);
		
		JLabel lblNewLabel_2 = new JLabel("Microscope Functions");
		horizontalBox_3.add(lblNewLabel_2);
		lblNewLabel_2.setAlignmentY(Component.TOP_ALIGNMENT);
		microscopeFunctionsComboBox = new JComboBox(optionsMicroscopeComboBox);
		microscopeFunctionsComboBox.addActionListener(outputActionListener);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_3.add(horizontalGlue_2);
		verticalBox_2.add(microscopeFunctionsComboBox);
		microscopeFunctionsComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		verticalBox_2.add(verticalStrut);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_2);
		
		JLabel lblNewLabel_1 = new JLabel("Staining Robot Functions");
		horizontalBox_2.add(lblNewLabel_1);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_1);
		
		stainingRobotFunctionsComboBox = new JComboBox(optionsStainingRobotComboBox);
		stainingRobotFunctionsComboBox.addActionListener(outputActionListener);
		verticalBox_2.add(stainingRobotFunctionsComboBox);
		stainingRobotFunctionsComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		verticalBox_2.add(verticalStrut_1);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		
		
		JLabel lblNewLabel = new JLabel("Loops");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		horizontalBox_1.add(lblNewLabel);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue);
		verticalBox_2.add(horizontalBox_1);
		
		loopsComboBox = new JComboBox(optionsLoopsComboBox);
		loopsComboBox.addActionListener(outputActionListener);
		verticalBox_2.add(loopsComboBox);
		loopsComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox_1.add(verticalGlue);
		
		Box verticalBox_5 = Box.createVerticalBox();
		verticalBox_1.add(verticalBox_5);
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		verticalBox_5.add(horizontalBox_6);
		
		Component horizontalGlue_5 = Box.createHorizontalGlue();
		horizontalBox_6.add(horizontalGlue_5);
		
		Box horizontalBox_7 = Box.createHorizontalBox();
		verticalBox_5.add(horizontalBox_7);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_7.add(horizontalGlue_3);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		wholeEditorBox.add(horizontalStrut);
		
		Box selectedModulesBox = Box.createVerticalBox();
		selectedModulesBox.setMaximumSize(style.getDimensionSelectedModules());
		selectedModulesBox.setMinimumSize(style.getDimensionSelectedModules());
		wholeEditorBox.add(selectedModulesBox);
		
		JLabel lblNewLabel_5 = new JLabel("Selected Modules");
		lblNewLabel_5.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 20));
		selectedModulesBox.add(lblNewLabel_5);
		
		Component verticalStrut_4 = Box.createVerticalStrut(20);
		selectedModulesBox.add(verticalStrut_4);
		
		panel = new RootPanel(this);
		
		wholeEditorBox.add(panel);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(panel);
		selectedModulesBox.add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(style.getWidthEditorModules()+60,1200));
		scrollPane.setMinimumSize(new Dimension(style.getWidthEditorModules()+30,500));
		scrollPane.setMaximumSize(new Dimension(style.getWidthEditorModules()+900,33200));
		
		Box parameterBox = Box.createVerticalBox();
		parameterBox.setMaximumSize(style.getDimensionParameters());
		parameterBox.setMinimumSize(style.getDimensionParameters());
		wholeEditorBox.add(horizontalStrut);
		wholeEditorBox.add(parameterBox);
		
		JLabel lblNewLabel_4 = new JLabel("Parameter Selection");
		lblNewLabel_4.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 20));
		parameterBox.add(lblNewLabel_4);
		
		Component verticalStrut_5 = Box.createVerticalStrut(20);
		parameterBox.add(verticalStrut_5);
		
		optionPanel = new JPanel();
		parameterBox.add(optionPanel);
		optionPanel.setMinimumSize(mfe.style.getDimensionParameters());
		
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
	
	
	
}



