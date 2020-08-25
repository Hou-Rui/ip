package storage;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import exception.CorruptedDataException;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import task.*;

public class DataStorage {
    private static final String DATA_STORAGE_FILE_NAME = "tasks.csv";

    public List<Task> read() {
        List<Task> tasks = new ArrayList<>();
        try {
            File dataFile = new File(getDataStoragePath(), DATA_STORAGE_FILE_NAME);
            dataFile.getParentFile().mkdirs(); // create the data directory if not exists
            dataFile.createNewFile(); // create the file if not exists
            FileInputStream inputStream = new FileInputStream(dataFile);
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
            for (String[] parts : reader) {
                Task task;
                boolean completed = Objects.equals(parts[1], "1");
                switch (parts[0]) {
                    case "T": {
                        task = new Todo(completed, parts[2]);
                        break;
                    }
                    case "E": {
                        LocalDate time = LocalDate.parse(parts[3]);
                        task = new Event(completed, parts[2], time);
                        break;
                    }
                    case "D": {
                        LocalDate time = LocalDate.parse(parts[3]);
                        task = new Deadline(completed, parts[2], time);
                        break;
                    }
                    default: {
                        throw new CorruptedDataException();
                    }
                }
                tasks.add(task);
            }
        } catch (SecurityException | IOException err) {
            System.err.println("Warning: unable to read data storage. "
                + "Your task list is not loaded.");
        } catch (DateTimeException | CorruptedDataException | IndexOutOfBoundsException err) {
            System.err.println("Warning: Corrupted data storage. "
                + "Your task list may not be fully loaded.");
        }
        return tasks;
    }
    
    public void update(List<Task> tasks) {
        try {
            File dataFile = new File(getDataStoragePath(), DATA_STORAGE_FILE_NAME);
            FileOutputStream outputStream = new FileOutputStream(dataFile);
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));
            for (Task task : tasks) {
                String[] line = new String[] {
                    task.getTaskType(),
                    task.isCompleted() ? "1" : "0",
                    task.getContent(),
                    Objects.toString(task.getTime()) // could be null
                };
                writer.writeNext(line);
            }
            writer.close();
        } catch (SecurityException | IOException err) {
            System.err.println("Warning: unable to write to data storage. "
                + "Your task list is not saved.");
        }
    }

    private static String getDataStoragePath() {
        AppDirs appDirs = AppDirsFactory.getInstance();
        return appDirs.getUserDataDir("tickbot", null, "HouRui");
    }
}