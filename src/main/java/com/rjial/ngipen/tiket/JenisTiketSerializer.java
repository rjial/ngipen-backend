package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class JenisTiketSerializer extends StdSerializer<JenisTiket> {

    public JenisTiketSerializer() {
        this(null);
    }
    public JenisTiketSerializer(Class<JenisTiket> t) {
        super(t);
    }

    @Override
    public void serialize(JenisTiket value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getNama());
    }
}
