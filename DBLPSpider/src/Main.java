import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

public class Main {

	public static void main(String[] args) {
		
		redirectSysout("D:\\out.txt");
		redirectSyserr("D:\\err.txt");

		ExecuteSQL executeSQL = new ExecuteSQL();
		Connection conn = DBUtils.getConnection();
		executeSQL.dropTables(conn);
		executeSQL.createTables(conn);
		DBUtils.releaseConnection(conn);

		long start = System.currentTimeMillis();

		TraverseConference traverseConference1 = new TraverseConference();
		traverseConference1.traverseConference();
		
		long end = System.currentTimeMillis();
		
		System.out.println("time used : " + (end - start) / 1000);
	}

	public static void redirectSysout(String out) {
		try {
			System.setOut(new PrintStream(new LoggerOutputStream(System.out, new FileOutputStream(out))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void redirectSyserr(String err) {
		try {
			System.setErr(new PrintStream(new LoggerOutputStream(System.err, new FileOutputStream(err))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
