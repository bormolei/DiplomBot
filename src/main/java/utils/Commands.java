package utils;

import java.util.Optional;
import java.util.stream.Stream;

public enum Commands {
    WEATHER("Погода"),
    CALENDAR("Календарь"),
    Main("На главную"),
    MYFILES("Мои файлы"),
    TICKET("Транспортные билеты");

    private final String  value;
    Commands(String value) {
        this.value = value;
    }

    public static Optional<Commands> fromString(String s){
        return Stream.of(Commands.values()).filter(x-> x.value.equals(s)).findFirst();
    }
}
