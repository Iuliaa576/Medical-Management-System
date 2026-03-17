package repository.jsonFile;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import domain.Appointment;
import repository.FileRepository;
import repository.RepositoryException;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AppointmentRepositoryJSONFile extends FileRepository<String, Appointment> {

    private final Gson gson;
    private final Type mapType = new TypeToken<HashMap<String, Appointment>>() {}.getType();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

    public AppointmentRepositoryJSONFile(String FileName) throws RepositoryException {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, ctx) -> new JsonPrimitive(FORMATTER.format(src)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, ctx) -> LocalDateTime.parse(json.getAsString(), FORMATTER))
                .create();

        this.FileName = FileName;
        readFromFile();
    }

    @Override
    protected void readFromFile() throws RepositoryException {
        File file = new File(FileName);
        if (!file.exists() || file.length() == 0) {
            this.elements = new HashMap<>();
            return;
        }

        try (Reader reader = new FileReader(FileName)) {
            HashMap<String, Appointment> data = gson.fromJson(reader, mapType);
            if (data != null) {
                this.elements = data;
            } else {
                this.elements = new HashMap<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            throw new RepositoryException("Error reading JSON file: " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (Writer writer = new FileWriter(FileName)) {
            gson.toJson(this.elements, mapType, writer);
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException("Error writing to JSON file: " + e.getMessage());
        }
    }
}
