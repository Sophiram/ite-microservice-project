package kh.edu.istad.platform.customer.persistence.convert;

import org.bson.BsonString;
import org.springframework.core.convert.converter.Converter;

public class EmailReadConverter implements Converter<BsonString, String> {
    @Override
    public String convert(BsonString source) {
        return source.getValue();
    }
}
