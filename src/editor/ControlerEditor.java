package editor;


import java.io.Serializable;
import java.util.ArrayList;

import editorModulesDefinitions.EndLoopGUI;
import editorModulesDefinitions.LoopIterableGUI;


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
				break; //this prevents additional execution of the body of the for loop
			}
			else{
				psp.perform();
				
			}
		}
	}
	
	//not nice but also no time... This finds the right parameter entry for the given tag depending on the iteration step,
	//only makes sense for LoopIterableGUI objects
	public String getIterationValue(String parameterTag){
		for (LoopModules loopModule :loopModules){
			ArrayList<editor.LoopModules.ParameterTag> pts = ((LoopIterableGUI) loopModule).getParameters();
			for (int i =0; i<pts.size();i++){
				if(pts.get(i).getParameterTag().equals(parameterTag)){
					return pts.get(i).getParameterList().get(loopModule.getCurrentIterationStep());
				}
			}
		}
		System.err.println("No loop with the given parameter tag:" + parameterTag+" could be found.");
		return "-1";
	}
}
