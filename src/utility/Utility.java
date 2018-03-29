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
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.math3.util.Precision;

import comperators.StormLocalizationFrameComperator;

import dataTypes.CameraParameters;
import dataTypes.ROIParameters;
import dataTypes.StormData;
import dataTypes.StormLocalization;
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
					weight[xsmall][ysmall] = 1./Math.exp(-(Math.pow((xsmall-128), 2)+Math.pow(ysmall-256, 2))/(2*325*325));
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
		if (useLS2 && vortex) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromVialTokenVialPositionVolumeShiftXShiftYFillStrokesFillVolumeLS2.csl");
		} else if(useLS2 &&!vortex) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromVialTokenVialPositionVolumeShiftXShiftYFillVolumeLS2.csl");
		}
		else if (!useLS2 && vortex) {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromVialTokenVialPositionVolumeShiftXShiftYFillStrokesFillVolumeLS1.csl");
		} else {
			template = OutputControl.readFile("C:\\Users\\Public\\Documents\\Chronos\\Sample lists\\FinalSampleLists\\TemplateSolutionPlacementFromVialTokenVialPositionVolumeShiftXShiftYFillVolumeLS1.csl");
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
	
	public static boolean showCheckChronosSoftwareStateDialog() {
		int result = JOptionPane.showConfirmDialog((Component) null, "Is the Chronos plugin started? Press ok to proceed (after you turned it on) and cancel to abort!",
		        "alert", JOptionPane.OK_CANCEL_OPTION);
		if (result ==2) {
			return false;
		}
		return true;
	}
	
	//function to write black numbers close to the center of an ImagePlus
	public static ImagePlus annotateImage(ImagePlus temp, double xPos, double yPos) {
		int spaceX = 2;
		int spaceY = 3;
		int startX =128;
		int startY = 256;
		xPos = Precision.round(xPos,0);
		yPos = Precision.round(yPos,0);
		double[] positions = {xPos,yPos};
		int[] valuesX ={1,0,1,
						1,0,1,
						0,1,0,
						1,0,1,
						1,0,1};
		int[] valuesY = {1,0,1,
						1,0,1,
						0,1,0,
						0,1,0,
						0,1,0};
		int[] valuesDouble = {0,0,0,
							0,1,0,
							0,0,0,
							0,1,0,
							0,0,0};
		int[] valuesMinus = {	0,0,0,
								0,0,0,
								1,1,1,
								0,0,0,
								0,0,0};
		int[] valuesPlus = {	0,0,0,
								0,1,0,
								1,1,1,
								0,1,0,
								0,0,0};
		int[] values0 = {1,1,1,
					    1,0,1,
					    1,0,1,
					    1,0,1,
					    1,1,1};
		int[] values1 = {0,0,1,
						 0,1,1,
						 0,0,1,
						 0,0,1,
						 0,0,1};
		int[] values2 = {1,1,1,
						 0,0,1,
						 1,1,1,
						 1,0,0,
						 1,1,1};
		int[] values3 ={1,1,1,
				 		0,0,1,
				 		1,1,1,
				 		0,0,1,
				 		1,1,1};
		int[] values4 ={1,0,1,
		 				1,0,1,
		 				1,1,1,
		 				0,0,1,
		 				0,0,1};
		int[] values5 ={1,1,1,
		 				1,0,0,
		 				1,1,1,
		 				0,0,1,
		 				1,1,1};
		int[] values6 ={1,1,1,
		 				1,0,0,
		 				1,1,1,
		 				1,0,1,
		 				1,1,1};
		int[] values7 ={1,1,1,
						0,0,1,
						0,1,1,
						0,0,1,
						0,0,1};
		int[] values8 ={1,1,1,
						1,0,1,
						1,1,1,
						1,0,1,
						1,1,1};
		int[] values9 ={1,1,1,
						1,0,1,
						1,1,1,
						0,0,1,
						1,1,1};
			
		for (int h =0;h<2;h++) {
			//add a plus sign to positive numbers
			boolean isPositive = positions[h]>=0;
			String currNumberStr;
			if (isPositive) {
				currNumberStr = "+"+positions[h];
			} else {
				currNumberStr = ""+positions[h];
			}
			String[] parts = currNumberStr.split("\\.");
			currNumberStr = parts[0];
			
			for (int i = 0;i<currNumberStr.length()+2;i++) {
				int[] template = new int[15];
				if (i>2) {
					try {
						switch (Integer.parseInt(currNumberStr.substring(i-2, i-1))) {
						case 0:
							template = values0;
							break;
						case 1:
							template = values1;
							break;
						case 2:
							template = values2;
							break;
						case 3:
							template = values3;
							break;
						case 4:
							template = values4;
							break;
						case 5:
							template = values5;
							break;
						case 6:
							template = values6;
							break;
						case 7:
							template = values7;
							break;
						case 8:
							template = values8;
							break;
						case 9:
							template = values9;
							break;
							
						}
					} catch (NumberFormatException e) {
						System.out.println(currNumberStr);
					}
				} else if (i==2) {
					if (isPositive) {
						template = valuesPlus;
					} else {
						template = valuesMinus;
					}
				}
				else if (i==1) {
					template = valuesDouble;
				} else if (i==0 &&h ==0) {
					template = valuesX;
				}
				else {
					template = valuesY;
				}
				
				for (int j = 0;j< 15;j++) {
					int idxX = startX+j%3+i*(3+spaceX);
					int idxY = (int) (startY+Math.floor(j/3))+(5+spaceY)*h;
					temp.getProcessor().putPixelValue(idxX, idxY, temp.getProcessor().getPixel(idxX, idxY)*Math.abs(1-template[j]));
				}
			}
		}	
		return temp;
	}
	
	public static void findFocusLockMirrorPosition(int mirrorPosInterval, int lowerLimit, int upperLimit , MainFrame mf) {
		//falls 2. Filterwheel 1 für Linse 0 ohne linse
		//mf.getCoreObject().setProperty(filterWheelName2tesFilterWheel, "State", 1);
		mf.setFocusLockState(false);
		mf.sleep(1000);
		mf.setFocusLockState(true);
		mf.sleep(1000);
		mf.moveXYStage(mf.getReferencePosition().getxPos(), mf.getReferencePosition().getyPos());
		double initialMirrorPos = mf.getLastMirrorPosition();
		String outputpathtmp = mf.getOutputPathForAutoFocus();
		deleteFolder(new File(outputpathtmp));
		int nbrImagesPerRound = mf.getNbrFramesForAutofocus();
		int exposureTime = mf.getExposureTimeForAutofocus();
		int laserIndex = mf.getLaserIndexForAutoFocus();
		double laserPower = mf.getLaserPowerForAutoFocus();
		int filterWheelPos = mf.getFilterWheelPositionForAutoFocus();
		ArrayList<Double> trueMirrorPosition = new ArrayList<Double>();
		mf.setAction("Autofocus");
		CameraParameters camParam = new CameraParameters(exposureTime, mf.getGainForAutofocus(), nbrImagesPerRound, true);
		mf.setCameraParameter(camParam);
		mf.setFilterWheelPosition(filterWheelPos);
		mf.setLaserIntensity(laserIndex, laserPower);
		//reference image acquisition
		for (int relDiff = lowerLimit; relDiff<upperLimit; relDiff=(int) (relDiff+mirrorPosInterval)) {
			mf.setPathForMeasurment(outputpathtmp);
			mf.setMeasurementTag("relDiff"+relDiff);
			
			mf.setMirrorPosition(initialMirrorPos+relDiff);
			mf.sleep(200);
			trueMirrorPosition.add(mf.getMirrorPosition());
			mf.startSequenceAcquisition(false);
			while (mf.isAcquisitionRunning()){
				mf.sleep(200);
			}
		}
		mf.setLaserIntensity(laserIndex, 0.1);
		//find and evaluate bead z values
		mf.sleep(2500);
		double referenceMeanZ = getMeanZValue(mf.getPathForReferenceMeasurement()+mf.getRelativeOutputPath()+"LeftChannel"+mf.getRelativeOutputTag()+"pt000.txt");
		System.out.println(referenceMeanZ);
		ArrayList<Double> meanZMeasurement = new ArrayList<Double>();
		ArrayList<Double> difference = new ArrayList<Double>();
		ArrayList<Integer> shifts = new ArrayList<Integer>();
		for (int relDiff = lowerLimit, i=0; relDiff<upperLimit; relDiff=(int) (relDiff+mirrorPosInterval),i++) {
			meanZMeasurement.add(getMeanZValue(outputpathtmp+"\\relDiff"+relDiff+"\\Auswertung\\RapidStorm\\LeftChannelrelDiff"+relDiff+"pt000.txt"));
			difference.add(meanZMeasurement.get(i)-referenceMeanZ);
			shifts.add(relDiff);
		}
		//find mirror position that matches closest to the reference position by finding switch in sign in the difference
		boolean lastDifferenceWasPositive = difference.get(0)>0;
		//needed shift for mirror position
		double trueDiff = 0;
		for (int relDiff = lowerLimit, i=0; relDiff<upperLimit; relDiff=(int) (relDiff+mirrorPosInterval), i++) {
			boolean thisDifferenceIsPositive = difference.get(i)>0;
			if (lastDifferenceWasPositive == thisDifferenceIsPositive) {
				
			} else {
				trueDiff = shifts.get(i)- (meanZMeasurement.get(i) - referenceMeanZ)/( meanZMeasurement.get(i-1) - meanZMeasurement.get(i)) * (shifts.get(i-1) - shifts.get(i));
				break;
			}
		}
		mf.setMirrorPosition(initialMirrorPos + trueDiff);
		mf.setLastMirrorPosition(initialMirrorPos + trueDiff);
		mf.setAction("Standby");
		////falls 3D filterwheel auf Position 1 lassen, falls 2. Filterwheel auf Position 0 stellen
		//if (mf.getStateDo3DReconstruction()) {
		//	mf.getCoreObject().setProperty(filterWheelName2tesFilterWheel, "State", 1);
		//} else {
		//	mf.getCoreObject().setProperty(filterWheelName2tesFilterWheel, "State", 0);
		//}
		
	}
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	public static double getMeanZValue(String fname) {
		StormData reference = OutputControl.readStormData(fname);
		if (reference.getSize()==0) {
			System.out.println("No localizations found!!!");
			return 0;
		}
		ArrayList<ArrayList<StormLocalization>> beads = findBeads(reference, reference);
		double meanZ = 0;
		for (int i = 0; i<beads.get(0).size();i++) {
			meanZ += beads.get(0).get(i).getZ();
		}
		meanZ = meanZ/beads.get(0).size();
		return meanZ;
	}
	
	
	public static ArrayList<ArrayList<double[]>> findBeadCandidatesTraceBased(StormData sd1, 
			StormData sd2, int minimalTracelength){
		ArrayList<ArrayList<double[]>> retList = new ArrayList<ArrayList<double[]>>();
		ArrayList<double[]> candidatesCh1 = new ArrayList<double[]>();
		ArrayList<double[]> candidatesCh2 = new ArrayList<double[]>();
		ArrayList<ArrayList<StormLocalization>> tracesCh1 = 
				findTraces(sd1.getLocs(), 200, 200, 400, 50);
		int maxTraceLength = 0;
		for (int i = 0; i< tracesCh1.size(); i++){
			if (tracesCh1.get(i).size() > maxTraceLength){
				maxTraceLength = tracesCh1.get(i).size();
				System.out.println("Maximal TraceLengthCh1: "+maxTraceLength);
			}
			if (tracesCh1.get(i).size()>minimalTracelength){
				double meanX = 0;
				double meanY = 0;
				for (int j = 0; j<tracesCh1.get(i).size(); j++){
					meanX = meanX + tracesCh1.get(i).get(j).getX();
					meanY = meanY + tracesCh1.get(i).get(j).getY();
				}
				double[] tmp = {meanX / tracesCh1.get(i).size(),meanY / tracesCh1.get(i).size()};
				candidatesCh1.add(tmp);
			}
		}
		ArrayList<ArrayList<StormLocalization>> tracesCh2 = 
				findTraces(sd2.getLocs(), 200, 200, 200, 50);
		maxTraceLength = 0;
		for (int i = 0; i< tracesCh2.size(); i++){
			if (tracesCh2.get(i).size() > maxTraceLength){
				maxTraceLength = tracesCh2.get(i).size();
				System.out.println("Maximal TraceLengthCh2: "+maxTraceLength);
			}
			if (tracesCh2.get(i).size()>minimalTracelength){
				double meanX = 0;
				double meanY = 0;
				for (int j = 0; j<tracesCh2.get(i).size(); j++){
					meanX = meanX + tracesCh2.get(i).get(j).getX();
					meanY = meanY + tracesCh2.get(i).get(j).getY();
				}
				double[] tmp = {meanX / tracesCh2.get(i).size(),meanY / tracesCh2.get(i).size()};
				candidatesCh2.add(tmp);
			}
		}
		retList.add(candidatesCh1);
		retList.add(candidatesCh2);
		return retList;
	}
	
	private static ArrayList<ArrayList<StormLocalization>> findBeads(StormData sd1, StormData sd2) {
		ArrayList<ArrayList<double[]>> beadEstimates = findBeadCandidatesTraceBased(sd1,sd2,20);

		ArrayList<ArrayList<StormLocalization>> listOfBeadsCh1 = new ArrayList<ArrayList<StormLocalization>>(); //an Arraylist for each potential bead
		ArrayList<ArrayList<StormLocalization>> listOfBeadsCh2 = new ArrayList<ArrayList<StormLocalization>>(); //to collect all localizations to be averaged later
		for (int i = 0; i<beadEstimates.get(0).size(); i++){
			listOfBeadsCh1.add(new ArrayList<StormLocalization>());
		}
		for (int i = 0; i<beadEstimates.get(1).size(); i++){
			listOfBeadsCh2.add(new ArrayList<StormLocalization>());
		}
		sd1.sortX();
		sd2.sortX();
		double lateralTolerance = 100; //in nm
		for (int i = 0; i<sd1.getSize();i++){
			for (int j = 0; j<beadEstimates.get(0).size();j++){
				if(Math.abs(sd1.getElement(i).getX()-beadEstimates.get(0).get(j)[0])<lateralTolerance
				&& Math.abs(sd1.getElement(i).getY()-beadEstimates.get(0).get(j)[1])<lateralTolerance){
					listOfBeadsCh1.get(j).add(sd1.getElement(i));
				}
			}
		}
		for (int i = 0; i<sd2.getSize();i++){
			for (int j = 0; j<beadEstimates.get(1).size();j++){
				if(Math.abs(sd2.getElement(i).getX()-beadEstimates.get(1).get(j)[0])<lateralTolerance
				&& Math.abs(sd2.getElement(i).getY()-beadEstimates.get(1).get(j)[1])<lateralTolerance){
					listOfBeadsCh2.get(j).add(sd2.getElement(i));
				}
			}
		}
			
		ArrayList<StormLocalization> sl1 = new ArrayList<StormLocalization>();
		ArrayList<StormLocalization> sl2 = new ArrayList<StormLocalization>();
	
		for (int j = 0; j<listOfBeadsCh1.size(); j++){
			double posx = 0;
			double posy = 0;
			double posz = 0;
			double weights = 0;
			ArrayList<StormLocalization> currBead = listOfBeadsCh1.get(j);
			for (int i = 0; i < currBead.size(); i++){
				posx = posx + currBead.get(i).getX() * Math.sqrt(currBead.get(i).getIntensity()); //weight by square root of intensity
				posy = posy + currBead.get(i).getY() * Math.sqrt(currBead.get(i).getIntensity());
				posz = posz + currBead.get(i).getZ() * Math.sqrt(currBead.get(i).getIntensity());
				weights = weights + Math.sqrt(currBead.get(i).getIntensity());
			}
			sl1.add(new StormLocalization(posx / weights, posy / weights, posz / weights, 0, currBead.size()));
		}
		
		for (int j = 0; j<listOfBeadsCh2.size(); j++){
			double posx = 0;
			double posy = 0;
			double posz = 0;
			double weights = 0;
			ArrayList<StormLocalization> currBead = listOfBeadsCh2.get(j);
			for (int i = 0; i < currBead.size(); i++){
				posx = posx + currBead.get(i).getX() * Math.sqrt(currBead.get(i).getIntensity()); //weight by squareroot of intensity
				posy = posy + currBead.get(i).getY() * Math.sqrt(currBead.get(i).getIntensity());
				posz = posz + currBead.get(i).getZ() * Math.sqrt(currBead.get(i).getIntensity());
				weights = weights + Math.sqrt(currBead.get(i).getIntensity());
			}
			sl2.add(new StormLocalization(posx / weights, posy / weights, posz / weights, 0, currBead.size()));
		}
		
		ArrayList<ArrayList<StormLocalization>> retList = new ArrayList<ArrayList<StormLocalization>>();
		retList.add(sl1);
		retList.add(sl2);
		return retList;
	}
	
	
	
	public static ArrayList<ArrayList<StormLocalization>> findTraces(ArrayList<StormLocalization> locs, double dx, double dy, double dz, int maxdistBetweenLocalizations) {
		return findTraces( locs, dx, dy, dz, maxdistBetweenLocalizations, true);
	}
	
	public static ArrayList<ArrayList<StormLocalization>> findTraces(ArrayList<StormLocalization> locs, double dx, double dy, double dz, int maxdistBetweenLocalizations, boolean showProgress) {
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(locs,compFrame);
		int framemax = locs.get(locs.size()-1).getFrame();
		int framemin = locs.get(0).getFrame();
		//System.out.println(framemax+" "+framemin);

		ArrayList<ArrayList> connectedPoints = new ArrayList<ArrayList>();
		ArrayList<ArrayList<StormLocalization>> traces = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<ArrayList<StormLocalization>> frames = new ArrayList<ArrayList<StormLocalization>>();
		
		for (int k = 0; k<=framemax+1; k++) {
			frames.add(new ArrayList<StormLocalization>());
		}
		for (int j = 0; j< locs.size(); j++){
			frames.get(locs.get(j).getFrame()).add(locs.get(j)); //frames contains one list for each frame the data of the current subset is fed into it.
		}
		
						
		for (int i = 0; i<framemax+1; i++){
			for (int j = 0; j<frames.get(i).size(); j++){
				StormLocalization currLoc = frames.get(i).get(j);
				ArrayList<StormLocalization> currTrace = new ArrayList<StormLocalization>();
				currTrace.add(currLoc);
				int currFrame = currLoc.getFrame();
				int evaluatedFrame = currFrame + 1;
				//System.out.println(i+" "+j);
				while (currFrame + maxdistBetweenLocalizations >= evaluatedFrame && evaluatedFrame < framemax){//runs as long as there are consecutive localizations within a maximum distance of maxdistBetweenLoc...
					for (int k = 0; k<frames.get(evaluatedFrame).size(); k++){//runs through all locs of the currently evaluated frame
						StormLocalization compLoc = frames.get(evaluatedFrame).get(k);
						if (Math.abs(currLoc.getY()-compLoc.getY())<dy && Math.abs(currLoc.getX()-compLoc.getX())<dx && Math.abs(currLoc.getZ()-compLoc.getZ())<dz) {
							frames.get(evaluatedFrame).remove(k); // remove found localization to avoid duplication
							currFrame = evaluatedFrame; //currFrame describes the frame of the current localization so it is changed to the frame of the matching loc which becomes the new current loc
							evaluatedFrame = currFrame +1;
							currTrace.add(compLoc);
							currLoc = compLoc;
							break;
						}
					}
					evaluatedFrame += 1;
				}
				traces.add(currTrace);
				if (showProgress){

				}
			}
			//System.out.println(i +" " +frames.get(i).size());
		}
		System.out.println("Number of detected traces: "+traces.size()+" Number of all localizations: "+locs.size());
		return traces;
	}
	
	public static ArrayList<ArrayList<StormLocalization>> findTraces2(ArrayList<StormLocalization> locs, double dx, double dy, double dz, int maxdistBetweenLocalizations) {
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(locs,compFrame);
		int framemax = locs.get(locs.size()-1).getFrame();
		int framemin = locs.get(0).getFrame();
		//System.out.println(framemax+" "+framemin);

		ArrayList<ArrayList> connectedPoints = new ArrayList<ArrayList>();
		ArrayList<ArrayList<StormLocalization>> traces = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<ArrayList<StormLocalization>> frames = new ArrayList<ArrayList<StormLocalization>>();
		
		for (int k = 0; k<=framemax+1; k++) {
			frames.add(new ArrayList<StormLocalization>());
		}
		for (int j = 0; j< locs.size(); j++){
			frames.get(locs.get(j).getFrame()).add(locs.get(j)); //frames contains one list for each frame the data of the current subset is fed into it.
		}
		
		for (int i = 0; i<framemax+1; i++){
			for (int j = 0; j<frames.get(i).size(); j++){
				StormLocalization currLoc = frames.get(i).get(j);
				ArrayList<StormLocalization> currTrace = new ArrayList<StormLocalization>();
				currTrace.add(currLoc);
				int currFrame = currLoc.getFrame();
				int evaluatedFrame = currFrame + 1;
				//System.out.println(i+" "+j);
				while (currFrame + maxdistBetweenLocalizations >= evaluatedFrame && evaluatedFrame < framemax){//runs as long as there are consecutive localizations within a maximum distance of maxdistBetweenLoc...
					ArrayList<Double> distX = new ArrayList<Double>();
					ArrayList<Double> distY = new ArrayList<Double>();
					ArrayList<Double> distZ = new ArrayList<Double>();
					int idx = 0;
					for (int k = 0; k<frames.get(evaluatedFrame).size(); k++){//runs through all locs of the currently evaluated frame
						StormLocalization compLoc = frames.get(evaluatedFrame).get(k);
						distX.add(Math.abs(currLoc.getX()-compLoc.getX()));
						distY.add(Math.abs(currLoc.getY()-compLoc.getY()));
						distZ.add(Math.abs(currLoc.getZ()-compLoc.getZ()));

						
					}
					double mindist = 9e9;
					idx = -1;
					for (int k = 0; k<frames.get(evaluatedFrame).size(); k++){
						double dist = Math.sqrt(distX.get(k)*distX.get(k)+distY.get(k)*distY.get(k)+distZ.get(k)*distZ.get(k));
						if (dist<mindist&&dist<(Math.sqrt(dx*dx+dy*dy+dz*dz))){
							mindist = dist;
							idx = k;
						}		
					}
					if (idx>-1){
						currTrace.add(frames.get(evaluatedFrame).get(idx));
						currLoc = frames.get(evaluatedFrame).get(idx);
						currFrame = evaluatedFrame;
					}
					evaluatedFrame += 1;
				}
				traces.add(currTrace);

			}
			//System.out.println(i +" " +frames.get(i).size());
		}
		System.out.println("Number of detected traces: "+traces.size()+" Number of all localizations: "+locs.size());
		return traces;
	}
	
	public static ArrayList<StormLocalization> connectTraces(
			ArrayList<ArrayList<StormLocalization>> traces) {
		// consecutive detections will be merged spatial coordinates are averaged
		//intensities added and the first frame is chosen for the connected localization
		ArrayList<StormLocalization> connectedLoc = new ArrayList<StormLocalization>();
		int counter = 0;
		for (int i = 0; i< traces.size(); i++) {
			if (traces.get(i).size() < 10){ //beads are not connected
				if (traces.get(i).size()>1){
					counter = counter + 1;
				}
				double x = 0, y = 0, z = 0, intensity =0,angle = 0;
				int frame = traces.get(i).get(0).getFrame();
				for (int j = 0; j<traces.get(i).size(); j++) {
					x = x + traces.get(i).get(j).getX();
					y = y + traces.get(i).get(j).getY();
					z = z + traces.get(i).get(j).getZ();
					intensity = intensity + traces.get(i).get(j).getIntensity();
					angle = angle +traces.get(i).get(j).getAngle();
				}
				x = x / traces.get(i).size();
				y = y / traces.get(i).size();
				z = z / traces.get(i).size();
				angle =angle / traces.get(i).size();
				connectedLoc.add(new StormLocalization(x,y,z,frame,intensity,angle));	
			}
		}
		return connectedLoc;
	}
}
