import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream body;
    private final List<NameValuePair> params;
    private final Map <String, String> parameter;

    public Request(String method, String path, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.params = URLEncodedUtils.parse(path, StandardCharsets.UTF_8);
        this.parameter = new HashMap<>();
        for (NameValuePair param : params) {
            if (param.getName() != null && param.getValue() != null) {
                parameter.put(param.getName(), param.getValue());
            }
        }

    }

    public String getQueryParam(String name) {
        String result;
        int i = name.indexOf("?");
        if (i == -1) {
            return name;
        }
        result = name.substring(0, i);
        return result;
    }

    public List<NameValuePair> getQueryParams() {
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
