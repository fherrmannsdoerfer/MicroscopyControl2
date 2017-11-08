package utility;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.MaximumFinder;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JTextField;

import dataTypes.ROIParameters;


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

	public static ImagePlus stichTileScan(ImagePlus[][] tileScanImages, int numberStepsX, int numberStepsY,
			int overlapInPixels) {
		ImageProcessor iP = new FloatProcessor(numberStepsX*256, numberStepsY * 512);
		ImagePlus completeImage = new ImagePlus();
		for (int x = 0; x < numberStepsX ;x++){
			for (int y = 0; y < numberStepsY;y++){
				for (int xSmall = 0;xSmall < 256;xSmall++){
					for (int ySmall = 0; ySmall < 512;ySmall++){
						iP.putPixelValue(xSmall+(x*256-overlapInPixels), ySmall+(y*512-overlapInPixels), (tileScanImages[x][y].getProcessor().getPixelValue(xSmall, ySmall)));
					}
				}
			}
		}
		completeImage.setProcessor(iP);
		return completeImage;
	}
}
