/*
 * Testfälle:
 * Werden Teilstrings korrekt gefunden? (Start, Länge, Ende...)
 * Werden gefundene Teilstrings korrekt abgelegt?
 *
 */
package YAPLAF.LCSV;

import java.util.LinkedList;

/**
 * LCSVektor sucht Gemeinsamkeiten (MatchingStrings) in zwei gegebenen Texten.
 * Dabei kommt ein abgewandter Algorithmus zum Finden von Common Strings (<http://en.wikipedia.org/wiki/Longest_common_substring_problem>)
 * erweitert um die Möglichkeit mehrere Substrings zu speichern zum Einsatz. Zum
 * Speichern von zwei uebereinstimmenden Stellen werden die Klassen
 * MatchingString und MatchingStringContainer verwendet.
 *
 * Der eigentlche Aufruf erfolgt durch die Methode start()!!!
 *
 * @author PPR Group (Martin Weik)
 * @version 0.9.1 (pre relaese, untested)
 *
 * changes 0.9.1 - common strings sollten jetzt richtig gefunden werden (start &
 * ende)
 */

public class LCSVektor { // implements Runnable {
	private String src;
	private int srclength;

	private String dst;
	private int dstlength;

	private int[][] table;
	private int schranke;

	private int count;

	private int matchingStringCount;

	private MatchingStringContainer container;
	private java.util.LinkedList<MatchingString> analyzedParts;

	// StringQueue für MultiThreading
	// private LCSAlgorithmusStringQueue queue;

	public LCSVektor() {
		this.src = null;
		this.dst = null;
		this.schranke = 10;
		this.container = new MatchingStringContainer();
		// this.queue = null;

		this.analyzedParts = new java.util.LinkedList<MatchingString>();
	}

	public LCSVektor(int schranke, String verfahren) {
		this();
		this.schranke = schranke;
		// container.setMethod(verfahren);
	}

	// public void setQueue(LCSAlgorithmusStringQueue queue) {
	// this.queue = queue;
	// }

	public void setSrc(String src) {
		this.src = src;
		this.srclength = src.length();
	}

	public void setDst(String dst) {
		this.dst = dst;
		this.dstlength = dst.length();
	}

	public int getCount() {
		// vorher noch irgendwas rechnen...
		return this.count;
	}

	public void sort(int i) {
		this.container.sort(i);
	}

	public void setSchranke(int schranke) {
		this.schranke = schranke;
	}

	/*
	 * nicht implementiert... dient zum Multi Threading
	 *
	 * public void run() { String[] text; // Wenn keine queue übergeben worden
	 * ist, Exception werfen... if (this.queue == null) { // throw new
	 * Exception("QUEUE nicht gesetzt"); } // Element holen - solange Elemente
	 * vorhanden sind boolean run = true; while (run) { if ((text = queue.get()) !=
	 * null) { this.setSrc(text[0]); this.setDst(text[1]); // this.start(); }
	 * else { // auf Queue warten } } }
	 */

	/**
	 * Standardmethode fuer Aufruf
	 */
	public int start() throws Exception {
		// MatchingStringContainer zuruecksetzen -> leeren
		this.container.reset();

		// Zaehler fuer durchgefuehrte Vergleiche
		this.count++;

		if (this.src == null || this.dst == null) {
			throw new Exception("src oder dst nicht gesetzt");
		}
		// Variablen zum Finden & Vergleichen von MatchingStrings
		int voriges = 0, jetzt = 0;
		// TODO kann diese Zeile geloescht werden???
		// this.table = null;

		// Array mit 2 * Laenge von dst mit 0 initialisieren
		// uber modulo 2 kann dann auf die aktuelle und vorige Zeile zugegriffen
		// werden -> spart Speicher
		this.table = new int[2][this.dstlength + 1];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j <= this.dstlength; j++) {
				this.table[i][j] = 0;
			}
		}

		// Buchstaben fuer Buchstaben verleichen
		for (int i = 0; i < this.srclength; i++) {
			for (int j = 0; j < this.dstlength; j++) {
				// den vorhergenden Vergleich merken
				voriges = this.table[i % 2][j];
				// Wenn Treffer, Feld um 1 hochzaehlen
				if (src.charAt(i) == dst.charAt(j)) {
					jetzt = voriges + 1;

				}
				// Ansonsten pruefen, ob Substring eingefuegt werden soll
				// und aktuellen Wert wieder auf 0 setzen!!!
				else {
					jetzt = 0;
					if (voriges > this.schranke) {
						container.insert(i - voriges, j - voriges, voriges);
					}
				}
				// aktuellen Wert in Tabelle schreiben
				this.table[(i + 1) % 2][j + 1] = jetzt;
			}
			// Am Ende jeder Zeile die letzte Spalte ueberpruefen
			if (jetzt > this.schranke) {
				container.insert(i - voriges, dstlength - voriges - 1,
						voriges + 1);
			}
		}
		// Die letzte Zeile auf CommonStrings pruefen
		int i = this.srclength % 2;
		for (int j = 1; j < this.dstlength; j++) {
			voriges = this.table[i][j];
			if (voriges > this.schranke) {
				container.insert(srclength - voriges, j - voriges, voriges);
			}
		}

		// Ergebnisse zurueckgeben
		this.matchingStringCount = container.getMatchingStringCount();
		return container.getMatchingStringSum();
	}

	@Deprecated
	public java.util.LinkedList<LCSVektorAnalyzedPartsMessage> analyzeParts() {
		this.container.sort(0);
		// this.analyzedParts = new java.util.LinkedList<MatchingString>();
		LCSVektorAnalyzedPartsMessage temp1 = this.analyzePart(0);

		this.container.sort(1);
		// this.analyzedParts = new java.util.LinkedList<MatchingString>();
		LCSVektorAnalyzedPartsMessage temp2 = this.analyzePart(1);
		/*
		 * if(temp1.prozent > temp2.prozent) { return temp1; } else { return
		 * temp2; }
		 */
		java.util.LinkedList<LCSVektorAnalyzedPartsMessage> liste = new java.util.LinkedList<LCSVektorAnalyzedPartsMessage>();
		liste.add(temp1);
		liste.add(temp2);

		return liste;
	}

	@Deprecated
	private LCSVektorAnalyzedPartsMessage analyzePart(int i) {
		/*
		 * richtwert ... int die MatchingStrings in der Kette müssen mindestens
		 * diese Länge haben. Sobald sie erreicht worden ist, wird die das
		 * Verhältnis von Länge & Übereinstimmung berrechnet Danach das erste
		 * Element entfernen... wenn Länge > Richtwert nocheinmal Verhältnis
		 * bestimmen Solange Länge > Richtwert : 1 Element entfernen und
		 * analysieren Ansonsten nächstes Element aus MatchingStringContainer
		 * holen
		 *
		 * Aufzuzeichnende Daten: Start Ende Länge Übereinstimmung (absolut)
		 * Übereinstimmung (Prozent)
		 */

		int richtwert = 400; // Mindestlänge im Text, die betrachtet werden
		// soll.

		int betrachtetSumLaenge = 0; // Gesamtlaenge der betrachteten Stellen
		// (start des ersten elements bis ende
		// des letzten elements - wegen
		// vorheriger Sortierung geht das)
		int matchingStringSumLaenge = 0; // Gesamtlaenge aller
		int start = 0;
		int ende = 0;

		// solange es Elemente gibt
		container.resetPointer();
		this.analyzedParts = new LinkedList<MatchingString>();
		MatchingString letztes = new MatchingString(0, 0, 0);
		MatchingString erstes = new MatchingString(0, 0, 0);

		int prozent;
		LCSVektorAnalyzedPartsMessage merki = new LCSVektorAnalyzedPartsMessage(
				0, 0, 0, 0, 0);

		while (container.hasNextMatchingString()) {
			// Auffüllen
			letztes = container.nextMatchingString();
			if (this.analyzedParts.size() == 0) {
				start = letztes.start[i];
			}
			this.analyzedParts.add(letztes);
			matchingStringSumLaenge += letztes.length;
			ende = letztes.end[i];
			betrachtetSumLaenge = ende - start;
			while (this.analyzedParts.size() > 0
					&& betrachtetSumLaenge > richtwert) {
				// analysieren...
				erstes = this.analyzedParts.poll();
				// start = erstes.start[i];
				// betrachtetSumLaenge = ende - start;
				// analysieren ... betrachtetSumLaenge / matchingStringSumLaenge
				prozent = (matchingStringSumLaenge * 100) / betrachtetSumLaenge;
				if (prozent > merki.prozent) {
					int count = this.analyzedParts.size() + 1;
					merki = new LCSVektorAnalyzedPartsMessage(start, ende,
							matchingStringSumLaenge, prozent, count);
				}
				matchingStringSumLaenge -= erstes.length;
				start = this.analyzedParts.getFirst().start[i];
				betrachtetSumLaenge = ende - start;
			}
		}

		/*
		 * int percentageFromLength = 10; int percentageFromPart = 60; int
		 * minlength = 0; int maxlength = 10;
		 *
		 * int count = 0;
		 *
		 * MatchingString startPart; MatchingString endPart;
		 *
		 * while(i < 10) { }
		 */

		return merki;
	}

	@Deprecated
	public java.util.LinkedList<LCSVektorAnalyzedPartsMessage> analyzePartsNew() {
		this.container.sort(0);
		//
		LCSVektorAnalyzedPartsMessage temp1 = this.analyzePartNew(0);

		this.container.sort(1);
		//
		LCSVektorAnalyzedPartsMessage temp2 = this.analyzePartNew(1);
		/*
		 * if(temp1.prozent > temp2.prozent) { return temp1; } else { return
		 * temp2; }
		 */
		java.util.LinkedList<LCSVektorAnalyzedPartsMessage> liste = new java.util.LinkedList<LCSVektorAnalyzedPartsMessage>();
		liste.add(temp1);
		liste.add(temp2);

		return liste;
	}

	/**
	 * @param i
	 * @return
	 */

	@Deprecated
	@SuppressWarnings("unused")
	private LCSVektorAnalyzedPartsMessage analyzePartNew(int i) {
		/*
		 * Häufung finden... Signifikante Häufung ist ab 70% gegeben Nach jeder
		 * Zufügeoperation wird der Quotient neu berechnet, einfügeoperation,
		 * solange der quotient steigt, sollte er fallen, das erste element
		 * entfernen, bis er wieder steigt.
		 *
		 * quot_alt = quotient quotient = (matchingStringSumLaenge * 100) /
		 * betrachtetLaenge -> > 0 wenn(quotient < quot_alt) { collection.pop(),
		 * solange (quotient_neu < quot_alt)
		 *
		 * Neudefinition: Schranke_prozent: ab welcher Prozentzahl, soll
		 * gespeichert werden? Schranke_laenge:ab welcher (betrachteten) Länge
		 * soll gespeichert werden? Bedingung... ein MatchingString darf nur in
		 * einem "Teilabschnitt" vorkommen!!!
		 *
		 */

		// Initialisierung
		int betrachtetSumLaenge = 0; // Gesamtlaenge der betrachteten Stellen
		// (start des ersten elements bis ende
		// des letzten elements - wegen
		// vorheriger Sortierung geht das)
		int matchingStringSumLaenge = 0; // Gesamtlaenge aller
		int start = 0;
		int ende = 0;

		this.analyzedParts = new LinkedList<MatchingString>();
		MatchingString erstes = null;
		int prozent_neu = 0;
		int prozent_alt = 0;
		LCSVektorAnalyzedPartsMessage merki = null;

		// Konfiguration
		int richtwert = 400; // Mindestlänge im Text, die betrachtet werden
		// soll.
		int betrachtetSumLaengeFaktor = 1;
		int prozentFaktor = 100;
		float bewertung_neu = 0;
		float bewertung_alt = 0;

		for (MatchingString letztes : this.container) {
			// Auffüllen
			if (this.analyzedParts.size() == 0) {
				start = letztes.start[i];
				bewertung_alt = 0;
			}
			this.analyzedParts.add(letztes);
			matchingStringSumLaenge += letztes.length;
			ende = letztes.end[i];
			betrachtetSumLaenge = ende - start;

			prozent_neu = (matchingStringSumLaenge * 100) / betrachtetSumLaenge;
			bewertung_alt = bewertung_neu;
			bewertung_neu = (float) (betrachtetSumLaenge * betrachtetSumLaengeFaktor)
					* (float) (prozent_neu * prozent_neu);

			// wenn neuer merki besser als alter
			if (true) {
				// was tun?
			}

			// while(bewertung_neu < bewertung_alt) {
			// }
		}
		return merki;
	}

	public int getMatchingStringCount() {
		return this.matchingStringCount;
	}

	public String getHistory() {
		return this.container.history;
	}

	public String toString() {
		return this.container.toString(this.src, this.dst);
	}

	/**
	 * Vorsicht bei Multithreading!!!
	 * @return
	 */
	@Deprecated
	public MatchingStringContainer getContainer() {
		return this.container;
	}
}
