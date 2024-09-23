import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.Dimension;

public class Main {
    private static File file;
    private static int k;

    private static List<String> setA = new ArrayList<>();
    private static List<String> setB = new ArrayList<>();

    private static List<String> union = new ArrayList<>();
    private static List<String> prod = new ArrayList<>();
    private static List<String> star = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        getInputFile();
        askUserK();
        getSets();

        setUnion();
        setProd();
        setStar();

        displayInfo();
    }



    /*
    Find the Union between set A and B
     */
    public static void setUnion() {

        // handle if the sets are empty
        if ((setA.size() == 1 && setA.get(0).equals("")) &&
                (setB.size() == 1 && setB.get(0).equals(""))) {
            union.add('\u2205' + ""); // empty set
            return;
        } else if (setA.size() == 1 && setA.get(0).equals("")) {
            union.addAll(setB); // A empty, B isn't
            return;
        } else if (setB.size() == 1 && setB.get(0).equals("")) {
            union.addAll(setA); // B empty, A isn't
            return;
        }

        // Add set A then check for set B
        union.addAll(setA);

        // if the element in B is not in setA, add it to the union set
        for (String element: setB) {
            if (!union.contains(element)) {
                union.add(element);
            }
        }
    }



    /*
    Find the Product of set A and B
     */
    public static void setProd() {

        // handle if the sets are empty
        if((setA.size() == 1 && setA.get(0).equals("")) ||
                (setB.size() == 1 && setB.get(0).equals(""))) {
            prod.add('\u2205' + ""); // empty set
        } else {
            // A prod B
            for (int i = 0; i < setA.size(); i++) {
                for (int j = 0; j < setB.size(); j++) {
                    prod.add(setA.get(i) + setB.get(j));
                }
            }
        }
    }



    /*
    Find A^k
    */
    public static void setStar() {

        // if k<=0 or setA is empty, make the message show empty set and return
        if (k <= 0 || (setA.size() == 1 && setA.get(0).equals(""))) {
           star.add('\u2205' + "");
           return;
        }

        // start with A^1 before creating combinations
        star.addAll(setA);


        // start at i=1 because A^1 is the set itself which is already accounted for
        // when creating star List
        for (int i = 1; i < k; i++) {
            // store combinations
            List<String> combination = new ArrayList<>();
            for (String result: star) {
                for (String element: setA) {
                    String current = result + element;
                    combination.add(current);
                }
            }
            // delete previous results and add new results
            star.clear();
            star.addAll(combination);
        }
    }



    /*
    Get set A and B from the file
     */
    public static void getSets() throws IOException {
        String a, b;

        Scanner read = new Scanner(file);

        // get set A from file
        if (read.hasNextLine()) {
            a = read.nextLine();
        } else {
            a = "";
        }

        // get set B from file
        if (read.hasNextLine()) {
            b = read.nextLine();
        } else {
            b = "";
        }

        // remove the curly braces then get elements
        a = a.replaceAll("[{}]", "");
        String[] setA_unorganized = a.split(",");

        b = b.replaceAll("[{}]", "");
        String[] setB_unorganized = b.split(",");

        
        for (String element: setA_unorganized) {
            // if element is its first occurence (no duplicates) add it to the set
            if (!setA.contains(element)) {
                setA.add(element);
            }
        }

        for (String element: setB_unorganized) {
            // if element is its first occurence (no duplicates) add it to the set
            if (!setB.contains(element)) {
                setB.add(element);
            }
        }
    }



    /*
    Ask the user for the value of K.
     */
    public static void askUserK() {
        boolean retry;

        do {
            String message = "Enter your value of k (A^k): ";
            String input = JOptionPane.showInputDialog(message);

            try {
                k = Integer.parseInt(input);
                retry = false;

            } catch (NumberFormatException e) {
                // if input doesn't match integer type, ask to retry
                message = "Wrong input type, enter an integer value. Do you want to retry?";
                int choice = JOptionPane.showConfirmDialog(null, message);
                retry = choice == JOptionPane.YES_OPTION ? true : false;

                if (retry == false) {
                    System.exit(0);
                }
            }
        } while (retry == true);

    }


    /*
    Get input file from user
     */
    public static void getInputFile() throws IOException {
        int status;
        boolean retry;

        do {
            // use JFileChooser to get file from user
            JFileChooser chooser = new JFileChooser();
            status = chooser.showOpenDialog(null);

            if (status == JFileChooser.APPROVE_OPTION) {
                // if a file is selected and confirmed, set retry to false
                file = chooser.getSelectedFile();
                retry = false;

            } else {
                // if a file wasn't selected or confirmed ask user if they want to retry
                String message = "Approve option not pressed, do you want to retry?";
                int choice = JOptionPane.showConfirmDialog(null, message);
                retry = choice == JOptionPane.YES_OPTION ? true : false;

                // exit program if user doesn't want to retry
                if (retry == false) {
                    System.exit(0);
                }
                continue;
            }

            if (!file.exists() || !file.isFile()) {
                // if the file doesn't exist or the format is wrong, ask user to retry
                String message = "Something went wrong with the file, do you want to retry?";
                int choice = JOptionPane.showConfirmDialog(null, message);
                retry = choice == JOptionPane.YES_OPTION ? true : false;

                // exit program if user doesn't want to retry
                if (retry == false) {
                    System.exit(0);
                }
            }

        } while (retry == true);
    }



    /*
    Display the operations done with the sets
     */
    public static void displayInfo() {
        String unionDisplayMessage =  "Set A " + '\u222A' + " Set B: " + getSetInfo(union);
        String prodDisplayMessage = "Set A " + '\u25E6' + " Set B: " + getSetInfo(prod);
        String starDisplayMessage = "Set A^" + k + ": " + getSetInfo(star);

        String A = getSetInfo(setA);
        String B = getSetInfo(setB);

        // concat all messages into one block for printing
        String displayMessage = "Set A: " + A + "\n\n"
                + "Set B:" + B + "\n\n"
                + unionDisplayMessage + "\n\n"
                + prodDisplayMessage + "\n\n"
                + starDisplayMessage + "\n\n";

        // set up the text area to display message
        JTextArea ta = new JTextArea(displayMessage);
        ta.setLineWrap(true);

        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(null, sp);
    }



    /*
    Format set into a neat message as a string
     */
    public static String getSetInfo(List<String> set) {

        String message = "{";

        for (int i = 0; i < set.size() - 1; i++) {
            message += set.get(i) + ",";
        }

        message += set.get(set.size() - 1) + "}";

        return message;
    }

}