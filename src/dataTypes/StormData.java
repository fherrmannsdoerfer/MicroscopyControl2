
package dataTypes;


import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import utility.Utility;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import comperators.StormLocalizationFrameComperator;
import comperators.StormLocalizationIntComperator;
import comperators.StormLocalizationXComperator;
import comperators.StormLocalizationYComperator;
import comperators.StormLocalizationZComperator;
import comperators.TraceYComperator;


public class StormData implements Serializable{
	boolean verbose = true;
	boolean isSortedByFrame = false;
	private ArrayList<StormLocalization> locs = new ArrayList<StormLocalization>();
	private String path;
	private String fname;
	private String basename = "";
	private String processingLog = "-";
	private ArrayList<Object> logs = new ArrayList<Object>();
	private String outputPath;
	

	public StormData(StormData sl){
		this.locs = sl.getLocs();
		this.fname = sl.getFname();
		this.path = sl.getPath();
		this.basename = sl.basename;
		this.processingLog = sl.getProcessingLog();
		this.logs = sl.logs;
	}
	

	public StormData(){
		this.fname = "fname not set yet";
		this.path = "path not set yet";
		this.basename = "basename not set yet";
		
	}
	private StormData(ArrayList<StormLocalization> sl, String path, String fname, String basename){
		this.locs = sl;
		this.path = path;
		this.fname = fname;
		this.basename = basename;
	}
	
	private StormData(ArrayList<StormLocalization> sl, String path, String fname){
		this.locs = sl;
		this.path = path;
		this.fname = fname;
		this.basename = findBasename(fname);
	}
	
	private StormData(ArrayList<StormLocalization> sl, String path){
		this.locs = sl;
		this.path = path;
	}
	
	public StormData(String fullpath){
		this.fname = "fname not set yet";
		this.path = "path not set yet";
		importData(fullpath);
	}
	
	public ArrayList<StormLocalization> importData(String fullpath){
		BufferedReader br = null;
		String line = "";
		String delimiter = " ";
		try {
			int counter = 0;
			ArrayList<Integer> errorLines = new ArrayList<Integer>(); 
			br = new BufferedReader(new FileReader(fullpath));
			line = br.readLine(); //skip header
			String[] headerComma = line.split(",");
			String[] headerBlank = line.split(" ");
			String firstCharHeader = "";
			if (line.length() > 0){
				firstCharHeader = line.substring(0, 1);
			}
			if (headerBlank.length > headerComma.length){
				while ((line = br.readLine())!= null){
					String[] tmpStr = line.split(delimiter);
					
					counter  = counter + 1;
					try{
						if (tmpStr.length == 4) { //2D data
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Integer.valueOf(tmpStr[2]), Double.valueOf(tmpStr[3]));
							getLocs().add(sl);
						}
						else if(tmpStr.length == 5) { //3d data
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), Math.round(Float.valueOf(tmpStr[3])), Double.valueOf(tmpStr[4]));
							getLocs().add(sl);
						}
						else if(tmpStr.length == 6 && firstCharHeader.contains("#")) { //Malk output
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Integer.valueOf(tmpStr[3]), Double.valueOf(tmpStr[4]));
							getLocs().add(sl);
						}
						else if(tmpStr.length == 6 && !firstCharHeader.contains("#")){
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]),Integer.valueOf(tmpStr[3]), Double.valueOf(tmpStr[4]), Double.valueOf(tmpStr[5]));
							getLocs().add(sl);
						}
						else if(tmpStr.length == 7) { //no Malk output
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), Integer.valueOf(tmpStr[3]), Double.valueOf(tmpStr[4]));
							getLocs().add(sl);
						}
						else {System.out.println("File format not understood!");}
					}
					catch(java.lang.NumberFormatException ne){System.out.println("Problem in line:"+counter+ne); errorLines.add(counter);}
				}
			}
			else if (headerBlank.length <= headerComma.length) {
				while ((line = br.readLine())!= null){
					String[] tmpStr = line.split(",");
					
					counter  = counter + 1;
					try{
						if (tmpStr.length >=10) { // 3D
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), Double.valueOf(tmpStr[3]), Double.valueOf(tmpStr[0]).intValue(), Double.valueOf(tmpStr[6]));
							getLocs().add(sl);
						}
						if (tmpStr.length <10) { //2D
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), 0., Double.valueOf(tmpStr[0]).intValue(), Double.valueOf(tmpStr[4]));
							getLocs().add(sl);
						}
					} catch (java.lang.NumberFormatException ne){System.out.println("Problem in line:"+counter+ne); errorLines.add(counter);}
				}
			}
			else {
				
			}
			if (verbose){
				System.out.println("File contains "+getLocs().size()+" localizations.");
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(path+fname);
		}
		catch (IOException e) {e.printStackTrace();}
		return locs;
	}
	
	public synchronized void sortFrame(){
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(getLocs(),compFrame);
		isSortedByFrame = true;
	}
	
	public ArrayList<StormLocalization> getList(){
		return getLocs();
	}
	
	public StormLocalization getElement(int index){
		if (getLocs().size() > index){
			return getLocs().get(index);
		}
		else {
			System.err.println("Index "+index+" exceeds the number of elements ("+getLocs().size()+")!");
			return null;
		}
	}
	
	public void addElement(StormLocalization sl){
		getLocs().add(sl);
	}
	
	public void append(StormData sd){
		this.locs.addAll(sd.getLocs());
	}
	
	public synchronized int findFirstIndexForFrame(int frame){ //finds the index with the first appearance of a framenumber larger or equal the given frame
		if (isSortedByFrame){
		}
		else {
			sortFrame();
		}
		int ret = 0;
		for (int i = 0;i<getLocs().size();i++){
			if (getLocs().get(i).getFrame() >= frame){
				return i;
			}
		}
		if(verbose){
			System.out.println("Given frame "+frame+"is larger than any contained localization!");
		}
		return getLocs().size()-1; //if the given frame is larger than any frame the last index is reported
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public void setFname(String fname){
		this.fname = fname;
	}
	
	public void setProcessingLog(String proclog){
		this.processingLog = proclog;
	}
	
	public String getPath(){
		if (path.endsWith("\\")){
			return path;
		}
		else{
			return path+"\\";
		}
	}
	
	public String getFname(){
		return fname;
	}
	
	public String getProcessingLog(){
		return processingLog;
	}
	
	public int findLastIndexForFrame(int frame){ //finds the last index for which the frame is equal or lower the given frame 
		if (isSortedByFrame){
		}
		else {
			sortFrame();
		}
		int ret = 0;
		for (int i = 0;i<getLocs().size();i++){
			if (getLocs().get(i).getFrame() > frame){
				return Math.max(0, i-1); //if the given frame is lower than any frame the first index is reported
			}
		}
		if (verbose){
			System.out.println("Given frame "+frame+"is larger than any contained localization!");
		}
		return getLocs().size()-1;
	}
	
	public int getSize(){
		return getLocs().size();
	}
	
	public ArrayList<Double> getDimensions(){
		return getDimensions(this.locs);
	}
	
	public ArrayList<Double> getDimensions(ArrayList<StormLocalization> locs){ //returns minimal and maximal positions in an ArrayList in the following order (xmin, xmax, ymin, ymax, zmin, zmax, minFrame, maxFrame)
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = 0;
		double maxY = 0;
		double maxZ = 0;
		double minFrame = Double.MAX_VALUE;
		double maxFrame = 0;
		double minInt = Double.MAX_VALUE;
		double maxInt = 0;
		for (int i = 0; i<locs.size(); i++){
			StormLocalization sl = locs.get(i);
			double currX = sl.getX();
			double currY = sl.getY();
			double currZ = sl.getZ();
			double currFrame = (double) sl.getFrame();
			double currInt = sl.getIntensity();
			
			if (minX > currX) {
				minX = currX;
			}
			if (maxX < currX) {
				maxX = currX;
			}
			if (minY > currY) {
				minY = currY;
			}
			if (maxY < currY) {
				maxY = currY;
			}
			if (minZ > currZ) {
				minZ = currZ;
			}
			if (maxZ < currZ) {
				maxZ = currZ;
			}
			if (minFrame>currFrame) {
				minFrame = currFrame;
			}
			if (maxFrame<currFrame){
				maxFrame = currFrame;
			}
			if (minInt > currInt) {
				minInt = currInt;
			}
			if (maxInt < currInt) {
				maxInt = currInt;
			}
		}
		
		ArrayList<Double> ret = new ArrayList<Double>();
		ret.add(minX);
		ret.add(maxX);
		ret.add(minY);
		ret.add(maxY);
		ret.add(minZ);
		ret.add(maxZ);
		ret.add(minFrame);
		ret.add(maxFrame);
		ret.add(minInt);
		ret.add(maxInt);
		
		return ret;
	}
		
	float[][] addFilteredPoints(float[][] image, double sigma, int filterwidth, 
			double pixelsize, ArrayList<StormLocalization> sd, int mode, int intensityMode){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		//double factor = 100*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		//System.out.println(sd.getSize());
		for (int i = 0; i<getSize(); i++){
			StormLocalization sl = sd.get(i);
			double factor = 0;
			switch (intensityMode){
				case 0: //intensities are based on photon counts
					factor = sl.getIntensity() *1/(2*Math.PI*sigma*sigma);
					break;
				case 1: //intensities in the rendered image are based on the number of localizations
					factor = 1 *1/(2*Math.PI*sigma*sigma);
					break;
			}
			
			double posX = 0;
			double posY = 0;
			switch (mode){
				case 0:
					posX = sl.getX()/pixelsize; //position of current localization
					posY = sl.getY()/pixelsize;
					break;
				case 1:
					posX = sl.getX()/pixelsize; //position of current localization
					posY = sl.getZ()/pixelsize;
					break;
				case 2:
					posX = sl.getY()/pixelsize; //position of current localization
					posY = sl.getZ()/pixelsize;
					break;
			}
			
			int pixelXStart = (int)Math.round(posX) - (filterwidth+1)/2;
			int pixelYStart = (int)Math.round(posY) - (filterwidth+1)/2;
			float corrFactor = 1; //factor to compensate the cutoff due to discrete Gaussian
			if (intensityMode == 1){
				for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
					for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
						try{
							corrFactor += (float)(factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							//System.out.println("factor: "+factor+" k: "+k+" l: "+l+"posX: "+posX+"posY: "+posY+" image[k][l]" +image[k][l]+" res: "+(float)(factor * Math.exp(-0.5/sigma/sigma*(Math.pow((k-posX),2)+Math.pow((l-posY),2)))));
						} catch(IndexOutOfBoundsException e){e.toString();}
					}
				}
				corrFactor -=1;
			}
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					try{
						image[k][l] = image[k][l] + (float)(factor/corrFactor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
						//System.out.println("factor: "+factor+" k: "+k+" l: "+l+"posX: "+posX+"posY: "+posY+" image[k][l]" +image[k][l]+" res: "+(float)(factor * Math.exp(-0.5/sigma/sigma*(Math.pow((k-posX),2)+Math.pow((l-posY),2)))));
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
		}
		return image;
	}
	
	ArrayList<float[][]> addFilteredPoints(ArrayList<float[][]> coloredImage, double sigma, int filterwidth, double pixelsize,double percentile, ArrayList<StormLocalization> sd){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor2 = -0.5/sigma/sigma;
		ArrayList<Double> dims = getDimensions();
		double zMin = dims.get(4);
		double zMax = dims.get(5);
		zMax = zMax - zMin;//all z should lie between 0 and a certain maximum for the rendering
		if (verbose){
			System.out.println("zMax: "+zMax);
		}
		float[][] redChannel = coloredImage.get(0);
		float[][] greenChannel = coloredImage.get(1);
		float[][] blueChannel = coloredImage.get(2);
		for (int i = 1; i<getSize(); i++){
			StormLocalization sl = sd.get(i);
			double factor = 0.033*sl.getIntensity()*1/(2*Math.PI*sigma*sigma);
			double posX = sl.getX()/pixelsize; //position of current localization
			double posY = sl.getY()/pixelsize;
			double posZ = sl.getZ() - zMin;
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					double kk = 1;
					try{
						double weight = factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2)));
						if (true){
							redChannel[k][l] = (float) (redChannel[k][l] + getColorRedToBlack(posZ,zMax,0) * weight);
							greenChannel[k][l] = (float) (greenChannel[k][l] +getColorRedToBlack(posZ,zMax,1) * weight);
							blueChannel[k][l] = (float) (blueChannel[k][l] +getColorRedToBlack(posZ,zMax,2) * weight);
							if (redChannel[k][l]<0||greenChannel[k][l]<0||blueChannel[k][l]<0){
								System.out.println(k+" "+l);
							}
						}
					} catch(Exception e){
						//System.out.println(e.toString());
					}
				}
			}
		}
		double max= 0;
		double min = 1e19;
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				max = Math.max(redChannel[i][j],max);
				max = Math.max(greenChannel[i][j],max);
				max = Math.max(blueChannel[i][j],max);
				min = Math.min(redChannel[i][j],min);
				min = Math.min(greenChannel[i][j],min);
				min = Math.min(blueChannel[i][j],min);
			}
		}
		ArrayList<float[][]> normalizedChannels = normalizeChannels(redChannel, greenChannel, blueChannel,percentile);
		coloredImage.clear();
		coloredImage.add(normalizedChannels.get(0));
		coloredImage.add(normalizedChannels.get(1));
		coloredImage.add(normalizedChannels.get(2));
//		coloredImage.clear();
//		coloredImage.add(redChannel);
//		coloredImage.add(greenChannel);
//		coloredImage.add(blueChannel);
		return coloredImage;
	}
	
private static float getColorRedToBlack(double posZ, double zMax, int color) {
		
		if (posZ < 0.2* zMax&&posZ>=0){
			//blue rises from 0 to 1
			if (color == 2){
				return (float) (5*posZ / zMax);
			}
		}
		else if (posZ < 0.4* zMax&&posZ>0){
			//green rises from 0 to 1 blue stays one
			if (color == 1){
				return (float)(5*posZ/zMax - 1);
			}
			if (color == 2){
				return (float) 1;//(2 - 4*posZ/zMax)	;
			}
		}
		else if (posZ < 0.6* zMax&&posZ>0){
			//green stays one, blue goes to zero again
			if (color == 1){
				return (float) 1;//(4*posZ/zMax - 2);
			}
			if (color == 2){
				return (float) (3 - 5*posZ/zMax);
			}
		}
		else if (posZ<0.8*zMax&&posZ>0) {
			//green goes to zero red rises
			if (color == 0){
				return (float) (5*posZ/zMax - 3);
			}
			if (color == 1){
				return (float) (4-5*posZ/zMax);
			}
		}
		else if (posZ<=zMax&&posZ>0){
			//red goes from 1 to 0.5
			if (color == 0){
				return (float) (3-2.5*posZ/zMax);
			}
		}
		return 0;
	}
	
	private static float getColor(double posZ, double zMax, int color) {
		
		if (posZ < 0.25* zMax&&posZ>=0){
			//blue rises from 0 to 1
			if (color == 2){
				return (float) (4*posZ / zMax);
			}
		}
		else if (posZ < 0.5* zMax&&posZ>0){
			//green rises from 0 to 1 blue stays one
			if (color == 1){
				return (float)(4*posZ/zMax - 1);
			}
			if (color == 2){
				return (float) 1;//(2 - 4*posZ/zMax)	;
			}
		}
		else if (posZ < 0.75* zMax&&posZ>0){
			//green stays one, blue goes to zero again
			if (color == 1){
				return (float) 1;//(4*posZ/zMax - 2);
			}
			if (color == 2){
				return (float) (3 - 4*posZ/zMax);
			}
		}
		else if (posZ<zMax&&posZ>0) {
			//green goes to zero red rises
			if (color == 0){
				return (float) (4*posZ/zMax - 3);
			}
			if (color == 1){
				return (float) (4-4*posZ/zMax);
			}
		}
		return 0;
	}

	float[][] normalizeChannel(float[][] image){
		return normalizeChannel(image,0.99f);
	}
	float[][] normalizeChannel(float[][] image, float percentile){
		double max = 0;
		double min = Double.MAX_VALUE;
		for (int i = 0; i<image.length;i++){
			for(int j = 0; j<image[0].length; j++){
				max = Math.max(image[i][j],max);
				min = Math.min(image[i][j],min);
			}
		}
		int nbrIntesnsities = 1000000;
		int[] hist = new int[nbrIntesnsities+1];
		int nbrEntries = 0;
		for (int i=0;i<nbrIntesnsities;i++){
			hist[i] = 0;
		}
		for (int i = 0; i<image.length;i++){
			for(int j = 0; j<image[0].length; j++){
				//image[i][j] = (float) Math.ceil((image[i][j] - min)/(max - min) * 65535);
				hist[(int) Math.ceil((image[i][j] - min)/(max - min) * nbrIntesnsities)] += 1;
				nbrEntries +=1;
				//System.out.println(hist[0]+ " "+ (int)redChannel[i][j]+ "nbrEntries "+ nbrEntries);
			}
		}
		//double percentile = 0.99;
		int sum = 0;
		double counts = nbrEntries - hist[0];//counts is the number of intensities above 0
		double newMaximum = 0;
		for (int i=1;i<nbrIntesnsities;i++){
			//System.out.println("sum: "+sum+" counts: "+ counts+"nbrEntries "+nbrEntries+"hist[0] "+hist[0]);
			sum = sum +hist[i];
			if (sum>=percentile * counts){
				newMaximum = i*(max -min)/((float)nbrIntesnsities) + min;
				break;
			}
			newMaximum = i*(max -min)/((float)nbrIntesnsities) + min;
		}
		if (verbose){
			System.out.println("Normalization:  Max: "+max+" newMax: "+newMaximum);
		}
		for (int i = 0; i<image.length;i++){
			for(int j = 0; j<image[0].length; j++){
				image[i][j] = (float)Math.min((image[i][j] )/(newMaximum)*65535,65535);
			}
		}
		return image;		
	}
	
	ArrayList<float[][]> normalizeChannels(float[][] redChannel, float[][] greenChannel, float[][] blueChannel, double percentile){
		double max = 0;
		double min = Double.MAX_VALUE;
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				max = Math.max(redChannel[i][j],max);
				max = Math.max(greenChannel[i][j],max);
				max = Math.max(blueChannel[i][j],max);
				min = Math.min(redChannel[i][j],min);
				min = Math.min(greenChannel[i][j],min);
				min = Math.min(blueChannel[i][j],min);
			}
		}
		int[] hist = new int[65536];
		int nbrEntries = 0;
		for (int i=0;i<65536;i++){
			hist[i] = 0;
		}
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				redChannel[i][j] = (float) Math.ceil((redChannel[i][j] - min)/(max - min) * 65535);
				greenChannel[i][j] = (float) Math.ceil((greenChannel[i][j] - min)/(max - min) * 65535);
				blueChannel[i][j] = (float) Math.ceil((blueChannel[i][j] - min)/(max - min) * 65535);
				hist[(int)redChannel[i][j]] += 1;
				hist[(int)greenChannel[i][j]] += 1;
				hist[(int)blueChannel[i][j]] += 1;
				nbrEntries +=3;
				//System.out.println(hist[0]+ " "+ (int)redChannel[i][j]+ "nbrEntries "+ nbrEntries);
			}
		}
		
		int sum = 0;
		double counts = nbrEntries - hist[0];//counts is the number of intensities above 0
		double newMaximum = 0;
		for (int i=1;i<65536;i++){
			//System.out.println("sum: "+sum+" counts: "+ counts+"nbrEntries "+nbrEntries+"hist[0] "+hist[0]);
			sum = sum +hist[i];
			newMaximum = i;
			if (sum>percentile * counts){
				
				break;
			}
		}
		if (verbose){
			System.out.println("Normalization:  Max: "+max+" newMax: "+newMaximum);
		}
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				redChannel[i][j] = (float)Math.min((redChannel[i][j] )/(newMaximum)*65535,65535);
				greenChannel[i][j] = (float)Math.min((greenChannel[i][j] )/(newMaximum)*65535,65535);
				blueChannel[i][j] = (float)Math.min((blueChannel[i][j] )/(newMaximum)*65535,65535);
			}
		}
		
		ArrayList<float[][]> ret = new ArrayList<float[][]>();
		ret.add(redChannel);
		ret.add(greenChannel);
		ret.add(blueChannel);
		return ret;
	}
	
	public ArrayList<StormLocalization> getLocs() {
		return locs;
	}
	public void setLocs(ArrayList<StormLocalization> locs) {
		this.locs = locs;
	}
	
	public void createLineSample(double driftx, double drifty, int locsPerFrame, int frames){
		locs = new ArrayList<StormLocalization>();
		for (int frame = 0; frame< frames; frame++){
			double contributionDriftX = driftx * frame / frames;
			double contributionDriftY = drifty * frame / frames;
			//System.out.println(contributionDriftX+" "+contributionDriftY);
			double x = 0;
			double y = 0;
			double t = 0;
			for (int locPerFrameCounter = 0; locPerFrameCounter < locsPerFrame; locPerFrameCounter++) {
				double s1 = (Math.random()*8);
				int s2 = (int)s1;
				//System.out.println(s2);
				switch (s2){
				case 0:
					//System.out.println("case0");
					t = Math.random()*Math.PI;
					x = 10000 * Math.cos(t);
					y = 10000 * Math.cos(t) * Math.sin(t);	
					break;
				case 1:
					//System.out.println("case1");
					t = Math.random()*Math.PI;
					x = 6000 * Math.sin(t)*Math.sin(t);
					y = 10000 * Math.cos(t) * Math.cos(t);
					break;
				case 2:
					//System.out.println("case2");
					t = Math.random()*Math.PI;
					x = 10000 * Math.sin(t);
					y = 10000 * Math.cos(t) * Math.cos(t);
					break;
				case 3:
					//System.out.println("case3");
					t = Math.random()*Math.PI;
					x = 10000 * Math.cos(t)*Math.tan(t);
					y = 3000 * Math.cos(t) * Math.sin(t);
					break;
				case 4:
					//System.out.println("case3");
					t = Math.random()*Math.PI;
					x = 10000 * Math.cos(t) + 4000;
					y = 3000 *Math.sin(t)+ 4000;
					break;
				case 5:
					//System.out.println("case3");
					t = 2*Math.random()*Math.PI;
					x = 3000 * Math.cos(t) + 4000;
					y = 3000 *Math.sin(t)+ 6000;
					break;
				case 6:
					//System.out.println("case3");
					x = Math.random()*600+3000;
					y = Math.random()*600+3000;
					break;
					
				case 7:
					//System.out.println("case3");
					x = Math.random()*100+2000;
					y = Math.random()*1000+10000;
					break;
				}

				locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), Math.random(), frame, 100));
				if (frame<frames-2){
					locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), Math.random(), frame+1, 100));
				}
			}
		}
	}
	
	public void createSingleLineSample(double driftx, double drifty, int locsPerFrame, int frames){
		locs = new ArrayList<StormLocalization>();
		for (int frame = 0; frame< frames; frame++){
			double contributionDriftX = driftx * frame / frames;
			double contributionDriftY = drifty * frame / frames;
			//System.out.println(contributionDriftX+" "+contributionDriftY);
			double x = 0;
			double y = 0;
			double z = 0;
			double t = 0;
			for (int locPerFrameCounter = 0; locPerFrameCounter < locsPerFrame; locPerFrameCounter++) {
				x = Math.random() * 2000;
				y = 500;
				z = x * 0.6;

				locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), z+(Math.random()*50-25), frame, 100));
				if (frame<frames-2){
					locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), z+(Math.random()*50-25), frame+1, 100));
				}
			}
		}
	}
	
	
	
	public ArrayList<StormLocalization> connectPoints(double dx, double dy, double dz, int maxdistBetweenLocalizations) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<StormLocalization>> traces = Utility.findTraces(locs, dx, dy, dz, maxdistBetweenLocalizations);
		ArrayList<StormLocalization> connectedLoc = Utility.connectTraces(traces);
		this.processingLog += "Con"; 
	
	
		this.locs = connectedLoc;
		
		return connectedLoc;
	}
	
	
	
	
	
	
	public StormData findSubset(int minFrame, int maxFrame, boolean setZCoordToZero){
		int currframe = minFrame;
		StormData subset = new StormData();
		subset.setFname(fname);
		subset.setPath(path);
		int start = findFirstIndexForFrame(minFrame);
		int ende = findLastIndexForFrame(maxFrame);
		for (int i = start; i<ende; i++){
			if (setZCoordToZero){
				StormLocalization sl = getElement(i);
				sl.setZ(0);
				subset.addElement(sl);
			}
			else{
				subset.addElement(getElement(i));
			}
		}
		return subset;
	}
	
	public StormData findSubset(int minFrame, int maxFrame){ //only returns StormLocalizations which come from frames between minFrame and maxFrame
		return findSubset(minFrame, maxFrame, false); 
	}
	
	public void sortX(){
		isSortedByFrame = false;
		Comparator<StormLocalization> compX = new StormLocalizationXComperator();
		Collections.sort(this.locs,compX);
	}
	
	
	public ArrayList<ArrayList<Integer>> getLocsPerFrame(){
		int binWidth = 50;
		int maxFrame = ((Double) getDimensions().get(7)).intValue();
		ArrayList<Integer> frames = new ArrayList<Integer>();
		ArrayList<Integer> locsPerFrame = new ArrayList<Integer>();
		for (int i = 0; i<(maxFrame/binWidth)+1; i++){
			frames.add(i*binWidth);
			locsPerFrame.add(0);
		}
		sortFrame();
		for (int i = 0;i<getLocs().size();i++){
			locsPerFrame.set(locs.get(i).getFrame()/binWidth,locsPerFrame.get(locs.get(i).getFrame()/binWidth)+1);
		}
		
		ArrayList<ArrayList<Integer>> tmp = new ArrayList<ArrayList<Integer>>();
		tmp.add(frames);
		tmp.add(locsPerFrame);

		return tmp;
	}
	
	public void addStormData(StormData tmp) {
		if (this.getLocs().size()==0){
			this.path = tmp.getPath();
			this.fname = tmp.getFname();
		}
		int lastFrame = (int) ((double)getDimensions().get(7));
		int firstFrame = (int) ((double)tmp.getDimensions().get(6));
		for (int i = 0; i< tmp.getSize(); i++){
			StormLocalization sl = tmp.getElement(i);
			sl.setFrame(sl.getFrame()+lastFrame-firstFrame);
			getLocs().add(sl);
		}
		
	}
	

	public String findBasename(String fname){
		return basename =  fname.substring(0, fname.length()-4);
	}
	
	public void setBasename(String basename){
		this.basename = basename;
	}
	
	public String getBasename(){
		if (this.basename.equals("")){
			return findBasename(this.fname);
		}
		else {
			return this.basename;		
		}
	}
	
	//get name of parent folder
	public String getMeassurement(){
		String[] parts = path.split("\\\\");
		return parts[parts.length-3];
	}
	
	

	public void addToProcessingLog(String extenstion){
		this.processingLog = this.processingLog + extenstion;
	}
	public void addToLog(Object obj){
		logs.add(obj);
	}
	public ArrayList<Object> getLog(){
		return logs;
	}

	

	public void setLog(ArrayList<Object> logs){
		this.logs = logs;
	}
	public void copyAttributes(StormData sd){
		this.fname = sd.getFname();
		this.path = sd.getPath();
		this.logs = sd.getLog();
		this.processingLog = sd.getProcessingLog();
	}
	
	public void copyStormData(StormData sd){
		this.fname = sd.getFname();
		this.path = sd.getPath();
		this.logs = sd.getLog();
		this.locs = sd.getLocs();
		this.processingLog = sd.getProcessingLog();
	}
	
	public ArrayList<StormLocalization> cropCoords(double xmin, double xmax, double ymin, double ymax){
		return cropCoords(xmin, xmax, ymin, ymax, this.getDimensions().get(4), this.getDimensions().get(5), this.getDimensions().get(6).intValue(), this.getDimensions().get(7).intValue(), this.getDimensions().get(8),this.getDimensions().get(9));
	}
	
	public ArrayList<StormLocalization> cropCoords(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax){
		return cropCoords(xmin, xmax, ymin, ymax, zmin, zmax, this.getDimensions().get(6).intValue(), this.getDimensions().get(7).intValue(), this.getDimensions().get(8),this.getDimensions().get(9));
	}
	
	public ArrayList<StormLocalization> adjustCrop(ArrayList<StormLocalization> sl, double ymin, double ymax, double xmin, double xmax){
		Comparator<StormLocalization> compY = new StormLocalizationYComperator();
		Collections.sort(sl,compY);
		Collections.sort(this.locs, compY);
		double oldYmax = ymin; 
		if(sl.get(sl.size()-1).getY()<ymin){//if there is no overlap
			sl.clear();
		}
		else{
			while (sl.get(0).getY()<ymin){
				sl.remove(0);
			}
			oldYmax = sl.get(sl.size()-1).getY();
		}
		for (int i = 0; i<this.getLocs().size(); i++){
			StormLocalization csl = this.getLocs().get(i);
			if (csl.getY()>oldYmax && csl.getY()<ymax&csl.getX()>xmin&&csl.getX()<xmax){
				sl.add(this.getLocs().get(i));
			}
			if (csl.getY()>ymax){
				break;
			}
		}
		return sl;
	}
	
	public ArrayList<StormLocalization> scaleCoords(double scaleX, double scaleY, double scaleZ){
		for (int i = 0; i< this.locs.size(); i++){
			this.locs.get(i).setX(this.locs.get(i).getX()*scaleX);
			this.locs.get(i).setY(this.locs.get(i).getY()*scaleY);
			this.locs.get(i).setZ(this.locs.get(i).getZ()*scaleZ);
		}
		return this.locs;
	}
	
	public synchronized ArrayList<StormLocalization> cropCoords(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax, int framemin, int framemax, double minInt, double maxInt){
		Comparator<StormLocalization> compX = new StormLocalizationXComperator();
		Collections.sort(this.locs,compX);
		ArrayList<StormLocalization> croppedList = new ArrayList<StormLocalization>();
		for (int i = 0; i<this.locs.size(); i++){
			if (this.locs.get(i).getX()<xmin){
				continue;
			}
			if (this.locs.get(i).getX()>xmax){
				continue;
			}
			croppedList.add(this.locs.get(i));
		}
		Comparator<StormLocalization> compY = new StormLocalizationYComperator();
		Collections.sort(croppedList,compY);
		ArrayList<StormLocalization> croppedList2 = new ArrayList<StormLocalization>();
		for (int i = 0; i<croppedList.size(); i++){
			if (croppedList.get(i).getY()<ymin){
				continue;
			}
			if (croppedList.get(i).getY()>ymax){
				continue;
			}
			croppedList2.add(croppedList.get(i));
		}
		croppedList.clear();
		Comparator<StormLocalization> compZ = new StormLocalizationZComperator();
		Collections.sort(croppedList2,compZ);
		for (int i = 0; i<croppedList2.size(); i++){
			if (croppedList2.get(i).getZ()<zmin){
				continue;
			}
			if (croppedList2.get(i).getZ()>zmax){
				continue;
			}
			croppedList.add(croppedList2.get(i));
		}
		croppedList2.clear();
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(croppedList,compFrame);
		for (int i = 0; i<croppedList.size(); i++){
			if (croppedList.get(i).getFrame()<framemin){
				continue;
			}
			if (croppedList.get(i).getFrame()>framemax){
				continue;
			}
			croppedList2.add(croppedList.get(i));
		}
		
		croppedList.clear();
		Comparator<StormLocalization> compInt = new StormLocalizationIntComperator();
		Collections.sort(croppedList2,compInt);
		for (int i = 0; i<croppedList2.size(); i++){
			if (croppedList2.get(i).getIntensity()<minInt){
				continue;
			}
			if (croppedList2.get(i).getIntensity()>maxInt){
				continue;
			}
			croppedList.add(croppedList2.get(i));
		}
		
		this.locs = croppedList;
		return croppedList;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	
	//add value specified in shift to the coordinate of the coordDim th dimension
	//order is x,y,z,frame,int,angle
	public void shift(int coordDim, double shift) {
		for (StormLocalization sl:locs){
			switch (coordDim) {
				case 0:
					sl.setX(sl.getX()+shift);
					break;
				case 1:
					sl.setY(sl.getY()+shift);
					break;
				case 2:
					sl.setZ(sl.getZ()+shift);
					break;
				case 3:
					sl.setFrame((int) (sl.getFrame()+shift));
					break;
				case 4:
					sl.setIntensity(sl.getIntensity()+shift);
					break;
				case 5:
					sl.setAngle(sl.getAngle()+shift);
					break;
				default:
					System.out.println("no valid coordinat to shift");
			}
		}
	}
	

	
	
		
}
	

