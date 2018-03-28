package microscopeControl;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.micromanager.utils.ImageUtils;

import utility.Utility;
import mmcorej.CMMCore;


//Class that does all the camera related tasks like image acquisition or the capturing of widefield images
public class CameraWorker  {
	private MainFrame mf;
	private String cameraName;
	private CMMCore core;

	private boolean livePreviewRunning;
	private boolean acquisitionRunning;
	
	Thread acquisitionThread;
	Thread livePreviewThread;
	
	public CameraWorker(MainFrame mf){
		this.mf = mf;
		this.cameraName = mf.getCamName();
		this.core = mf.core;
	}
	
	//function that captures a single image, not used for image acquisition of 
	//measurements with several thousands of frames
	public ImagePlus captureImage() {
		ImagePlus imp;
		try {
			Object img;
			double exp = mf.getExposureTime();
			int gain = mf.getEmGain();
			core.setProperty(cameraName,"Exposure", exp);
			core.setProperty(cameraName,"Gain", gain);
			
			core.snapImage();
			img = core.getImage();
			ImageProcessor ipr = ImageUtils.makeProcessor(core,img);
    	    imp = new ImagePlus("",ipr);
    	    
			
		} catch (Exception e) {
			imp = null;
			e.printStackTrace();
		}	
		return imp;
	}
	
	//functions that checks the ROI size and if it is selected, as well as if the selected output path has been used before
	protected boolean checkAcquisitionSettings() {
		boolean startMeasurement = true;
		String path = mf.getOutputFolder();
		String measurementTag = mf.getMeasurementTag();
		
		//Check if the directory already exists which could indicate that the path or measurement tag was not altered and 
		//the older measurement will be overwritten
		if ((new File(path+"\\"+"LeftChannel")).exists()||(new File(path+"\\"+"RightChannel")).exists()){
			int dialogResult = JOptionPane.showConfirmDialog (null, "The directory where you save your movie"
					+ " does already exist! Do you want to continue?","Warning",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.NO_OPTION){
				return false;
			}
		}
		
		//check if a custom ROI was set but the checkbox was forgotten to check
		if (mf.isROISet() && !mf.isROIApplied()){
			int dialogResult = JOptionPane.showConfirmDialog (null, "You chose a ROI but did not check apply rectangle!"
					+ " Do you wish to record only the selected ROI?","Warning",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				mf.setROIselected(true);
			}
		}
	
		//check if the selected ROI is very small, which might happen when klicking onto the display without the intention to create a ROi;
		if(mf.isROITooSmall()){
			int dialogResult = JOptionPane.showConfirmDialog (null, "The selected ROI is quite small "
					+ "("+mf.getROIWidth()+" x "+mf.getROIHeight()+" pixels)! Do you wish to"
							+ " proceed?","Warning",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.NO_OPTION){
				return false;
			}
		}
		return startMeasurement;
	}
	
	public void startLivePreview(){
		livePreviewThread = new Thread(new LivePreview());
		livePreviewThread.start();
	}

	//this method first checks if it 
	public void startSequenceAcquisition(boolean applyChecks) {
		if (applyChecks) {
			if (checkAcquisitionSettings()&& mf.checkSpace(mf.getMinimalFreeSpaceForSingleMeasurement())){
				prepareAcquisition();
			}
			else{
				System.out.println("The settings check went wrong!");
			}
		} else {
			prepareAcquisition();
		}
	};
	
	private void prepareAcquisition() {
		livePreviewRunning = false;		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mf.initializeCommentaryOutput();
		//create new Thread. Otherwise the GUI would not be responsive
		acquisitionThread = new Thread(new AcquisitionThread(mf));
		acquisitionThread.start();
		mf.setEnableStartAcquisition(false);
	}
	
	//this class is used to create the Thread that takes care of the image sequence acquisition
	class AcquisitionThread implements Runnable {
		double exposure;
		int nbrFrames;
		int gain;
		String path;
		String measurementTag;
		boolean useFirstVariableSet = true; // Variable which determines which set of variables is used for storage of the current stack (the other variables are saved)
		boolean useFrameTransfer;
		ImageStack stackLeft;
        ImageStack stackRight;
        ImageStack stackLeft2;
        ImageStack stackRight2;
        int stackCounter;
        CMMCore core;
		public AcquisitionThread(MainFrame mf) {
			this.exposure = mf.getExposureTime();
			this.nbrFrames = mf.getFrameNumber();
			this.gain = mf.getEmGain();
			this.path = mf.getPath();
			this.measurementTag = mf.getMeasurementTag();
			this.useFrameTransfer = mf.isFrameTransferSelected();
			this.core = mf.getCoreObject();
		}
		public void run() {
	    	Object img;
	    	
	    	//create the folder for the current measurement and also the LeftChannel
	    	//and RightChannel subfolders to store the tiff-stacks
	    	OutputControl.createOutputFolders(path, measurementTag);
    		try {    		
    			if (useFrameTransfer) {
					core.setProperty(cameraName, "FrameTransfer", "On");
    			}
				else {
					core.setProperty(cameraName, "FrameTransfer", "Off");
				}
			
				core.setProperty(cameraName,"Gain", mf.getEmGain());
				mf.setNumberFramesForCurrentAcquisition(nbrFrames);
	
				core.setCircularBufferMemoryFootprint(2000);
	
		    	core.setProperty(core.getCameraDevice(),"Exposure", exposure);
		    	//the additional 5000 frames are needed that the acquisition loop does not end
		    	//prematurely. The acquisition is interrupted once the user set number of frames
		    	//were recorded.
		    	core.startSequenceAcquisition(nbrFrames+5000, exposure, false);
		    	acquisitionRunning = true;
		    	int frame = 0;
		    	mf.setAction("Acquisition");
		    	mf.setFrameCount(" / "+String.valueOf(nbrFrames));
		    	
	    		OutputControl.createLogFile(measurementTag, gain, exposure, path, nbrFrames);	
	
	            core.getBytesPerPixel();
	            int imgWidth, imgHeight;
	            
	            if (mf.isROIApplied()) {
	            	imgWidth = mf.getROIWidth();
	            	imgHeight = mf.getROIHeight();
	            }
	            else {
	            	imgWidth = 256;
	            	imgHeight = 512;
	            }
	            
	            //creation of the ImageStacks that will be used to store the recorded frames.
	            //To have enough time to store the image stacks on the disk two stacks are used,
	            //so that the other stack can be filled while the first stack is saved to disk.
	            stackLeft = new ImageStack(imgWidth, imgHeight);
	            stackRight = new ImageStack(imgWidth, imgHeight);
	            stackLeft2 = new ImageStack(imgWidth, imgHeight);
	            stackRight2 = new ImageStack(imgWidth, imgHeight);
	            double bytesPerFrame = imgWidth * imgHeight * 2; // 16 bit images
	            int maxImagesPerStack = (((int) Math.floor(3.5e9 / bytesPerFrame))/1000) * 1000; // max 3.5 GB per Stack 1000 urspruenglich
	            int imagesInCurrentStack = 0;
	            stackCounter = 0;
	            
		    	System.currentTimeMillis();
		    	while (frame<nbrFrames && acquisitionRunning){//for whatever reason a few frames are always missing, so the loop will not exit...
		    	   if (core.getRemainingImageCount() > 0) {
		    		  mf.setFrameCount(String.valueOf(frame+1)+" / "+String.valueOf(nbrFrames));
		    		  mf.setCurrentFrame(frame);
	    		  
		    	      img = core.popNextImage();
		    	     
		    	      ImagePlus imp = normalizeMeasurement(img, gain);
		    	      //if necessary the images are cropped
		    	      ArrayList<ImagePlus> channels = Utility.cropImages(imp.getProcessor(), mf.isROIApplied(), mf.getROIParameters());
		    	      
		    	      if (frame%20 == 0){
		    	    	  mf.triggerPSFRateEstimation(imp);
		    	      }
		    
		    	      //both channels
		    	      if (mf.getSelectedChannel() == 0){
		    	    	  //if (frame%100 ==0){
		    	    		  //System.out.println("current Frame: "+ frame);
		    	    		  //whichStackIsUsed(useFirstVariableSet);
		    	    	 // }
		    	    	  if (useFirstVariableSet){
		    	    		  stackLeft.addSlice(channels.get(0).getProcessor());
				    	      stackRight.addSlice(channels.get(1).getProcessor());
		    	    	  }
		    	    	  else {
		    	    		  stackLeft2.addSlice(channels.get(0).getProcessor());
				    	      stackRight2.addSlice(channels.get(1).getProcessor());
		    	    	  }	    	      
		    	      }
		    	      //left channel only
		    	      else if (mf.getSelectedChannel() == 1){
			    	      if (useFirstVariableSet){
		    	    		  stackLeft.addSlice(channels.get(0).getProcessor());
		    	    		  
		    	    	  }
		    	    	  else {
		    	    		  stackLeft2.addSlice(channels.get(0).getProcessor());
		    	    	  }
		    	      }
		    	      //right channel only
		    	      else {
		    	    	  if (useFirstVariableSet){
		    	    		  stackLeft.addSlice(channels.get(1).getProcessor());
		    	    	  }
		    	    	  else {
		    	    		  stackLeft2.addSlice(channels.get(1).getProcessor());
		    	    	  }
		    	      }
		    	      imagesInCurrentStack += 1;
		    	      mf.showCurrentImage(imp);
		    	      frame++;
		    	   }
		    	   else {
		    		   Thread.sleep(100);
		    	   }
		    	   // if the current stack is "full"
		    	   if (imagesInCurrentStack == maxImagesPerStack){
		    		   System.out.println("Stack size of: "+maxImagesPerStack+" was reached.");
		    		   imagesInCurrentStack = 0;
		    		   
		    		   if (useFirstVariableSet){
		    			   stackLeft2 = new ImageStack(imgWidth, imgHeight);
		    			   stackRight2 = new ImageStack(imgWidth, imgHeight);
		    		   }
	    			   else{
	    				   stackLeft = new ImageStack(imgWidth, imgHeight);
		    			   stackRight = new ImageStack(imgWidth, imgHeight);
	    			   }
		    		   saveStack();
	    			   //writeStacks(comboBoxWhichPart.getSelectedIndex(), stackCounter, measurementTag,stackLeft,stackRight);
		    		   useFirstVariableSet = !useFirstVariableSet;
		    		   stackCounter+=1;
		    		   //whichStackIsUsed(useFirstVariableSet);
		    	   }
		    	}			    	
		    	mf.setEnableStartAcquisition(true);
	
				core.stopSequenceAcquisition();
				saveStack();
    		}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
		private void whichStackIsUsed(boolean firstSet){
			if (firstSet){
 			   System.out.println("First Variable Set is currently used");
 		   }
 		   else{
 			   System.out.println("Second Variable Set is currently used");
 		   }
		}
		
	private class SavingThread implements Runnable{
		 ImageStack stack1;
		 ImageStack stack2;
		 int selectedIndex;
		 int stackCounter;
		 String nameChannel1 = "LeftChannel";
		 String nameChannel2 = "RightChannel";
		 
		 SavingThread(ImageStack stack1, ImageStack stack2, int selectedIndex, int stackCounter){
			 this.stack1 = stack1;
			 this.stack2 = stack2;
			 this.selectedIndex = selectedIndex;
			 this.stackCounter = stackCounter;
		 }
		 @Override
		 public void run(){
			 String basename1 = findBasename(path,measurementTag, stackCounter,nameChannel1);
			 String basename2 = findBasename(path,measurementTag, stackCounter,nameChannel2);
			   
			 //String basename1 = "LeftChannel"+measurementTag+"pt"+String.format("%03d", stackCounter);
			 String pathTiffFile1 = path+"\\"+measurementTag+"\\"+nameChannel1+"\\"+basename1+".tif";
			
			 //  String basename2 = "RightChannel"+measurementTag+"pt"+String.format("%03d", stackCounter);
			 String pathTiffFile2 = path+"\\"+measurementTag+"\\"+nameChannel2+"\\"+basename2+".tif";
			 
			 String outputPath = mf.getRelativeOutputPath();
			   if (selectedIndex == 0){
					OutputControl.writeStack(stack1, pathTiffFile1);
					OutputControl.writeStack(stack2, pathTiffFile2);

					if (mf.isSimulatneousReconstruction()){
						mf.startReconstruction(pathTiffFile1, basename1, path, outputPath, measurementTag);
						mf.startReconstruction(pathTiffFile2, basename2, path, outputPath, measurementTag);
					}
		    	}
		        else if (selectedIndex == 1){
		        	OutputControl.writeStack(stack1,pathTiffFile1);
					if (mf.isSimulatneousReconstruction()){
						mf.startReconstruction(pathTiffFile1, basename1, path, outputPath, measurementTag);
		    		}
		    	}
		        else {
		        	OutputControl.writeStack(stack2,pathTiffFile2);
					if (mf.isSimulatneousReconstruction()){
						mf.startReconstruction(pathTiffFile2, basename2, path, outputPath, measurementTag);
		    		}
		        }		    					   
		   }
		 
		 //in case of multiple measurements with the same output folder, 
		 //check if the file already exists and increase the counter until
		 //a new filename is generated
		 String findBasename(String path, String measurementTag, int counter, String channel) {
			 String basename;
			 String pathTiffFile;
			 do {
				 basename = channel + measurementTag+"pt"+String.format("%03d", counter);
				 pathTiffFile = path+"\\"+measurementTag+"\\"+channel+"\\"+basename+".tif";
				 counter = counter + 1;
			 } while (new File(pathTiffFile).exists());
			 
			 
			 return basename;
		 }

	}
		
	private void saveStack(){
		SavingThread saveThread;
		if (useFirstVariableSet){
			saveThread = new SavingThread(stackLeft, stackRight,mf.getSelectedChannel(), stackCounter);
		}
		else{
			saveThread = new SavingThread(stackLeft2, stackRight2,mf.getSelectedChannel(), stackCounter);
		}
		saveThread.run();
	}

	}
	
	
	//live preview shows the current camera images but does not save them
	class LivePreview implements Runnable {
		LivePreview(){
			livePreviewRunning = true;
		}
		@Override
		public void run() {
			Object img;
			double exp = mf.getExposureTime();
			int gain = mf.getEmGain();
			boolean changeParams = true;
			int counter = 0;
			mf.setAction("Live Preview");
			while (livePreviewRunning) {
				try {
					//in case the user changes the exposure or em-gain settings the parameters shall be updated
					if (!(exp==mf.getExposureTime()) | !(gain == mf.getEmGain())) {
						changeParams = true;
						exp = mf.getExposureTime();
						gain = mf.getEmGain();
					}
					else {changeParams = false;}
					if (changeParams){
						core.setProperty(core.getCameraDevice(),"Exposure", exp);
						core.setProperty(core.getCameraDevice(),"Gain", gain);
					}
				
					core.snapImage();
					img = core.getImage();

					ImagePlus imp = normalizeMeasurement(img, gain);
					mf.showCurrentImage(imp);
					if (counter % 20 == 0){
						mf.triggerPSFRateEstimation(imp);
					}
				} catch (Exception e) {
					e.printStackTrace();
					livePreviewRunning = false;
				}
			}
		}		
	}
	
	//calculates photons from the digital numbers reported from the camera
	//the offset induced by the camera is not subtracted since no negative 
	//values are allowed and the distribution of the background pixels is changed.
	//This means that the reported intensities contain an additional part of 
	//200 * 4.81 / cameraEMGain
	ImagePlus normalizeMeasurement(Object img, int gain) {
		ImageProcessor ipr = ImageUtils.makeProcessor(core,img);
		//the offset should not be subtracted since no negative values are allowed and the distribution of the background pixels is changed
	    //ipr.subtract(200); //photo electrons = (digital count - offset)* sensistivity / gain
	   // ipr.multiply(4.81);
	    //ipr.multiply(1./gain); //the image contains now the number of photo electrons.
	    ImagePlus imp = new ImagePlus("",ipr);
	    return imp;
	}

	public void stopSequenceAcquisition() {
		acquisitionRunning = false;
		mf.setAction("");
	}
	
	public void stopLivePreview(){
		livePreviewRunning = false;
		mf.setAction("");
	}

	public void closeShutter() {
		try {
			core.setProperty(cameraName, "Shutter (Internal)","Closed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void openShutter() {
		try {
			core.setProperty(cameraName, "Shutter (Internal)","Open");	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setExposureTime(double expTime) {
		try {
			core.setProperty(cameraName,"Exposure", expTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
