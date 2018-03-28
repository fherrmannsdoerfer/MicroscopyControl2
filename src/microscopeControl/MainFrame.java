package microscopeControl;
import ij.ImagePlus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.micromanager.api.MultiStagePosition;
import org.micromanager.api.PositionList;
import org.micromanager.api.ScriptInterface;
import org.micromanager.utils.MMScriptException;

import utility.Utility;
import dataTypes.CameraParameters;
import dataTypes.ROIParameters;
import dataTypes.XYStagePosition;
import editor.ControlerEditor;
import editor.MainFrameEditor;
import mmcorej.CMMCore;

//MainFrame is the parent window for all control elements
public class MainFrame extends JFrame {

	//Path to the python executable
	String pathToPython = "C:\\ProgramData\\Anaconda3\\python.exe";
	
	//Path to log File Editor
	private String pathToLogFile = "D:\\Measurements\\LogFileEditor.txt";
	//Path to the exchange folder monitored by the Chronos plugin
	private String pathToExchangeFolder ="C:\\Users\\Public\\Folder For Chronos\\ExchangeFolder";
	private String pathTo3DCalibrationFileRapidStorm ="D:\\Measurements\\_STORM_general\\calibFiles\\20180301_Calibration_661Laser.txt";
	//name of the hardware set in Micro-Manager
	private String camName = "iXon Ultra";
	private String zObjectiveName = "FocusLocPIZMotorObjective";
	private String xyStageName = "SmaractXY";
	//Stage directly mounted on the xy stage
	private String zStageName = "PIZStage";
	private String mirrorFocuslock = "SmaractZSpiegel";
	private String filterWheelName = "Thorlabs Filter Wheel";
	private String[] filterNames = {"488 no UV", "561 no UV", "660", "488", "561", "free"};
	//name specified in Micro Manager
	private String[] laserNames = {"CoherentCube405","CoherentObis488","CoherentObis561","CoherentCube661"};
	
	//Wavelengths of the used lasers
	private String[] laserWavelengths = {"405 nm", "488 nm", "561 nm","661 nm"};
	private int indexOfUVLaser = 0;
	
	//minimal tolerated error for free disk memory in GB
	private double minimalFreeSpaceForSingleMeasurement = 16;
	private double minimalFreeSpaceForEditor = 200;
	
	//shift from center sample to objective
	//for the case that the center of the sample does not match with the center of the objective
	//this values can be used to reset the origin for the tilescan function and for the move to 
	//command from the StageControl class
	private double xShiftCenterSampleToObjective = 0;
	private double yShiftCenterSampleToObjective = 0;
	
	//reference position for autofocus
	private XYStagePosition referencePosition;
	private String outputPathForAutoFocus = "D:\\Measurements\\AutofocusTmp\\";
	private String referencePositionOutputPath = "D:\\Measurements\\";
	private String referencePositionOutputTag = "referenceMeasurementForAutofocus";
	private int exposureTimeForAutofocus = 100;
	private int gainForAutofocus = 10;
	private int nbrFramesForAutofocus = 100;
	private double lastMirrorPosition = -1;
	private int filterWheelPositionForAutoFocus = 2;
	private int laserIndexForAutoFocus = 3;
	private double laserPowerForAutoFocus = 2;
	
	//central object to control all hardware
	CMMCore core;
	
	//contains for example information about the position lists
	ScriptInterface app;
	
	//objects for all widgets 
	CameraWorker camWorker;
	XYStageWorker xyStageWorker;
	CameraParameter camParam;
	CameraControl camCon;
	StageControl stageCon;
	LaserControl laserCon;
	Display disp;
	OutputPathControl outCon;
	ROISettings roiSet;
	EditorControl editCon;
	AutomatedReconstructionControl autoRecCon;
	PifocPositionAndMonitor pifocCon;
	FilterWheelControl filterWheelCon;
	CommentControl comCon;
	StatusDisplayControl statusCon;
	
	JPanel contentPane;
	
	//variables from CameraParameter
	boolean showMiddleLine = false;
	boolean acquisitionIsRunning= false;
	int shiftX = 0;
	int shiftY = 0;
	//variables from CameraControl
	boolean livePreviewRunning = false;
	
	//dimensions for many program parts like CameraParameter, AutomatedReconstructionControl, ROISettings, etc
	int column2MinWidth = 260;
	int column2PrefWidth = 400;
	int column2MaxWidth = 400;
	Dimension minSize = new Dimension(column2MinWidth, 200);
	Dimension prefSize = new Dimension(column2PrefWidth, 200);
	Dimension maxSize = new Dimension(column2MaxWidth, 300);
	Dimension outPathDims = new Dimension(column2PrefWidth,90);
	Dimension camConDims = new Dimension(column2PrefWidth,140);
	String fontName = new JLabel().getFont().getFontName();
	int fontSize = new JLabel().getFont().getSize();
	Font titleFont = new Font(fontName,Font.BOLD,fontSize);
	JLabel messageLabel = new JLabel(".");
	
	private PositionList posList;
	private int currentFrame = 0;
	private int numberFramesForCurrentAcquisition = 0;
	
	File experimentFolder = new File(System.getProperty("user.home")+"//ExperimentEditor");
	final JFileChooser experimentFileChooserLoad = new JFileChooser(experimentFolder);
	
	//this variable stores the current position which is defined by the ROILoop
	private XYStagePosition currentXYPositionFromLoop;
	
	public MainFrame(CMMCore core, ScriptInterface app)
	{
		System.out.println("Central class that handles all interactions between interface and hardware. (MainFrame)");
		this.app = app;
		this.core = core;
		this.setBounds(100,00,972,1130);
		this.setResizable(false);
		contentPane = new JPanel();
		//initialize individual widgets (small windows e.g. for camera parameters). Every part of the GUI from microscope control
		//is organized in these widgets
		statusCon = new StatusDisplayControl(this);
		camWorker = new CameraWorker(this);
		xyStageWorker = new XYStageWorker(this);
		camParam = new CameraParameter(this, minSize,prefSize,maxSize);
		camCon = new CameraControl(this, camConDims,camConDims,camConDims);
		laserCon = new LaserControl(this);
		disp = new Display(this);
		outCon = new OutputPathControl(this,outPathDims,outPathDims,outPathDims);
		roiSet = new ROISettings(this,minSize,prefSize,maxSize);
		editCon = new EditorControl(this,minSize,prefSize,maxSize);
		autoRecCon = new AutomatedReconstructionControl(this, minSize, prefSize, maxSize);
		pifocCon = new PifocPositionAndMonitor(this,minSize,new Dimension(column2PrefWidth,250),maxSize);
		filterWheelCon = new FilterWheelControl(this, minSize, prefSize, maxSize);
		comCon = new CommentControl(this, minSize, prefSize, maxSize);
		stageCon = new StageControl(this,minSize,prefSize,maxSize);
		this.setContentPane(contentPane);
		this.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		this.setTitle("Microscope Control V2 Main Window");
		this.setVisible(true);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setMaximumSize(new Dimension(column2MaxWidth,300));
		tabbedPane.setPreferredSize(new Dimension(column2PrefWidth,250));
		
		tabbedPane.addTab("Camera Param.", camParam);
		tabbedPane.addTab("ROI Settings", roiSet);
		tabbedPane.addTab("RapidSTORM", autoRecCon);
		tabbedPane.addTab("Stage Control", stageCon);
		tabbedPane.addTab("Z-Calibration", new ZCalibration(this, minSize, prefSize, maxSize));
		tabbedPane.addTab("Filter Wheel", filterWheelCon);
		tabbedPane.addTab("Editor", editCon);
		
		JPanel column1 = new JPanel();
		column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
		column1.add(disp);
		column1.add(laserCon);
		column1.add(statusCon);
				
		JPanel column2 = new JPanel();
		column2.setLayout(new BoxLayout(column2, BoxLayout.Y_AXIS));
		column2.add(comCon);
		column2.add(tabbedPane);
		column2.add(outCon);
		column2.add(camCon);
		column2.add(pifocCon);		
		
		add(column1);
		add(column2);
		//function that sets settings like the exposure time for the first time
		setUp();
	}
	
	private void setUp() {
		camWorker.setExposureTime(camParam.getExposureTime());
		filterWheelCon.setFilterInitially(2);
		//einkommentieren wenn Z steuerung über Stage möglich ist
//		setZStagePosition(zStageName,100);
		pifocCon.setZStagePosition(25);
		xyStageWorker.setXYStageSpeed(500);		
	}
	
	public void setZStagePosition(String stagename,double position) {
		try {
			core.setProperty(stagename, "Position",position);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//getter for device names
	public String getCamName() {return camName;}

	//getter and setter for CameraParameter
	public boolean getShowMiddleLine() {return roiSet.isMiddleLineSelected();}
	public int getShiftX() {return roiSet.getShiftX();}
	public int getShiftY() {return roiSet.getShiftY();}
	
	//getter and setter for CameraControl
	public boolean getLivePreviewRunning() {return livePreviewRunning;}
	public void setLivePreviewRunning(boolean lpr) {livePreviewRunning = lpr;}

	public void setEnableStartAcquisition(boolean state) {
			acquisitionIsRunning = !state;
			camCon.setStartAcquisitionButtonState(state);
		}
	public void setEnableStartLivePreviewButton(boolean state) {camCon.setStartLivePreviewButtonState(state);}
	public void openShutter() {camWorker.openShutter();}
	public void closeShutter() {camWorker.closeShutter();}
	
	public int getEmGain() {return camParam.getEmGain();}
	public double getExposureTime() {return camParam.getExposureTime();}
	
	//function calls from Display
	public void resetRect() {disp.resetRect();}
	public void showCurrentImage(ImagePlus img) {disp.updateImage(img);}

	//function calls from OutputPathControl
	public String getPath() {return outCon.getPath();}
	public String getMeasurementTag() {return outCon.getMeasurementTag();}
	public String getOutputFolder() {
		return outCon.getPath()+"\\"+outCon.getMeasurementTag()+"\\";
	}
	public void createOutputFolder(){
		OutputControl.createFolder(getOutputFolder());
	}
	
	//function calls from ROISettings
	public int getSelectedChannel(){return roiSet.getSelectedChannel();}
	
	public double getZStagePosition() {return pifocCon.getZStagePosition();}
	public void setZStagePosition(double val) {pifocCon.setZStagePosition(val);}
	
	public void setAction(String action) {statusCon.setAction(action);}

	public void setFrameCount(String fc) {statusCon.setFrame(fc);}

	public void sleep(int ms) {core.sleep(ms);}

	public ImagePlus captureImage(){return camWorker.captureImage();}
	
	public String getZObjectiveName() {return zObjectiveName;}

	//public void setFocusLockState(int state) throws Exception {core.setProperty(zObjectiveName, "External sensor", state);}
	public void setFocusLockState(boolean state) {pifocCon.setFocusLockState(state);}
	
	public boolean isROISet() {return roiSet.isCustomROISet();}

	public boolean isROIApplied() {return roiSet.isROIApplied();}

	public void setROIselected(boolean state) {roiSet.setROIApplied(state);}

	public boolean isROITooSmall() {return roiSet.isROITooSmall();}

	public int getROIWidth() {return roiSet.getROIWidth();}
	public int getROIHeight() {return roiSet.getROIHeight();}

	public void startSequenceAcquisition(boolean applyChecks) {camWorker.startSequenceAcquisition(applyChecks);}

	public boolean isFrameTransferSelected() { return camParam.isFrameTransferSelected();}

	public int getFrameNumber() {return camParam.getFrameNumber();}

	public CMMCore getCoreObject() {return this.core;}

	public ROIParameters getROIParameters() {return roiSet.getROIParameters();}

	public void triggerPSFRateEstimation(ImagePlus imp) {laserCon.addBlinkingNumber(Utility.findNumberOfBlinkingEvents(imp));}

	public String getPythonPath() {return this.pathToPython;}
	public void startReconstruction(String pathToTiffFile, String basename,String path, String outputPath, String measurementTag) {autoRecCon.startReconstruction(pathToTiffFile, basename, path, outputPath, measurementTag);}
	public boolean isSimulatneousReconstruction() {return autoRecCon.isSimulatneousReconstruction();}
	
	public String getRelativeOutputPath() {return autoRecCon.getRelativeOutputPath();}
	
	public void setStateDoSimulatneousReconstruction(boolean state) {autoRecCon.setStateDoSimulatneousReconstruction(state);}
	
	public void setStateDo3DReconstruction(boolean state) {autoRecCon.setStateDo3DReconstruction(state);}

	public void stopSequenceAcquisition() {camWorker.stopSequenceAcquisition();}

	public void startLivePreview() {camWorker.startLivePreview();}
	public void stopLivePreview() {camWorker.stopLivePreview();}

	public Font getTitelFont() {return titleFont;}

	public String getFilterName(int i) {return filterNames[i];}

	public void setFilterWheelPosition(int index) {
		filterWheelCon.setFilterWheelPosition(index);
	}
	
	public String[] getLaserNames() {return this.laserNames;}

	public String[] getLaserWavelengths() {return this.laserWavelengths;}

	public int getIndexOfUVLaser() {return indexOfUVLaser;}	
	
	public PositionList getPositionList(){
		try {
			return app.getPositionList();
		} catch (MMScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return new PositionList();
		}
	}

	public void startEditor() {
		ControlerEditor controler = new ControlerEditor();
		MainFrameEditor mfe = new MainFrameEditor(controler,this);
		controler.setMainFrameReference(mfe);
		mfe.setVisible(true);
		mfe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public String loadExperiment() {
		int returnVal = experimentFileChooserLoad.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION){
			return experimentFileChooserLoad.getSelectedFile().getAbsolutePath();
		}
		return "Import Was Canceled";
	}

	public void startExperiment() {
		// TODO Auto-generated method stub
		
	}

	public void stopExperiment() {
		// TODO Auto-generated method stub
		
	}

	public void setCameraStatus(String status) {statusCon.setCameraStatus(status);}
	
	public String getXYStageName(){
		return xyStageName;
	}

	public XYStagePosition getXYStagePosition() {return xyStageWorker.getXYStagePosition();}
	public XYStagePosition getXYStagePositionShifted() {return xyStageWorker.getXYStagePositionShifted();}

	public void moveXYStage(double xPos, double yPos) {xyStageWorker.moveTo(xPos,yPos);}
	public void moveXYStageShiftedCoordinates(double xPosWrong, double yPosWrong) {xyStageWorker.moveToShiftedCoordinates(xPosWrong, yPosWrong);}
	
	WindowListener main_window_WindowListener =new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent arg0) {
			laserCon.shutDownAllLasers();
			camWorker.closeShutter();
		}
	};

	public void captureAndStoreWidefieldImage(double exposureTime) {
		double oldExposureTime = this.getExposureTime();
		camParam.setExposureTime(exposureTime);
		captureAndStoreWidefieldImage();
		camParam.setExposureTime(oldExposureTime);
	}
	
	
	public void captureAndStoreWidefieldImage() {
		ImagePlus img = captureImage();
		int counter = 0;
	    createOutputFolder();
		while(true){
			counter = counter + 1;
			File f = new File(getOutputFolder()+"\\"+getMeasurementTag()+"widefieldimg_"+counter+".tiff");
			if (f.exists()){}
			else{
				System.out.println(getOutputFolder()+"\\"+getMeasurementTag()+"widefieldimg_"+counter+".tiff");
				OutputControl.saveSingleImage(img, getOutputFolder()+"\\"+getMeasurementTag()+"widefieldimg_"+counter+".tiff");
				break;
			}
		}
	}

	public XYStagePosition getCurrentXYPositionFromLoop() {
		return currentXYPositionFromLoop;
	}

	public void setCurrentXYPositionFromLoop(XYStagePosition currentXYPositionFromLoop) {
		this.currentXYPositionFromLoop = currentXYPositionFromLoop;
	}

	public void setLaserIntensity(int index, double power) {laserCon.setLaserPower(index, power);
	}

	public void pauseThread(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] getFilterNames() {return filterNames;}

	public void setCameraParameter(CameraParameters camParam2) {camParam.setCameraParameters(camParam2);}

	public void setMeasurementTag(String text) {outCon.setMeasurementTag(text);}

	public void moveMirrorPositionRelative(double d) {
		setMirrorPosition(getMirrorPosition()+d);
	}
	
	public void setMirrorPosition(double d) {
		try {
			core.setPosition(mirrorFocuslock, (int)Math.round(d));
		} catch (Exception e) {
			e.printStackTrace();}
		}
	public double getMirrorPosition() {try {return core.getPosition(mirrorFocuslock);} catch(Exception e) {e.printStackTrace();return (Double) null;}}
	
	public boolean isAcquisitionRunning(){
		return acquisitionIsRunning;
	}
	
	public void setCurrentFrame(int currFrame) {
		this.currentFrame = currFrame;
	}
	
	public int getCurrentFrame() {
		return this.currentFrame;
	}

	public void setRoiParams(int width, int height, int posX, int posY) {
		roiSet.setROIWidth(width);
		roiSet.setROIHeight(height);
		roiSet.setPosX(posX);
		roiSet.setPosY(posY);
	}

	public int getNumberFramesForCurrentAcquisition() {
		return numberFramesForCurrentAcquisition;
	}

	public void setNumberFramesForCurrentAcquisition(int numberFramesForCurrentAcquisition) {
		this.numberFramesForCurrentAcquisition = numberFramesForCurrentAcquisition;
	}

	public String getPathToExchangeFolder() {
		return pathToExchangeFolder;
	}

	public void setPathToExchangeFolder(String pathToExchangeFolder) {
		this.pathToExchangeFolder = pathToExchangeFolder;
	}

	public String getFilterWheelName() {
		return this.filterWheelName;
	}

	public void setFilterWheelPositionCombobox(int index) {laserCon.setFilterSelectionToIndex(index);}

	public void setPathForMeasurment(String pathToOutputFolder) {
		outCon.setPathOutputFolder(pathToOutputFolder);
	}

	public String getPathTo3DCalibrationFileRapidStorm() {
		return pathTo3DCalibrationFileRapidStorm;
	}
	
	public boolean getStateDo3DReconstruction() {
		return autoRecCon.getStateDo3DReconstruction();
	}

	public void setPathTo3DCalibrationFileRapidStorm(String pathTo3DCalibrationFileRapidStorm) {
		this.pathTo3DCalibrationFileRapidStorm = pathTo3DCalibrationFileRapidStorm;
	}

	public String getPathToLogFile() {
		return pathToLogFile;
	}

	public void setPathToLogFile(String pathToLogFile) {
		this.pathToLogFile = pathToLogFile;
	}
	
	public void writeToEditorLogfile(String content) {
		OutputControl.writeStringToFile(content, pathToLogFile, true);
	}
	public void initializeCommentaryOutput(){
		comCon.writeCommentarySection();
	}
	public void writeCommentaryToOutputFolder(String content, String filename, boolean append){
		String outputPath = getOutputFolder()+filename;
		createOutputFolder();
		OutputControl.writeStringToFile(content, outputPath, append);
	}
	
	public void setEnableUVControlState(boolean state) {laserCon.setEnableUVControlState(state);}
	public void setMinimalBlinkingNbr(int nbr) {laserCon.setBlinkingNumber(nbr);}

	public double getXShift() {return xShiftCenterSampleToObjective;}
	public double getYShift() {return yShiftCenterSampleToObjective;}
	
	public double getFreeSpaceInGB(String harddrive) {return OutputControl.getFreeSpaceInGB(harddrive);}
	public boolean checkSpace(double minimalValue) {return Utility.checkSpace(getPath(), minimalValue, this);}

	public double getMinimalFreeSpaceForSingleMeasurement() {
		return minimalFreeSpaceForSingleMeasurement;
	}

	public void setMinimalFreeSpaceForSingleMeasurement(double minimalFreeSpaceForSingleMeasurement) {
		this.minimalFreeSpaceForSingleMeasurement = minimalFreeSpaceForSingleMeasurement;
	}

	public double getMinimalFreeSpaceForEditor() {
		return minimalFreeSpaceForEditor;
	}

	public void setMinimalFreeSpaceForEditor(double minimalFreeSpaceForEditor) {
		this.minimalFreeSpaceForEditor = minimalFreeSpaceForEditor;
	}

	public void findFocus(double upperBound, double lowerBound, double stepsize) {
		pifocCon.findAutoFocus(upperBound, lowerBound, stepsize);
	}

	public void setComments(String comment) {
		comCon.setComment(comment);
	}

	public void setReferencePosition(XYStagePosition xyStagePosition) {
		referencePosition = xyStagePosition;
	}

	public XYStagePosition getReferencePosition() {
		return referencePosition;
	}

	public String getReferencePositionOutputPath() {
		return referencePositionOutputPath;
	}

	public String getReferencePositionOutputTag() {
		return referencePositionOutputTag;
	}

	public int getExposureTimeForAutofocus() {
		return exposureTimeForAutofocus;
	}

	public int getGainForAutofocus() {
		return gainForAutofocus;
	}

	public int getNbrFramesForAutofocus() {
		return nbrFramesForAutofocus;
	}

	public void setLastMirrorPosition(double mirrorPosition) {
		lastMirrorPosition = mirrorPosition;
	}
	
	public double getLastMirrorPosition() {
		return lastMirrorPosition;
	}

	public int getFilterWheelPositionForAutoFocus() {
		return filterWheelPositionForAutoFocus;
	}

	public int getLaserIndexForAutoFocus() {
		return laserIndexForAutoFocus;
	}

	public double getLaserPowerForAutoFocus() {
		return laserPowerForAutoFocus;
	}

	public String getOutputPathForAutoFocus() {
		return outputPathForAutoFocus;
	}

	public String getPathForReferenceMeasurement() {
		return (referencePositionOutputPath+"\\"+referencePositionOutputTag+"\\");
	}

	public String getRelativeOutputTag() {
		return referencePositionOutputTag;
	}
	

	
}
