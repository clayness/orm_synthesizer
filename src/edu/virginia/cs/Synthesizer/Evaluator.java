package edu.virginia.cs.Synthesizer;

import edu.mit.csail.sdg.alloy4.*;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.parser.Module;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

import java.util.*;


public class Evaluator {

	edu.mit.csail.sdg.alloy4compiler.ast.Module root = null;
    A4Solution ans = null;
    protected String result;
    protected ArrayList<String> resultsArray = new ArrayList<String>();

    public Evaluator() {
    }

    public Evaluator(edu.mit.csail.sdg.alloy4compiler.ast.Module rootElement, A4Solution solution) {
        root = rootElement;
        ans = solution;
    }

    /**
     * This object performs expression evaluation.
     *
     * @param args AlloyFile Query
     */

    public static void main(String args[]) {

//        System.out.println("args[0]: " + args[0]);
//        System.out.println("args[1]: " + args[1]);


        Evaluator e = new Evaluator();
        e.solve(args[0]);
        //e.query(args[1]);
//        System.out.println("results: " + e.query(args[1]).toString());
//        System.out.println("results: " + e.query("state.ran").toString());

    }


    protected void solve(String AlloyFile) {
        // Alloy4 sends diagnostic messages and progress reports to the A4Reporter.
        // By default, the A4Reporter ignores all these events (but you can extend the A4Reporter to display the event for the user)
        A4Reporter rep = new A4Reporter() {
            // For example, here we choose to display each "warning" by printing it to System.out
            @Override
            public void warning(ErrorWarning msg) {
                System.out.print("Relevance Warning:\n" + (msg.toString().trim()) + "\n\n");
                System.out.flush();
            }
        };


        try {
            root = CompUtil.parseEverything_fromFile(rep, null, AlloyFile);
        } catch (Err err) {

            err.printStackTrace();
        }
        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;//.KK;//.MiniSatJNI; //.MiniSatProverJNI;//.SAT4J;

        options.symmetry = 20;
        options.skolemDepth = 1;

        for (Command command : root.getAllCommands()) {
            // Execute the command
            //System.out.println("============ Command "+command+": ============");
            System.out.println("Synthesizing Code ...");
            try {
                ans = TranslateAlloyToKodkod.execute_command(rep, root.getAllReachableSigs(), command, options);
            } catch (Err err) {
                err.printStackTrace();
            }
            for (ExprVar a : ans.getAllAtoms()) {
                root.addGlobal(a.label, a);
            }
            for (ExprVar a : ans.getAllSkolems()) {
                root.addGlobal(a.label, a);
            }
        }
    }

    protected ArrayList<String> query(String inputQuery) {
        resultsArray.clear();
        Expr e;
        //e.g., inputQuery = "Sensor";
        try {
            e = CompUtil.parseOneExpression_fromString(root, inputQuery);
            result = ans.eval(e).toString();
        } catch (Err err) {
        	System.err.println("Error in query: " + inputQuery);
            err.printStackTrace();
        }
//        System.out.println("new result: " + result);
        // e.g., MIDAS_SCC/IntrusionAlarmAnalyzer$0
        StringTokenizer st = new StringTokenizer(result, "{,} ");
        String tmp;
        while (st.hasMoreTokens()) {
            tmp = st.nextToken();
            resultsArray.add(tmp);
        }
        return resultsArray;
    }
    
    protected ArrayList<String> queryNames(String inputQuery) {
        ArrayList<String> names = new ArrayList<String>();
        Expr e;
        String r = null;
        //e.g., inputQuery = "Sensor";
        try {
            e = CompUtil.parseOneExpression_fromString(root, inputQuery);
            r = ans.eval(e).toString();
        } catch (Err err) {
        	System.err.println("Error in query: " + inputQuery);
            err.printStackTrace();
        }
//        System.out.println("new result: " + result);
        // e.g., MIDAS_SCC/IntrusionAlarmAnalyzer$0
        StringTokenizer st = new StringTokenizer(r, "{,} ");
        String tmp;
        while (st.hasMoreTokens()) {
            tmp = st.nextToken();
            names.add(tmp.substring(0, tmp.indexOf("$")));
        }
        return names;
    }


}
