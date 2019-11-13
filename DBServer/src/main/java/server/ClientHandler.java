package server;

import com.google.gson.Gson;
import core.Core;
import core.database.DatabaseBlock;
import server.user.UserBlock;
import util.parser.ParserFactory;
import util.parser.annoation.Permission;
import util.parser.parsers.*;
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
    private UserBlock userBlock;
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
                String sql = input.readLine();
                //if (sql == null) break;
                Result result = handleSQL(sql);
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

    private Result handleSQL(String sql) {
        Parser parser = ParserFactory.generateParser(sql);
        if (parser == null) return ResultFactory.buildInvalidNameResult(sql);

        Class child = parser.getClass();
        //Connect, Disconnect or quit
        if (!child.isAnnotationPresent(Permission.class)) return handleParser(parser);

        //Should connect first
        if (!authenticated) return ResultFactory.buildUnauthorizedResult();

        Permission permission = (Permission) child.getAnnotation(Permission.class);
        Result result = core.getGrant(username, permission.value());
        if (result.code == ResultFactory.SUCCESS) {
            return handleParser(parser);
        } else {
            return ResultFactory.buildUnauthorizedResult();
        }
    }

    private Result handleParser(Parser parser) {
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
        } else if (database == null) {
            return ResultFactory.buildUnauthorizedResult("Choose Database first");
        } else if (parser instanceof CreateTableParser) {
            return handleCreateTable((CreateTableParser) parser);
        } else if (parser instanceof DropTableParser) {
            return handleDropTable((DropTableParser) parser);
        } else if (parser instanceof InsertParser) {
            return handleInsert((InsertParser) parser);
        } else if (parser instanceof ReleaseDatabaseParser) {
            return handleReleaseDatabase((ReleaseDatabaseParser) parser);
        } else if (parser instanceof CreateIndexParser) {
            return handleCreateIndex((CreateIndexParser) parser);
        }
        return null;
    }

    private Result handleCreateIndex(CreateIndexParser parser) {
        return core.createIndex(parser, database);
    }

    private Result handleReleaseDatabase(ReleaseDatabaseParser parser) {
        Result result = core.releaseDatabase(database);
        database = null;
        return result;
    }

    private Result handleInsert(InsertParser parser) {
        return core.insert(parser, database);
    }

    private Result handleDropTable(DropTableParser parser) {
        return core.dropTable(parser.getTableName(), database);
    }

    private Result handleChooseDatabase(ChooseDatabaseParser parser) {
        Result result = core.chooseDatabase(parser);
        if (result.code == ResultFactory.SUCCESS) database = (DatabaseBlock) result.data;
        return result;
    }

    private Result handleCreateTable(CreateTableParser parser) {
        return core.createTable(parser, database);
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
