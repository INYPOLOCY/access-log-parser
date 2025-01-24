import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private final String ipAddress;
    private final LocalDateTime dateTime;
    private final HttpMethod method;
    private final String requestPath;
    private final int responseCode;
    private final long dataSize;
    private final String referer;
    private final UserAgent userAgent;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "dd/MMM/yyyy:HH:mm:ss Z", Locale.ROOT
    );

    public LogEntry(String logLine) {
        String[] parts = logLine.split(" ");
        if (parts.length < 12) {
            throw new IllegalArgumentException("Некорректный формат строки лога.");
        }

        this.ipAddress = parts[0];
        String dateTimeString = parts[3] + " " + parts[4]; // "[25/Sep/2022:06:25:04" + "+0300]"
        dateTimeString = dateTimeString.substring(1, dateTimeString.length() - 1);
        this.dateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);

        this.method = HttpMethod.valueOf(parts[5].substring(1));
        this.requestPath = parts[6];
        this.responseCode = Integer.parseInt(parts[8]);
        this.dataSize = parts[9].equals("-") ? 0L : Long.parseLong(parts[9]);
        this.referer = parts[10].equals("\"-\"") ? null : parts[10].substring(1, parts[10].length() - 1);
        String userAgentString = logLine.split("\"")[5];
        this.userAgent = new UserAgent(userAgentString);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public long getDataSize() {
        return dataSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}

enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH
}
