package dansplugins.mailboxes.utils;

import java.util.ArrayList;

public class ArgumentParser {

    public String[] dropFirstArgument(String[] args) {
        String[] toReturn = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            toReturn[i - 1] = args[i];
        }

        return toReturn;
    }

    public ArrayList<String> getArgumentsInsideDoubleQuotes(String[] args) {
        ArrayList<String> toReturn = new ArrayList<>();

        String argumentString = String.join(" ", args);

        int index = 0;
        while (true) {
            int start = findIndexOfFirstDoubleQuote(index, argumentString);
            if (start == -1) {
                break;
            }
            int end = findIndexOfFirstDoubleQuote(start + 1, argumentString);

            if (end == -1) {
                break;
            }

            toReturn.add(argumentString.substring(start + 1, end));
            index = end + 1;
        }

        return toReturn;
    }

    private int findIndexOfFirstDoubleQuote(int startingIndex, String argumentString) {

        for (int i = startingIndex; i < argumentString.length(); i++) {

            if (argumentString.charAt(i) == '"') {
                return i;
            }

        }

        return -1;
    }

}