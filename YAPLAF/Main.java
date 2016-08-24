package YAPLAF;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import YAPLAF.util.*;
import YAPLAF.LCSV.*;

public class Main {
	/**
	 * Ablauf:
	 * <ol>
	 * <li>Konfigurationsparameter einlesen (DefaultProperties + YAPLAF.conf)</li>
	 * <li>Ausgabe in Datei mitprotokollieren</li>
	 * <li>Abgaben einlesen mit TuwelSaxHelper: gibt Liste mit Abgaben zurueck</li>
	 * <li>LCSVektor initialisieren und "Schranke" setzen</li>
	 * <li>Ausgabe in CSV starten + Ab welchem Prozentsatz soll Speicherung
	 * erfolgen?</li>
	 * <li>Variablen fuer Dreiecksschleife deklarieren</li>
	 * <li>"Dreieckschleife"</li>
	 * <ul>
	 * <li> Fuer jedes eindeutige Paar Abgaben wird ein Durchlauf mit LCSVektor
	 * gestartet</li>
	 * <li>Die Summe der Uebereinstimmungen steht in Variable "result"</li>
	 * <li>Wenn Prozent Uebereinstimmung ueber Schwellwert, Ausgabe in CSV</li>
	 * </ul>
	 * <li>Offene Filehandles schliessen und auf Beendigung warten</li>
	 * </ol>
	 *
	 * @param args
	 *            werden nicht beruecksichtigt
	 */
	public static void main(String args[]) {

		// Einstellungen laden
		// Defaultproperties laden
		java.util.Properties prop = new java.util.Properties(
				new DefaultProperties());
		// Konfigurationsdatei laden
		String configfn = "YAPLAF.conf";
		try {
			prop.load(new FileInputStream(configfn));
		} catch (IOException e) {
			System.out.println("Fehler beim Einlesen der Datei " + configfn
					+ ".");
			System.out.println("Verwende Default Werte...");
		}

		// Ausgabe in Datei "umbiegen"
		LogWriter out = new LogWriter();
		out.setLogFile(prop.getProperty("LogWriterFilename"));
		out.open();

		// Datei mit Abgaben oeffnen
		File f = new File(prop.getProperty("YAPLAF_XML_filename"));
		if (f.canRead()) {
			out.println("Datei " + prop.getProperty("YAPLAF_XML_filename")
					+ " erfolgreich geöffnet");
		} else {
			out.println("Datei " + prop.getProperty("YAPLAF_XML_filename")
					+ " konnte nicht geöffnet werden");
			System.exit(10);
		}

		// XML Handle laden
		TuwelSaxHelper handler = new TuwelSaxHelper();
		// XML Parser initialisieren und starten
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(f, handler);
		} catch (Throwable t) {
			out.println(t);
		}

		// Liste mit Abgaben abholen...
		LinkedList<Abgabe> abgaben = handler.getAbgaben();

		out.println("Es existieren " + abgaben.size() + " Abgaben.");
		// calc <= Rechteck bilden - Diagonale; davon die Haelfte
		int calc = ((abgaben.size() * abgaben.size()) - abgaben.size()) / 2;
		out.println("Es werden " + calc + " Vergleiche durchgeführt.");

		// LCSVektor initialiseren
		// lcsschranke gibt die MindestLaenge eines MatchingString (Common
		// Substring) an, der aufgenommen werden soll
		LCSVektor lcs = new LCSVektor();
		int lcsschranke = 10;
		try {
			lcsschranke = Integer.parseInt(prop
					.getProperty("LCSVektor_minStringlaenge"));
		} catch (NumberFormatException e) {
			// ...
		}
		out.println("Setze LCSSchranke auf " + lcsschranke);
		lcs.setSchranke(lcsschranke);

		/*
		 * CSV Ausgabe
		 */
		// Kopfzeile fuer CSV Ausgabe erstellen
		String[] headline = { "Matrikel#1", "Vorname1", "Nachname1", "Laenge1",
				"Abgabezeit1", "LCS = % von Text1", "Matrikel#2",
				"Vorname2", "Nachname2", "Laenge2", "Abgabezeit2",
				"LCS = % von Text2", "LCS", "Anzahl MatchingStrings" };

		// Ausgabe in CSV initialisieren (Dateinamenschablone, Kopfzeile,
		// Zeilenzahl pro Datei)
		CSVWriter writi = new CSVWriter(prop.getProperty("CSVWriter_filename"),
				headline, new Integer(prop
						.getProperty("CSVWriter_Zeilen_Datei")).intValue());

		// Ab welchem Verhaeltnis MatchingStrings / Textlaenge soll Alarm
		// geschlagen werden?
		int prozentSchwelle = 0;
		try {
			prozentSchwelle = Integer.parseInt(prop
					.getProperty("YAPLAF_Prozent_Exportschwelle"));
		} catch (NumberFormatException e) {
			out.println("Fehler!  "
					+ prop.getProperty("YAPLAF_Prozent_Exportschwelle")
					+ " ist keine ganze Zahl.");
			out.println("Bitte Konfigurationsdatei " + configfn
					+ " überprüfen.");
			out.println(e);
			System.exit(10);
		}

		/*
		 * Diese Variablen werden erst in der Dreiecks-Schleife benötigt aber
		 * schon hier instanziert
		 */
		// Laenge der Uebereinstimmungen zweier Texte
		int result = 0;
		// aktZeile & zeilenZaehler werden fuer Formeln im CSV benoetigt
		int aktZeile;
		int zeilenZaehler = 0;
		// Variablen zur Abschaetzung der Dauer
		long now, ende, init = new Date().getTime();
		int schnitt;
		DateFormat format = DateFormat.getTimeInstance(DateFormat.LONG);

		out.println("aktuelle Zeit: " + format.format(new Date()));

		// Variablen fuer LCSVektor
		int[] laenge = new int[2];
		int[] prozentLaenge = new int[2];
		int[] matrikelnr = new int[2];
		String[] vorname = new String[2];
		String[] nachname = new String[2];
		String[] abgabezeit = new String[2];
		// zur Bestimmung der Reihenfolge in der Ausgabe
		int x;
		int y;

		// TODO Sinnhaftigkeit von over100 ueberpruefen
		// Ausgabe fuer Texte, die ueber 100% Uebereinstimmung haben (was nicht
		// sein darf!!!)
		LogWriter over100 = new LogWriter();
		over100.setLogFile("over100.txt");
		over100.open();

		// Fuer jede Kombination von zwei Abgaben...
		for (int i = 0; i < abgaben.size(); i++) {
			// "Quelltext" übergeben
			lcs.setSrc(abgaben.get(i).getAbgabeText());
			for (int j = i + 1; j < abgaben.size(); j++) {
				// "Zieltext übergeben
				lcs.setDst(abgaben.get(j).getAbgabeText());

				// Werte fuer spaetere Ausgabe merken
				laenge[0] = abgaben.get(i).getAbgabeTextlaenge();
				laenge[1] = abgaben.get(j).getAbgabeTextlaenge();

				matrikelnr[0] = abgaben.get(i).getMatrikelnr();
				matrikelnr[1] = abgaben.get(j).getMatrikelnr();

				vorname[0] = abgaben.get(i).getVorname();
				vorname[1] = abgaben.get(j).getVorname();

				nachname[0] = abgaben.get(i).getNachname();
				nachname[1] = abgaben.get(j).getNachname();

				abgabezeit[0] = abgaben.get(i).getAbgabezeit();
				abgabezeit[1] = abgaben.get(j).getAbgabezeit();

				try {
					result = lcs.start();
				} catch (Exception e) {
					out
							.println("Ein Fehler ist bei der Ausführung des Algorithmus aufgetreten. Die Stelle ist nicht bekannt...");
					out.println(e);
					System.exit(1);
				}

				// gesammelte Daten auswerten
				prozentLaenge[0] = 0;
				try {
					prozentLaenge[0] = result * 100 / laenge[0];
				} catch (Exception e) {
				}

				prozentLaenge[1] = 0;
				try {
					prozentLaenge[1] = result * 100 / laenge[1];
				} catch (Exception e) {
				}

				// x > y
				if (prozentLaenge[0] > prozentLaenge[1]) {
					x = 0;
					y = 1;
				} else {
					x = 1;
					y = 0;
				}

				if (prozentLaenge[x] >= prozentSchwelle) {
					zeilenZaehler++; // Info fuer Formel in CSV!!!
					aktZeile = zeilenZaehler + 1; // Info fuer Formel in
					// CSV!!!

					// Stringarray mit Werten fuer Ausgabe erstellen
					String[] toWrite = {
							new Integer(matrikelnr[x]).toString(),
							vorname[x],
							nachname[x],
							new Integer(laenge[x]).toString(),
							abgabezeit[x],
							"=M" + aktZeile + "/D" + aktZeile,
							
							new Integer(matrikelnr[y]).toString(),
							vorname[y],
							nachname[y],
							new Integer(laenge[y]).toString(),
							abgabezeit[y],
							"=M" + aktZeile + "/J" + aktZeile,
							
							new Integer(result).toString(),
							new Integer(lcs.getMatchingStringCount())
									.toString() };

					writi.write(toWrite);
				}

				// TODO Sinnhaftigkeit --- nein
				// Wird wahrscheinlich nicht benoetigt
				/*
				 * if (prozentLaenge[x] > 100) { // Über 100 Prozent... Sachen
				 * in over100 ausgeben over100.println("######## über 100%
				 * ##########"); over100.println(); over100.println("###Text1");
				 * over100.println(abgaben.get(i).getAbgabeText());
				 * over100.println(); over100.println("###Text2");
				 * over100.println(abgaben.get(j).getAbgabeText());
				 * over100.println(); over100.println("###MatchingStrings");
				 * over100.println(lcs.toString()); over100.println(); }
				 */

				// Informative Ausgabe...
				out.println();
				out.println("Durchlauf " + lcs.getCount() + " von " + calc);
				out.print("Vergleiche " + matrikelnr[0] + " mit "
						+ matrikelnr[1] + ": " + result);
				out.println(" Länge1: " + laenge[0] + " (" + prozentLaenge[0]
						+ "%)  Länge2: " + laenge[1] + " (" + prozentLaenge[1]
						+ "%)");
				now = new Date().getTime();
				schnitt = (int) ((now - init) / lcs.getCount());
				out.println("Durchschnittliche Laufzeit: " + schnitt + "ms");
				ende = init + (schnitt * calc);
				out.println("Ende voraussichtlich um "
						+ format.format(new Date(ende)));
			}
		}
		writi.close();

		try {
			over100.closeThis();
			out.closeThis();
		} catch (Exception e) {
		}

		System.out.println();
		System.out.println("Bitte mit Eingabe beenden...");

		try {
			System.in.read();
		} catch (Exception e) {
		}
	}
}
