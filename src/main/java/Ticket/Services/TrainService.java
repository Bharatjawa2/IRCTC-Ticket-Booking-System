package Ticket.Services;

import Ticket.entities.Train;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "src/main/java/Ticket/localDB/trains.json";

    public TrainService() throws IOException {
        loadTrainListFromFile();
    }

    private void loadTrainListFromFile() throws IOException {
        File trains = new File(TRAIN_DB_PATH);
        System.out.println("Loading trains from: " + trains.getAbsolutePath());
        if (trains.exists() && trains.length() > 0) {
            System.out.println("File exists. Loading data...");
            trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
        } else {
            System.out.println("File does not exist or is empty. Initializing empty train list.");
            trainList = new ArrayList<>();
        }
    }

    public List<Train> searchTrains(String source, String destination) {
        if (source == null || destination == null || source.isEmpty() || destination.isEmpty()) {
            throw new IllegalArgumentException("Source and destination cannot be null or empty.");
        }
        return trainList.stream()
                .filter(train -> validTrain(train, source, destination))
                .collect(Collectors.toList());
    }

    public void addTrain(Train newTrain) {
        if (newTrain == null) {
            throw new IllegalArgumentException("Train cannot be null.");
        }

        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            updateTrain(newTrain);
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train updatedTrain) {
        if (updatedTrain == null) {
            throw new IllegalArgumentException("Updated train cannot be null.");
        }

        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            throw new IllegalArgumentException("Train with ID " + updatedTrain.getTrainId() + " not found.");
        }
    }

    private boolean validTrain(Train train, String source, String destination) {
        return train.getStations().contains(source) && train.getStations().contains(destination);
    }

    private void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save train list to file.", e);
        }
    }
}