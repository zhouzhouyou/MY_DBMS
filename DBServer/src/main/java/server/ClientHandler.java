package server;

import com.google.gson.Gson;
import core.Core;
import core.database.DatabaseBlock;
import util.parser.ParserFactory;
import util.parser.annoation.Permission;
import util.parser.parsers.*;
import util.result.Request;
import util.result.Result;
import util.result.ResultFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintStream output;
    private DatabaseBlock database;
    private boolean authenticated = false;
    private Core core = Core.INSTANCE;
    private String username;
    private Gson gson;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        gson = new Gson();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String input = this.input.readLine();
                Request request = Request.fromGson(input);
//                if (request.databaseName != null)
                Result result = handleSQL(request.sql, request.databaseName);
                output.println(gson.toJson(result));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Result handleSQL(String sql, String databaseName) {
        Parser parser = ParserFactory.generateParser(sql);
        if (parser == null) return ResultFactory.buildInvalidNameResult(sql);

        Class child = parser.getClass();
        //Connect, Disconnect or quit
        if (!child.isAnnotationPresent(Permission.class)) return handleParser(parser, databaseName);

        //Should connect first
        if (!authenticated) return ResultFactory.buildUnauthorizedResult();

        Permission permission = (Permission) child.getAnnotation(Permission.class);
        Result result = core.getGrant(username, permission.value());
        if (result.code == ResultFactory.SUCCESS) {
            return handleParser(parser, databaseName);
        } else {
            return ResultFactory.buildUnauthorizedResult();
        }
    }

    private Result handleParser(Parser parser, String databaseName) {
        if (parser instanceof ConnectParser) {
            return handleConnect((ConnectParser) parser);
        } else if (parser instanceof DisconnectParser) {
            return handleDisconnect((DisconnectParser) parser);
        } else if (parser instanceof CreateDatabaseParser) {
            return handleCreateDatabase((CreateDatabaseParser) parser);
        } else if (parser instanceof DropDatabaseParser) {
            return handleDropDatabase((DropDatabaseParser) parser);
        } else if (parser instanceof ChooseDatabaseParser) {
            return handleChooseDatabase((ChooseDatabaseParser) parser);
        } else if (databaseName == null) {
            return ResultFactory.buildUnauthorizedResult("Missing Database Info");
        } else if (parser instanceof CreateTableParser) {
            return handleCreateTable((CreateTableParser) parser, databaseName);
        } else if (parser instanceof DropTableParser) {
            return handleDropTable((DropTableParser) parser, databaseName);
        } else if (parser instanceof InsertParser) {
            return handleInsert((InsertParser) parser, databaseName);
        } else if (parser instanceof ReleaseDatabaseParser) {
            return handleReleaseDatabase((ReleaseDatabaseParser) parser, databaseName);
        } else if (parser instanceof CreateIndexParser) {
            return handleCreateIndex((CreateIndexParser) parser, databaseName);
        }
        return null;
    }

    private Result handleCreateIndex(CreateIndexParser parser, String databaseName) {
        return core.createIndex(parser, databaseName);
    }

    private Result handleReleaseDatabase(ReleaseDatabaseParser parser, String databaseName) {
        Result result = core.releaseDatabase(databaseName);
        database = null;
        return result;
    }

    private Result handleInsert(InsertParser parser, String databaseName) {
        return core.insert(parser, databaseName);
    }

    private Result handleDropTable(DropTableParser parser, String databaseName) {
        return core.dropTable(parser.getTableName(), databaseName);
    }

    private Result handleChooseDatabase(ChooseDatabaseParser parser) {
        Result result = core.chooseDatabase(parser);
        if (result.code == ResultFactory.SUCCESS) database = (DatabaseBlock) result.data;
        return result;
    }

    private Result handleCreateTable(CreateTableParser parser, String databaseName) {
        return core.createTable(parser, databaseName);
    }

    private Result handleDropDatabase(DropDatabaseParser parser) {
        return core.dropDatabase(parser.getDatabaseName());
    }

    private Result handleCreateDatabase(CreateDatabaseParser parser) {
        return core.createDatabase(parser.getDatabaseName(), false);
    }

    private Result handleDisconnect(DisconnectParser parser) {
        if (!authenticated) return ResultFactory.buildFailResult("not connected");
        authenticated = false;
        username = null;
        return ResultFactory.buildSuccessResult(null);
    }

    private Result handleConnect(ConnectParser parser) {
        Result result = core.connect(parser.getName(), parser.getPassword());
        if (result.code == ResultFactory.SUCCESS) {
            authenticated = true;
            username = parser.getName();
        }
        return result;
    }
}
