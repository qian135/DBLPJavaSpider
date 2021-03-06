import java.util.ArrayList;
import java.util.List;

public class Node {

	private String title;
	private String conference;
	private int year;
	private String pagination;
	private List<String> authors;
	
	public Node() {
		authors = new ArrayList<String>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getConference() {
		return conference;
	}

	public void setConference(String conference) {
		this.conference = conference;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getPagination() {
		return pagination;
	}

	public void setPagination(String pagination) {
		this.pagination = pagination;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	@Override
	public String toString() {
		return "Node [title=" + title + ", conference=" + conference + ", year=" + year + ", pagination=" + pagination
				+ ", authors=" + authors + "]";
	}
	
}
