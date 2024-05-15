package com.ulla.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 通用BigDecimal转换Json
 * @author 1
 */
public class BigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.nonNull(value)) {
            gen.writeString(value.stripTrailingZeros().toPlainString());
        } else {//如果为null的话，就写null
            gen.writeNull();
        }
    }
}
