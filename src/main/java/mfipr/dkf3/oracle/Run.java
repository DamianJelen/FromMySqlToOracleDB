package mfipr.dkf3.oracle;

public class Run {
    public static void runCreateDB() {
        DropTables.runDropQueries();
        CreateTables.runQueries();
        InsertToTables.runInsertQuery();
        Statistics.runStats();
    }
}
