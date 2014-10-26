import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

/**
 * Created by arseny on 07.10.14.
 */
public class YandexDisk {

    private static String token = "";
    private static String resourcesURL =
        "https://cloud-api.yandex.net/v1/disk/resources/";

    public enum CommandType {
        LS("ls"),
        RM("rm"),
        CP("cp"),
        MV("mv"),
        DOWNLOAD("download"),
        UPLOAD("upload"),
        NOC(""); //not a command

        private String typeValue;

        private CommandType(String type) {
            typeValue = type;
        }

        static public CommandType getCommandType(String pType) {
            for (CommandType type : CommandType.values()) {
                if (type.getTypeValue().equals(pType)) {
                    return type;
                }
            }
            return NOC;
        }

        public String getTypeValue() {
            return typeValue;
        }
    }

    static InputStream processRequest(HttpsURLConnection connection) throws Exception {
        int code = connection.getResponseCode();
        InputStream is = null;
        if (200 <= code && code < 300) {
            is = connection.getInputStream();
        } else if (400 <= code && code < 600) {
            is = connection.getErrorStream();
        }
        return is;
    }

    static void printResponse(InputStream is) throws Exception {
        if (is != null) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    static void processDownloadRequest(HttpsURLConnection connection,
                                       String destinationPath) throws Exception {
        InputStream is = processRequest(connection);
        int contentLength = connection.getContentLength();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[contentLength];
        int dataLength = 0;
        while ((dataLength = is.read(buf)) != -1) {
            baos.write(buf, 0, dataLength);
        }
        byte[] response = baos.toByteArray();

        FileOutputStream fos = new FileOutputStream(destinationPath);
        fos.write(response);
    }

    static void execCp(String source, String destination) throws Exception {
        String sourcePath = URLEncoder.encode(source, "UTF-8");
        String destinationPath = URLEncoder.encode(destination, "UTF-8");
        URL requestURL = new URL(resourcesURL + "copy"
                                 + "?from=" + sourcePath
                                 + "&path=" + destinationPath);
        HttpsURLConnection connection =
            (HttpsURLConnection) requestURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        InputStream is = processRequest(connection);

        printResponse(is);
    }

    static void execMv(String source, String destination) throws Exception {
        String sourcePath = URLEncoder.encode(source, "UTF-8");
        String destinationPath = URLEncoder.encode(destination, "UTF-8");
        URL requestURL = new URL(resourcesURL + "move"
                                 + "?from=" + sourcePath
                                 + "&path=" + destinationPath);
        HttpsURLConnection connection =
            (HttpsURLConnection) requestURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        InputStream is = processRequest(connection);

        printResponse(is);
    }

    static void execRm(String source) throws Exception {
        String sourcePath = URLEncoder.encode(source, "UTF-8");
        URL requestURL = new URL(resourcesURL + "?path=" + sourcePath);
        HttpsURLConnection connection =
                (HttpsURLConnection) requestURL.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        InputStream is = processRequest(connection);

        printResponse(is);
    }

    static void execLs(String dir) throws Exception {
        String sourcePath = URLEncoder.encode(dir, "UTF-8");
        URL requestURL = new URL(resourcesURL + "?path=" + sourcePath);
        HttpsURLConnection connection =
            (HttpsURLConnection) requestURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        InputStream is = processRequest(connection);

        if (is != null) {
            System.out.println(dir);
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONArray jsonArray =
                            new JSONObject(line).getJSONObject("_embedded")
                                                .getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        System.out.println(
                                jsonArray.getJSONObject(i).getString("name")
                        );
                    }
                } catch (JSONException ex) {
                    System.err.println(ex.getMessage());
                    System.err.println("Possible cause: wrong source dir");
                    System.exit(1);
                }

            }
        }
    }

    static void execDownload(String remote, String local) throws Exception {
        String sourcePath = URLEncoder.encode(remote, "UTF-8");
        URL requestURL = new URL(resourcesURL + "download"
                + "?path=" + sourcePath);
        HttpsURLConnection connection =
                (HttpsURLConnection) requestURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        InputStream is = processRequest(connection);

        if (is != null) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String link = new JSONObject(line).getString("href");
                URL downloadURL = new URL(link);
                HttpsURLConnection downloadConnection =
                        (HttpsURLConnection) downloadURL.openConnection();
                downloadConnection.setRequestMethod("GET");
                downloadConnection.setRequestProperty("Authorization",
                                                      "OAuth " + token);

                processDownloadRequest(downloadConnection, local);
            }
        }
    }

    static void execUpload(String local, String remote) throws Exception {
        String destinationPath = URLEncoder.encode(remote, "UTF-8");
        URL requestURL = new URL(resourcesURL + "upload"
                + "?path=" + destinationPath);
        HttpsURLConnection connection =
                (HttpsURLConnection) requestURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        InputStream is = processRequest(connection);

        if (is != null) {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String link = new JSONObject(line).getString("href");
                URL uploadURL = new URL(link);
                HttpsURLConnection uploadConnection =
                        (HttpsURLConnection) uploadURL.openConnection();
                uploadConnection.setRequestMethod("PUT");
                uploadConnection.setDoOutput(true);

                OutputStream os = uploadConnection.getOutputStream();

                FileInputStream fis = new FileInputStream(new File(local));

                byte[] buf = new byte[8192];
                int dataLength = 0;
                while ((dataLength = fis.read(buf)) != -1) {
                    os.write(buf, 0, dataLength);
                    os.flush();
                }

                InputStream uis = uploadConnection.getInputStream();
            }
        }
    }

    static void exec(String commandLine) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(commandLine, " \t");
        CommandType command = CommandType.getCommandType(tokenizer.nextToken());
        switch (command) {
            case RM:
                if (tokenizer.countTokens() == 1) {
                    String source = tokenizer.nextToken();
                    execRm(source);
                } else {
                    System.err.println("rm usage: rm source");
                }
                break;
            case CP:
                if (tokenizer.countTokens() == 2) {
                    String source = tokenizer.nextToken();
                    String destination = tokenizer.nextToken();
                    execCp(source, destination);
                } else {
                    System.err.println("cp usage: source destination");
                }
                break;
            case MV:
                if (tokenizer.countTokens() == 2) {
                    String source = tokenizer.nextToken();
                    String destination = tokenizer.nextToken();
                    execMv(source, destination);
                } else {
                    System.err.println("mv usage: mv source destination");
                }
                break;
            case LS:
                if (tokenizer.countTokens() == 1) {
                    String dir = tokenizer.nextToken();
                    execLs(dir);
                } else {
                    System.err.println("ls usage: ls dir");
                }
                break;
            case DOWNLOAD:
                if (tokenizer.countTokens() == 2) {
                    String remote = tokenizer.nextToken();
                    String local = tokenizer.nextToken();
                    execDownload(remote, local);
                } else {
                    System.out.println(tokenizer.countTokens());
                    System.out.println(tokenizer.nextToken());
                    System.err.println("Download usage: download remote local");
                }
                break;
            case UPLOAD:
                if (tokenizer.countTokens() == 2) {
                    String local = tokenizer.nextToken();
                    String remote = tokenizer.nextToken();
                    execUpload(local, remote);
                } else {
                    System.out.println(tokenizer.countTokens());
                    System.out.println(tokenizer.nextToken());
                    System.err.println("Upload usage: upload local remote");
                }
                break;
            case NOC:
                System.err.println("Unknown command: " + command + ". "
                        + "Available commands: "
                        + "ls, rm, cp, mv, download, upload.");
                System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: token command1 params1;"
                               + "command2 params2 + ...");
        } else {
            token = args[0];
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; ++i) {
                sb.append(args[i]);
                sb.append(" ");
            }
            StringTokenizer tokenizer = new StringTokenizer(sb.toString(), ";");
            String curToken;
            while (tokenizer.hasMoreTokens()) {
                curToken = tokenizer.nextToken();
                exec(curToken);
            }
        }
    }
}
