/***************************************************************************
 *   Copyright (C) 2007 by André Platzer                                   *
 *   @informatik.uni-oldenburg.de                                          *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package de.uka.ilkd.key.dl.strategy.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.WeakHashMap;

import orbital.util.SequenceIterator;
import orbital.util.Setops;
import de.uka.ilkd.key.dl.formulatools.Prog2LogicConverter;
import de.uka.ilkd.key.dl.formulatools.ReplacementSubst;
import de.uka.ilkd.key.dl.formulatools.TermTools;
import de.uka.ilkd.key.dl.model.DLProgram;
import de.uka.ilkd.key.dl.model.DiffSystem;
import de.uka.ilkd.key.dl.model.NamedElement;
import de.uka.ilkd.key.dl.model.Star;
import de.uka.ilkd.key.dl.transitionmodel.DependencyStateGenerator;
import de.uka.ilkd.key.gui.Main;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.StatementBlock;
import de.uka.ilkd.key.logic.ConstrainedFormula;
import de.uka.ilkd.key.logic.IteratorOfConstrainedFormula;
import de.uka.ilkd.key.logic.IteratorOfTerm;
import de.uka.ilkd.key.logic.JavaBlock;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.Sequent;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermBuilder;
import de.uka.ilkd.key.logic.op.Modality;
import de.uka.ilkd.key.logic.op.QuanUpdateOperator;
import de.uka.ilkd.key.pp.LogicPrinter;
import de.uka.ilkd.key.pp.ProgramPrinter;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.updatesimplifier.ArrayOfAssignmentPair;
import de.uka.ilkd.key.rule.updatesimplifier.AssignmentPair;
import de.uka.ilkd.key.rule.updatesimplifier.Update;
import de.uka.ilkd.key.strategy.termgenerator.TermGenerator;

/**
 * DiffInd candidates.
 * 
 * @author ap
 */
public class DiffIndCandidates implements TermGenerator {
    private static final boolean DEBUG_CANDIDATES = false;
    private static final boolean DEBUG_GENERATOR = false;


    public final static TermGenerator INSTANCE = new DiffIndCandidates();

    private static final TermBuilder tb = TermBuilder.DF;

    private DiffIndCandidates() {
    }

    public IteratorOfTerm generate(RuleApp app, PosInOccurrence pos, Goal goal) {
        Term term = pos.subTerm();
        // unbox from update prefix
        while (term.op() instanceof QuanUpdateOperator) {
            term = ((QuanUpdateOperator) term.op()).target(term);
        }
        if (!(term.op() instanceof Modality && term.javaBlock() != null
                && term.javaBlock() != JavaBlock.EMPTY_JAVABLOCK && term
                .javaBlock().program() instanceof StatementBlock)) {
            throw new IllegalArgumentException("inapplicable to " + pos);
        }
        final DLProgram program = (DLProgram) ((StatementBlock) term
                .javaBlock().program()).getChildAt(0);
        Term currentInvariant;
        if (program instanceof DiffSystem) {
            currentInvariant = ((DiffSystem) program).getInvariant();
        } else if (program instanceof Star) {
            currentInvariant = tb.tt();
        } else {
            throw new IllegalArgumentException("Don't know how to handle "
                    + program);
        }
        final Services services = goal.proof().getServices();

        if (DEBUG_CANDIDATES) {
            System.out.println("INDCANDIDATES " + app.rule().name() + " ...");
        }
        // we do not need post itself as candidate because diffind or strategy
        // can handle this.
        // we only consider sophisticated choices
        // l.add(post); // consider diffind itself als diffstrengthening
        final Iterator<Term> candidateGenerator = 
            indCandidates(goal.sequent(), pos, currentInvariant,
                        services);
        return new IteratorOfTerm() {

            @Override
            public boolean hasNext() {
                return candidateGenerator.hasNext();
            }

            @Override
            public Term next() {
                return candidateGenerator.next();
            }
            
        };
    }

    /**
     * Determine diffind candidates for the formula at the given position in the
     * given sequent. Find candidates relative to the given current invariant,
     * which is already known to be invariant.
     * 
     * @param seq
     * @param pos
     * @param currentInvariant
     * @param services
     * @return
     */
    protected Iterator<Term> indCandidates(Sequent seq, PosInOccurrence pos,
            Term currentInvariant, Services services) {
        Term term = pos.subTerm();
        final Update update = Update.createUpdate(term);
        // unbox from update prefix
        if (term.op() instanceof QuanUpdateOperator) {
            term = ((QuanUpdateOperator) term.op()).target(term);
            if (term.op() instanceof QuanUpdateOperator)
                throw new AssertionError(
                        "assume that nested updates have been merged");
        }
        if (!(term.op() instanceof Modality && term.javaBlock() != null
                && term.javaBlock() != JavaBlock.EMPTY_JAVABLOCK && term
                .javaBlock().program() instanceof StatementBlock)) {
            throw new IllegalArgumentException("inapplicable to " + pos);
        }
        final DLProgram program = (DLProgram) ((StatementBlock) term
                .javaBlock().program()).getChildAt(0);
        // compute transitive closure of dependency relation
        final Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> tdep =
            computeTranstitiveDependencies(program, services);
        final Set<de.uka.ilkd.key.logic.op.ProgramVariable> modifieds = getModifiedVariables(tdep);
        Set<de.uka.ilkd.key.logic.op.ProgramVariable> frees = getFreeVariables(tdep);
        if (!frees.containsAll(modifieds)) {
            System.out.println("WARNING: dependencies should be reflexive. Hence modified variables "
                    + modifieds + " should be contained in free variables " + frees + " for " + program);
        }
        frees = Collections.unmodifiableSet(frees);

        // find candidates
        final Set<Term> possibles = getMatchingCandidates(update,
                currentInvariant, seq, services, modifieds);
        //System.out.println("POSSIBLE CANDIDATES:  ....\n" + possibles);

        // quick cache for singleton clauses
        Set<Set<Term>> resultConjuncts = new LinkedHashSet<Set<Term>>();
        // lazily generated store for non-singleton clauses 
        Set<Set<Term>> resultPowerGenerators1 = new LinkedHashSet<Set<Term>>();
        // compare variables according to number of dependencies
        PriorityQueue<de.uka.ilkd.key.logic.op.ProgramVariable> depOrder = new PriorityQueue<de.uka.ilkd.key.logic.op.ProgramVariable>(
                tdep.size() + 1, dependencyComparator(tdep));
        depOrder.addAll(tdep.keySet());
        while (!depOrder.isEmpty()) {
            // min is the minimal element, i.e. the element which depends on the
            // least number of variables
            final de.uka.ilkd.key.logic.op.ProgramVariable min = depOrder.poll();
            // cluster of variables that min depends on, transitively
            final Set<de.uka.ilkd.key.logic.op.ProgramVariable> cluster = tdep.get(min);
            assert cluster != null : "transitive closure should contain all information for "
                    + min;
            // assert depOrder.containsAll(cluster) : "choosing minimum " + min
            // + " from " + dep
            // + " entails that remaining " + depOrder
            // + " still contains all dependent vars from its cluster " +
            // cluster;
            // find formulas that only refer to cluster
            Set<Term> matches = selectMatchingCandidates(possibles, cluster,
                    modifieds, frees);
            if (DEBUG_GENERATOR) {
                System.out.println("    GENERATORS: for minimum " + min + " with its cluster "
                    + cluster + " generators are " + matches);
            }
            if (!matches.isEmpty()) {
                // only add subsets of size 1
                for (Term t : matches) {
                    resultConjuncts.add(Collections.singleton(t));
                }
                // lazily add all nonempty subsets of size>1
                resultPowerGenerators1.add(matches);
            }
            depOrder.removeAll(cluster);
        }
        
        // order by size (number of conjuncts), ascending and cluster coverage, descending
        final Comparator<Set<Term>> sizeComparator = clusterComparator(tdep);
        List<Set<Term>> orderedResultConjuncts = new ArrayList<Set<Term>>(
                resultConjuncts);
        Collections.sort(orderedResultConjuncts, sizeComparator);

        // as last resort, add all for the universal cluster but put them late 
        Set<Term> matches = selectMatchingCandidates(possibles, Setops.union(modifieds, frees),
                modifieds, frees);
        // lazily generated store for non-singleton clauses 
        Set<Set<Term>> resultPowerGenerators2 = new LinkedHashSet<Set<Term>>();
        if (!matches.isEmpty()) {
            Set<Term> extraGenerators = new LinkedHashSet<Term>(matches);
            // only add subsets of size 1
            for (Term t : matches) {
                Set<Term> ts = Collections.singleton(t);
                if (!resultConjuncts.contains(ts)) {
                    orderedResultConjuncts.add(ts);
                    extraGenerators.add(t);
                }
            }
            // lazily add all nonempty subsets of size>1
            resultPowerGenerators2.add(matches);
            if (DEBUG_GENERATOR) {
                System.out.println("    EXTRA-GENERATORS: are " + extraGenerators);
            }
        }

        Collections.sort(orderedResultConjuncts, sizeComparator);

        List<Term> result = new LinkedList<Term>();
        for (Set<Term> s : orderedResultConjuncts) {
            assert s.size() == 1 : "use only singletons, first";
            result.add(s.iterator().next());
        }
        // remove trivial candidates
        result.remove(tb.ff());
        result.remove(tb.tt());

        if (DEBUG_CANDIDATES) {
            System.out.println("INDCANDIDATE-BASIS ...");
            for (Term c : result) {
                try {
                    final LogicPrinter lp = new LogicPrinter(new ProgramPrinter(
                            null), Main.getInstance().mediator().getNotationInfo(), services);
                    lp.printTerm(c);
                    System.out.print("...  " + lp.toString());
                } catch (Exception ignore) {
                    System.out.println("......  " + c.toString());
                    ignore.printStackTrace();
                }
            }
        }
        // quickly return size-1 formulas, and only lazily generate powersets
        return new SequenceIterator(new Iterator[] {
                result.iterator(),
                new LazyPowerGenerator(resultPowerGenerators1, resultPowerGenerators2, resultConjuncts, sizeComparator)
        });
    }

    /**
     * Compares (conjunctive sets of) terms depending on the dependency cluster of their variable occurrences.
     * @param tdep
     * @return
     */
    private static final Comparator<Set<Term>> clusterComparator(final Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> tdep) {
        /**
         * Cache for clusters of terms
         */
        final Map<Term, Set<de.uka.ilkd.key.logic.op.ProgramVariable>> clusters = new WeakHashMap<Term, Set<de.uka.ilkd.key.logic.op.ProgramVariable>>();
        return new Comparator<Set<Term>>() {
            /**
             * Get the cluster of a term, i.e., all its ProgramVariables including transitive dependencies
             * @param t
             * @return
             */
            private Set<de.uka.ilkd.key.logic.op.ProgramVariable> getCluster(Term t) {
                Set<de.uka.ilkd.key.logic.op.ProgramVariable> c = clusters.get(t);
                if (c != null) { 
                    return c;
                }
                c = new LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>();
                for (de.uka.ilkd.key.logic.op.ProgramVariable pv: TermTools.projectProgramVariables(TermTools.getSignature(t))) {
                    c.add(pv);
                    if (tdep.containsKey(pv)) {
                        c.addAll(tdep.get(pv));
                    } else {
                        // occurs but has no dependencies, including itself
                    }
                }
                clusters.put(t, c);
                return c;
            }

            private Set<de.uka.ilkd.key.logic.op.ProgramVariable> getCluster(Set<Term> ts) {
                Set<de.uka.ilkd.key.logic.op.ProgramVariable> c = new LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>();
                for (Term t: ts) {
                    c.addAll(getCluster(t));
                }
                return c;
            }

            public int compare(Set<Term> arg0, Set<Term> arg1) {
                int sizeCmp = arg0.size() - arg1.size();
                if (sizeCmp != 0) {
                    return sizeCmp;
                }
                Set<de.uka.ilkd.key.logic.op.ProgramVariable> occurrence0 = TermTools.projectProgramVariables(TermTools.getSignature(arg0));
                Set<de.uka.ilkd.key.logic.op.ProgramVariable> cluster0 = getCluster(arg0);
                Set<de.uka.ilkd.key.logic.op.ProgramVariable> occurrence1 = TermTools.projectProgramVariables(TermTools.getSignature(arg1));
                Set<de.uka.ilkd.key.logic.op.ProgramVariable> cluster1 = getCluster(arg1);
                occurrence0.retainAll(cluster0);
                occurrence1.retainAll(cluster1);
                // negative if occurrence0 covers more of cluster0 than occurrence1 covers of cluster1
                int clustercoverage = occurrence1.size() * cluster0.size() - occurrence0.size() * cluster1.size();
                return clustercoverage;
            }
        };
    }

    //

    /**
     * Get all possibly matching formulas, regardless of their actual form.
     * 
     * @param update
     *                the update characterising the state at which to determine
     *                candidates.
     * @param currentInvariant
     *                the current known invariant, which holds but is not yet
     *                strong enough to imply post.
     * @param seq
     *                the sequent for which we want to find candidates.
     * @return
     */
    private Set<Term> getMatchingCandidates(Update update,
            Term currentInvariant, Sequent seq, Services services,
            Set<de.uka.ilkd.key.logic.op.ProgramVariable> modifieds) {
        // @todo need to conside possible generation renamings by update
        final ReplacementSubst revert = revertStateChange(update, services,
                modifieds);
        if (DEBUG_CANDIDATES) {
            System.out.println("REVERT " + revert);
        }
        Set<Term> invariant = TermTools.splitConjuncts(currentInvariant);
        // System.out.println(" INVARIANT " + invariant + " of " +
        // system.getInvariant() + " of " + system);

        Set<Term> matches = new LinkedHashSet<Term>();
        ArrayOfAssignmentPair asss = update.getAllAssignmentPairs();
        for (int i = 0; i < asss.size(); i++) {
            AssignmentPair ass = asss.getAssignmentPair(i);
            Term xhp = ass.locationAsTerm();
            assert xhp.arity() == 0 : "only works for atomic locations";
            assert ass.location() instanceof de.uka.ilkd.key.logic.op.ProgramVariable  : "expecting arity 0 program variables";
            de.uka.ilkd.key.logic.op.ProgramVariable x = (de.uka.ilkd.key.logic.op.ProgramVariable) ass.location();
            Term t = ass.value();
            // System.out.println(x + "@" + x.getClass());
            // turn single update into equation
            Term revertedt = revert.apply(t);
            if (TermTools.occursIn(xhp, revertedt)) {
                // if x occurs in t then can't do that without alpha-renaming stuff
                continue;
            }
            Term equation = tb.equals(xhp, revertedt);
            assert equation.op() instanceof de.uka.ilkd.key.logic.op.Equality : "different equalities shouldn't be mixed up: "
                    + " "
                    + xhp
                    + " equaling "
                    + revertedt
                    + " gives "
                    + equation
                    + " with operator " + equation.op();
            if (!TermTools.subsumes(invariant, equation)) {
                matches.add(equation);
            }
        }
        // @todo respect different update levels
        for (IteratorOfConstrainedFormula i = seq.antecedent().iterator(); i
                .hasNext();) {
            final ConstrainedFormula cf = i.next();
            Term fml = cf.formula();
            // @todo if fml contains both a key and a value of
            // revert.getReplacements then skip
            fml = revert.apply(fml);
            if (FOSequence.INSTANCE.isFOFormula(fml)
                    && !TermTools.subsumes(invariant, fml)) {
                matches.add(fml);
            }
        }
        return matches;
    }

    /**
     * Determines the update state reversals of update.
     */
    private ReplacementSubst revertStateChange(Update update,
            Services services, Set<de.uka.ilkd.key.logic.op.ProgramVariable> modifieds) {
        Map<Term, Term> undos = new HashMap<Term, Term>();
        ArrayOfAssignmentPair asss = update.getAllAssignmentPairs();
        for (int i = 0; i < asss.size(); i++) {
            AssignmentPair ass = asss.getAssignmentPair(i);
            Term x = ass.locationAsTerm();
            assert x.arity() == 0 : "only works for atomic locations";
            assert ass.location() instanceof de.uka.ilkd.key.logic.op.ProgramVariable  : "expecting arity 0 program variables";
            de.uka.ilkd.key.logic.op.ProgramVariable xvar = (de.uka.ilkd.key.logic.op.ProgramVariable) ass.location();
            if (!modifieds.contains(xvar))
                continue;
            x = tb.var(xvar);
            Term t = ass.value();
            if (TermTools.getSignature(t).isEmpty()) {
                // skip trivial reverting to, e.g., pure number expressions
                continue;
            }
            if (t.arity() > 0) {
                // TODO what aboout this case?
                continue;
            }
            undos.put(t, x);
        }
        return ReplacementSubst.create(undos);
    }

    /**
     * Select those candidates that have a promising form.
     * 
     * @param seq
     * @param mycluster within which cluster of modified variables to look
     * @return
     */
    private Set<Term> selectMatchingCandidates(Set<Term> candidates,
            Set<de.uka.ilkd.key.logic.op.ProgramVariable> mycluster, Set<de.uka.ilkd.key.logic.op.ProgramVariable> mymodifieds,
            Set<de.uka.ilkd.key.logic.op.ProgramVariable> myfrees) {
        Set<Name> cluster = TermTools.projectNames(mycluster);
        Set<Name> modifieds = TermTools.projectNames(mymodifieds);
        Set<Name> frees = TermTools.projectNames(myfrees);
        // @todo need to conside possible generation renamings by update
        Set<Term> matches = new LinkedHashSet<Term>();
        for (Term fml : candidates) {
            final Set<Name> occurrences = Collections
                    .unmodifiableSet(TermTools.projectNames(TermTools.projectProgramVariables(FOVariableNumberCollector
                            .getVariables(fml))));
            if (Setops.intersection(occurrences, modifieds).isEmpty()) {
                // System.out.println(" skip " + fml + " as no change. Changes:
                // " + modifieds + " disjoint from occurrences " + occurrences);
                // trivially invariant as nothing changes
                continue;
            }
            if (!cluster.containsAll(Setops.intersection(occurrences, frees))) {
                // System.out.println(" skip " + fml + " as " + occurrences + "
                // not in " + vars + " ");
                // variables with more dependencies
                //if (Setops.intersection(Setops.intersection(occurrences, cluster),modifieds).isEmpty()) {
                    continue;
            }
            // FV(fml)\cap MV(system) != EMPTY
            matches.add(fml);
        }
        return matches;
    }

    // helper methods

    /**
     * compare variables according to number of dependencies
     */
    private Comparator<de.uka.ilkd.key.logic.op.ProgramVariable> dependencyComparator(
            final Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> tdep) {
        Comparator<de.uka.ilkd.key.logic.op.ProgramVariable> dependencyComparator = new Comparator<de.uka.ilkd.key.logic.op.ProgramVariable>() {
            @Override
            public int compare(de.uka.ilkd.key.logic.op.ProgramVariable o1,
                    de.uka.ilkd.key.logic.op.ProgramVariable o2) {
                int size = tdep.get(o1).size();
                int size2 = tdep.get(o2).size();
                if (size == size2) {
                    return o1.name().toString().compareTo(
                            o2.name().toString());
                } else {
                    return size - size2;
                }
            }
        };
        return dependencyComparator;
    }

    /**
     * Determine free variables set from dependency relation
     */
    private Set<de.uka.ilkd.key.logic.op.ProgramVariable> getFreeVariables(
            final Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> tdep) {
        // free variables FV(system)
        Set<de.uka.ilkd.key.logic.op.ProgramVariable> frees = new LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>();
        for (LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable> s : tdep.values()) {
            // @todo frees.addAll(modifieds) as well?
            frees.addAll(s);
        }
        return frees;
    }

    /**
     * Determine modified variables set from dependency relation
     */
    private Set<de.uka.ilkd.key.logic.op.ProgramVariable> getModifiedVariables(
            final Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> tdep) {
        // modified variables MV(system)
        Set<de.uka.ilkd.key.logic.op.ProgramVariable> modifieds = new LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>();
        for (de.uka.ilkd.key.logic.op.ProgramVariable s : tdep.keySet()) {
            modifieds.add(s);
        }
        modifieds = Collections.unmodifiableSet(modifieds);
        return modifieds;
    }


    /**
     * Caches transitive dependency information for DiffSystems.getDifferentialFragment()
     * @internal we exploit that dependency information for DiffSystems does not depend on invariant regions.
     */
//    private final Map<DiffSystem, Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>>> dependencyCache =
//        new WeakHashMap<DiffSystem, Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>>>();
    private Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> computeTranstitiveDependencies(
            final DLProgram program, Services services) {
//        if (program instanceof DiffSystem) {
//            Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> cached = 
//                dependencyCache.get(((DiffSystem)program).getDifferentialFragment());
//            if (cached != null) {
//                return cached;
//            }
//        }
        Map<de.uka.ilkd.key.dl.model.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.dl.model.ProgramVariable>> dep = computeDependencies(program);
        final Map<de.uka.ilkd.key.dl.model.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.dl.model.ProgramVariable>> tdep = DependencyStateGenerator
                .createTransitiveClosure(dep);
        // convert dlProgramVariable to ProgramVariable
        final Map<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>> convertedtdep =
            new LinkedHashMap<de.uka.ilkd.key.logic.op.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>>();
        for (Map.Entry<de.uka.ilkd.key.dl.model.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.dl.model.ProgramVariable>> s : tdep
                .entrySet()) {
            if (!s.getValue().contains(s.getKey())) {
//                System.out.println("WARNING: transitive dependencies are typically reflexive. Hence "
//                        + s.getKey() + " should be contained in " + s.getValue());
            }
            LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable> converted = new LinkedHashSet<de.uka.ilkd.key.logic.op.ProgramVariable>(s.getValue().size()+1);
            for (de.uka.ilkd.key.dl.model.ProgramVariable dlpv : s.getValue()) {
                converted.add(Prog2LogicConverter.getCorresponding(dlpv, services));
            }
            convertedtdep.put(Prog2LogicConverter.getCorresponding(s.getKey(), services), converted);
        }
//        if (program instanceof DiffSystem) {
//            dependencyCache.put(((DiffSystem)program).getDifferentialFragment(), convertedtdep);
//        }
        return convertedtdep;
    }

    private Map<de.uka.ilkd.key.dl.model.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.dl.model.ProgramVariable>> computeDependencies(
            final DLProgram program) {
        final Map<de.uka.ilkd.key.dl.model.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.dl.model.ProgramVariable>> dep = DependencyStateGenerator
                .generateDependencyMap(program).getDependencies();
        for (Map.Entry<de.uka.ilkd.key.dl.model.ProgramVariable, LinkedHashSet<de.uka.ilkd.key.dl.model.ProgramVariable>> s : dep
                .entrySet()) {
            if (!s.getValue().contains(s.getKey())) {
                System.out.println("WARNING: transitive dependencies are typically reflexive. Hence "
                        + s.getKey() + " should be contained in " + s.getValue());
            }
        }
        return dep;
    }

    // @todo refactor to get rid of this duplication
    private static Set<Name> projectNames2(Set<? extends NamedElement> s) {
        Set<Name> r = new LinkedHashSet<Name>();
        for (NamedElement n : s) {
            r.add(n.getElementName());
        }
        return r;
    }


    /**
     * An iterator which only stores the power set generators and starts generating lazily, i.e.,
     * at the first call.
     * @author ap
     *
     */
    private static class LazyPowerGenerator implements Iterator {
        
        private final Comparator<Set<Term>> sizeComparator;

        // quick cache for singleton clauses
        private final Set<Set<Term>> alreadyCoveredConjuncts;
        // lazily generated store for non-singleton clauses 
        private final Set<Set<Term>> resultPowerGenerators;
        private final Set<Set<Term>> resultPowerGenerators2;
        
        private Iterator<Term> lazySource = null;

        private Iterator<Term> lazyInit() {
            List<Set<Term>> orderedResultConjuncts = new ArrayList<Set<Term>>();
            for (Set<Term> matches : resultPowerGenerators) {
                // add all nonempty subsets of size > 1 (because size 1 has already been covered)
                Set<Set<Term>> subsets = Setops.powerset(matches);
                subsets.remove(Collections.EMPTY_SET);
                subsets.removeAll(alreadyCoveredConjuncts);
                subsets.removeAll(orderedResultConjuncts);
                orderedResultConjuncts.addAll(subsets);
                alreadyCoveredConjuncts.addAll(subsets);
            }
            Collections.sort(orderedResultConjuncts, sizeComparator);

            for (Set<Term> matches : resultPowerGenerators2) {
                // add all nonempty subsets of size > 1 (because size 1 has already been covered)
                Set<Set<Term>> subsets = Setops.powerset(matches);
                subsets.remove(Collections.EMPTY_SET);
                subsets.removeAll(alreadyCoveredConjuncts);
                subsets.removeAll(orderedResultConjuncts);
                orderedResultConjuncts.addAll(subsets);
                alreadyCoveredConjuncts.addAll(subsets);
            }
            Collections.sort(orderedResultConjuncts, sizeComparator);
            
            List<Term> result = new LinkedList<Term>();
            for (Set<Term> s : orderedResultConjuncts) {
                result.add(tb.and(TermTools.genericToOld(s)));
            }
            // remove trivial candidates
            result.remove(tb.ff());
            result.remove(tb.tt());
            if (DEBUG_GENERATOR) { 
                System.out.println("LAZY INDCANDIDATE ...");
                for (Term c : result) {
                    try {
                        final LogicPrinter lp = new LogicPrinter(new ProgramPrinter(
                                null), Main.getInstance().mediator().getNotationInfo(), Main.getInstance().mediator().getServices());
                        lp.printTerm(c);
                        System.out.print("...  " + lp.toString());
                    } catch (Exception ignore) {
                        System.out.println("......  " + c.toString());
                        ignore.printStackTrace();
                    }
                }
            }
            return result.iterator();
        }
        public LazyPowerGenerator(Set<Set<Term>> resultConjuncts,
                Set<Set<Term>> resultPowerGenerators,
                Set<Set<Term>> resultPowerGenerators2,
                Comparator<Set<Term>> sizeComparator) {
            super();
            this.alreadyCoveredConjuncts = resultConjuncts;
            this.resultPowerGenerators = resultPowerGenerators;
            this.resultPowerGenerators2 = resultPowerGenerators2;
            this.sizeComparator = sizeComparator;
        }

        @Override
        public boolean hasNext() {
            if (lazySource == null) {
                lazySource = lazyInit();
            }
            return lazySource.hasNext();
        }

        @Override
        public Object next() {
            if (lazySource == null) {
                lazySource = lazyInit();
            }
            return lazySource.next();
        }

        @Override
        public void remove() {
            if (lazySource == null) {
                lazySource = lazyInit();
            }
            lazySource.remove();
        }

    }

}
