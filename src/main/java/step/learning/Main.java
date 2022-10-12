package step.learning;

import com.google.inject.Guice;

public class Main {
    public static void main(String[] args) {
        var configModule = new ConfigModule();
//        Guice.createInjector(configModule).getInstance(AppUser.class).run();
        Guice.createInjector(configModule).getInstance(App.class).run();
        configModule.close();
    }
}