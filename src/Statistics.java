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
    private final Set<String> nonExistentPages;
    private final Map<String, Integer> osFrequency;
    private final Map<String, Integer> browserFrequency;


    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.existingPages = new HashSet<>();
        this.nonExistentPages = new HashSet<>();
        this.osFrequency = new HashMap<>();
        this.browserFrequency = new HashMap<>();
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

    public Set<String> getNonExistentPages() {
        return this.nonExistentPages;
    }

    public Map<String, Integer> getOsFrequency() {
        return this.osFrequency;
    }

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStatistics = new HashMap<>();
        int totalBrowsers = 0;

        for (Integer count : browserFrequency.values()) {
            totalBrowsers += count;
        }

        if (totalBrowsers > 0) {
            for (String browser : browserFrequency.keySet()) {
                int count = browserFrequency.get(browser);
                double percentage = (double) count / totalBrowsers;
                browserStatistics.put(browser, percentage);
            }
        }


        return browserStatistics;

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
