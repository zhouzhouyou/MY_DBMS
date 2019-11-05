package util.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLSegment {
    private static final String Crlf = "|";

    private String start;
    private String body;
    private String end;

    private String bodySplitPattern;
    private String segmentRegExp;

    private List<String> bodyPieces;

    public SQLSegment(String segmentRegExp, String bodySplitPattern) {
        start = "";
        body = "";
        end = "";
        this.segmentRegExp = segmentRegExp;
        this.bodySplitPattern = bodySplitPattern;
        bodyPieces = new ArrayList<String>();
    }

    public void parse(String sql) {
        Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            start = matcher.group(1);
            body = matcher.group(2);
            end = matcher.group(3);
            parseBody();
        }
    }

    private void parseBody() {
        List<String> ls = new ArrayList<>();
        Pattern pattern = Pattern.compile(bodySplitPattern, Pattern.CASE_INSENSITIVE);
        body = body.trim();
        Matcher matcher = pattern.matcher(body);
        StringBuffer buffer = new StringBuffer();
        boolean result = matcher.find();

        while (result) {
            matcher.appendReplacement(buffer, Crlf);
            result = matcher.find();
        }
        matcher.appendTail(buffer);

        //ls.add(start);
        String[] array = buffer.toString().split("[|]");
        Arrays.asList(array).forEach(s -> ls.add(s.trim()));

        bodyPieces = ls;
    }

    public String getParsedSqlSegment() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(start).append(Crlf);
        bodyPieces.forEach(piece -> buffer.append(piece).append(Crlf));
        return buffer.toString();
    }

    public String getStart() {
        return start;
    }

    public String getBody() {
        return body;
    }

    public String getEnd() {
        return end;
    }

    public String getBodySplitPattern() {
        return bodySplitPattern;
    }

    public String getSegmentRegExp() {
        return segmentRegExp;
    }

    public List<String> getBodyPieces() {
        return bodyPieces;
    }
}
