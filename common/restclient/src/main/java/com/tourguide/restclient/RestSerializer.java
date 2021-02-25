package com.tourguide.restclient;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public interface RestSerializer {
    void serialize(Writer dst, Object value) throws IOException;

    <T> T deserialize(Reader src, Class<T> type) throws IOException;

    <T> List<T> deserializeList(Reader src, Class<T> type) throws IOException;
}
