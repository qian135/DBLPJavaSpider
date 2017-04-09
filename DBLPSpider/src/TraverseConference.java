import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class TraverseConference {
	private ExecuteSQL mExecuteSQL;
	
	TraverseConference() {
		mExecuteSQL = new ExecuteSQL();
	}
	
    public void traverseConference() {
        String[] conferenceName = {"ASPLOS"};
        //http://dblp.dagstuhl.de/search/publ/inc?q=ASPLOS&h=2&f=1
        //StringBuffer url = new StringBuffer("http://dblp.org/search?q=");
        StringBuffer url = new StringBuffer("http://dblp.dagstuhl.de/search/publ/inc?q=");
        StringBuffer temp = new StringBuffer();
        
        
        for (int i = 0;i < conferenceName.length;i++) {	
        	int paperNum = 30;//设置一次动态加载的论文数 
        	int startPageIndex = 0;//某次动态加载开始的页号
        	Boolean yearFlag = false;
        	while (true) {
        		temp.append(url);//生成url
                temp.append(conferenceName[i]);
                temp.append("&h=" + String.valueOf(paperNum) + "&f=" + String.valueOf(startPageIndex));
                GetNetString getNetString1 = new GetNetString();
                String sourceString = null;//源网页字符串
				try {
					sourceString = getNetString1.getNetString(temp.toString());
				} catch (Exception e) {
					e.printStackTrace();
					if(e instanceof SocketTimeoutException) {
		        		try {
							Thread.sleep(3000);
						} catch (InterruptedException e2) {
							e2.printStackTrace();
						}
		        		try {
							getNetString1.getNetString("temp.toString()");
						} catch (Exception e1) {
							e1.printStackTrace();
							if(e1 instanceof SocketTimeoutException) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e2) {
									e2.printStackTrace();
								}
								try {
									getNetString1.getNetString("temp.toString()");
								} catch (Exception e2) {
									e2.printStackTrace();		
									if(e2 instanceof SocketTimeoutException) {
										LogRecord logRecord = new LogRecord();
										logRecord.recordSocketTimeoutException(e2,temp.toString());
									}
								}
							}					
						}
		        	}					
				}
                
                List<Node> nodes = null;
				try {
					nodes = parse(sourceString, conferenceName[i]);
				} catch (Exception e) {
					System.out.println(temp.toString());
					e.printStackTrace();
				}
                if (nodes.size() != 0) {
                    System.out.println("进度为：" + nodes.get(0).getConference() + " " + nodes.get(0).getYear());
				}
                 for(Node node : nodes) {
                	if (mExecuteSQL.insertToPaperTable(node) == false) {//只爬取2000年及之后的数据
						yearFlag = true;
						break;
					}
                    mExecuteSQL.insertToPaper_AuthorTable(node);                                                  
                }
                temp.delete(0,temp.length());//清楚暂存的url字符串               
                if (yearFlag) {//只爬取2000年及之后的数据
					break;
				}
                
                startPageIndex += paperNum;
			}           
        }       
    }
    
    public List<Node> parse(String html, String conference) {
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select("div.data[itemprop=headline]");
		List<Node> nodes = new ArrayList<Node>();
		
		for(Element record : elems) {
			Node n = new Node();
			Elements authorList = record.select("span[itemprop=author] span[itemprop=name]");
			for(Element authorSpan : authorList) {
				n.authors.add(authorSpan.text());
			}
			Elements titles = record.select("span.title[itemprop=name]");
			if (titles.size() == 0) {
				continue;
			}
			n.title = titles.get(0).text();
			Elements conferences = record.select("span[itemprop=isPartOf] span[itemprop=name]");
			if(conferences.size() == 0) {
				continue;
			}
			n.conference = conferences.get(0).text();
			if(!n.conference.equals(conference)) {
				continue;
			}
			Elements years = record.select("span[itemprop=datePublished]");
			if (years.size() == 0) {
				continue;
			}
			n.year = Integer.parseInt(years.get(0).text());
			Elements paginations = record.select("span[itemprop=pagination]");
			if (paginations.size() == 0) {
				continue;
			}
			n.pagination = paginations.get(0).text();
			nodes.add(n);
		}
		return nodes;
	}
    
}