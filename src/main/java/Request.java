import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream body;
    private final MultiMap parameter;
    private List<NameValuePair> params;

    public Request(String method, String path, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.parameter = new MultiValueMap<>();
        try {
            params = URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
            for (NameValuePair param : params) {
                if (param.getName() != null && param.getValue() != null) {
                    parameter.put(param.getName(), param.getValue());
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
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

    public MultiMap getQueryParams() {
        return parameter;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
