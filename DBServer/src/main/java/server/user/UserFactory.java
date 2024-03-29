package server.user;

import util.file.BlockCollections;
import util.file.FileUtils;
import util.file.exception.IllegalNameException;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static util.file.Path.USER_PATH;

public enum UserFactory {
    INSTANCE;

    private UserCollection collection;
    private Map<String, UserBlock> map = new HashMap<>();

    UserFactory() {
        if (BlockCollections.exists(USER_PATH)) {
            try {
                collection = (UserCollection) BlockCollections.deserialize(USER_PATH);
                collection.list.forEach(user -> map.put(user.name, user));
            } catch (IllegalNameException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            collection = new UserCollection();
        }
        if (!exists("system")) {
            createUser("system", "123456");
        }
    }

    public boolean exists(String name) {
        return map.containsKey(name);
    }

    /**
     * Create a new user.
     *
     * @param name     username
     * @param password user's password (MD5)
     * @return util.result
     */
    public Result createUser(String name, String password) {
        if (!FileUtils.isValidFileName(name)) return ResultFactory.buildInvalidNameResult(name);
        if (exists(name)) return ResultFactory.buildObjectAlreadyExistsResult();
        UserBlock userBlock = new UserBlock(name, password);
        collection.add(userBlock);
        map.put(name, userBlock);
        saveInstance();
        return ResultFactory.buildSuccessResult(name);
    }

    public Result dropUser(String name) {
        try {
            UserBlock userBlock = getUser(name);
            collection.remove(userBlock);
            map.values().removeIf(block -> block.equals(userBlock));
            saveInstance();
            return ResultFactory.buildSuccessResult(name);
        } catch (Exception e) {
            return ResultFactory.buildInvalidNameResult(name);
        }
    }

    /**
     * Get user from the {@link #map}.
     *
     * @param name username
     * @return user block
     * @throws Exception when the user doesn't exist
     */
    public UserBlock getUser(String name) throws Exception {
        if (!exists(name)) throw new Exception(name + "not exists");
        return map.get(name);
    }

    /**
     * Change grant({@code grantType}) of user({@code target}) by user({@code source}).
     *
     * @param source    the user wishes to change others' grant
     * @param target    user to be changed
     * @param grantType grant type to be changed
     * @param grant     give grant or not
     * @return util.result
     */
    public Result grant(UserBlock source, String target, String grantType, boolean grant) {
        if (!source.grant) return ResultFactory.buildUnauthorizedResult();
        try {
            UserBlock userBlock = getUser(target);
            Field field = userBlock.getClass().getDeclaredField(grantType);
            field.set(userBlock, grant);
            return ResultFactory.buildSuccessResult(null);
        } catch (NoSuchFieldException e) {
            return ResultFactory.buildObjectNotExistsResult();
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    /**
     * Change grant({@code grantType}) of user({@code target}) by user({@code source}).
     *
     * @param source    the user wishes to change others' grant
     * @param target    user to be changed
     * @param grantType grant type to be changed
     * @param grant     give grant or not
     * @return util.result
     */
    public Result grant(String source, String target, String grantType, boolean grant) {
        try {
            UserBlock sourceBlock = getUser(source);
            return grant(sourceBlock, target, grantType, grant);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(source);
        }
    }

    /**
     * Get the state of a certain grant of a user.
     *
     * @param userBlock user
     * @param grantType grant type
     * @return util.result whether the user has this grant
     */
    public Result getGrant(UserBlock userBlock, String grantType) {
        try {
            Field field = userBlock.getClass().getDeclaredField(grantType);
            return ResultFactory.buildSuccessResult(field.getBoolean(userBlock));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return ResultFactory.buildObjectNotExistsResult();
        }
    }

    /**
     * Get the state of a certain grant of a user.
     *
     * @param user      user
     * @param grantType grant type
     * @return util.result whether the user has this grant
     * @see #getGrant(UserBlock, String)
     */
    public Result getGrant(String user, String grantType) {
        if (user.equals("system")) return ResultFactory.buildSuccessResult(true);
        try {
            return getGrant(getUser(user), grantType);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult();
        }
    }

    /**
     * Client try to connect as a user.
     *
     * @param user     username
     * @param password password
     * @return util.result whether the connect is valid
     */
    public Result connect(String user, String password) {
        try {
            UserBlock userBlock = getUser(user);
            if (userBlock.password.equals(password)) return ResultFactory.buildSuccessResult(user);
        } catch (Exception e) {
            //ignore
        }
        return ResultFactory.buildObjectNotExistsResult();
    }

    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
