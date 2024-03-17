package com.rjial.ngipen.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class EventSerializer extends StdSerializer<Event> {
    public EventSerializer() {
        this(null);
    }
    public EventSerializer(Class<Event> t) {
        super(t);
    }

    @Override
    public void serialize(Event value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getName());
    }
}
