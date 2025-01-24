import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private final Set<String> existingPages;
    private final Set<String> nonExistentPages;
    private final Map<String, Integer> osFrequency;
    private final Map<String, Integer> browserFrequency;
    private long normalUserVisits;
    private long errorRequestsCount;
    private final Set<String> uniqueUserIPs;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.existingPages = new HashSet<>();
        this.nonExistentPages = new HashSet<>();
        this.osFrequency = new HashMap<>();
        this.browserFrequency = new HashMap<>();
        this.normalUserVisits = 0;
        this.errorRequestsCount = 0;
        this.uniqueUserIPs = new HashSet<>();
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
        } else if (logEntry.getResponseCode() == 404) {
            nonExistentPages.add(logEntry.getRequestPath());
        }

        if (logEntry.getResponseCode() >= 400 && logEntry.getResponseCode() < 600) {
            errorRequestsCount++;

        }

        String userAgent = logEntry.getUserAgent().getFullUserAgent();
        String os = extractOsFromUserAgent(userAgent);

        if (osFrequency.containsKey(os)) {
            osFrequency.put(os, osFrequency.get(os) + 1);
        } else {
            osFrequency.put(os, 1);
        }
        String browser = extractBrowserFromUserAgent(userAgent);
        if (browserFrequency.containsKey(browser)) {
            browserFrequency.put(browser, browserFrequency.get(browser) + 1);
        } else {
            browserFrequency.put(browser, 1);
        }

        if (!userAgent.toLowerCase().contains("bot")) {
            normalUserVisits++;
            uniqueUserIPs.add(logEntry.getIpAddress());
        }
    }

    public double getAverageVisitsPerUser() {
        if (uniqueUserIPs.isEmpty()) {
            return 0;
        }
        return (double) normalUserVisits / uniqueUserIPs.size();
    }

    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0;
        }

        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        return hours > 0 ? (double) errorRequestsCount / hours : errorRequestsCount;
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
        return existingPages;
    }

    public Set<String> getNonExistentPages() {
        return nonExistentPages;
    }

    public Map<String, Integer> getOsFrequency() {
        return osFrequency;
    }

    public Map<String, Double> getBrowserStatistics() {
        int totalBrowsers = browserFrequency.values().stream().mapToInt(Integer::intValue).sum();

        return browserFrequency.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (double) entry.getValue() / totalBrowsers
                ));
    }

    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime) || normalUserVisits == 0) {
            return 0;
        }
        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        return hours > 0 ? (double) normalUserVisits / hours : normalUserVisits;
    }

    private String extractOsFromUserAgent(String userAgent) {
        int startIndex = userAgent.indexOf('(') + 1;
        int endIndex = userAgent.indexOf(';', startIndex);
        if (startIndex > 0 && endIndex > startIndex) {
            return userAgent.substring(startIndex, endIndex).trim();
        }
        return "Unknown OS";
    }

    private String extractBrowserFromUserAgent(String userAgent) {
        int startIndex = 0;
        int endIndex = userAgent.indexOf(' ');

        if (endIndex > startIndex) {
            return userAgent.substring(startIndex, endIndex).trim();
        }
        return "Unknown Browser";
    }
}