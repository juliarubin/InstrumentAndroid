package com.android.drop.features.astatic;

import java.io.IOException;
import java.util.Collections;

import org.xmlpull.v1.XmlPullParserException;

import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

import com.android.drop.features.data.Constants;

public class AnalysisWrapper {
	private static AnalysisWrapper instance = null;
	private static CallGraph sootCallGraph = null;

	private AnalysisWrapper() {
	}
	
	public static AnalysisWrapper getInstance() {
		if (instance == null) {
			instance = new AnalysisWrapper();
			//initCallGraphGen();
		}
		return instance;
	}
	
	public static CallGraph getSootCallGraph() {
		if (sootCallGraph == null) {
			initCallGraphGen();
		}
		return sootCallGraph;
	}
	
	private static void initCallGraphGen() {
		SetupApplication app = new SetupApplication(Constants.SOOT_ANDROID_PLATFORM, Constants.INPUT_DIR + Constants.APP_NAME + ".apk");
		try {
			app.calculateSourcesSinksEntrypoints(Constants.BASE_DIR  + "SourcesAndSinks_empty.txt");
			soot.G.reset();
			
			Options.v().set_src_prec(Options.src_prec_apk);
			Options.v().set_process_dir(Collections.singletonList(Constants.INPUT_DIR + Constants.APP_NAME + ".apk"));           
			Options.v().set_android_jars(Constants.SOOT_ANDROID_PLATFORM);
			Options.v().set_whole_program(true);
			Options.v().set_allow_phantom_refs(true);
			Options.v().set_output_format(Options.output_format_none);
			Options.v().setPhaseOption("cg.spark", "on");
			//PhaseOptions.v().setPhaseOption("cg.cha", "enabled:true"); // true or off
			Scene.v().loadNecessaryClasses();      
			SootMethod entryPoint = app.getEntryPointCreator().createDummyMain();
			Options.v().set_main_class(entryPoint.getSignature());
			Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
			System.out.println(entryPoint.getActiveBody());
			PackManager.v().runPacks();
			//CHATransformer.v().transform();
			sootCallGraph = Scene.v().getCallGraph();
			System.out.println(sootCallGraph.size());	
		} catch (IOException | XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}
}
