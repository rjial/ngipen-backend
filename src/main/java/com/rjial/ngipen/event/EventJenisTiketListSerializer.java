package com.rjial.ngipen.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.rjial.ngipen.tiket.JenisTiket;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventJenisTiketListSerializer extends StdSerializer<List<JenisTiket>> {

    protected EventJenisTiketListSerializer(Class<List<JenisTiket>> t) {
        super(t);
    }

    public EventJenisTiketListSerializer() {
        this(null);
    }

    @Override
    public void serialize(List<JenisTiket> jenisTikets, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jenisTikets.sort((o1, o2) -> {
            return (int) (o1.getHarga() - o2.getHarga());
        });
        jsonGenerator.writeObject(jenisTikets);
    }
}
