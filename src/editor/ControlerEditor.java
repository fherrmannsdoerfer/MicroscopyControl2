package editor;


import java.io.Serializable;
import java.util.ArrayList;

import editorModulesDefinitions.EndLoopGUI;


public class ControlerEditor implements Serializable{
	MainFrameEditor mfe;
	ArrayList<LoopModules> loopModules = new ArrayList<LoopModules>();
	
	
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
				loopModules.add(thisModule);
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
				loopModules.remove(thisModule);
			}
			else{
				psp.perform();
			}
		}
	}
	
	public int getIterationCounter(String loopTag){
		for (LoopModules loopModule :loopModules){
			if (loopModule.getLoopTag().equals(loopTag)){
				return loopModule.getCurrentIterationStep();
			}
		}
		System.err.println("No loop with the given loop tag:" + loopTag+" could be found.");
		return -1;
	}
}
