package app.spaulding.cef;

import app.spaulding.cef.avro.CEF;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for Common Event Format for logs
 *
 * @author josh@spaulding.app
 */
public class CEFParser {

    private static final String cefRegex = "(?<!\\\\)\\|";
    private static final String cefExtension = "(?<!\\\\)=";
    private static final Pattern cefPattern = Pattern.compile(cefRegex);
    private static final Pattern extensionPattern = Pattern.compile(cefExtension);

    public static CEF parseToCEF(String event) {
        CEF cef = new CEF();
        Map<CharSequence, CharSequence> extension = new HashMap<>();

        Matcher m = cefPattern.matcher(event);
        int counter = 0, index = 0;
        while (counter < 7 && m.find()) {
            String val = event.substring(index, m.start());
            switch (counter) {
                case 0:
                    try {
                        cef.setVersion(Integer.valueOf(val.substring(4)));
                    } catch (NumberFormatException nfe) {
                        //TODO Log error to error topic
                        cef.setVersion(0);
                    }
                    break;
                case 1:
                    cef.setDeviceVendor(val);
                    break;
                case 2:
                    cef.setDeviceProduct(val);
                    break;
                case 3:
                    cef.setDeviceVersion(val);
                    break;
                case 4:
                    cef.setDeviceEventClassId(val);
                    break;
                case 5:
                    cef.setName(val);
                    break;
                case 6:
                    cef.setSeverity(val);
                    break;
            }
            index = m.end();
            counter++;
        }

        String ext = StringUtils.strip(event.substring(index));
        m = extensionPattern.matcher(ext);
        index = 0;
        String key = null;
        String value = null;
        while (m.find()) {
            if (key == null) {
                key = ext.substring(index, m.start());
                index = m.end();
                if (!m.find()) {
                    break;
                }
            }
            value = ext.substring(index, m.start());
            index = m.end();
            int v = value.lastIndexOf(" ");
            if (v > 0) {
                String temp = value.substring(0, v).trim();
                extension.put(key, temp);
                key = value.substring(v).trim();
            }
        }
        value = ext.substring(index);
        extension.put(key, value);
        cef.setExtension(extension);
        return cef;
    }

    public static String toString(CEF cef) {
        StringBuilder cefString = new StringBuilder("CEF:");

        cefString.append(cef.getVersion());
        cefString.append("|");
        cefString.append(cef.getDeviceVendor());
        cefString.append("|");
        cefString.append(cef.getDeviceProduct());
        cefString.append("|");
        cefString.append(cef.getDeviceVersion());
        cefString.append("|");
        cefString.append(cef.getDeviceEventClassId());
        cefString.append("|");
        cefString.append(cef.getName());
        cefString.append("|");
        cefString.append(cef.getSeverity());
        cefString.append("|");
        cef.getExtension().forEach((key, value) -> cefString.append(" " + key + "=" + value));

        return cefString.toString();
    }
}
