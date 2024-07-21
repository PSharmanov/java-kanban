package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDataTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Override
    public void write(final JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        Optional<LocalDateTime> value = Optional.ofNullable(localDateTime);
        if (value.isPresent()) {
            jsonWriter.value(localDateTime.format(dtf));
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        try {
            String value = jsonReader.nextString();
            if (value.equals("null")) {
                return null;
            }
            return LocalDateTime.parse(value, dtf);
        } catch (IllegalStateException exception) {
            jsonReader.nextNull();
            return null;
        }

    }

}

