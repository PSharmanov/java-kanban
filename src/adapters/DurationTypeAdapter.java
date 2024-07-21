package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(duration.getSeconds());
        } else {
            jsonWriter.value(0);
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.ofSeconds(jsonReader.nextInt());
    }
}
