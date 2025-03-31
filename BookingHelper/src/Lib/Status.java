package Lib;

public enum Status {
    PENDING(0),
    CONFIRMED(1),
    CHECKED_IN(2),
    CHECKED_OUT(3),
    CANCELLED(4);

    private final int value;

    Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
