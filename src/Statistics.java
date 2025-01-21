import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getDataSize();

        LocalDateTime entryTime = logEntry.getDateTime();
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return totalTraffic;
        }
        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        return hours > 0 ? (double) totalTraffic / hours : totalTraffic;
    }
}