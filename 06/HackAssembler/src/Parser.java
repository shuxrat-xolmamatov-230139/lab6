import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Parser {
    enum CommandType {
        A_COMMAND,
        C_COMMAND,
        L_COMMAND
    }

    private List<String> commands;
    private int pointer;
    private int LCommandCounter;
    private String thisCommand;

    public Parser() {
    }

    public Parser(String filePath) {
        LCommandCounter = 0;
        initPointer();
        commands = new ArrayList();
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            line = in.readLine();
            while (line != null) {
                // remove whitespace
                line = line.replaceAll("\\s", "").trim();
                // remove empty lines and comments
                if (line.equals("") || line.startsWith("//")) {
                    line = in.readLine();
                    continue;
                }
                // remove inline comments
                String[] splitRes = line.split("//");
                // add the command to the list
                commands.add(splitRes[0]);
                // read the next line
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    //    System.out.println(commands);
    }

    public Boolean hasMoreCommands() {
        return pointer < commands.size() - 1;
    }

    public void addLCommand() {
        LCommandCounter++;
    }

    public int getLCommandCounter() {
        return LCommandCounter;
    }

    public void advance() {
        pointer++;
        this.thisCommand = commands.get(pointer);
    }

    // return the type of the current command
    public CommandType commandType() {
        if (thisCommand.startsWith("@")) {
            return CommandType.A_COMMAND;
        } else if (thisCommand.startsWith("(")) {
            return CommandType.L_COMMAND;
        } else {
            return CommandType.C_COMMAND;
        }
    }

    // extract the symbol or binary of the current A or L command
    public String symbol() {
        try {
            if (commandType() == CommandType.A_COMMAND) {
                return thisCommand.substring(1);
            } else if (commandType() == CommandType.L_COMMAND) {
                return thisCommand.substring(1, thisCommand.length() - 1);
            } else {
                throw new RuntimeException("line" + pointer + " is not A_COMMAND or L_COMMAND");
            }
        } catch (RuntimeException r) {
            System.err.println(r.getMessage());
            return null;
        }
    }

    public static boolean isVar(String str) {
        Pattern pattern = Pattern.compile("^[a-z][a-z0-9_$.]*$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str).matches();
    }

    public String dest() {
        try {
            if (commandType() != CommandType.C_COMMAND) {
                throw new RuntimeException("line" + pointer + " is not C_COMMAND");
            }
            if (thisCommand.contains("=")) {
                return thisCommand.split("=")[0];
            } else {
                return "null";
            }
        } catch (RuntimeException r) {
            System.err.println(r.getMessage());
            return null;
        }
    }

    public String comp() {
        try {
            if (commandType() != CommandType.C_COMMAND) {
                throw new RuntimeException("line" + pointer + " is not C_COMMAND");
            }

            // remove jump part
            String noJump = this.thisCommand.split(";")[0];

            // remove dest part
            if (thisCommand.contains("=")) {
                return noJump.split("=")[1];
            } else {
                return noJump;
            }
        } catch (RuntimeException r) {
            System.err.println(r.getMessage());
            return null;
        }
    }

    public String jump() {
        try {
            if (commandType() != CommandType.C_COMMAND) {
                throw new RuntimeException("line" + pointer + " is not C_COMMAND");
            }
            if (thisCommand.contains(";")) {
                return thisCommand.split(";")[1];
            } else {
                return "null";
            }
        } catch (RuntimeException r) {
            System.err.println(r.getMessage());
            return null;
        }
    }


    public void initPointer() {
        pointer = -1;
    }

    public int getPointer() {
        return pointer;
    }
}
