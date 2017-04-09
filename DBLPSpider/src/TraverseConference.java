import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class TraverseConference {

	public void traverseConference() {
		String[] conferenceName = { "ASPLOS" };
		// http://dblp.dagstuhl.de/search/publ/inc?q=ASPLOS&h=2&f=1
		// StringBuffer url = new StringBuffer("http://dblp.org/search?q=");
		String url = "http://dblp.dagstuhl.de/search/publ/inc";

		int thcnt = 10;
		for (int i = 0; i < conferenceName.length; i++) {
			int yearLimit = 2000;
			boolean flag = true;
			ExecutorService service = Executors.newFixedThreadPool(thcnt);
			for (int th = 0; th < thcnt && flag; th++) {
				GetNetString net = new GetNetString(url, conferenceName[i], yearLimit);
				service.submit(net);
			}
			service.shutdown();
			try {
				service.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
