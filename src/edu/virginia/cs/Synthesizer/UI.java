package edu.virginia.cs.Synthesizer;


import edu.mit.csail.sdg.alloy4.Err;

import javax.swing.*;
import java.util.*;
import java.io.*;


public class UI {
    static String appType = "SCC";
    static String archStyle = "II";
    static String appDesc = "MIDAS";
    static String workspace = ".\\";
    private static String mergedFile;
    private static String solutionDirectory = ".\\";

    static int maxSolNo = 3;

    public static void main(String args[]) {

        solutionDirectory = ".";
        if (args.length == 2) {
            parseFirst2args(args);

            System.out.println("The Application Type is: " + appType + ", and the Application Description file is: " + appDesc);
            System.out.println("Please specify the desired architectural style.");
        } else if (args.length == 3) {
            parseFirst2args(args);

            parseThirdArgs(args[2]);


//            System.out.println("====================================");
//            System.out.println("Application Type: " + appType );
//            System.out.println("Application Description file: " + appDesc );
//            System.out.println("Architectural style: " + archStyle);
//            System.out.println("The Application Type is: " + appType + ", and the Application Description file is: " + appDesc+ ", and the choice Architectural style is: " + archStyle);
//            System.out.println("====================================");

            solve(maxSolNo);
//            System.out.println("After Solve");
            return;
        } else if (args.length == 4) {
            parseFirst2args(args);

            parseThirdArgs(args[2]);

            maxSolNo = Integer.parseInt(args[3]);

            solve(maxSolNo);
//            System.out.println("After Solve");
            return;
        } else if (args.length == 5) {
            parseFirst2args(args);

            parseThirdArgs(args[2]);

            maxSolNo = Integer.parseInt(args[3]);
            solutionDirectory = new String(args[4]);

            solve(maxSolNo);
//            System.out.println("After Solve");
            return;

        } else { // Gets inputs through the command prompt program
            do {
                System.out.print("> ");
                //System.out.println("Enter Name of the Domain: ");

                BufferedReader dataIn = new BufferedReader(
                        new InputStreamReader(System.in));

                String command = new String();
                // Read entered data into name variable
                try {
                    command = dataIn.readLine();
                } catch (IOException e) {
                    System.out.println("Error!");
                }

                List<String> commandParameters = new ArrayList<String>();


                StringTokenizer st = new StringTokenizer(command);
                while (st.hasMoreTokens()) {
                    commandParameters.add(st.nextToken());
                }
                if (commandParameters.get(0).contains("appType")) {
                    appType = commandParameters.get(1);

                    //String file = "Workspace\\" + appType + "\\" + appType + ".als";
                    String file = workspace + appType + "\\" + appType + ".als";
                    editAlloyScript(file);

                    System.out.println("The " + commandParameters.get(1) + " application type has been saved.");
                } else if (commandParameters.get(0).contains("archStyle")) {
                    archStyle = commandParameters.get(1);


                    //String file = "Workspace\\" + appType + "\\" + archStyle + ".als";
                    String file = workspace + appType + "\\" + archStyle + ".als";
//                editAlloyScript(file);

                    System.out.println("The " + commandParameters.get(1) + " architectural style has been saved.");

                } else if (commandParameters.get(0).contains("appDesc")) {
                    appDesc = commandParameters.get(1);

                    //String file = "Workspace\\" + appType + "\\" + appDesc + ".als";
                    String file = workspace + appType + "\\" + appDesc + ".als";
                    editAlloyScript(file);

                    System.out.println("The " + commandParameters.get(1) + " application description has been saved.");
                } else if (commandParameters.get(0).contains("merge")) {

                    buildMergedSCC_IIModel();

                } else if (commandParameters.get(0).contains("test")) {

                    // Find the first solution, should it exists
                    //System.out.printf("\nPress C to Continue...\n");
                    System.out.println("Enter any key for next solution or Enter the character E to exit");
                    Scanner kb = new Scanner(System.in);
                    boolean pressed = false;
                    String entered = "";
                    pressed = pressKey(kb, pressed);
                    while (!pressed) {
                        //find next solution
                        pressed = pressKey(kb, pressed);
                    }

                }
/*
            else if (commandParameters.get(0).contains("acme")) {

                Parser_SCC_II p1 = new Parser_SCC_II("K:\\Eclipse-Workspapce\\IntellijIDEA\\ArchitecturalMapping\\Workspace\\SCC\\LunarLander_SCC_II.als_answer_0.xml",
                        "K:\\Eclipse-Workspapce\\IntellijIDEA\\ArchitecturalMapping\\Workspace\\SCC\\LunarLander_SCC_II.als_answer_0.acme");
            }
*/
                else if (commandParameters.get(0).contains("edit")) {

                    //String file = "Workspace\\" + commandParameters.get(1) + ".als";
                    String file = workspace + commandParameters.get(1) + ".als";
                    editAlloyScript(file);


                    System.out.println("The " + commandParameters.get(1) + " has been saved.");
                    //doSomething0();

                    //doSomething1();
                } else if (commandParameters.get(0).contains("solve")) {

                    solve(maxSolNo);

                }
                //else if (commandParameters.get(0).contains("list_problems"))
                else if (commandParameters.get(0).contains("exit")) {
                    break;
                } else {
                    System.out.println("command not found!");
                }

            } while (true);
        }
    }

    private static void parseThirdArgs(String arg) {
        archStyle = arg;
        if ((archStyle.toLowerCase().contains("ii"))
                || (archStyle.toLowerCase().contains("implicit"))) {
            archStyle = "II";
        } else if ((archStyle.toLowerCase().contains("oo"))
                || (archStyle.toLowerCase().contains("object"))) {
            archStyle = "OO";
        } else if ((archStyle.toLowerCase().contains("pipe"))
                || (archStyle.toLowerCase().contains("pnf"))
                || (archStyle.toLowerCase().contains("pf"))) {
            archStyle = "PnF";
        } else if (archStyle.toLowerCase().contains("rest")) {
            archStyle = "REST";
        } else {
            System.err.println("Your architectural style choice is not supported");
            System.exit(1);
        }
    }

    private static void parseFirst2args(String[] args) {
        if (args[0].contains("SCC")) // Sense-Compute-Control
            appType = "SCC";
        else if (args[0].contains("ACF")) // Aspect-Oriented Composition of Functions
            appType = "ACF";
        else if (args[0].contains("CF")) // Composition of Functions
            appType = "CF";
        else if (args[0].contains("SD")) // State Machine
            appType = "SD";
        else {
            System.err.println("The first argument should contain the Application Type");
            System.exit(1);
        }

        appDesc = args[1];
    }

    private static void solve(int maxSolNoParam) {
        // The case that the user did not pass the solution Directory as a parameter 
        if (solutionDirectory.equals("."))
            solutionDirectory = workspace + appType;
        mergedFile = workspace + appType + "\\" + appDesc + "_" + archStyle + ".als";
        try {
            //SmartBridge solver = new SmartBridge("K:\\Eclipse-Workspapce\\IntellijIDEA\\ArchitecturalMapping\\Workspace\\SCC\\LunarLander_SCC_II.als");
//            SmartBridge solver = new SmartBridge(solutionDirectory, mergedFile, appDesc, appType, archStyle, maxSolNoParam);
            SmartBridge solver = new SmartBridge(solutionDirectory, mergedFile, maxSolNoParam);
//            System.out.println("After Solver");
        } catch (Err err) {
            err.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static void buildMergedSCC_OOModel() {
        //-------------------------------
        //------Creating Alloy Model-----
        //-------------------------------

        String moduleName = appDesc + "_" + archStyle;
        mergedFile = workspace + appType + "\\" + moduleName + ".als";
        //mergedAlloyFile = workspace + appType + "\\" + appDesc + "_" + archStyle + ".als";

        try {
            String composerFile = appType + "_" + archStyle;
            FileWriter fstream = new FileWriter(mergedFile);
            BufferedWriter out = new BufferedWriter(fstream);


            // module LunarLander_SCC_II
            out.write("module " + moduleName + "\r\n");

            //import composerFile: open SCC_II
            out.write("open " + composerFile + "\r\n");

            //import appDesc: open LunarLander_SCC
            out.write("open " + appDesc + "\r\n");

            out.write("pred show{\r\n");
            out.write("\thandle[]\r\n");
            out.write("\t# System = 1\r\n");
            out.write("}\r\nrun show for 14");

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void buildMergedSCC_IIModel() {

        //-------------------------------
        //------Creating Alloy Model-----
        //-------------------------------

        String moduleName = appDesc + "_" + archStyle;
        String composerFile = appType + "_" + archStyle;
        // Create file
        //mergedAlloyFile = "Workspace\\" + appType + "\\" + moduleName + ".als";
        mergedFile = workspace + appType + "\\" + moduleName + ".als";

        try {
            FileWriter fstream = new FileWriter(mergedFile);
            BufferedWriter out = new BufferedWriter(fstream);


            // module LunarLander_SCC_II
            out.write("module " + moduleName + "\r\n");

            //import composerFile: open SCC_II
            out.write("open " + composerFile + "\r\n");

            //import appDesc: open LunarLander_SCC
            out.write("open " + appDesc + "\r\n");

            /*
            pred show{
                handle[]
                # System = 1
            }
            run show for 12
            */
            out.write("pred show{\r\n");
            out.write("\thandle[]\r\n");
            out.write("\t# System = 1\r\n");
            out.write("}\r\nrun show for 12");

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static boolean pressKey(Scanner kb, boolean pressed) {
        String entered;
        entered = "";
        entered = kb.next();
        System.out.printf(entered + "\n");
        if ((entered.equals("E")) || entered.equals("e"))
            pressed = true;
        return pressed;
    }

    private static void editAlloyScript(String AlloyFile) {
        //String program = "C:\\WINDOWS\\notepad.exe";
        String program = "notepad";
        //String file = "K:\\Eclipse-Workspapce\\IntellijIDEA\\ArchitecturalMapping\\Workspace\\UI.als";
        //String file = "Workspace\\"+ commandParameters.get(1)+".als";

        Runtime load = Runtime.getRuntime();
        try {
            load.exec(program + " " + AlloyFile);
        } catch (IOException ee) {
            JOptionPane.showMessageDialog(null, "The document doesn't exist!",
                    "Not found Document!", JOptionPane.ERROR_MESSAGE);
            ee.printStackTrace();
        }
    }
}


