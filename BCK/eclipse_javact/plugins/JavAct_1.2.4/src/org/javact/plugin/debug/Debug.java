package org.javact.plugin.debug;


import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.swt.SWT;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.javact.plugin.debug.views.EventView;
import org.javact.plugin.debug.views.PlacesView;
import org.javact.plugin.debug.views.PrincipalView;
import org.javact.plugin.tools.JavActUtilities;


import java.awt.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


/**
 * The class is used to interpret the .debug file
 *
 * @author Guillaume Danel
 * @version $Revision: 1.1 $
 */
public class Debug {
	//~ Static fields/initializers ---------------------------------------------
	
	/* The static positions of the fields in a line of the debug file
	 * (the fields are separated by a "\" in this file)
	 */
	public static int DATE = 0;
	public static int MACHINE = 1;
	public static int PORT = 2;
	public static int BEHAVIOR = 3;
	public static int ACTOR = 4;
	public static int JVMFREEMEMORY = 5;
	public static int CPU = 6;
	public static int FREERAM = 7;
	public static int METHOD = 8;
	public static int ARGS = 9;
	
	// the string associated to each event and the number of arguments written in the file
	//!\ Warning : the following static values MUST be the SAME as the ones is javact.util.DebugTools
	public final static String createLocalDebugTxt = "ActorLocalCreation";
	public final static String createLocalFromControlerDebugTxt = "ActorLocalCreationFromControler(GoTo)";  
	public final static String goToTxt = "Goto";
	public final static String sendTxt = "Send";
	public final static String sendWithReplyTxt = "SendWithReply";
	public final static String mailBoxTxt = "GetMessage";
	public final static String becomeTxt = "Become";
	public final static String suicideTxt = "Suicide";   
	public final static String createDebug1Txt = "CreateAtPlaceFromBehavior";
	public final static String createDebug2Txt = "CreateAtPlaceFromBehaviorAndComposants";
	public final static String withBoxTxt = "ChangeBox";
	public final static String withBecTxt = "ChangeBec";
	public final static String withCrtTxt = "ChangeCrt";
	public final static String withMveTxt = "ChangeMve";
	public final static String withSndTxt = "ChangeSnd";
	public final static String withLifTxt = "ChangeLif";
	
	
	
	
	// Used to fill the fields of the pulldown button "ParametersAction"
	public static String[] PARAM = {
		"Date", "Machine", "Port", "Behavior", "Actor", "JVM Free Memory",
		"CPU", "Free RAM", "Method", "Arguments"
	};
	
	//~ Instance fields --------------------------------------------------------
	
	/* The hashMap of the actor which are moving at the time considered
	 * The key is the real name of the actor
	 */
	private HashMap moving = new HashMap();
	
	/* The hashMap of the methods to be shown in the console
	 * The key is the name of the method (example Debug.suicideTxt)
	 */
	private HashMap methodsToBeShown = new HashMap();
	
	/*  The hashMap of PlaceDebug : the places which appeared in the debug file
	 *  The key is the name of the place
	 */
	private HashMap places = new HashMap();    
	
	/*  The hashMap of ActorDebug : the actors which commited suicide
	 */
	private HashMap dead = new HashMap();
	
	// A vector of EventDebug : the events that occured during the execution
	private Vector events = new Vector();
	
	// Used to know which information is to be shown
	private boolean[] toShow = new boolean[10];
	
	// The total number of events appearing in the debug file
	private int nbEvent;
	
	// The number of the event that has last been seen
	private int step;
	
	//~ Constructors -----------------------------------------------------------
	
	/**
	 * Creates a new Debug object.
	 */
	public Debug() {
		/* The following operation has been commented because it was the
		 * source of strange behaviors. fillevents() is now called
		 * after the creation of a new Debug object (see ResetAction and
		 * ActionDebug)
		 * 
		 * filling the hashmap events and places
		 * initDebug();
		 */
	}
	
	//~ Methods ----------------------------------------------------------------
	
	/**
	 * Changes the field i of toShow in its opposite
	 *
	 * @param i The number of the field that has to be changed
	 */
	public void changeParameter(int i) {
		toShow[i] = !toShow[i];
	}
	
	/**
	 * Re-initializes the basic values of the Debug object,
	 * without re-generating the event vector
	 */
	public void reset() {
		// Clearing the actors of the places
		
		Iterator keyIterator = places.keySet().iterator();
		Iterator actorIterator;
		ActorDebug actor;
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();
		HashMap actors;
		
		// delete the actors and their views
		while (keyIterator.hasNext()) {
			actors = ((PlaceDebug) places.get((String) keyIterator.next())).getActors();
			actorIterator = actors.keySet().iterator();
			// Each actor of places being traced needs its view to be disposed
			while (actorIterator.hasNext()) {
				actor = (ActorDebug) actors.get((String) actorIterator.next());
				if(actor.getEventView() != null && window != null){
					window.getActivePage().hideView(actor.getEventView());
				}
			}
			actors.clear();
		}
		
		// deleting the views of the dead actors
		keyIterator = dead.keySet().iterator();
		while (keyIterator.hasNext()) {
			actor = ((ActorDebug) dead.get((String) keyIterator.next()));
			if(actor.getEventView() != null && window != null){
				window.getActivePage().hideView(actor.getEventView());
			}
		}
		
		dead.clear();
		
		// Clearing the "moving" hashmap
		moving.clear();        
		
		// Puting step at 0
		step = 0;
		
		// Drawing the view for the places
		PlacesView view1;
		view1 = (PlacesView) PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow()
		.getActivePage()
		.findView(PlacesView.ID);
		
		if (view1 != null) {
			view1.redraw();
		}
		
		// Updating the principal view
		PrincipalView view2 = (PrincipalView) PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow()
		.getActivePage()
		.findView(PrincipalView.ID);
		
		if (view2 != null) {
			view2.update();
		}
		
		// Clearing the event view
		EventView view3 = (EventView) PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow()
		.getActivePage()
		.findView(EventView.ID);
		
		if (view3 != null) {
			view3.clear();
		}
		
	}
	
	/**
	 * Initializes the attributes of a Debug object
	 * Fill the events hashmap, and the places hashmap by the way
	 */
	public void initDebug() {
		events.clear();
		places.clear();
		dead.clear();
		moving.clear();
		
		// Puting step at 0
		step = 0;
		
		// Initialization of toShow
		for (int i = 0; i < toShow.length; i++) {
			toShow[i] = true;
		}
		
		// Initialization of methodsToBeShown
		methodsToBeShown.put(createLocalDebugTxt, "true");
		methodsToBeShown.put(createLocalFromControlerDebugTxt, "true");
		methodsToBeShown.put(goToTxt, "true");
		methodsToBeShown.put(sendTxt, "true");
		methodsToBeShown.put(sendWithReplyTxt, "true");
		methodsToBeShown.put(mailBoxTxt, "true");
		methodsToBeShown.put(becomeTxt, "true");
		methodsToBeShown.put(suicideTxt, "true");
		methodsToBeShown.put(createDebug1Txt, "true");
		methodsToBeShown.put(createDebug2Txt, "true");
		methodsToBeShown.put(withBoxTxt, "true");
		methodsToBeShown.put(withBecTxt, "true");
		methodsToBeShown.put(withCrtTxt, "true");
		methodsToBeShown.put(withMveTxt, "true");
		methodsToBeShown.put(withSndTxt, "true");
		methodsToBeShown.put(withLifTxt, "true");
		
		// The project that is to be debuged
		IJavaProject javaProject = JavActUtilities.javaProjectDebug;
		
		if (javaProject != null) {
			String debugFilePath = javaProject.getProject().getLocation()
			.toOSString() + File.separator +
			".debug";
			BufferedReader reader = null;
			
			try {
				// An access to the debug file of this project
				reader = new BufferedReader(new FileReader(debugFilePath));
			} catch (FileNotFoundException e) {
				JavActUtilities.error("Debug Mode",
						"File " + debugFilePath + " not found", e);
				
				return;
			}
			
			boolean corrupted = false;
			String error = "";
			int line = 1;
			int firstCorruptedLine = 1;
			
			String s;
			String name = "";
			String[] split;
			
			try {
				// Reading the debug file
				while ((s = reader.readLine()) != null) {
					line++;
					// Separating the fields ("\\\\" only means a "\" as a regular expression)
					split = s.split("\\\\");
					try {
						// Retrieving the name of the machine where the event occured
						name = split[MACHINE] + ":" + split[PORT];
						try {
							// Testing if by mistake the name was allowed with abnormal values 
							Integer.parseInt(split[PORT]);
						} catch (NumberFormatException e) {
							if (!corrupted){
								firstCorruptedLine = line;
							}
							corrupted = true;
							error += "The .debug file is corrupted at line " + line + "\n";
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						if (!corrupted){
							firstCorruptedLine = line;
						}
						corrupted = true;
						error += "The .debug file is corrupted at line " + line + "\n";
					}
					if (!corrupted){
						// Adding the event
						events.add(new EventDebug(split));
						// Adding the place (or replacing it if it already exists)
						places.put(name, new PlaceDebug(name));
					}
				}
				if (corrupted){
					JavActUtilities.error("Debug Mode", error + 
							"\nThe debug will stop at the event number " + firstCorruptedLine);
				}
				
				// Initializing the number of events
				nbEvent = events.size();
				
				// Drawing the view for the places
				PlacesView view1;
				view1 = (PlacesView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.findView(PlacesView.ID);
				
				if (view1 != null) {
					view1.redraw();
				}
				
				// Updating the principal view
				PrincipalView view2 = (PrincipalView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.findView(PrincipalView.ID);
				
				if (view2 != null) {
					view2.update();
				}
				
				// Clearing the event view
				EventView view3 = (EventView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.findView(EventView.ID);
				
				if (view3 != null) {
					view3.clear();
				}
			} catch (IOException e) {
				JavActUtilities.error("Debug Mode",
						"Error while reading " + debugFilePath, e);
				
				return;
			}
			
			try {
				// Closing the reader of the debug file
				reader.close();
			} catch (IOException e) {
				JavActUtilities.error("Debug Mode",
						"Error while closing " + debugFilePath, e);
			}
		}
	}
	
	/**
	 * @return The total number of events in the debug file
	 */
	public int getNbEvents() {
		return nbEvent;
	}
	
	/**
	 * @param i The number of the field
	 *
	 * @return The value of the field i of toShow
	 */
	public boolean getParam(int i) {
		return toShow[i];
	}
	
	/**
	 * @return A vector containing the same values as the hashMap places
	 */
	public Vector getPlaces() {
		Vector myPlaces = new Vector();
		Iterator it = places.values().iterator();
		
		while (it.hasNext()) {
			myPlaces.add(it.next());
		}
		
		return myPlaces;
	}
	
	/**
	 * @return The number of the first event that has no been treated yet
	 */
	public int getStep() {
		return step;
	}
	
	/**
	 * @param i The number by which the attribute step has to be incremented
	 */
	public void incStep(int i) {
		step += i;
	}
	
	/**
	 * Modifies places and moving according to the event number i
	 *
	 * @param i The number of the event
	 */
	public void takeEventIntoAccount(int i) {
		if (events.isEmpty()) {
			JavActUtilities.error("Debug Mode", "There is no event");
			return;
		}
		
		EventDebug event = (EventDebug) events.elementAt(i);
		
		// Caracteristics of the actor concerned by the event
		String behavior = event.getBehavior();
		String actor = event.getActor();
		String method = event.getMethod();
		
		// The actorDebug concerned by this event (initialized later)
		ActorDebug actorDebug = null;
		
		// The color of the text on the screen
		Color color = Color.BLUE;
		
		// Modifying the hashmap places from now
		
		// The place where the action is executed
		PlaceDebug debugPlace = (PlaceDebug) places.get(event.getMachine() +
				":" + event.getPort());
		
		if (method.equals(goToTxt)) {
			// The actor is moving, his place will change
			actorDebug = (ActorDebug) debugPlace.getActors().get(actor);
			debugPlace.removeActor(actorDebug);
			// Saving the actor
			moving.put(actor, actorDebug);
			color = Color.BLACK;
		} else
			if (method.equals(createLocalFromControlerDebugTxt)) {
				// A moving actor has arrived at destination
				
				/* moving.containsKey(actor) is always false for this version
				 * because the actor changes its real name (reference) when it moves
				 */
				if (moving.containsKey(actor)) {
					// The actor comes from a debug place
					ActorDebug wasMoving = (ActorDebug) moving.get(actor);
					actorDebug = new ActorDebug(wasMoving.getDisplayName(), behavior);
					moving.remove(actorDebug.getRealName());
					// The actor is added to its destination
					debugPlace.addActor(actorDebug);
				} else {
					// The actor is created and added to its destination
					actorDebug = new ActorDebug(actor, behavior);
					debugPlace.addActor(actorDebug);
				}
				color = new Color(75, 175, 75);
			} else
				if (method.equals(createLocalDebugTxt)) {
					// A new actor is created
					actorDebug = new ActorDebug(actor, behavior);
					debugPlace.addActor(actorDebug);
					color = new Color(50, 200, 50);
				} else
					if (method.equals(becomeTxt)) {
						// An existing actor changes its behavior
						actorDebug = (ActorDebug) debugPlace.getActors().get(actor);
						actorDebug.setActorClass(behavior);
						color = new Color(200, 100, 255);
					} else
						if (method.equals(suicideTxt)) {
							// An actor is commiting suicide
							actorDebug = (ActorDebug) debugPlace.getActors().get(actor);
							debugPlace.removeActor(actorDebug);
							dead.put(actorDebug.getRealName(),actorDebug);
							color = Color.RED;
						} else {
							actorDebug = (ActorDebug) debugPlace.getActors().get(actor);
						}
		
		// printing the messages
		boolean printEvent;
		// if the method is getMessage it looks like "GetMessage[JAMPrint]" in the debug file
		if (method.startsWith(mailBoxTxt)){
			printEvent = ((String)methodsToBeShown.get(mailBoxTxt)).equals("true");
		} else {
			printEvent = ((String)methodsToBeShown.get(method)).equals("true");
		}
		
		if (printEvent){
			
			// Message on the place window
			if (debugPlace.getEventView() != null && actorDebug.isChecked()) {
				debugPlace.getEventView()
				.appendText(event.format()
						.replaceAll(actorDebug.getRealName(),
								actorDebug.getDisplayName()), color, SWT.NORMAL);
			}
			
			if (debugPlace.isChecked() && actorDebug.isChecked()) {
				// Message on the global window
				EventView eventView = null;
				eventView = (EventView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.findView(EventView.ID);
				eventView.appendText(event.format()
						.replaceAll(actorDebug.getRealName(),
								actorDebug.getDisplayName()), color, SWT.NORMAL);
			}
			
			if (actorDebug.isTraced()) {
				// Message on the actor window
				actorDebug.getEventView()
				.appendText(event.format()
						.replaceAll(actorDebug.getRealName(),
								actorDebug.getDisplayName()), color, SWT.NORMAL);
			}
			
		}
		
		debugPlace.getPlaceComposite().refresh();
	}
	
	public boolean getMethod(String methodName) {
		return ((String)methodsToBeShown.get(methodName)).equals("true");
	}
	
	public void changeMethod(String methodName) {
		String bool = (String) methodsToBeShown.get(methodName);
		methodsToBeShown.remove(methodName);
		if (bool.equals("true")){
			methodsToBeShown.put(methodName,"false");
		} else {
			methodsToBeShown.put(methodName,"true");
		}
	}
}
