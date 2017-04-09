public class Main {

    public static void main(String[] args) {
    	ExecuteSQL executeSQL = new ExecuteSQL();
    	executeSQL.dropTables();
    	executeSQL.createTables();
    	
        TraverseConference traverseConference1 = new TraverseConference();
        traverseConference1.traverseConference();
    }

}