package banking;

import banking.ui.UI;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

    private static UI ui  = UI.getInstance();
    public static String dbURL = "jdbc:sqlite:";
    public static String dbFilename;
    public static final String tableName = "card";

    public static void run(String[] args) {
        if (checkDBFileProvided(args)) {
            while (true) {
                ui.start();
            }
        } else {
            System.out.println("Database not provided!");
            System.exit(1);
        }
    }

    private static boolean checkDBFileProvided(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fileName")) {
                try {
                    dbFilename = args[i + 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    return false;
                }
                dbURL = dbURL + dbFilename;
                createCardTable();
                return true;
            }
        }
        return false;
    }

    private static void createCardTable() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(dbURL);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0" +
                        ")");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
