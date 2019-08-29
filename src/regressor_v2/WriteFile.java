package regressor_v2;
// code based on http://www.homeandlearn.co.uk/java/write_to_textfile.html
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class WriteFile {
	private String path;
	private boolean append_to_file = false;
	private FileWriter write;
	private PrintWriter print_line;

	public WriteFile(String file_path, boolean append_value) throws IOException {
		path = file_path;
		append_to_file = append_value;
		write = new FileWriter(path, append_to_file);
		print_line = new PrintWriter(write);

	}

	public void writeToFile(String textLine) {
		print_line.print(textLine);
		print_line.flush();
	}

	public void close() {
		print_line.close();
	}
}
