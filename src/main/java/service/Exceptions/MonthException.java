package service.Exceptions;

public class MonthException extends Exception {
    private int monthNumber;

    public int getMonthNumber() {
        return monthNumber;
    }

    public MonthException(String message, int monthNumber){
        super(message);
        this.monthNumber = monthNumber;
    }
}
