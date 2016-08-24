package YAPLAF.LCSV;

@Deprecated
public class LCSVektorAnalyzedPartsMessage {

	int betrachtetStart;
	int betrachtetEnde;
	int matchingStringSumLaenge;
	int prozent;
	int matchingStringCount;

	public LCSVektorAnalyzedPartsMessage(int start, int ende,
			int matchingStringSumLaenge, int prozent, int count) {
		this.betrachtetStart = start;
		this.betrachtetEnde = ende;
		this.matchingStringSumLaenge = matchingStringSumLaenge;
		this.prozent = prozent;
		this.matchingStringCount = count;
	}

	public String toString() {
		return "Start: " + this.betrachtetStart + " Ende: " + this.betrachtetEnde + " Laenge: " + (this.betrachtetEnde - this.betrachtetStart) + " MatchingStringSum: " + this.matchingStringSumLaenge + " Prozent: " + this.prozent + "% Count: " + this.matchingStringCount;
	}
}
