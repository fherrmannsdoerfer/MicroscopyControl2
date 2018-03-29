package microscopeControl;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import dataTypes.StormData;


public class OutputControl {
	public static boolean createFolder(String folderName){
		return (new File(folderName)).mkdirs();
	}

	public static void saveSingleImage(ImagePlus img, String fname) {
		FileSaver fs = new FileSaver(img);
		fs.saveAsTiff(fname);
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
		try{
			ImagePlus leftStack = new ImagePlus("", stackLeft);
			FileSaver fs = new FileSaver(leftStack);
			fs.saveAsTiffStack(pathTiffFile);	
		}
		catch (IllegalArgumentException e){
			e.printStackTrace();
			System.out.println("Stack was probably empty!!! For sure something went wrong!");
		}
	}

	public static void createOutputFolders(String path, String measurementTag) {
		(new File(path+"\\"+measurementTag)).mkdirs();
		(new File(path+"\\"+measurementTag+"\\LeftChannel")).mkdirs();
		(new File(path+"\\"+measurementTag+"\\RightChannel")).mkdirs();
	}
	
	public static void writeFileContainingSampleListPathToExchangeFolder(String pathToExchangeFolder, String pathToSampleList) {
		String fname = pathToExchangeFolder+"\\pathToSampleList.txt";
		PrintWriter outputStream;
		try {
			outputStream = new PrintWriter(new FileWriter(fname));
			outputStream.print(pathToSampleList);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String readFile(String filePath)
    {
		try {
		FileReader fileReader = new FileReader(filePath);
		String fileContents = "";
		int i ;
		try {
			while((i =  fileReader.read())!=-1){
				char ch = (char)i;
			    fileContents = fileContents + ch; 
			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileContents;
		}
		catch(Exception e) {
			e.printStackTrace();
			return "";
		}
    }
	
	static void writeStringToFile(String string, String filename, boolean append) {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(
				    new File(filename), append)); 
			pw.println(string);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	static double getFreeSpaceInGB(String harddrive) {
		return (new File(harddrive)).getFreeSpace()/1e9/1.024;
	}

	public static StormData readStormData(String pathToReferenceMeasurement) {
		return new StormData(pathToReferenceMeasurement);
	}

}
