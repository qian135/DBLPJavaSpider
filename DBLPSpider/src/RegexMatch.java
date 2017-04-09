import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexMatch {

	// private String sourceString;//待匹配文本
	// private String regexString;//正则表达式
	private ArrayList<String> resultArray;

	public RegexMatch() {
		resultArray = new ArrayList<>();
	}

	public ArrayList<String> regexMatch(String sourceString, String regexString) {// 正则匹配部分

		// this.sourceString = sourceString;
		// this.regexString = regexString;
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regexString);
		// 定义一个matcher用来做匹配
		Matcher matcher = null;
		if (sourceString != null) {
			matcher = pattern.matcher(sourceString);
		}
		// 如果找到了
		if (matcher != null) {
			while (matcher.find()) {
				if (matcher.group(1) != null) {
					resultArray.add(matcher.group(1));
				} else {
					resultArray.add(matcher.group(2));
				}
			}
		}
		return resultArray;
	}

	public void clearResultArray() {
		resultArray.clear();
	}

	public ArrayList<String> getResult() {
		return resultArray;
	}

}
