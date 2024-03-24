package com.rjial.ngipen.payment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PaymentStatusSerializer extends StdSerializer<PaymentStatus> {

    public PaymentStatusSerializer() {
        this(null);
    }
    public PaymentStatusSerializer(Class<PaymentStatus> t) {
        super(t);
    }

    @Override
    public void serialize(PaymentStatus value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        switch (value) {
            case ACCEPTED -> {
                gen.writeString("Accepted");
            }
            case NOT_ACCEPTED -> {
                gen.writeString("Not Accepted");
            }
            case REFUND -> {
                gen.writeString("Refunded");
            }
            case WAITING_FOR_VERIFY -> {
                gen.writeString("Waiting for verified");
            }
        }
    }
}
