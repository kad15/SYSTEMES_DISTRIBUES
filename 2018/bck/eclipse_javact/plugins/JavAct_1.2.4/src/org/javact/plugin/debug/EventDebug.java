package org.javact.plugin.debug;

import org.javact.plugin.actions.ActionDebug;


/**
 * This class is used to manipulate an event of the debug file
 *
 * @author Grégoire Sage
 * @version $Revision: 1.1 $
  */
public class EventDebug {
    //~ Instance fields --------------------------------------------------------

	/* The line from the debug file, splited according to the patern "\\\\" (which is "\"
	 * in as regular expression)
	 */	
    private String[] line;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EventDebug object.
     *
     * @param _line The line from the debug file, splited according to the patern "\\\\" (which is "\"
	 * in as regular expression)
     */
    public EventDebug(String[] _line) {
        line = _line;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Format the information of the event into a string
     *
     * @return A string describing the event (with only the information chosen)
     */
    public String format() {
        Debug d = ActionDebug.debug;
        String res = "[" + (d.getStep() + 1) + "/" + d.getNbEvents() + "]   ";

        if (d.getParam(Debug.DATE)) {
            res += (getDate() + "   ");
        }

        if (d.getParam(Debug.MACHINE)) {
            res += (getMachine() + "   ");
        }

        if (d.getParam(Debug.PORT)) {
            res += (getPort() + "   ");
        }

        if (d.getParam(Debug.BEHAVIOR)) {
            res += (getBehavior() + "   ");
        }

        if (d.getParam(Debug.ACTOR)) {
            res += (getActor() + "   ");
        }

        if (d.getParam(Debug.JVMFREEMEMORY)) {
            res += (getJVMFreeMemory() + " bytes   ");
        }

        if (d.getParam(Debug.CPU)) {
            res += (getCPU() + "   ");
        }

        if (d.getParam(Debug.FREERAM)) {
            res += (getFreeRAM() + "   ");
        }

        if (d.getParam(Debug.METHOD)) {
            res += (getMethod() + "   ");
        }

        if (d.getParam(Debug.ARGS)) {
            String[] args = getArgs();

            for (int i = 0; i < args.length; i++) {
                res += (args[i] + "   ");
            }
        }

        return res;
    }

    /**
     * @return A string representing the actor appearing in the event
     */
    public String getActor() {
        return line[Debug.ACTOR];
    }

    /**
     * @return A string representing the arguments of the method appearing in the event
     */
    public String[] getArgs() {
        String[] args = new String[line.length - Debug.ARGS];

        for (int i = 0; i < args.length; i++) {
            args[i] = line[i + Debug.ARGS];
        }

        return args;
    }

    /**
     * @return A string representing the behaviot appearing in the event
     */
    public String getBehavior() {
        return line[Debug.BEHAVIOR];
    }

    /**
     * @return A string representing the CPU value appearing in the event
     */
    public String getCPU() {
        return line[Debug.CPU];
    }

    /**
     * @return A string representing the date appearing in the event
     */
    public String getDate() {
        String dateBrut = line[Debug.DATE];

        return dateBrut.substring(1, dateBrut.length() - 1);
    }

    /**
     * @return A string representing the free RAM appearing in the event
     */
    public String getFreeRAM() {
        return line[Debug.FREERAM];
    }

    /**
     * @return A string representing the JVM's free memory appearing in the event
     */
    public String getJVMFreeMemory() {
        return line[Debug.JVMFREEMEMORY];
    }

    /**
     * @return A string representing the machine appearing in the event
     */
    public String getMachine() {
        return line[Debug.MACHINE];
    }

    /**
     * @return A string representing the method appearing in the event
     */
    public String getMethod() {
        return line[Debug.METHOD];
    }

    /**
     * @return A string representing the port appearing in the event
     */
    public String getPort() {
        return line[Debug.PORT];
    }
}
