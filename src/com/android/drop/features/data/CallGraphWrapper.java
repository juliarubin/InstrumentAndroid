package com.android.drop.features.data;

import java.io.IOException;
import java.util.Collections;

import org.xmlpull.v1.XmlPullParserException;

import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

public class CallGraphWrapper {
	private static CallGraphWrapper instance = null;
	private static CallGraph sootCallGraph = null;

	private CallGraphWrapper() {
	}
	
	public static CallGraphWrapper getInstance() {
		if (instance == null) {
			instance = new CallGraphWrapper();
			init();
		}
		return instance;
	}
	
	public static CallGraph getSootCallGraph() {
		if (sootCallGraph == null) {
			init();
		}
		return sootCallGraph;
	}
	
	private static void init() {
		SetupApplication app = new SetupApplication(Constants.SOOT_ANDROID_PLATFORM, Constants.INPUT_DIR + Constants.APP_NAME + ".apk");
		try {
			app.calculateSourcesSinksEntrypoints(Constants.BASE_DIR  + "SourcesAndSinks.txt");
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
}
