package properties;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "file:src/main/resources/url.properties"
})

public interface PropsUrl extends Config {

    /**
     * метод вызова заданного url из файла пропертей
     */
    @Key("base.url.sberBank")
    String baseUrlSberBank();

    @Key("base.url.vtbBank")
    String baseUrlVtbBank();

    @Key("base.url.openBank")
    String baseUrlOpenBank();

    @Key("base.url.alfaBank")
    String baseUrlAlfaBank();
}
