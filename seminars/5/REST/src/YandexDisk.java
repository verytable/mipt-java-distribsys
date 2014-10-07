import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by arseny on 07.10.14.
 */
class YandexDisk {
    private static String token = "";
    private static String resourcesURL = "https://cloud-api.yandex.net/v1/disk/resources";

    public static void main(String[] args) throws Exception {
        token = args[0];
        String folderName = URLEncoder.encode(args[1], "UTF-8");
        URL requestURL = new URL(resourcesURL + "?path=" + folderName);
        HttpsURLConnection connection = (HttpsURLConnection) requestURL.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        int code = connection.getResponseCode();
        InputStream is = null;
        if (200 <= code && code < 300) {
            is = connection.getInputStream();
        } else if (400 <= code && code < 600) {
            is = connection.getErrorStream();
        }

        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
