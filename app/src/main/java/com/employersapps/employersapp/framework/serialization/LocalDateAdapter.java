package com.employersapps.employersapp.framework.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        String text = value.format(
                DateTimeFormatter.ISO_DATE
        );
        out.value(text);
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String text = in.nextString();
        return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
    }
}
