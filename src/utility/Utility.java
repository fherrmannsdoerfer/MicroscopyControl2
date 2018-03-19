package utility;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.MaximumFinder;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import microscopeControl.MainFrame;
import microscopeControl.OutputControl;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Polygon;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import dataTypes.ROIParameters;
import dataTypes.XYStagePosition;
import editor.MainFrameEditor;


//some useful general functions
public class Utility implements Serializable {
	
	//function to save space when formating the JTextFields
	static public JTextField setFormatTextFields(JTextField txtField, int width, int height, int nbrColumns){
		txtField.setHorizontalAlignment(JTextField.RIGHT);
		txtField.setMaximumSize(new Dimension(width, height));
		txtField.setMinimumSize(new Dimension(width, height));
		txtField.setColumns(nbrColumns);
		return txtField;
	}
	
	//function that parses the parameter to either return a value or in case of a tag starting with '%' the appropriate value
	//if multiple tags are used the output is concatenated
	//find the tags defined by %tag% and return the complete parameter String with replaced tags
		public static String parseParameter(String parameter, MainFrameEditor mfe){
			ArrayList<String> tags = new ArrayList<String>();
			String retString = "";
			int counterPercent = 0;
			int startStr = 0;
			for (int i =0; i<parameter.length();i++){
				if (parameter.substring(i, i+1).equals("%")){
					counterPercent += 1;
					//This condition is true for the first %
					if (((counterPercent-1)%2)==0){
						//if the first char is a % then there is nothing to add on the left side therefore skipp the adding
						if (i>0){retString += parameter.substring(startStr, i);}
						startStr = i;
					} else if (i>0& (counterPercent%2==0)) { //this condition is true for the second % therefore completing the current tag
						String currTag = parameter.substring(startStr, i+1);
						tags.add(currTag);
						retString += mfe.getControlerEditorReference().getIterationValue(currTag);
						startStr = i+1;
					}
				}
			}
			if (!parameter.contains("%")) {
				retString = parameter;
			}else { //if there is something behind the last tag is must be added as well
				retString += parameter.substring(startStr, parameter.length());
			}
			return retString;
		}
		
	
	public static ArrayList<ImagePlus> cropImages(ImageProcessor imp,
			boolean roiApplied, ROIParameters params) {
		if (roiApplied){
			imp.setRoi(params.getPosX(),params.getPosY(), params.getRoiWidth(), params.getRoiHeight());
		}
		else {
			imp.setRoi(0,0,256,512);
		}
		ImageProcessor leftChannel = imp.crop();
		if (roiApplied){
			imp.setRoi(params.getPosX() + params.getShiftX(),params.getPosY()+params.getShiftY(), params.getRoiWidth(), params.getRoiHeight());
		}
		else {
			imp.setRoi(256,0,256,512);
		}
		ImageProcessor rightChannel = imp.crop();
		ImagePlus leftImg = new ImagePlus("", leftChannel);
		ImagePlus rightImg = new ImagePlus("", rightChannel);
		ArrayList<ImagePlus> list = new ArrayList<ImagePlus>();
		list.add(leftImg);
		list.add(rightImg);
		return list;
	}
	
	//function that estimates the number of blinking events in the given frame
	//the image is slightly smoothed. Maxima that exceed the surrounding pixels
	//by more than 4 standard deviations (calculated form all pixels in the image)
	//are counted as blinking events.
	public static int findNumberOfBlinkingEvents(ImagePlus img){
		if (img != null){ 
			GaussianBlur gb = new GaussianBlur();
			gb.blurGaussian(img.getProcessor(), 1,1,0.02);
			MaximumFinder mf = new MaximumFinder();
			Polygon maxima = mf.getMaxima(img.getProcessor(), 4*img.getStatistics().stdDev,true);
			return maxima.npoints;
		}
		return 0;
	}

	/*
	public static ImagePlus stichTileScanAlt(ImagePlus[][] tileScanImages, int numberStepsX, int numberStepsY,
			int overlapInPixelsX, int overlapInPixelsY) {
		ImageProcessor iP = new FloatProcessor(numberStepsX*256, numberStepsY * 512);
		ImagePlus completeImage = new ImagePlus();
		for (int x = 0; x < numberStepsX ;x++){
			for (int y = 0; y < numberStepsY;y++){
				for (int xSmall = 0;xSmall < 256;xSmall++){
					for (int ySmall = 511; ySmall >= 0;ySmall--){
						iP.putPixelValue(xSmall+(x*256-overlapInPixelsX), ySmall+(y*512-overlapInPixelsY), (tileScanImages[x][y].getProcessor().getPixelValue(xSmall, ySmall)));
					}
				}
			}
		}
		completeImage.setProcessor(iP);
		return completeImage;
	}*/
	
	public static ImagePlus stichTileScan(ImagePlus[][] tileScanImages, int numberStepsX, int numberStepsY,
			int overlapInPixelsX, int overlapInPixelsY, boolean correctIntensities) {
 		int heightCoreRegion = 512-overlapInPixelsY;
		int widthCoreRegion = 256-overlapInPixelsX;
		int startPixelX = (256-widthCoreRegion)/2;
		int startPixelY = (512-heightCoreRegion)/2;
		ImageProcessor iP = new FloatProcessor(numberStepsX*widthCoreRegion, numberStepsY * heightCoreRegion);
		
		//try to correct for the Gaussian illumination by reducing the intensity of the center in the stiched image
		double[][] weight = new double[256][512]; 
		for (int xsmall =startPixelX, xPos = 0;xsmall <(startPixelX+widthCoreRegion);xsmall++, xPos++){
			for (int ysmall =startPixelY, yPos = 0;ysmall <(startPixelY+heightCoreRegion);ysmall++,yPos++){
				if (correctIntensities) {
					weight[xsmall][ysmall] = 1./Math.exp(-(Math.pow((xsmall-128), 2)+Math.pow(ysmall-256, 2))/(2*260*260));
				} else {
					weight[xsmall][ysmall]=1;
				}
			}
		}
		for (int x = 0; x < numberStepsX ;x++){
			for (int y = 0; y < numberStepsY;y++){
				for (int xsmall =startPixelX, xPos = 0;xsmall <(startPixelX+widthCoreRegion);xsmall++, xPos++){
					for (int ysmall =startPixelY, yPos = 0;ysmall <(startPixelY+heightCoreRegion);ysmall++,yPos++){
						iP.putPixelValue(xPos+widthCoreRegion*x, yPos+heightCoreRegion*y,weight[xsmall][ysmall]* tileScanImages[x][y].getProcessor().getPixelValue(xsmall, ysmall));
					}
				}
			}
		}
		ImagePlus completeImage = new ImagePlus();
		completeImage.setProcessor(iP);
		return completeImage;
	}
	
	private static Pair calculateShifts(double targetShiftX, double targetShiftY,XYStagePosition xyStagePosition) {
		double stageX = -xyStagePosition.getxPos()/1000;
		double stageY = xyStagePosition.getyPos()/1000;
		//intended point is left border with 1 mm safty margin
		int xShift = (int) (stageX+targetShiftX);
		int yShift = (int) (stageY+targetShiftY);
		
		if (xShift%2 == 0) {
			xShift +=1;
		}
		if (yShift%2 == 0) {
			yShift +=1;
		}
		
		while (true) {
			if ((Math.sqrt((xShift-stageX)*(xShift-stageX)+(yShift-stageY)*(yShift-stageY)))>6) {
				System.err.println("warning syringe to close to border!");
				xShift = xShift +2;
			}
			else {
				break;
			}
		}
		
		String stringX;
		String stringY;
		if (yShift>=0) {
			stringY = String.format("%02d", yShift);
		}
		else {
			stringY = String.format("%03d", yShift);
		}
		if (xShift>=0) {
			stringX = String.format("%02d", xShift);
		}
		else {
			stringX = String.format("%03d", xShift);
		}
		Pair retVal = new Pair(stringX,stringY);
		return retVal;
	}

	private static class Pair{
		public String xShift;
		public String yShift;
		public Pair (String xShift, String yShift) {
			this.xShift = xShift;
			this.yShift = yShift;
		}
	}
	
	public static void startChronosPlugin(String pathToExchangeFolder, String pathToSampleList) {
		//first check if there are files in the Chronos folder
		File[] contentOfExchangeFolder = (new File(pathToExchangeFolder)).listFiles();
		if (contentOfExchangeFolder.length>1) {
			System.out.println("There are to many files in the exchange folder!");
		}
		else {
			if (contentOfExchangeFolder.length ==0 || contentOfExchangeFolder[0].toString().contains("SampleListProcessed")) { //this hints to a successful execution of the last sample list presented to the Chronos software
				if (contentOfExchangeFolder.length== 1) {
					contentOfExchangeFolder[0].delete(); //delete the file
				}
				OutputControl.writeFileContainingSampleListPathToExchangeFolder(pathToExchangeFolder, pathToSampleList); //write a new file to the exchange folder containing the specified sample list
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					File[] newContentOfExchangeFolder = (new File(pathToExchangeFolder)).listFiles();
					if (newContentOfExchangeFolder[0].toString().contains("SampleListProcessed")) { //when Chronos is finished it replaces the File containing the path to the sample list with a file called SampleListProcessed the program can proceed then
						break;
					}
				}
			} else { //some other file is in the folder maybe from an other instance maybe a sample list that is currently processed, show error pause execution
				int result = -5555;

				result = JOptionPane.showConfirmDialog((Component) null, "Something went wrong, the robot might be already running! Hit any button to proceed.",
				        "alert", JOptionPane.OK_CANCEL_OPTION);
				
				//System.exit(-1);
			}
		}
	}

	public static void createSampleListForSolutionAdding(int vialNumber, int volume, boolean useLS2, boolean vortex, int vortexVolume, int vortexCycle, XYStagePosition xyStagePosition, String pathToExchangeFolder) {
		String template;
		if (useLS2) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromVialTokenVialPositionVolumeShiftXShiftYFillStrokesFillVolumeLS2.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromVialTokenVialPositionVolumeShiftXShiftYFillStrokesFillVolumeLS1.csl");
		}
		if (!vortex) {
			vortexCycle = 0;
		}
		
		String toReplaceVortexVolume = ">tokenVortexVolume<";
		String replacementVortexVolume = String.format(">%d<", vortexVolume);
		String toReplaceVortexCycles = ">tokenVortexCycles<";
		String replacementVortexCycles = String.format(">%d<", vortexCycle);
		
		String toReplaceVialNumber = ">tokenVial<";
		String replacementVialNumber = String.format(">%d<",vialNumber);
		String toReplaceVolume = ">tokenVolume<";
		String replacementVolume = String.format(">%d<",volume);
		
		String toReplacementShiftX = ">tokenShiftX<";
		String toReplacementShiftY = ">tokenShiftY<";
		//x gets minus since only one axis is inverted relative to the coordinate system of the staining robot
		String replacementShiftX = String.format(Locale.US,">%.1f<", -xyStagePosition.getxPos()/1000);
		String replacementShiftY = String.format(Locale.US,">%.1f<", xyStagePosition.getyPos()/1000);
		
		String outputXMLFileContent = template.replace(toReplaceVialNumber, replacementVialNumber);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolume, replacementVolume);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftX, replacementShiftX);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftY, replacementShiftY);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVortexVolume, replacementVortexVolume);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVortexCycles, replacementVortexCycles);
		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
	}
	
	
	
	public static void createSampleListForSolutionRemoval(int volumePerSpot, XYStagePosition xyStagePosition, String pathToExchangeFolder) {
		String template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionRemovalFromSampleTokenVolumePerSpotShiftXShiftYLS2.csl");
		String toReplaceVialNumber = ">tokenVolume<";
		String replacementVialNumber = String.format(">%d<",volumePerSpot);
				
		String toReplacementShiftX = ">tokenShiftX<";
		String toReplacementShiftY = ">tokenShiftY<";
		//x gets minus since only one axis is inverted relative to the coordinate system of the staining robot
		String replacementShiftX = String.format(Locale.US,">%.1f<", -xyStagePosition.getxPos()/1000);
		String replacementShiftY = String.format(Locale.US,">%.1f<", xyStagePosition.getyPos()/1000);
		
		String outputXMLFileContent = template.replace(toReplaceVialNumber, replacementVialNumber);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftX, replacementShiftX);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftY, replacementShiftY);
		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
	}

	public static void createSampleListForWashing3Times(int injectionVolume, int volumePerSpot, int waitTime,boolean removeSolutionFirst, boolean leaveSolutionLast,
			XYStagePosition xyStagePosition, String pathToExchangeFolder) {
		String template;
		if (removeSolutionFirst & leaveSolutionLast) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\Washing\\TemplateWashThreeTimesFindPBSLeavePBSTokenInjectionVolumeShiftXShiftYVolumePerSpotTimeLS2.csl");	
		} else if(removeSolutionFirst & !leaveSolutionLast) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\Washing\\TemplateWashThreeTimesFindPBSLeaveDryTokenInjectionVolumeShiftXShiftYVolumePerSpotTimeLS2.csl");
		} else if(!removeSolutionFirst & !leaveSolutionLast) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\Washing\\TemplateWashThreeTimesFindDryLeaveDryTokenInjectionVolumeShiftXShiftYVolumePerSpotTimeLS2.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\Washing\\TemplateWashThreeTimesFindDryLeavePBSTokenInjectionVolumeShiftXShiftYVolumePerSpotTimeLS2.csl");
		}
		
		String toReplaceInjection = ">tokenInjection<";
		String replacementInjection = String.format(">%d<",injectionVolume);
		String toReplaceVolume = ">tokenVolume<";
		String replacementVolume = String.format(">%d<",volumePerSpot);
		String toReplaceTime = ">tokenTime<";
		String replacementTime = String.format(">%d<", waitTime);
		
		String toReplacementShiftX = ">tokenShiftX<";
		String toReplacementShiftY = ">tokenShiftY<";
		//x gets minus since only one axis is inverted relative to the coordinate system of the staining robot
		String replacementShiftX = String.format(Locale.US,">%.1f<", -xyStagePosition.getxPos()/1000);
		String replacementShiftY = String.format(Locale.US,">%.1f<", xyStagePosition.getyPos()/1000);
		
		String outputXMLFileContent = template.replace(toReplaceInjection, replacementInjection);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolume, replacementVolume);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceTime, replacementTime);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftX, replacementShiftX);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftY, replacementShiftY);
		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
		
	}

	public static void createSampleListForSolutionAddingFromWashingStation(int index, int volume, boolean useLS2,
			int pumpDuration, XYStagePosition xyStagePosition, String pathToExchangeFolder) {
		String template;
		if (useLS2) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromWashingRunPumpStationTokenVolumeIndexShiftXShiftYLS2.csl");			
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromWashingRunPumpStationTokenVolumeIndexShiftXShiftYLS1.csl");
		}
		String toReplaceIndex = ">tokenIndex<";
		String replacementIndex = String.format(">%d<",index);
		String toReplaceVolume = "tokenVolume";
		String replacementVolume = String.format("%d",volume);
		String toReplacePumpDuration = ">tokenPumpDuration<";
		String replacementPumpDuration = String.format(">%d<",pumpDuration);
		
		String toReplacementShiftX = ">tokenShiftX<";
		String toReplacementShiftY = ">tokenShiftY<";
		//x gets minus since only one axis is inverted relative to the coordinate system of the staining robot
		String replacementShiftX = String.format(Locale.US,">%.1f<", -xyStagePosition.getxPos()/1000);
		String replacementShiftY = String.format(Locale.US,">%.1f<", xyStagePosition.getyPos()/1000);
		
		String outputXMLFileContent = template.replace(toReplaceIndex, replacementIndex);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolume, replacementVolume);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftX, replacementShiftX);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacementShiftY, replacementShiftY);
		outputXMLFileContent = outputXMLFileContent.replace(toReplacePumpDuration, replacementPumpDuration);
		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
	}

	public static void createSampleListForSolutionAddingFromWashingStationToVial(int index, int volume, int vialNumber, String pathToExchangeFolder) {
		String template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromWashingStationToVialTokenVolumeIndexVialNbrLS2.csl");
		String toReplaceVialNumber = ">tokenVial<";
		String replacementVialNumber = String.format(">%d<",vialNumber);
		String toReplaceIndex = ">tokenIndex<";
		String replacementIndex = String.format(">%d<",index);
		String toReplaceVolume = ">tokenVolume<";
		String replacementVolume = String.format(">%d<",volume);
		
		String outputXMLFileContent = template.replace(toReplaceVialNumber, replacementVialNumber);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolume, replacementVolume);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndex, replacementIndex);

		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
	}
	
	public static void createSampleListForRunPumps(int index, int time, boolean useLS2, String pathToExchangeFolder) {
		String template;
		if (useLS2) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateRunPumpsTokenPumpNumberPumpTimeLS2_MK.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateRunPumpsTokenPumpNumberPumpTimeLS1_MK.csl");
		}
		String toReplaceTime = ">tokenPumpDuration<";
		String replacementTime = String.format(">%d<",time);
		String toReplaceIndex = ">tokenWStationIndex<";
		String replacementIndex = String.format(">%d<",index);
		
		String outputXMLFileContent = template.replace(toReplaceTime, replacementTime);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndex, replacementIndex);

		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
	}


	public static void createSampleListForWashingSyringe(int index, int repetitions, boolean useLS2,
			String pathToExchangeFolder) {
		String template;
		if (useLS2) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\WashSyringe\\WashSyringeTokenIndexRepetitionsLS2.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\WashSyringe\\WashSyringeTokenIndexRepetitionsLS1.csl");
		}
		String toReplaceRepetition = ">tokenReps<";
		String replacementRepetition = String.format(">%d<",repetitions);
		String toReplaceIndex = ">tokenIndex<";
		String replacementIndex = String.format(">%d<",index);

		
		String outputXMLFileContent = template.replace(toReplaceRepetition, replacementRepetition);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndex, replacementIndex);

		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
		
	}

	public static void createSampleListForVortexingVial(int index, int repetitions, int vortexVolume, boolean useLS2,
			String pathToExchangeFolder) {
		String template;
		if (useLS2) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateVortexVialTokenIndexRepetitionsVolumeLS2.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateVortexVialTokenIndexRepetitionsVolumeLS1.csl");
		}
		String toReplaceIndexSource = ">tokenIndexSource<";
		String replacementIndexSource = String.format(">%d<",index);
		String toReplaceReps = ">tokenReps<";
		String replacementReps = String.format(">%d<", repetitions);
		String toReplaceVolumeVortex = ">tokenVortexVolume<";
		String replacementVolumeVortex = String.format(">%d<", vortexVolume);

		
		String outputXMLFileContent = template.replace(toReplaceIndexSource, replacementIndexSource);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceReps, replacementReps);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolumeVortex, replacementVolumeVortex);

		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
	}

	public static void createSampleListForTransfereFromVialToVial(int indexSource, int indexDest, int volume,
			boolean useLS2, boolean vortex, int vortexVolume, int vortexReps, String pathToExchangeFolder) {
		String template;
		if (useLS2) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateTransfereSolutionFromVialToVialTokensVolumeVialSourceVialDestFillStrokesFillVolumeLS2.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateTransfereSolutionFromVialToVialTokensVolumeVialSourceVialDestFillStrokesFillVolumeLS1.csl");
		}
		if (!vortex) {
			vortexReps = 0;
		}
		String toReplaceVolume = ">tokenVolume<";
		String replacementVolume = String.format(">%d<",volume);
		String toReplaceIndexSource = ">tokenIndexSource<";
		String replacementIndexSource = String.format(">%d<",indexSource);
		String toReplaceIndexDest = ">tokenIndexDest<";
		String replacementIndexDest = String.format(">%d<",indexDest);
		String toReplaceNbrFillStrokes = ">tokenNbrFillStrokes<";
		String replacementNbrFillStrokes = String.format(">%d<", vortexReps);
		String toReplaceVolumeVortex = ">tokenVortexVolume<";
		String replacementVolumeVortex = String.format(">%d<", vortexVolume);
		
		String outputXMLFileContent = template.replace(toReplaceVolume, replacementVolume);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndexSource, replacementIndexSource);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndexDest, replacementIndexDest);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceNbrFillStrokes, replacementNbrFillStrokes);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolumeVortex, replacementVolumeVortex);

		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
	}

	public static void createSampleListForPreparationOfMEA(int volumePBSForStock, int indexVialMeaStock,
			int indexVialMeaFinal, int volumePBSForFinal, int volumeMEAStockForFinal, int volumeNaOHForFinal,
			int indexVialNaOH, int nbrVortexCycles, int nbrWashingCycles, int volumeVortex, String pathToExchangeFolder, boolean createStock) {
		String template;
		if (createStock) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplatePrepareBufferAndMEATokenVolumesVialsVortexFillStrokesFillVolumeLS1AndLS2.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplatePrepareBufferTokenVolumesVialsVortexFillStrokeFillVolumeLS1AndLS2.csl");
		}
		String toReplaceVolPBSForStock = ">tokenPBSForStock<";
		String replacementVolPBSForStock = String.format(">%d<",volumePBSForStock);
		String toReplaceIndexVialMeaStock = ">tokenIndexMEAStock<";
		String replacementIndexVialMeaStock = String.format(">%d<",indexVialMeaStock);
		String toReplaceIndexVialMeaFinal = ">tokenIndexMEAFinal<";
		String replacementIndexVialMeaFinal = String.format(">%d<",indexVialMeaFinal);
		String toReplaceVolumePBSForFinal = ">tokenVolumePBSForFinal<";
		String replacementVolumePBSForFinal = String.format(">%d<",volumePBSForFinal);
		String toReplaceVolumeMeaStockForFinal = ">tokenMEAStockForFinal<";
		String replacementVolumeMeaStockForFinal = String.format(">%d<",volumeMEAStockForFinal);
		String toReplaceVolumeNaOHForFinal = ">tokenNaOHForFinal<";
		String replacementIndexDest = String.format(">%d<",volumeNaOHForFinal);
		String toReplaceIndexVialNaOH = ">tokenIndexNaOH<";
		String replacementIndexVialNaOH = String.format(">%d<",indexVialNaOH);
		String toReplaceNbrVortexCycles = ">tokenRepsVortex<";
		String replacementNbrVortexCycles = String.format(">%d<",nbrVortexCycles);
		String toReplaceNbrWashingCycles = ">tokenRepsWash<";
		String replacementNbrWashingCycles = String.format(">%d<",nbrWashingCycles);
		String toReplaceVolumeVortex = ">tokenVolumeVortex<";
		String replacementVolumeVortex = String.format(">%d<",volumeVortex);
	
		String outputXMLFileContent = template.replace(toReplaceVolPBSForStock, replacementVolPBSForStock);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndexVialMeaStock, replacementIndexVialMeaStock);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndexVialMeaFinal, replacementIndexVialMeaFinal);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolumePBSForFinal, replacementVolumePBSForFinal);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolumeMeaStockForFinal, replacementVolumeMeaStockForFinal);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolumeNaOHForFinal, replacementIndexDest);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceIndexVialNaOH, replacementIndexVialNaOH);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceNbrVortexCycles, replacementNbrVortexCycles);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceNbrWashingCycles, replacementNbrWashingCycles);
		outputXMLFileContent = outputXMLFileContent.replace(toReplaceVolumeVortex, replacementVolumeVortex);

		OutputControl.writeFile("C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl", outputXMLFileContent);
		startChronosPlugin(pathToExchangeFolder, "C:\\Users\\Public\\Folder For Chronos\\tmpSampleList.csl");
		
	}
	
	public static boolean checkSpace(String outputPath, double minimalToleratedSpace,MainFrame mf) {
		String harddrive = outputPath.substring(0, 2);
		if (mf.getFreeSpaceInGB(harddrive)< minimalToleratedSpace) {
			int result = JOptionPane.showConfirmDialog((Component) null, "There is only "+mf.getFreeSpaceInGB(harddrive)+" gb of free space left! Do you want to proceed or cancel?",
			        "alert", JOptionPane.OK_CANCEL_OPTION);
			if (result ==2) {
				return false;
			}
		}
		return true;
		
	}
	
}
