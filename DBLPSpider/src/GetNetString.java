import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class GetNetString implements Callable<Boolean> {// 获取网页源代码字符串

	public static final int paperNum = 30;
	public static int startPageIndex = 0;
	private static boolean flag = true;

	private static int cnt = 1;
	private static final int CONNECTION_TIMEOUT = 30 * 1000; // 设置请求超时30秒钟 根据业务调整
	private static final int READ_TIMEOUT = 30 * 1000; // 设置等待数据超时时间30秒钟 根据业务调整

	private final String basseUrl;
	private String url;// 输入的网址
	
	private String conference;
	private int start;
	private ExecuteSQL exc;
	private int yearLimit;
	private int id;

	public GetNetString(String url, String conference, ExecuteSQL exc, int yearLimit) {
		super();
		this.conference = conference;
		this.exc = exc;
		this.yearLimit = yearLimit;
		this.basseUrl = url;
		reset();
	}

	@Override
	public Boolean call() throws Exception {
		while (flag) {
			System.out.println(id + " : start download records from " + start + " to " + (start + paperNum));
			String sourceString = null;
			int timeout = 0;
			while (timeout < 3) {
				try {
					sourceString = creatResult();// 源网页字符串
					break;
				} catch (SocketTimeoutException e) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
					timeout++;
					if (timeout == 3) {
						e.printStackTrace();
						LogRecord logRecord = new LogRecord();
						logRecord.recordSocketTimeoutException(e, url);
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}

			if (sourceString != null) {
				System.out.println(id + " : finished download records from " + start + " to " + (start + paperNum));
				List<Node> nodes = parse(sourceString);
				System.out.println(id + " : parse records from " + start + " to " + (start + paperNum));

				for (Node n : nodes) {
					exc.insertToPaperTable(n);
					exc.insertToPaper_AuthorTable(n);
				}
				System.out.println(id + " : save records from " + start + " to " + (start + paperNum));
			} else {
				System.out.println(id + " : failed to download records from " + start + " to " + (start + paperNum));
			}

			reset();
		}
		return flag;
	}

	public String creatResult() throws SocketTimeoutException {
		StringBuilder result = new StringBuilder();
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
					if (t.indexOf("139site.com") != -1) {// 防止移动的DNS劫持
						LogRecord logRecord = new LogRecord();
						logRecord.recordURLExceptionLog(url);
					}
				}
			}
			// 初始化 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			// 用来临时存储抓取到的每一行的数据
			String line;
			while ((line = in.readLine()) != null) {
				// 遍历抓取到的每一行并将其存储到result里面
				result.append(line);
			}
			return result.toString();
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
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

	public List<Node> parse(String html) {
		List<Node> nodes = new ArrayList<Node>();
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select("div.data[itemprop=headline]");

		int i = start;
		for (Element record : elems) {
			Node n = new Node();
			n.setId(i++);

			Elements authorList = record.select("span[itemprop=author] span[itemprop=name]");
			for (Element authorSpan : authorList) {
				n.getAuthors().add(authorSpan.text());
			}

			Elements titles = record.select("span.title[itemprop=name]");
			if (titles.size() == 0) {
				continue;
			}
			n.setTitle(titles.get(0).text());

			Elements conferences = record.select("span[itemprop=isPartOf] span[itemprop=name]");
			if (conferences.size() == 0) {
				continue;
			}
			n.setConference(conferences.get(0).text());
			if (!n.getConference().equals(conference)) {
				continue;
			}

			Elements years = record.select("span[itemprop=datePublished]");
			if (years.size() == 0) {
				continue;
			}
			n.setYear(Integer.parseInt(years.get(0).text()));
			if (n.getYear() < yearLimit) {
				flag = false;
				System.out.println(id + " : --------- year limit excceed ---------");
				break;
			}

			Elements paginations = record.select("span[itemprop=pagination]");
			if (paginations.size() == 0) {
				continue;
			}
			n.setPagination(paginations.get(0).text());

			nodes.add(n);
		}
		return nodes;
	}

	public void reset() {
		this.start = startPageIndex;
		this.url = basseUrl + "?q=" + conference + "&h=" + String.valueOf(paperNum) + "&f="
				+ String.valueOf(this.start);
		startPageIndex += paperNum;
		id = cnt++;
	}

}
