package YAPLAF.util;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
/**
 * Klasse zum Einlesen von XML Dateien aus TUWEL Backup
 *
 * Wird als SAX Handler verwendet und gibt eine LinkedList mit Abgaben zurueck.
 * @author Martin Weik
 *
 */
public class TuwelSaxHelper extends DefaultHandler {
	private boolean isUser = false;
	private boolean isMatrikelNr = false;
	private boolean isStudentdata = false;
	private boolean isVorname = false;
	private boolean isNachname = false;
	private boolean isAbgabezeit = false;

	private String userStr;
	private String matrikelStr;
	private String dataStr;
	private String vornameStr;
	private String nachnameStr;
	private String abgabezeitStr;

	private int count;
	private Abgabe temp;
	private LinkedList<Abgabe> abg;

	private String puffer;

	public TuwelSaxHelper() {
		this.count = 0;
		this.abg = new LinkedList<Abgabe>();


		this.userStr = "USER";
		this.matrikelStr = "MATRIKELNR";
		this.dataStr = "STUDENTDATA";
		this.vornameStr = "FIRSTNAME";
		this.nachnameStr = "LASTNAME";
		this.abgabezeitStr = "STUDENTMODIFIED";


		/*
		this.userStr = "SUBMISSION";
		this.matrikelStr = "USERID";
		this.dataStr = "DATA1";
		this.vornameStr = "FIRSTNAME";
		this.nachnameStr = "LASTNAME";
		*/
	}

	public TuwelSaxHelper(String userStr, String matrikelStr, String dataStr, String vornameStr, String nachnameStr, String abgabezeitStr) {
		this();
		this.userStr = userStr;
		this.matrikelStr = matrikelStr;
		this.dataStr = dataStr;
		this.vornameStr = vornameStr;
		this.nachnameStr = nachnameStr;
		this.abgabezeitStr = abgabezeitStr;
	}
	public int getCount() {
		return this.count;
	}
	public LinkedList<Abgabe> getAbgaben() {
		return this.abg;
	}

	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts) throws SAXException {
		if(!this.isUser) {
			if (qualifiedName.equals(this.userStr)) {
				this.count++;
				this.isUser = true;
				this.temp = new Abgabe();
				//System.out.println("Benutzer start");
			}
		}
		else if(qualifiedName.equals(this.vornameStr)) {
			this.isVorname = true;
		}
		else if(qualifiedName.equals(this.nachnameStr)) {
			this.isNachname = true;
		}
		else if(qualifiedName.equals(this.matrikelStr)) {
			this.isMatrikelNr = true;
		}
		else if(qualifiedName.equals(this.dataStr)) {
			this.isStudentdata = true;
			puffer = "";
		}
		else if(qualifiedName.equals(this.abgabezeitStr)) {
			this.isAbgabezeit = true;
		}
	}

	public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if(this.isUser) {
			if (qualifiedName.equals(this.userStr)) {
				this.isUser = false;
				abg.add(temp);
				//System.out.println("Benutzer ende");
			}
			else if(qualifiedName.equals(this.vornameStr)) {
				this.isVorname = false;
			}
			else if(qualifiedName.equals(this.nachnameStr)) {
				this.isNachname = false;
			}
			else if(qualifiedName.equals(this.matrikelStr)) {
				this.isMatrikelNr = false;
			}
			else if(qualifiedName.equals(this.dataStr)) {
				temp.setStudentdata(this.puffer);
				this.isStudentdata = false;
			}
			else if(qualifiedName.equals(this.abgabezeitStr)) {
				this.isAbgabezeit = false;
			}
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if(this.isUser) {
			if(this.isMatrikelNr) {
				String tempString = new String(ch, start, length);
				try {
					temp.setMatrikelnr(new Integer(tempString));
				}
				catch (Exception e) {
					System.out.println("HAHA");
					temp.setMatrikelnr(0);
				}
			}
			else if(this.isVorname) {
				temp.setVorname(new String(ch, start, length));
			}
			else if (this.isNachname) {
				temp.setNachname(new String(ch, start, length));
			}
			else if(this.isStudentdata) {
				this.puffer += new String(ch, start, length);
			}
			else if(this.isAbgabezeit) {
				temp.setAbgabezeit(new String(ch, start, length));
			}
		}
	}
}
