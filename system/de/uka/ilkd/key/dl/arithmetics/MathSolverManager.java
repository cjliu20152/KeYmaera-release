/**
 * File created 25.01.2007
 */
package de.uka.ilkd.key.dl.arithmetics;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uka.ilkd.key.dl.options.DLOptionBean;
import de.uka.ilkd.key.dl.utils.XMLReader;
import de.uka.ilkd.key.gui.Settings;

/**
 * Manager class for the arithemtic solvers. It reads the initialization data
 * from the config file and initializes the available classes and puts them into
 * a HashMap.
 * 
 * @author jdq
 * @since 25.01.2007
 * 
 */
public abstract class MathSolverManager {
    /**
     * 
     */
    private static final String CONFIG_XML = "hybridkey.xml";

    private static Map<String, ICounterExampleGenerator> COUNTEREXAMPLE_GENERATORS = new HashMap<String, ICounterExampleGenerator>();

    private static Map<String, IODESolver> ODESOLVERS = new HashMap<String, IODESolver>();

    private static Map<String, IQuantifierEliminator> QUANTIFIER_ELMINIATORS = new HashMap<String, IQuantifierEliminator>();

    private static Map<String, ISimplifier> SIMPLIFIERS = new HashMap<String, ISimplifier>();

    /**
     * @param filename
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws DOMException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static void initialize(String filename) throws DOMException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException,
            SecurityException, InvocationTargetException,
            NoSuchMethodException, XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {

        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "/MathSolvers/MathSolver";
        Document document = new XMLReader(filename).getDocument();
        NodeList nodes = (NodeList) xpath.evaluate(expression, document,
                XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Class<?> forName = Class.forName((String) xpath.evaluate("class",
                    node, XPathConstants.STRING));
            IMathSolver solver = (IMathSolver) forName.getDeclaredConstructor(
                    Node.class).newInstance(node);
            if (solver instanceof ICounterExampleGenerator) {
                COUNTEREXAMPLE_GENERATORS.put(solver.getName(),
                        (ICounterExampleGenerator) solver);
            }
            if (solver instanceof IODESolver) {
                ODESOLVERS.put(solver.getName(), (IODESolver) solver);
            }
            if (solver instanceof IQuantifierEliminator) {
                QUANTIFIER_ELMINIATORS.put(solver.getName(),
                        (IQuantifierEliminator) solver);
            }
            if (solver instanceof ISimplifier) {
                SIMPLIFIERS.put(solver.getName(), (ISimplifier) solver);
            }
            try {
                String optStr = (String) xpath.evaluate("optionbean", node,
                        XPathConstants.STRING);
                if (optStr != null && !optStr.equals("")) {
                    Class<? extends Settings> options = (Class<? extends Settings>) Class.forName(optStr);
                    Settings object = (Settings) options.getDeclaredField("INSTANCE").get(options);
                    DLOptionBean.INSTANCE.addSubOptionBean(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the list of available mathsolvers
     * 
     * @return the list of available mathsolvers
     */
    public static Set<String> getODESolvers() {
        if (ODESOLVERS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ODESOLVERS.keySet();
    }

    /**
     * Returns the list of available mathsolvers
     * 
     * @return the list of available mathsolvers
     */
    public static Set<String> getSimplifiers() {
        if (SIMPLIFIERS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return SIMPLIFIERS.keySet();
    }

    /**
     * Returns the list of available mathsolvers
     * 
     * @return the list of available mathsolvers
     */
    public static Set<String> getQuantifierEliminators() {
        if (QUANTIFIER_ELMINIATORS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return QUANTIFIER_ELMINIATORS.keySet();
    }

    /**
     * Returns the list of available mathsolvers
     * 
     * @return the list of available mathsolvers
     */
    public static Set<String> getCounterExampleGenerators() {
        if (COUNTEREXAMPLE_GENERATORS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return COUNTEREXAMPLE_GENERATORS.keySet();
    }

    /**
     * Returns the MathInterface with the given name or null if it does not
     * exist
     * 
     * @param name
     *                the name of the interface to get
     * @return the MathInterface with the given name or null if it does not
     *         exist
     */
    public static IODESolver getODESolver(String name) {
        if (ODESOLVERS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ODESOLVERS.get(name);
    }

    /**
     * TODO jdq documentation since Aug 17, 2007
     * 
     * @return
     */
    public static IODESolver getCurrentODESolver() {
        IODESolver result = getODESolver(DLOptionBean.INSTANCE.getOdeSolver());
        if (result == null) {
            throw new IllegalStateException(
                    "ODESolver option is not set correctly. Could not find: "
                            + DLOptionBean.INSTANCE.getOdeSolver());
        }
        return result;
    }

    /**
     * Returns the MathInterface with the given name or null if it does not
     * exist
     * 
     * @param name
     *                the name of the interface to get
     * @return the MathInterface with the given name or null if it does not
     *         exist
     */
    public static ICounterExampleGenerator getCounterExampleGenerator(
            String name) {
        if (COUNTEREXAMPLE_GENERATORS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return COUNTEREXAMPLE_GENERATORS.get(name);
    }

    /**
     * TODO jdq documentation since Aug 17, 2007
     * 
     * @return
     */
    public static ICounterExampleGenerator getCurrentCounterExampleGenerator() {
        ICounterExampleGenerator result = getCounterExampleGenerator(DLOptionBean.INSTANCE
                .getCounterExampleGenerator());
        if (result == null) {
            throw new IllegalStateException(
                    "Counter example generator option is not set correctly. Could not find: "
                            + DLOptionBean.INSTANCE
                                    .getCounterExampleGenerator());
        }
        return result;
    }

    /**
     * Returns the MathInterface with the given name or null if it does not
     * exist
     * 
     * @param name
     *                the name of the interface to get
     * @return the MathInterface with the given name or null if it does not
     *         exist
     */
    public static IQuantifierEliminator getQuantifierElimantor(String name) {
        if (QUANTIFIER_ELMINIATORS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return QUANTIFIER_ELMINIATORS.get(name);
    }

    /**
     * TODO jdq documentation since Aug 17, 2007
     * 
     * @return
     */
    public static IQuantifierEliminator getCurrentQuantifierEliminator() {
        IQuantifierEliminator result = getQuantifierElimantor(DLOptionBean.INSTANCE
                .getQuantifierEliminator());
        if (result == null) {
            throw new IllegalStateException(
                    "Quantifier Eliminator option is not set correctly. Could not find: "
                            + DLOptionBean.INSTANCE.getQuantifierEliminator());
        }
        return result;
    }

    /**
     * Returns the MathInterface with the given name or null if it does not
     * exist
     * 
     * @param name
     *                the name of the interface to get
     * @return the MathInterface with the given name or null if it does not
     *         exist
     */
    public static ISimplifier getSimplifier(String name) {
        if (SIMPLIFIERS.isEmpty()) {
            try {
                initialize(CONFIG_XML);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return SIMPLIFIERS.get(name);
    }

    /**
     * TODO jdq documentation since Aug 17, 2007
     * 
     * @return
     */
    public static ISimplifier getCurrentSimplifier() {
        ISimplifier result = getSimplifier(DLOptionBean.INSTANCE
                .getSimplifier());
        if (result == null) {
            throw new IllegalStateException(
                    "Simplifier option is not set correctly. Could not find: "
                            + DLOptionBean.INSTANCE.getSimplifier());
        }
        return result;
    }

    /**
     * @directed
     */
    private XMLReader lnkXMLReader;

    /**
     * @directed
     * @label creates
     */
    private IMathSolver lnkIMathSolver;

    /**
     * TODO jdq documentation since Aug 17, 2007
     * 
     * @throws RemoteException
     */
    public static void resetAbortState() {
        for (ISimplifier solver : SIMPLIFIERS.values()) {
            try {
                solver.resetAbortState();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (IQuantifierEliminator solver : QUANTIFIER_ELMINIATORS.values()) {
            try {
                solver.resetAbortState();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (ICounterExampleGenerator solver : COUNTEREXAMPLE_GENERATORS
                .values()) {
            try {
                solver.resetAbortState();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (IODESolver solver : ODESOLVERS.values()) {
            try {
                solver.resetAbortState();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static boolean isCounterExampleGeneratorSet() {
        return !DLOptionBean.INSTANCE.getCounterExampleGenerator().equals("");
    }

    public static boolean isODESolverSet() {
        return !DLOptionBean.INSTANCE.getOdeSolver().equals("");
    }

    public static boolean isQuantifierEliminatorSet() {
        return !DLOptionBean.INSTANCE.getQuantifierEliminator().equals("");
    }

    public static boolean isSimplifierSet() {
        return !DLOptionBean.INSTANCE.getSimplifier().equals("");
    }

}