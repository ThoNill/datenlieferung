package tho.nill.datenlieferung.simpleAttributes;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class IKAdapter implements
        AttributeConverter<IK, Integer> {

    @Override
    public Integer convertToDatabaseColumn(IK attribute) {
        if (attribute == null) {
            return null;
        }
        return new Integer(attribute.getIK());
    }

    @Override
    public IK convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return new IK(dbData.intValue());
    }

 

}