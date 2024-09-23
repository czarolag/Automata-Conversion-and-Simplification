import javax.swing.*;
import java.io.*;
import java.util.*;

public class Main {
    private static File file;
    private static HashMap<String, Set<String>> CFG_map = new HashMap<>();
    private static String originalCFG = "";
    private static String modifiedCFG = "";

    public static void main(String[] args) throws IOException {

        // methods used for removing epsilon rules
        getInputFile();
        getCFG();
        removeEpsilonOnlyRules();
        removeEpsilon();
        // test();

        // methods used for removing useless rules
        removeUseless();
        printInfo();
    }




    /*
    *
    * Methods below were used for removing useless rules
    *
     */

    /*
    Method will handle most of the steps used for removing useless rules
     */
    public static void removeUseless() {
        Set<String> V = new HashSet<>(findSigma());

        boolean hasChanged = true;

        while (hasChanged) {
            hasChanged = false;

            for (String key : CFG_map.keySet()) {
                Set<String> terminals = CFG_map.get(key);

                for (String terminal : terminals) {

                    // create a set to track chars in terminal
                    Set<String> charSet = new HashSet<>();

                    // Add each char in the terminal to charSet
                    for (char ch : terminal.toCharArray()) {
                        charSet.add(String.valueOf(ch));
                    }

                    // If all chars in the charSet are in V, add the key to V
                    if (V.containsAll(charSet)) {
                        if (!V.contains(key)) {
                            V.add(key);

                            hasChanged = true;

                            // move to next key
                            break;
                        }
                    }
                }
            }
        }

        Set<String> keysToRemove = new HashSet<>();

        for (String key : CFG_map.keySet()) {

            // if key is not in V remove it,
            // else check each terminal and remove the ones that don't follow the rule
            if (!V.contains(key)) {
                keysToRemove.add(key);
            } else {

                Set<String> terminals = CFG_map.get(key);

                Set<String> terminalsToRemove = new HashSet<>();

                // Add each char in the terminal to charSet
                for (String terminal: terminals) {

                    // create a set to track chars in terminal
                    Set<String> charSet = new HashSet<>();

                    for (char ch: terminal.toCharArray()) {
                        charSet.add(String.valueOf(ch));
                    }

                    // remove rules whose right hand side symbols are not in V
                    if (!V.containsAll(charSet)) {
                        terminalsToRemove.add(terminal);
                    }
                }

                // remove terminals who are not present in V
                terminals.removeAll(terminalsToRemove);

                // if all rules were removed, delete the key
                if (terminals.isEmpty()) {
                    keysToRemove.add(key);
                }
            }
        }

        CFG_map.keySet().removeAll(keysToRemove);
        keysToRemove.clear();

        for (String key: CFG_map.keySet()) {
            if (key.equals("S")) {
                continue;
            }

            if (!isAccessible("S", key)) {
                System.out.println(key);
                keysToRemove.add(key);
            }
        }
        CFG_map.keySet().removeAll(keysToRemove);

        modifiedCFG = getMessage(CFG_map);

        // test();
    }



    /*
    Method will check if the key is accessible from S using BFS
     */
    public static boolean isAccessible(String startSymbol, String keyToCheck) {

        Set<String> visited = new HashSet<>();

        // Queue for BFS
        Queue<String> q = new LinkedList<>();
        q.add(startSymbol);

        while (!q.isEmpty()) {
            String curr = q.poll();

            // If already visited, skip it
            if (visited.contains(curr)) {
                continue;
            }

            // Mark the current as visited
            visited.add(curr);

            // Get the terminals
            Set<String> terminals = CFG_map.get(curr);

            // If there are no terminals
            if (terminals == null) {
                continue;
            }

            // Iterate over each terminal
            for (String terminal : terminals) {
                // Check if the keyToCheck is directly in the production
                if (terminal.contains(keyToCheck)) {
                    return true;
                }

                // Add all keys found to further explore
                for (char ch : terminal.toCharArray()) {

                    if (CFG_map.containsKey(String.valueOf(ch))) {
                        q.add(String.valueOf(ch));
                    }
                }
            }
        }

        return false;
    }



    /*
    Method finds Sigma in the language
     */
    public static Set<String> findSigma() {
        // V will contain sigma, not including epsilon since those were removed
        Set<String> V = new HashSet<>();

        // go through the CFG, if a char is not a Variable, it must be in Sigma
        for (String key: CFG_map.keySet()) {
            Set<String> terminals = CFG_map.get(key);


            for (String terminal: terminals) {
                for (char ch: terminal.toCharArray()) {
                    if (!CFG_map.containsKey(String.valueOf(ch))) {
                        V.add(String.valueOf(ch));
                    }
                }

            }
        }
        return V;
    }



    /*
    Method will print out information in a neat manner
     */
    public static void printInfo() {
        String message = "Original CFG: \n" + originalCFG + "\n\n";
        message += "Modified CFG: \n" + modifiedCFG;

        JOptionPane.showMessageDialog(null, message);
    }



    /*
    Method will generate the message to display
     */
    public static String getMessage(HashMap<String, Set<String>> map) {
        String message = "";

        for (String key: map.keySet()) {
            message += key + "-";

            Set<String> terminals = map.get(key);
            for (String terminal: terminals) {
                message += terminal + "|";
            }

            message += "\n";
        }

        return message;
    }




    /*
    *
    *  Methods below were used for removing epsilon rules
    *
     */

    public static void test() {
         for (String key: CFG_map.keySet()) {
             System.out.println(key);
             System.out.println(CFG_map.get(key));
         }
    }


    /*
    Get all combinations for new rules
     */
    public static Set<String> getCombinations(String input, Set<String> V) {
        Set<String> combinations = new HashSet<>();
        helper(input, "", 0, combinations, V);

        // fixed empty string being present in set
        combinations.remove("");
        return combinations;
    }



    /*
    helper method to generate combinations
     */
    public static void helper(String input, String curr, int index, Set<String> combinations, Set<String> V) {

        // base case
        if (index >= input.length()) {
            combinations.add(curr);
            return;
        }

        char ch = input.charAt(index);

        // If current character is a variable in the language, generate combinations
        if (V.contains(String.valueOf(ch))) {
            helper(input, curr, index+1, combinations, V);
            helper(input, curr+ch, index+1, combinations, V);

        } else {
            helper(input, curr + ch, index + 1, combinations, V);
        }
    }



    /*
    Remove epsilon rules
     */
    public static void removeEpsilon() {
        Set<String> V = new HashSet<>();
        boolean hasChanged = true;

        while (hasChanged) {
            hasChanged = false;

            for (String key: CFG_map.keySet()) {
                Set<String> terminals = CFG_map.get(key);

                if (terminals.contains("0") || terminals.containsAll(V)) {
                    if (!V.contains(key)) {
                        V.add(key);
                        hasChanged = true;
                    }
                }
            }
        }

        for (String key: V) {
            if (CFG_map.get(key).contains("0")) {

                // if epsilon is present, just remove the epsilon value
                CFG_map.get(key).remove("0");

            }
        }

        for (String key: CFG_map.keySet()) {
            Set<String> terminals = new HashSet<>(CFG_map.get(key));

            for (String terminal: terminals) {
                CFG_map.get(key).addAll(getCombinations(terminal, V));
            }
        }
    }



    /*
    Remove epsilon rules in the form of A -> 0
     */
    public static void removeEpsilonOnlyRules() {
        Set<String> toRemove = new HashSet<>();

        for (String key: CFG_map.keySet()) {
            Set<String> terminals = CFG_map.get(key);

            // if only epsilon is present, remove the rule
            if (terminals.contains("0") && terminals.size() == 1) {
                toRemove.add(key);
            }
        }

        // remove keys
        for (String key: toRemove) {
            CFG_map.remove(key);
        }

        // remove the elements from the right hand side as well
        for (String key: CFG_map.keySet()) {

            Set<String> terminals = CFG_map.get(key);
            Set<String> newTerminals = new HashSet<>();

            for (String terminal: terminals) {
                String newTerminal = terminal;

                for (String r: toRemove) {
                    newTerminal = newTerminal.replace(r, "");
                }

                newTerminals.add(newTerminal);
            }

            CFG_map.put(key, newTerminals);
        }
    }


    /*
    Get CFG from File
     */
    public static void getCFG() throws IOException {
        Scanner read = new Scanner(file);

        while (read.hasNextLine()) {

            String curr = read.nextLine();
            String[] CFG = curr.split("-");

            String variable = CFG[0];
            Set<String> terminals = new HashSet<>(Arrays.asList(CFG[1].split("\\|")));

            CFG_map.put(variable, terminals);
        }

        originalCFG = getMessage(CFG_map);
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
}