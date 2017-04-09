import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogRecord {
	
	private String time;
	private FileWriter fileWriter;
	
	public LogRecord() {
		Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = format.format(date);
        try {
			fileWriter = new FileWriter("Exception.txt",true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void recordURLExceptionLog (String url) {		
        String string = time + "\r\n" + "网页不存在,URL错误:" + url + "\r\n";//异常信息存储字符串
        try {
			fileWriter.write(string);
			fileWriter.flush();
	        fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}
	
	public void recordSocketTimeoutException(Exception exception, String url) {
		try {
			fileWriter.write(exception.toString() + "\r\n" + url);
			fileWriter.flush();
	        fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}

}
