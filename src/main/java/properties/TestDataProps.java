package properties;

import org.aeonbits.owner.ConfigFactory;

/**
 * инициализация переменной для вызова методов с помощью пропертей
 */
public class TestDataProps {
    public static PropsUrl propsUrl = ConfigFactory.create(PropsUrl.class);
}
