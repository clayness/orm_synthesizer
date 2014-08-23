package edu.virginia.cs.Synthesizer;

import java.io.IOException;
import java.util.Scanner;

public class PressKey {
    public static void main(String args[]) {
        // System.in.Read Version
        System.out.printf("System.in.Read Version..\nEnter Char ==> ");
        try {
            char temp = (char) System.in.read();
            System.out.printf("\nYou Entered: " + temp + "\n");
        } catch (Exception exe) {
            exe.printStackTrace();
        }

        // Scanner Version
        System.out.printf("\n\nScanner Version..\nEnter Char ==> ");
        Scanner kb = new Scanner(System.in);
        String tString = kb.next();
        char temp2 = tString.charAt(0);
        System.out.printf("\nYou Entered: " + temp2 + "\n");

        System.out.printf("\nPress C to Continue...\n");
        boolean pressed = false;
        String entered = "";
        while (!pressed) {
            entered = "";
            entered = kb.next();
            System.out.printf(entered + "\n");
            if ((entered.equals("C")) || entered.equals("c"))
                pressed = true;
        }

        try {
            char c;
            System.out.println("Enter any key for next solution or Enter the character E to exit");
            while (((c = getChar()) != 'E')) {
                System.out.println("You have entered " + c);
            }
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        // Don't wait for enter key Version
        /*
          public static void waitForCont()
          {
              System.out.printf("\nPress C to Continue...\n");
              boolean pressed = false;
              String entered = "";
              while(!pressed)
              {
                  entered=kb.next();
                  if((entered.equals("C"))||entered.equals("c"))
                      pressed=true;
              }
          }
          */


    }

    static public char getChar() throws IOException {
        char ch = (char) System.in.read();

        //input();
        return ch;
    }

    static public void input() throws IOException {
        while ((char) System.in.read() != '\n') ;
    }


}
