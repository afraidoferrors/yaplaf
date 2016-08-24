package YAPLAF.util;

import java.io.*;

/**
 * Klasse zur gleichzeitigen Ausgabe von Strings in eine Loggdatei und einem
 * weiteren PrintStream.
 *
 * @author Martin Weik
 *
 */
public class LogWriter {
	private PrintStream console;

	private String fn;
	private PrintWriter buff;
	private FileOutputStream file;

	public LogWriter() {
		this.console = System.out;
	}

	public void setLogFile(String fn) {
		this.fn = fn;
	}

	public void open() {
		try {
			this.file = new FileOutputStream(fn);
			this.buff = new PrintWriter(file, true);
		} catch (Exception e) {
			console.println("Kann Datei nicht öffnen!");
			System.exit(1);
		}
	}

	public void closeThis() {
		try {
			this.buff.flush();
			this.buff.close();
			this.buff = null;

			this.file.flush();
			this.file.close();
			this.file = null;
		} catch (Exception e) {
		}
	}

	public void print(String s) {
		try {
			this.buff.write(s);
			this.console.print(s);
		} catch (Exception e) {
		}
	}

	public void println(String s) {
		try {
			this.buff.println(s);
			this.console.println(s);
		} catch (Exception e) {
		}
	}

	public void println() {
		try {
			this.buff.println();
			this.console.println();
		} catch (Exception e) {
		}
	}

	public void print() {
	}

	public void println(Object o) {
		this.println(o.toString());
	}

	public void print(Object o) {
		this.print(o.toString());
	}
}
