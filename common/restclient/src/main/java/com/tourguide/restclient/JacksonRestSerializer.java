package com.tourguide.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JacksonRestSerializer implements RestSerializer {
    private final @NonNull ObjectMapper objectMapper;

    @Override
    public void serialize(Writer dst, Object value) throws IOException {
        objectMapper.writeValue(dst, value);
    }

    @Override
    public <T> T deserialize(Reader src, Class<T> type) throws IOException {
        return objectMapper.readValue(src, type);
    }

    @Override
    public <T> List<T> deserializeList(Reader src, Class<T> type) throws IOException {
        return objectMapper.readerForListOf(type).readValue(src);
    }
}
