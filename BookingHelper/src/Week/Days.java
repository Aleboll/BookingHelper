package Week;

public enum Days {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6);

    private final int value;

    Days(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static int GetDayOfTheWeek(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
    }
    public static String GetTodaysDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String GetTodaysWeekBeginingDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String[] GetWeekDates(String startDate) {
        String[] weekDates = new String[7];
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        String[] parts = startDate.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        calendar.set(year, month - 1, day);

        for (int i = 0; i < 7; i++) {
            weekDates[i] = String.format("%04d-%02d-%02d", year, month, day);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
            year = calendar.get(java.util.Calendar.YEAR);
            month = calendar.get(java.util.Calendar.MONTH) + 1;
            day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        }
        return weekDates;
    }
    public static String GetDatePlusSevenDays(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 7);
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH) + 1;
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
    public static int getDaysBetweenDates(String startDate, String endDate) {
        String[] startParts = startDate.split("-");
        String[] endParts = endDate.split("-");

        java.util.Calendar startCalendar = java.util.Calendar.getInstance();
        startCalendar.set(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]) - 1, Integer.parseInt(startParts[2]));

        java.util.Calendar endCalendar = java.util.Calendar.getInstance();
        endCalendar.set(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1]) - 1, Integer.parseInt(endParts[2]));

        long diffInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return (int) (diffInMillis / (1000 * 60 * 60 * 24));
    }
    public  static String GetDateMinusSevenDays(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -7);
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH) + 1;
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}
