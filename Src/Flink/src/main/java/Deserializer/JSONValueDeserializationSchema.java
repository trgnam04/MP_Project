package Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.io.Serializable;

public class JSONValueDeserializationSchema<T> implements DeserializationSchema<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private static final ThreadLocal<ObjectMapper> objectMapperThreadLocal = ThreadLocal.withInitial(() -> {
        ObjectMapper om = new ObjectMapper();
        // Đăng ký module hỗ trợ Java 8 date/time (JSR-310)
        om.registerModule(new JavaTimeModule());
        // Nếu cần, bạn có thể tắt WRITE_DATES_AS_TIMESTAMPS để hiển thị dưới dạng ISO-8601
        om.findAndRegisterModules();
        return om;
    });

    private final Class<T> targetType;

    public JSONValueDeserializationSchema(Class<T> targetType) {
        if (!Serializable.class.isAssignableFrom(targetType)) {
            throw new IllegalArgumentException(targetType.getName() + " must be Serializable!");
        }
        this.targetType = targetType;
    }

    @Override
    public T deserialize(byte[] bytes) throws IOException {
        return objectMapperThreadLocal.get().readValue(bytes, targetType);
    }

    @Override
    public boolean isEndOfStream(T event) {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(targetType);
    }
}
