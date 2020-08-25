package tickbot.ui.text;

public class Parser {
    private Runner runner = new Runner();

    public boolean executeCommand(String command) {
        if (command.isBlank()) {
            return true; // empty line, continue inputing
        }
        String[] args = command.split("\\s+");
        switch (args[0]) {
            case "bye": {
                Output.printMessage("See you next time!");
                return false; // end of input
            }
            case "help": {
                runner.help(args);
                break;
            }
            case "done": {
                runner.done(args);
                break;
            }
            case "delete": {
                runner.delete(args);
                break;
            }
            case "list": {
                runner.list(args);
                break;
            }
            case "todo": {
                runner.todo(args);
                break;
            }
            case "deadline": {
                runner.deadline(args);
                break;
            }
            case "event": {
                runner.event(args);
                break;
            }
            default: {
                Output.printMessage("Unknown command " + args[0] + ".");
            }
        }
        return true; // continue inputing
    }
}