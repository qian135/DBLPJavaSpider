import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


class GetNetString {//获取网页源代码字符串
	
	private Integer CONNECTION_TIMEOUT = 30 * 1000; //设置请求超时30秒钟 根据业务调整
	private Integer READ_TIMEOUT = 30 * 1000; //设置等待数据超时时间30秒钟 根据业务调整
	
    private String url;//输入的网址
    private String result;// 定义一个字符串用来存储网页内容

    public String getNetString(String url) throws Exception {
        this.url = url;
        creatResult();
        return result;
    }

    public void creatResult() throws Exception {

        // 定义一个缓冲字符输入流
    	BufferedReader in = null;
        try {
            // 将string转成url对象
            URL realUrl = new URL(url);
            // 初始化一个链接到那个url的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);           
            // 开始实际的连接
            connection.connect();
            String t = "";
			for (int i = 0; t != null; i++) {					
				t = connection.getHeaderField(i);
				if (t != null) {
					if(t.indexOf("139site.com") != -1) {//防止移动的DNS劫持
						LogRecord logRecord = new LogRecord();
						logRecord.recordURLExceptionLog(url);;
					}
				}				
			}
            // 初始化 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            // 用来临时存储抓取到的每一行的数据
            String line;
            while ((line = in.readLine()) != null) {
                // 遍历抓取到的每一行并将其存储到result里面
                result += line;
            }
        } catch (Exception e1) {
        	//e1.printStackTrace();
        	throw e1;
        } finally {// 使用finally来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}