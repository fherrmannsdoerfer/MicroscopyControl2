package utility;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.MaximumFinder;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import microscopeControl.OutputControl;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

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
	public static String parseParameter(String parameter, MainFrameEditor mfe){
		if (parameter.startsWith("%")){
			String[] tagNames = parameter.split("%");
			String returnString ="";
			for (int i = 1; i<tagNames.length; i++) {
				returnString = returnString + mfe.getControlerEditorReference().getIterationValue("%"+tagNames[i]);
			}
			return returnString;
		}	
		else{
			return parameter;
		}
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
			int overlapInPixelsX, int overlapInPixelsY) {
		int heightCoreRegion = 512-overlapInPixelsY;
		int widthCoreRegion = 256-overlapInPixelsX;
		int startPixelX = (256-widthCoreRegion)/2;
		int startPixelY = (512-heightCoreRegion)/2;
		ImageProcessor iP = new FloatProcessor(numberStepsX*widthCoreRegion, numberStepsY * heightCoreRegion);
		for (int x = 0; x < numberStepsX ;x++){
			for (int y = 0; y < numberStepsY;y++){
				for (int xsmall =startPixelX, xPos = 0;xsmall <(startPixelX+widthCoreRegion);xsmall++, xPos++){
					for (int ysmall =startPixelY, yPos = 0;ysmall <(startPixelY+heightCoreRegion);ysmall++,yPos++){
						iP.putPixelValue(xPos+widthCoreRegion*x, yPos+heightCoreRegion*y, tileScanImages[x][y].getProcessor().getPixelValue(xsmall, ysmall));
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
			stringY = "+"+yShift;
		}
		else {
			stringY = ""+yShift;
		}
		if (xShift>=0) {
			stringX = "+"+xShift;
		}
		else {
			stringX = ""+xShift;
		}
		Pair retVal = new Pair(stringX,stringY);
		return retVal;
	}

	//This method chooses the right sample list based on the position of the stage. 
	//The left border is preferred. A grid with 2 mm spacing is used.
	//Sample lists ranging from x = -13 mm; y = -5 mm up to x = 5 mm; y = 5 mm are provided
	//filename of the sample list files is for example AspirateFromCenterOfGlassBottomDishDisposeInWaste+5-5 in case of x=5 mm; y=-5 mm
	//The diameter of the inner circle is 14 mm, therefore for stage position 0,0 the file with x=-5; y = +-1 is selected
	public static String ChooseSampleListBasedOnStagePositionForRemovalOfSolution(XYStagePosition xyStagePosition) {
		Pair shifts = calculateShifts(-5.5, 0, xyStagePosition);
		String fname = String.format("AspirateFromCenterOfGlassBottomDishDisposeInWaste%s%s.csl", shifts.xShift, shifts.yShift);
		return fname;
	}

	public static String ChooseSampleListBasedOnStagePositionForAddingSolution(XYStagePosition xyStagePosition) {
		Pair shifts = calculateShifts(0, 0, xyStagePosition);
		String fname = String.format("SyringeToSample%s%s.csl", shifts.xShift, shifts.yShift);
		return fname;
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
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					File[] newContentOfExchangeFolder = (new File(pathToExchangeFolder)).listFiles();
					if (newContentOfExchangeFolder[0].toString().contains("SampleListProcessed")) { //when Chronos is finished it replaces the File containing the path to the sample list with a file called SampleListProcessed the program can proceed then
						break;
					}
				}
			} else { //some other file is in the folder maybe from an other instance maybe a sample list that is currently processed, dont do anything
				
			}
		}
	}
}
