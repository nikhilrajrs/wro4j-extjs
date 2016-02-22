package blah.blah;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class GenerateWro4jConfig {

    private static String template = "<js>$$$</js>";

    static String basePath = "C:/blah/at/blah";
    static String intermediary = "/extjs/app";
    static char seperator = '/';
    static String extension = ".js";
    static String applicationNamePrefix = "appName.";

    static Set<String> dependencies = new HashSet<String>();

    public static void main(String args[]) {
        try {
            findAllDependencies(basePath + "/extjs/app.js");
            for (String dep : dependencies) {
                System.out.println(dep);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static void findAllDependencies(String path) throws IOException {
        String regexString = Pattern.quote("model:'") + "(.*?)" + Pattern.quote("'");
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(readFile(path));
        if (matcher.find()) {
            String start = matcher.group(0);
            if (start.contains(applicationNamePrefix) && !start.contains("PivotModel")) {
                start = start.replace("model:'", "").replace("'", "").replaceAll(applicationNamePrefix, "");
                String newPath = basePath + intermediary;
                for (String part : Arrays.asList(StringUtils.split(start, '.'))) {
                    newPath = newPath + seperator + part;
                }
                findAllDependencies(newPath + extension);
            }
        }

        regexString = Pattern.quote("extend:'") + "(.*?)" + Pattern.quote("'");
        pattern = Pattern.compile(regexString);
        matcher = pattern.matcher(readFile(path));
        if (matcher.find()) {
            String start = matcher.group(0);
            if (start.contains(applicationNamePrefix)) {
                start = start.replace("extend:'", "").replace("'", "").replaceAll(applicationNamePrefix, "");
                String newPath = basePath + intermediary;
                for (String part : Arrays.asList(StringUtils.split(start, '.'))) {
                    newPath = newPath + seperator + part;
                }
                findAllDependencies(newPath + extension);
            }
        }

        regexString = Pattern.quote("requires:[") + "(.*?)" + Pattern.quote("]");
        pattern = Pattern.compile(regexString);
        matcher = pattern.matcher(readFile(path));
        if (matcher.find()) {
            String start = matcher.group(0);
            start = start.replace("requires:[", "").replace("]", "").replaceAll("'", "")
                    .replaceAll(applicationNamePrefix, "");
            List<String> fileList = Arrays.asList(StringUtils.split(start, ','));
            for (String file : fileList) {
                if (file.contains("Ext")) {
                    continue;
                }
                String newPath = basePath + intermediary;
                for (String part : Arrays.asList(StringUtils.split(file, '.'))) {
                    newPath = newPath + seperator + part;
                }
                findAllDependencies(newPath + extension);
            }
        }
        String result = template.replace("$$$", path.replace(basePath, ""));
        dependencies.add(result);
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String data = new String(encoded, Charset.forName("UTF-8"));
        data = data.replaceAll(System.getProperty("line.separator"), "");
        data = data.replaceAll(" ", "");
        return data;
    }
}
