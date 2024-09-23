import javax.swing.*;
import java.io.*;
import java.util.*;

public class Main {
    private static File file;
    private static HashMap<String, List<String>> stateMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        getInputFile();
        getStates();
        displayResults();
    }



    /*
    Method used to find E(q) using the helper method
     */
    public static List<String> findE(String state) {
        List<String> results = new ArrayList<>();
        findEHelper(state, results);
        return results;
    }



    /*
    Helper method to find transitions of the current state
     */
    public static void findEHelper(String state, List<String> results) {

        if (state.equalsIgnoreCase("empty")) {
            return;
        }

        results.add(state);

        for (String next: stateMap.get(state)) {
            findEHelper(next, results);
        }
    }



    /*
    Method to get states and make a map with (state: {transitions}) pairs
     */
    public static void getStates() throws IOException {

        Scanner read = new Scanner(file);

        while (read.hasNextLine()) {
            String stateInfo = read.nextLine();
            stateInfo = stateInfo.replaceAll("[{} ]", "");

            String[] states = stateInfo.split(",");

            // state[0] is the first column so this is our state
            // The rest of the indices are the paths
            stateMap.put(states[0], Arrays.asList(Arrays.copyOfRange(states, 1, states.length)));
        }
    }



    /*
    Get input file from user
     */
    public static void getInputFile()  {
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
    Method used to display the results as E(q) = {}
     */
    public static void displayResults() {
        String message = "";

        for (String state: stateMap.keySet()) {
            message += "E(" + state + ") = {";

            List<String> transitions = findE(state);

            for(int i = 0; i < transitions.size() - 1; i++) {
                message += transitions.get(i) + ",";
            }

            message += transitions.get(transitions.size() - 1) + "}\n";
        }

        JOptionPane.showMessageDialog(null, message);
    }
}