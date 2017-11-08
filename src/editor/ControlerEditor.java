package editor;


import java.io.Serializable;
import java.util.ArrayList;

import editorModulesDefinitions.EndLoopGUI;


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
		processModuleList(functions);
		//}
		
		System.out.println("Program finished");
	}

	private void processModuleList(ArrayList<EditorModules> functions) {
		for (int i =0; i<functions.size();i++){
			EditorModules psp = functions.get(i);
			if (psp instanceof LoopModules){
				LoopModules thisModule = (LoopModules) psp;
				thisModule.perform();
				for (int r= 0;r<thisModule.getNbrIterations();r++){
					ArrayList<EditorModules> subset = new ArrayList<EditorModules>();
					int indentCounter = 1;
					for (int j = i+1;j<functions.size(); j++){//start at i+1 to skip the Loop beginning
						EditorModules p = functions.get(j);
						if (p instanceof EndLoopGUI){
							indentCounter -= 1;
							if (indentCounter==0){break;}
						}
						else if (p instanceof LoopModules){indentCounter += 1;}
						else {}
						subset.add(p);
					}
					//recursive call only with the functions inside the loop
					processModuleList(subset);
					thisModule.nextStep();
				}
			}
			else{
				psp.perform();
			}
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
