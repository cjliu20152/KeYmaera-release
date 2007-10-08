package de.uka.ilkd.key.dl.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import de.uka.ilkd.key.dl.arithmetics.IMathSolver;
import de.uka.ilkd.key.dl.arithmetics.MathSolverManager;
import de.uka.ilkd.key.gui.AutoModeListener;
import de.uka.ilkd.key.proof.ProofEvent;

/**
 * Class used for the time statistic generation
 * 
 * @author jdq
 * 
 */
public class TimeStatisticGenerator implements AutoModeListener {

    private long time;

    private long oldT;

    private boolean start;

    private static final StatDialog statDialog = new StatDialog();

    public static TimeStatisticGenerator INSTANCE = new TimeStatisticGenerator();

    private TimeStatisticGenerator() {
        time = 0;
    }

    public static void hookTimeStatisticGenerator(JMenu menu) {
        JMenuItem item = new JMenuItem("Open statistic generator");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                statDialog.setVisible(true);
            }

        });
        menu.add(item);
    }

    static class StatDialog extends JFrame {
        /**
         * 
         */
        private static final long serialVersionUID = -185617352313612850L;

        private final JLabel label = new JLabel("Time: 0");

        public StatDialog() {
            add(label);
            pack();
        }
    }

    public void autoModeStarted(ProofEvent e) {
        if (!start) {
            start = true;
            oldT = System.currentTimeMillis();
        }
    }

    public void autoModeStopped(ProofEvent e) {
        if (start) {
            time += System.currentTimeMillis() - oldT;
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    statDialog.label.setText("Time: " + INSTANCE.time);
                    statDialog.pack();
                }
            });
            start = false;
        }
    }

    public long getTime() {
        return time;
    }

    /**
     * @return
     * @throws RemoteException
     */
    public String getSolverTimes() throws RemoteException {
        IMathSolver solver = MathSolverManager.getCurrentQuantifierEliminator();
        return solver.getTimeStatistics();
    }

    /**
     * @return
     * @throws RemoteException
     */
    public long getTotalCaclulationTime() throws RemoteException {
        IMathSolver solver = MathSolverManager.getCurrentQuantifierEliminator();
        return solver.getTotalCalculationTime();
    }

    /**
     * @return
     */
    public long getCachedAnwsers() throws RemoteException {
        IMathSolver solver = MathSolverManager.getCurrentQuantifierEliminator();
        return solver.getCachedAnwserCount();
    }

    /**
     * @return
     */
    public long getQueries() throws RemoteException {
        IMathSolver solver = MathSolverManager.getCurrentQuantifierEliminator();
        return solver.getQueryCount();
    }
}