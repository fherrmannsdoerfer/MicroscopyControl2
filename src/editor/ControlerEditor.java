package editor;


import java.io.Serializable;
import java.util.ArrayList;


public class ControlerEditor implements Serializable{
	MainFrameEditor mfe;
	
	public void resetProgressBar(ArrayList<EditorModules> functions){
	    for (int i = 0; i<functions.size(); i++){
	        functions.get(i).setProgressbarValue(0);
	    }
	}
	
	public void resetData(){
		
	}
	
	public void setMainFrameReference(MainFrameEditor mfe) {
		this.mfe = mfe;
	}
	public void startProcessing(
			ArrayList<EditorModules> functions) {

		//if (functions.get(0) instanceof BatchProcessingGUI){
		//	batchprocessingWorkflow(functions);
		//}
		//else{
		normalWorkflow(functions);
		//}
		
		System.out.println("Program finished");
	}

	private void normalWorkflow(ArrayList<EditorModules> functions) {
		for (EditorModules psp: functions){
			psp.perform();
		}
	}

	/*
	//if the first module is the BatchProcessing module this workflow is executed
	private void batchprocessingWorkflow(
			ArrayList<EditorModules> functions) {
		functions.get(0).perform();
		int numberModules = functions.size();
		int numberBatchprocessingCycles = ((BatchProcessingGUI)functions.get(0)).getPaths().size();
		for (int k = 0; k<numberBatchprocessingCycles; k++){
			try{
				resetData();
				resetProgressBar(functions);
				functions.get(0).setProgressbarValue((int)Math.floor(100*(float)k/(float)numberBatchprocessingCycles));
				if (functions.get(1) instanceof ImportModules){
					System.out.println("next module is import module");
					((ImportModules) functions.get(1)).setPath(((BatchProcessingGUI)functions.get(0)).getPaths().get(k));
				}
				else{
					System.out.println("please select an import module after the batchprocessing module");
				}
				for (int i = 1; i<numberModules;i++){
					functions.get(i).process(ch1, ch2);
				}
			}
			catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		functions.get(0).setProgressbarValue(99);
	}
	*/
}
