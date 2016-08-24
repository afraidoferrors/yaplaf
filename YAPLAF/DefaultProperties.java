package YAPLAF;

/**
 * Gibt Einstellungen, falls Konfigurationsdatei nicht gefunden werden kann.
 * @author Martin Weik
 *
 */
public class DefaultProperties extends java.util.Properties {

	/**
	 *
	 */
	private static final long serialVersionUID = 2847544911925579052L;

	public DefaultProperties() {
		super();

		this.setProperty("YAPLAF_XML_filename", "submissions.xml");
		this.setProperty("YAPLAF_Prozent_Exportschwelle", "20");

		this.setProperty("CSVWriter_filename", "ausgabe%n.csv");
		this.setProperty("CSVWriter_Zeilen_Datei", "1000");

		this.setProperty("LCSVektor_minStringlaenge", "10");
		this.setProperty("LCSVektor_matchingString_einfuegeMethode", "sum");

		this.setProperty("LogWriterFilename", "YAPLAF.log");

	}

}
