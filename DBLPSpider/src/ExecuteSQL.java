import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ExecuteSQL {

	private Connection mConnection;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ExecuteSQL() {
		try {
			// 连接数据库dblp（mysql默认的用户名和密码是root,root）
			mConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/db1", "snowwolf", "snowwolf"); // 链接本地MYSQL
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void createTables() {
		// create table Paper_Author-- 创建论文及作者姓名关联表
		// (
		// Author_Name char(50) not null,-- 作者名
		// Author_Surname char(50) not null,-- 作者姓
		// Paper_Title varchar(600) not null-- 论文标题
		// );
		//
		// create table Paper-- 创建论文表
		// (
		// Paper_Title varchar(600) not null,-- 论文标题
		// Conference_Name char(200) not null,-- 论文发表的会议名称
		// Paper_Year int not null,-- 论文发表的年份
		// Paper_Page_Number char(20) not null-- 论文发表的页码
		// );
		try {
			String createPaper_AuthorTable = "create table  Paper_Author(Author_Name char(50) not null,"
					+ "Author_Surname char(50),Paper_Title varchar(600) not null);";
			String createPaperTable = "create table Paper(Id int primary key, Paper_Title varchar(600) not null,Conference_Name char(200) not null,"
					+ "Paper_Year char(4) not null,Paper_Page_Number char(20) not null)";
			PreparedStatement preparedStatement = mConnection.prepareStatement(createPaper_AuthorTable);
			preparedStatement.executeUpdate();
			preparedStatement = mConnection.prepareStatement(createPaperTable);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dropTables() {
		String dropTables = "drop table if exists Paper_Author,Paper";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = mConnection.prepareStatement(dropTables);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void insertToPaper_AuthorTable(Node node) {
		List<String> authorNames = node.getAuthors();
		String paperTitle = node.getTitle();

		for (String authorName : authorNames) {
			String[] strings = authorName.split(" ", 2);
			String author_Name, author_Surname;
			if (strings.length == 2) {
				author_Name = strings[0];
				author_Surname = strings[1];
			} else {
				author_Name = strings[0];
				author_Surname = null;
			}
			String insertToPaper_AuthorTable = "insert into Paper_Author(Author_Name,Author_Surname,Paper_Title)values(?,?,?)";
			PreparedStatement preparedStatement;
			try {
				preparedStatement = mConnection.prepareStatement(insertToPaper_AuthorTable);
				preparedStatement.setString(1, author_Name);
				preparedStatement.setString(2, author_Surname);
				preparedStatement.setString(3, paperTitle);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public Boolean insertToPaperTable(Node node) {
		String insertToPaper = "insert into Paper(Id, Paper_Title,Conference_Name,Paper_Year,Paper_Page_Number)"
				+ "values(?,?,?,?,?)";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = mConnection.prepareStatement(insertToPaper);
			preparedStatement.setInt(1, node.getId());
			preparedStatement.setString(2, node.getTitle());
			preparedStatement.setString(3, node.getConference());
			preparedStatement.setInt(4, node.getYear());
			preparedStatement.setString(5, node.getPagination());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
