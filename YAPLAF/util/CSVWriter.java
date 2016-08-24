package YAPLAF.util;

import java.io.*;

/**
 * CSVWriter writes Values in csv Format
 *
 * @author PPR Group (Martin Weik)
 * @version 0.9 pre release
 *
 */

public class CSVWriter {
	private String filename;
	private String[] headline;

	private int fileCount;
	private int offsetRow;
	private int threshold;
	private int rowCount;

	FileOutputStream fWriter;
	PrintWriter bWriter;

	public CSVWriter(String filename, String[] headline, int threshold) {
		this.filename = filename;
		this.threshold = threshold;
		this.headline = headline;

		this.offsetRow = 1;
		this.fileCount = 0;
		this.rowCount = 0;

		this.fWriter = null;
		this.bWriter = null;
	}

	public void open() {
		this.nextFile();
	}

	public void nextFile() {
		try {
			if (this.bWriter != null) {
				this.bWriter.close();
			}
			if (this.fWriter != null) {
				this.fWriter.close();
			}
			this.fWriter = new FileOutputStream(this.filename.replaceAll("%n",
					new Integer(this.fileCount).toString()));
			this.bWriter = new PrintWriter(this.fWriter, true);

			for (String str : this.headline) {
				this.bWriter.write(str + ";");
			}

			// this.bWriter.write("Matrikel#1;Vorname1;Nachname1;Laenge1;Abgabezeit1;LCS
			// = % von
			// Text1;;Matrikel#2;Vorname2;Nachname2;Laenge2;Abgabezeit2;LCS = %
			// von Text2;;LCS");
			for (int i = 0; i < this.offsetRow; i++) {
				this.bWriter.println();
			}
			// this.bWriter.flush();
			this.fileCount++;
		} catch (Exception e) {
			System.out.println("Kann Ausgabedatei nicht öffnen");
			System.exit(0);
		}
	}

	public void write(String[] data) {
		if (this.rowCount % this.threshold == 0) {
			this.nextFile();
		}
		try {
			for (String part : data) {
				this.bWriter.write(part + ";");
			}
			this.bWriter.println();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
		this.rowCount++;
	}

	public void close() {
		try {
			if (this.bWriter != null) {
				this.bWriter.close();
			}
			if (this.fWriter != null) {
				this.fWriter.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void flush() {
		try {
			this.bWriter.flush();
			this.fWriter.flush();
		} catch (Exception e) {
		}
	}
}
