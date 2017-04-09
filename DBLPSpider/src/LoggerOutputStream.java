import java.io.IOException;
import java.io.OutputStream;

public class LoggerOutputStream extends OutputStream {

	private OutputStream[] streams;
	
	public LoggerOutputStream(OutputStream ... args) {
		this.streams = args;
	}
	
	@Override
	public void write(int b) throws IOException {
		for(OutputStream out : streams) {
			out.write(b);
			out.flush();
		}
	}
	
}
