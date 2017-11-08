package microscopeControl;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class OutputControl {
	public static boolean createFolder(String folderName){
		return (new File(folderName)).mkdirs();
	}

	public static void saveSingleImage(ImagePlus img, String fname) {
		int counter = 0;
		while(true){
			counter = counter + 1;
			fname = fname.substring(0,fname.lastIndexOf(".")) +counter+".tif";
			File file = new File(fname);
			if (file.exists()){
				
			}
			else{
				FileSaver fs = new FileSaver(img);
			    fs.saveAsTiff(fname);
			}
		}
		
	}
	
	public static void createLogFile(String measurementTag, int gain, double exposure, String path, int nbrFrames) {
		try {
			String fname = "\\log_"+measurementTag+".txt";
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"\\"+measurementTag+fname));
			outputStream.println("Automatically generated log file for measurement"+measurementTag);
			outputStream.println("Gain: "+String.valueOf(gain));
			outputStream.println("Exposure time: "+String.valueOf(exposure));
			outputStream.println("Number Frames: "+ String.valueOf(nbrFrames));
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
	public static void writeFile(String filename, String content) {
		PrintWriter outputStream;
		try {
			(new File(filename).getParentFile()).mkdirs();
			outputStream = new PrintWriter(filename);
			outputStream.print(content);
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeStack(ImageStack stackLeft, String pathTiffFile) {
		ImagePlus leftStack = new ImagePlus("", stackLeft);
		FileSaver fs = new FileSaver(leftStack);
		fs.saveAsTiffStack(pathTiffFile);	
	}

	public static void createOutputFolders(String path, String measurementTag) {
		(new File(path+"\\"+measurementTag)).mkdirs();
		(new File(path+"\\"+measurementTag+"\\LeftChannel")).mkdirs();
		(new File(path+"\\"+measurementTag+"\\RightChannel")).mkdirs();
	}
}
