package util.parser.parsers;

import util.parser.SQLSegment;
import util.parser.annoation.Body;
import util.parser.annoation.End;
import util.parser.annoation.Start;

import java.util.ArrayList;
import java.util.List;


public abstract class Parser {
    protected String originSQL;
    protected List<SQLSegment> segments;

    public Parser(String originSQL) {
        this.originSQL = originSQL;
        segments = new ArrayList<>();
        initialSegments();
    }

    private void initialSegments() {
        Class child = this.getClass();
        if (child.isAnnotationPresent(Start.class)) {
            Start start = (Start) child.getAnnotation(Start.class);
            segments.add(new SQLSegment(start.value(), start.split()));
        }
        if (child.isAnnotationPresent(Body.class)) {
            Body body = (Body) child.getAnnotation(Body.class);
            segments.add(new SQLSegment(body.value(), body.split()));
        }
        if (child.isAnnotationPresent(End.class)) {
            End end = (End) child.getAnnotation(End.class);
            segments.add(new SQLSegment(end.value(), end.split()));
        }
    }

    public List<List<String>> splitOriginSQLIntoSegment() {
        List<List<String>> lists = new ArrayList<>();
        segments.forEach(sqlSegment -> {
            sqlSegment.parse(originSQL);
            lists.add(sqlSegment.getBodyPieces());
        });
        return lists;
    }

    public String getParsesSQL() {
        StringBuffer buffer = new StringBuffer();
        segments.forEach(sqlSegment -> buffer.append(sqlSegment.getParsedSqlSegment()).append("n"));
        return buffer.toString().replaceAll("n+", "n");
    }
}
