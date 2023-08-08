package by.nortin.restjwtproject.utils;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@UtilityClass
public class ObjectUtils {

    public static <T> String[] getIgnoredProperties(T t, String... customProperties) {
        return concatWithArrayCopy(getNullProperties(t), customProperties);
    }

    private String[] getNullProperties(Object source) {
        final BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        return Stream.of(beanWrapper.getPropertyDescriptors())
                     .map(FeatureDescriptor::getName)
                     .filter(propertyName -> beanWrapper.getPropertyValue(propertyName) == null)
                     .toArray(String[]::new);
    }

    private <T> T[] concatWithArrayCopy(T[] firstArray, T[] secondArray) {
        T[] result = Arrays.copyOf(firstArray, firstArray.length + secondArray.length);
        System.arraycopy(secondArray, 0, result, firstArray.length, secondArray.length);
        return result;
    }
}
