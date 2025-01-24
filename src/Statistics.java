import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private final Set<String> existingPages;
    private final Map<String, Integer> osFrequency; // Для подсчета частоты операционных систем

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.existingPages = new HashSet<>();
        this.osFrequency = new HashMap<>();
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

        if (logEntry.getResponseCode() == 200) {
            existingPages.add(logEntry.getRequestPath());
        }

        String userAgent = logEntry.getUserAgent().getFullUserAgent();
        String os = extractOsFromUserAgent(userAgent);

        if (osFrequency.containsKey(os)) {
            osFrequency.put(os, osFrequency.get(os) + 1);
        } else {
            osFrequency.put(os, 1);
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

    public Set<String> getExistingPages() {
        return this.existingPages;
    }

    public Map<String, Integer> getOsFrequency() {
        return this.osFrequency;
    }
    private String extractOsFromUserAgent(String userAgent) {
        int startIndex = userAgent.indexOf('(') + 1;
        int endIndex = userAgent.indexOf(';', startIndex);
        if (startIndex > 0 && endIndex > startIndex) {
            return userAgent.substring(startIndex, endIndex).trim();
        }
        return "Unknown OS";
    }
}