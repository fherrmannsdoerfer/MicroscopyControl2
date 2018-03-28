package microscopeControl;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import utility.Utility;



//This class takes care of the automated RapidStorm reconstruction during the measurment
//it relies on RapidStorm  and Python to be installed and properly linked in the MainFrame class
//using the variables 
//String pathToPython = "C:\\ProgramData\\Anaconda3\\python.exe";
//in case of python
//or in case of RapidStorm to be set properly in the following code block in this class (approximately line 175)
//it is important the for the path these / instead of these \ separators are used!
//String content = "import os\n"+"os.system(\"\\\"C:/Program Files/rapidstorm3/bin/rapidSTORM.exe\\\" "
//		+ "--inputFile "+pathToTiffFile+" --Basename "+outputBasename+pixelsize+image+
//		" --chooseTransmission Table "+fitMethode+" --ThreeD Spline3D --ZCalibration "
//		+calibrationFile+ " --AutoTerminate --run\")";

//The whole automated reconstruction is a bit complicated as it cannot directly call RapidStorm but uses Python to do so
//the reason for that is that the command line feature of RapidStorm3 was turned off for the windows release, but with the trick
//of Python calling it it works. So in order to start the reconstruction a python script with the instruction to start Rapidstorm
//is created and executed which in turn then starts RapidStorm and gives all necessary parameters
public class AutomatedReconstructionControl extends JPanel {
	
	MainFrame mf;
	
	JCheckBox chkBoxSimultaneousProcessing;
	JCheckBox chkBoxDo3D;
	JTextField txtCalibrationPath;
	JTextField thresholdText;
	JTextField txtRelativeOutputPath;
	JComboBox fitMethodSelectionChkBox;
	JButton recalculateEverythingButton;
	
	ReconControll rc = new ReconControll();
	Runnable t = new Thread(rc);
	ExecutorService executor = Executors.newFixedThreadPool(7);		
	
	//consturctor that creates the panel for the module
	public  AutomatedReconstructionControl(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		executor.execute(t); //start thread that handles reconstruction

		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);

		setBorder(new TitledBorder(null, "RapidSTORM", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new GridLayout(6, 1,10,10));
		
		
		chkBoxSimultaneousProcessing = new JCheckBox("Do Simultaneous Processing");
		chkBoxSimultaneousProcessing.setSelected(true);
		
		chkBoxDo3D = new JCheckBox("3D data");
		chkBoxDo3D.setSelected(true);
		
		Box horizontalBoxChkBoxes = Box.createHorizontalBox();
		horizontalBoxChkBoxes.add(chkBoxSimultaneousProcessing);
		horizontalBoxChkBoxes.add(Box.createHorizontalGlue());
		horizontalBoxChkBoxes.add(chkBoxDo3D);
		
		Box horizontalBoxCalibration = Box.createHorizontalBox();
		horizontalBoxCalibration.add(new JLabel("Path to Calibration File:"));
		horizontalBoxCalibration.add(Box.createHorizontalGlue());
		
		txtCalibrationPath = new JTextField();
		txtCalibrationPath.setText(mf.getPathTo3DCalibrationFileRapidStorm());
		Utility.setFormatTextFields(txtCalibrationPath, 200, 20, 100);

		Box horizontalBoxOutputPath = Box.createHorizontalBox();
		txtRelativeOutputPath = new JTextField();
		txtRelativeOutputPath.setText("Auswertung\\RapidStorm\\");
		horizontalBoxOutputPath.add(new JLabel("Relative Output Path:"));
		horizontalBoxOutputPath.add(Box.createHorizontalStrut(20));
		horizontalBoxOutputPath.add(txtRelativeOutputPath);
		
		Box horizontalBoxThresholdComboboxValue = Box.createHorizontalBox();
		fitMethodSelectionChkBox = new JComboBox();
		fitMethodSelectionChkBox.addItem("Local Relative Threshold");
		fitMethodSelectionChkBox.addItem("Absolute Threshold");
		
		thresholdText = new JTextField();
		//here you can set the default Local Relative Threshold
		thresholdText.setText("30");
		Utility.setFormatTextFields(thresholdText, 50, 20, 5);
		
		horizontalBoxThresholdComboboxValue.add(new JLabel("Threshold:"));
		horizontalBoxThresholdComboboxValue.add(Box.createHorizontalStrut(20));
		horizontalBoxThresholdComboboxValue.add(fitMethodSelectionChkBox);
		horizontalBoxThresholdComboboxValue.add(Box.createHorizontalGlue());
		horizontalBoxThresholdComboboxValue.add(thresholdText);
		
		Box horizontalBoxButton = Box.createHorizontalBox();
		
		recalculateEverythingButton = new JButton("Recalculate everything");
		recalculateEverythingButton.addActionListener(new RecalculateEverythingButtonActionListener());

		horizontalBoxButton.add(Box.createHorizontalGlue());
		horizontalBoxButton.add(recalculateEverythingButton);
		horizontalBoxButton.add(Box.createHorizontalGlue());
		
		this.add(horizontalBoxChkBoxes);
		this.add(horizontalBoxCalibration);
		this.add(txtCalibrationPath);
		this.add(horizontalBoxOutputPath);
		this.add(horizontalBoxThresholdComboboxValue);
		this.add(horizontalBoxButton);
		
	}
	
	//function that creates the folder used to store the the python scripts temporarily 
	public void createReconStuff(String outputPath){
		(new File(outputPath+"\\PythonSkripts")).mkdirs();
	}
	
	public void startReconstruction(String pathToTiffFile, String basename,String path, String outputPath, String measurementTag){
		//PrintWriter outputStream;
		//String outputPath = txtRelativeOutputPath.getText();
		//String path = mf.getPath();
		//String measurementTag = mf.getMeasurementTag();
		//(new File(path+"\\"+measurementTag+"\\Auswertung\\RapidStorm\\PythonSkripts")).mkdirs();
		String outputBasename = path+"\\"+measurementTag+"\\"+outputPath+"\\"+basename;
		//String outputBasename = path+"\\"+measurementTag+"\\Auswertung\\RapidStorm\\"+basename;
		//Python needs slashes instead of backslashes
		outputBasename = outputBasename.replace("\\", "/");
		pathToTiffFile = pathToTiffFile.replace("\\", "/");
		String calibrationFile = txtCalibrationPath.getText();
		String threshold = thresholdText.getText();
		boolean use3D = chkBoxDo3D.isSelected();
		calibrationFile = calibrationFile.replace("\\", "/");
		String fitMethode = "";
		if (fitMethodSelectionChkBox.getSelectedItem().toString().equals("Local Relative Threshold")){
			fitMethode = "--FitJudgingMethod SquareRootRatio --SNR "+ threshold;
		}
		else{
			fitMethode = "--AmplitudeThreshold "+ threshold;
		}
		String image = "";
		String pixelsize = "";
		if (use3D){
			image = "--ChooseTransmission Image --ColourScheme ByCoordinate --HueCoordinate PositionZ";
			pixelsize = " --PixelSizeInNM 129,121 ";
		}
		else{
			image = "--ChooseTransmission Image --ColourScheme Grayscale";
			pixelsize = " --PixelSizeInNM 129,129 ";
		}
		String scriptPath = path+"\\"+measurementTag+"\\"+outputPath+"\\PythonSkripts\\";
		OutputControl.createFolder(scriptPath);
		String filename = scriptPath+basename+".py";
		String content = "import os\n"+"os.system(\"\\\"C:/Program Files/rapidstorm3/bin/rapidSTORM.exe\\\" "
				+ "--inputFile "+pathToTiffFile+" --Basename "+outputBasename+pixelsize+image+
				" --chooseTransmission Table "+fitMethode+" --ThreeD Spline3D --ZCalibration "
				+calibrationFile+ " --AutoTerminate --run\")";
		
		//write the python script that when executed reconstructs the specified Tiff-Stack
		OutputControl.writeFile(filename, content);
		//the python script is added to the list of files to be processed
		rc.addFile(filename);
	}
	
		
	//class that processes all python scripts that are added to its fileList
	class ReconControll implements Runnable{
		//file list with .py files that wait for processing
		ArrayList<String> fileList = new ArrayList<String>();
		//state that defines whether or not a reconstruction can be started
		//it is set to false during the reconstruction process
		boolean isAvailable = true;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (isAvailable && fileList.size()>0){
					String toProcess = fileList.get(0);
					fileList.remove(0);
					try {
						isAvailable = false;
						System.out.println(toProcess);
						
						String pythonPath = mf.getPythonPath();
						Process proc = new ProcessBuilder().command(pythonPath,toProcess.replace("\\", "/")).start();
						try {
							Thread.sleep(1000);
							proc.waitFor();
							isAvailable = true;
						} catch (InterruptedException e) {
							isAvailable = true;
						}
						isAvailable = true;
					} catch (IOException e) {
						e.printStackTrace();
						isAvailable = true;
					}
					
				}
			}
		}
		public void addFile(String file){
			fileList.add(file);
		}
		
	}
	
	//the reconstruction of the currently specified measurement will be repeated
	//using the current parameters set in the AutomatedReconstructionConrol widget
	class RecalculateEverythingButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String path = mf.getPath();
			String outputPath = getRelativeOutputPath();
			String measurementTag = mf.getMeasurementTag();
			File dir = new File(path+"\\"+measurementTag+"\\RightChannel\\");
	    	File[] files = dir.listFiles(new FilenameFilter() { 
	    	         public boolean accept(File dir, String filename)
	    	              { return filename.endsWith(".tif"); }
	    	} );
	    	if (mf.getSelectedChannel() == 0||(mf.getSelectedChannel() == 1)){
		    	for (int i=0;i<files.length;i++){
		    		System.out.println(files[i].toString());
		    		String fname = files[i].getName();
		    		String[] parts = fname.split("\\.");
		    		String basename = parts[0];
		    		startReconstruction(files[i].toString(),basename, path, outputPath, measurementTag);
		    	}
		    }
	    	if (mf.getSelectedChannel() == 0||(mf.getSelectedChannel() == 2)){
		    	for (int i=0;i<files.length;i++){
		    		System.out.println(files[i].toString());
		    		String fname = files[i].getName();
		    		String[] parts = fname.split("\\.");
		    		String basename = parts[0];
		    		startReconstruction(files[i].toString(),basename, path, outputPath, measurementTag);
		    	}
		    }
	    	dir = new File(path+"\\"+measurementTag+"\\LeftChannel\\");
	    	files = dir.listFiles(new FilenameFilter() { 
	    	         public boolean accept(File dir, String filename)
	    	              { return filename.endsWith(".tif"); }
	    	} );
	    	for (int i=0;i<files.length;i++){
	    		System.out.println(files[i].toString());
	    		String fname = files[i].getName();
	    		String[] parts = fname.split("\\.");
	    		String basename = parts[0];
	    		startReconstruction(files[i].toString(),basename, path, outputPath, measurementTag);
	    	}
	    	
		}
	}
	//get and set the state of the checkbox which controls whether or not the reconstruction should be started during the measurement 
	public boolean isSimulatneousReconstruction() {return chkBoxSimultaneousProcessing.isSelected();}
	public void setStateDoSimulatneousReconstruction(boolean state) {
		chkBoxSimultaneousProcessing.setSelected(state);
	}
	
	//set the state for the checkbox which controls 3D vs 2D reconstruction
	public void setStateDo3DReconstruction(boolean state) {
		chkBoxDo3D.setSelected(state);
	}
	
	public boolean getStateDo3DReconstruction() {
		return chkBoxDo3D.isSelected();
	}
	
	public String getRelativeOutputPath() {
		return txtRelativeOutputPath.getText();
	}
	
}
