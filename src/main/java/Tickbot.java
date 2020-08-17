import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiFunction;

public class Tickbot {
    private static Scanner inputScanner = new Scanner(System.in);
    private static List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        printMessage("Hello, this is tickbot! How can I help you?");
        boolean running = true;
        while (running) {
            System.out.print("==> ");
            if (!inputScanner.hasNextLine()) {
                break; // end of file
            }
            String command = inputScanner.nextLine();
            running = processCommand(command);
        }
    }

    private static boolean processCommand(String command) {
        if (command.isBlank()) {
            return true; // empty line, continue inputing
        }
        String[] args = command.split("\\s+");
        switch (args[0]) {
            case "bye": {
                printMessage("See you next time!");
                return false; // end of input
            }
            case "done": {
                try {
                    int index = Integer.parseInt(args[1]) - 1;
                    Task task = tasks.get(index);
                    if (task.isCompleted()) {
                        printMessage("Task already completed.");
                    } else {
                        task.setCompleted(true);
                        printMessage("Task completed: " + task);
                    }
                } catch (NumberFormatException err) {
                    printMessage("Invalid Syntax.");
                    printMessage("Usage: done <task_index>");
                } catch (IndexOutOfBoundsException err) {
                    printMessage("Sorry, No such task found.");
                }
                break;
            }
            case "list": {
                printMessage("Task list:");
                for (int i = 0; i < tasks.size(); i++) {
                    String message = String.format("%d. %s", i + 1, tasks.get(i));
                    printMessage(message);
                }
                break;
            }
            case "todo": {
                processAddTask(args, "TO-DO", (content, _time) -> new Todo(content), null);
                break;
            }
            case "deadline": {
                processAddTask(args, "Deadline", Deadline::new, "/by");
                break;
            }
            case "event": {
                processAddTask(args, "Event", Event::new, "/at");
                break;
            }
            default: {
                printMessage("Unknown command " + args[0] + ".");
            }
        }
        return true; // continue inputing
    }

    private static void processAddTask(
        String[] args,
        String taskName,
        BiFunction<String, String, ? extends Task> initializer,
        String timeMarker
    ) {
        String lowerTaskName = taskName.toLowerCase();
        if (args.length < 2) {
            printMessage("Please input the content of the " + lowerTaskName + ".");
            printMessage(String.format("Usage: %s <content> %s <time>",
                lowerTaskName, timeMarker));
            return;
        }
        String content = args[1];
        Optional<String> time = Optional.empty();
        try {
            for (int i = 2; i < args.length; i++) {
                if (time.isPresent()) {
                    time = Optional.of(time.get() + " " + args[i]);
                } else if (Objects.equals(args[i], timeMarker)) {
                    time = Optional.of(args[++i]);
                } else {
                    content += " " + args[i];
                }
            }
            Task task = initializer.apply(content,
                timeMarker == null ? null : time.get());
            tasks.add(task);
            printMessage(taskName + " added: " + task);
        } catch (IndexOutOfBoundsException err) {
            printMessage("Please input valid time after " + timeMarker + ".");
            printMessage(String.format("Usage: %s <content> %s <time>",
                lowerTaskName, timeMarker));
        } catch (NoSuchElementException err) {
            printMessage("Missing time for the " + lowerTaskName + ".");
            printMessage(String.format("Usage: %s <content> %s <time>",
                lowerTaskName, timeMarker));
        }
    }

    private static void printMessage(String message) {
        System.out.println("  " + message);
    }
}
