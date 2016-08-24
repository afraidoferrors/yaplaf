/*
 * Testfälle
 * Werden Texte korrekt "gekürzt"?
 */

package YAPLAF.util;

/**
 * Repraesentiert eine Abgabe Verfuegt an sich ueber keine Getter und Setter;
 * hat eine statische Methode "itrim", die Strings von Tags, Leerzeichen,
 * Zeilenschaltungen befreit und in Kleinschreibung umwandelt.
 *
 * @author Martin Weik
 *
 */

public class Abgabe {
	private int matnr;
	private String vorname;
	private String nachname;
	private String abgabeText;
	private String abgabeZeit;

	public Abgabe(int matnr, String abgabeText) {
		this.matnr = matnr;
		this.abgabeText = abgabeText;
	}

	public Abgabe() {
		this.matnr = 0;
		this.vorname = "";
		this.nachname = "";
		this.abgabeText = "";
	}

	public void setMatrikelnr(int matnr) {
		this.matnr = matnr;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public void setAbgabezeit(String abgabezeit) {
		this.abgabeZeit = abgabezeit;
	}

	public void setStudentdata(String abgabeText) {
		this.abgabeText = itrim(abgabeText).toLowerCase();
	}

	public int getMatrikelnr() {
		return this.matnr;
	}

	public String getVorname() {
		return this.vorname;
	}

	public String getNachname() {
		return this.nachname;
	}

	public String getAbgabeText() {
		return this.abgabeText;
	}

	public String getAbgabezeit() {
		return this.abgabeZeit;
	}

	public int getAbgabeTextlaenge() {
		return this.abgabeText.length();
	}

	public String toString() {
		return new Integer(this.matnr).toString();
	}

	public static String itrim(String source) {
		// Tags, Leerzeichen und Zeilenschaltungen entfernen...
		return source.replaceAll("<([^>]+)>", "").replaceAll(" ", "")
				.replaceAll("\n", "").toLowerCase(); // .replaceAll("\r",
														// "");

	}

}
