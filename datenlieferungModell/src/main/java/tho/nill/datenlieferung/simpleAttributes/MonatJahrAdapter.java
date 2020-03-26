package tho.nill.datenlieferung.simpleAttributes;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MonatJahrAdapter implements
        AttributeConverter<MonatJahr, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MonatJahr attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getMJ();
    }

    @Override
    public MonatJahr convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return new MonatJahr(dbData.intValue());
    }

 

}