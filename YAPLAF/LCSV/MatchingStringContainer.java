package YAPLAF.LCSV;

/**
 * A MatchingStringContainer implements methods to store, compare and manage MatchingString objects
 *
 * @author PPR Group (Martin Weik)
 * @version 0.9.4 (pre relaese, untested)
 *
 * changes 0.9.1
 * - this.temp wird bei einfügeoperation zurückgesetzt
 * changes 0.9.2
 * - Funktionen zum (externen) Durchlaufen der Liste linlist ergänzt: resetPoint, next
 * gedanken für 0.9.3
 * - Wie soll entschieden werden, ob ein ms eingefügt wird?
 *  1. der ms_neu ist länger als jeder überlappende ms_liste
 *  2. der ms_neu ist länger als die summer der überlappenden ms_liste im jeweiligen Text (aktuell)
 *  3. der ms_neu ist länger als alle überlappenden ms_liste zusammen
 *
 * neu in 0.9.4
 * - Sortierfunktion für MatchingStrings in i (mit BubbleSort)
 */

public class MatchingStringContainer implements Iterable<MatchingString> {
	private MatchingString linlist; //Start point of linear list

	private java.util.LinkedList<MatchingString> temp; //LinkedListt for temporary MatchingString storage

	private MatchingString matchingStringElement; //MatchingString that must be inserted
	private MatchingString matchingStringCurrent; //current MatchingString element in the linear list

	private int calculatedOverlapingStringLength[] = new int[2];
	private int calculatedOverlapingStringLengthUnique;

	public String history = "";
	private MatchingString pointer;

	/**
	 * Constructs an emtpy MatchingStringContainer.
	 *
	 */

	public MatchingStringContainer() {
		this.linlist = null;
		this.temp = new java.util.LinkedList<MatchingString>();

		this.calculatedOverlapingStringLength[0] = 0;
		this.calculatedOverlapingStringLength[1] = 0;
	}

	/**
	 * Resets an MatchingStringContainer to it's default state.
	 * This avoids the need to instance a new MatchingStringContainer.
	 *
	 */

	public void reset() {
		this.linlist = null; //Interne Liste zurücksetzen
		//this.temp.clear(); //Temporäre Liste zurücksetzen

		this.calculatedOverlapingStringLength[0] = 0;
		this.calculatedOverlapingStringLength[1] = 0;
		this.calculatedOverlapingStringLengthUnique = 0;

		this.history = "";
	}

	/**
	 * Instances a MatchingString an inserts it in a MatchingStringContainer.
	 *
	 * @param start1 The startpoint for this MatchingString in String 1
	 * @param start2 The startpoint for this MatchingString in String 2
	 * @param length The length of this MatchingString (in both Strings)
	 * @return true if the matchingString is inserted, false if not
	 */
	public boolean insert(int start1, int start2, int length) {
		this.matchingStringElement = new MatchingString(start1, start2, length);
		return this.insert();
	}

	/**
	 * Inserts a given MatchingString in a MatchingStringContainer
	 *
	 * @param matchingStringElement MatchingString to inerst
	 * @return true if the matchingString is inserted, false if not
	 */

	public boolean insert(MatchingString matchingStringElement) {
		this.matchingStringElement = matchingStringElement;
		return this.insert();
	}

	/**
	 * calculates if this.matchingStringElement should be inserted
	 * and if it calls computeInsert() to insert it
	 *
	 * @return true if the matchingString is inserted, false if not
	 */

	private boolean insert() {
		this.temp.clear();
		this.calculateOverlapingStringLength();

		//if this.matchingStringElement is longer than the overlaping parts in String 0 and String 1, it will be inserted

		//test 070606 Methode 2: if(this.calculatedOverlapingStringLength[0] < this.matchingStringElement.length && this.calculatedOverlapingStringLength[1] < this.matchingStringElement.length) {
		//test 070606 Methode 3:
		if(this.matchingStringElement.length > this.calculatedOverlapingStringLengthUnique) {
			this.history += "### MatchingString wird eingefügt ###\r\n" + this.matchingStringElement.toString() + "\r\n";
			this.history += "##### Zu löschende MatchingStrings - BEGINN #####\r\n";
			this.computeInsert();
			this.history += "##### Zu löschende MatchingStrings - ENDE #####\r\n";
			return true;
		}
		else {
			//Nothing to do... this.matchingStringElement will not be inserted
			this.history += "### MatchingString canceled ###\r\n" + this.matchingStringElement.toString() + "\r\n";
			return false;
		}
	}

	/**
	 * Calculates the number of MatchingString objects in this MatchingStringContainer
	 *
	 * @return
	 */

	public int getMatchingStringCount() {
		int count = 0;
		this.matchingStringCurrent = this.linlist;
		while(this.matchingStringCurrent != null) {
			count++;
			this.matchingStringCurrent = this.matchingStringCurrent.next;
		}
		return count;
	}

	/**
	 * Calculates the Length of Strings that overlap with this.matchingStringElement
	 * and inserts overlaping MatchingStrings in LinearList this.temp
	 *
	 */

	private void calculateOverlapingStringLength() {
		this.calculatedOverlapingStringLength[0] = 0; //reset the OverlapingStringLength
		this.calculatedOverlapingStringLength[1] = 0;
		this.calculatedOverlapingStringLengthUnique = 0;

		boolean isOverlaping = false; //if isOverlaping is true, this.matchingStringCurrent will be insertet in this.temp

		//go thru the linear list
		this.matchingStringCurrent = this.linlist; //start with this.linlist
		while(this.matchingStringCurrent != null) { //while an element exists
			if (this.matchingStringCurrent.overlap(this.matchingStringElement, 0)) { //if the MatchingStrings overlap int Text 0
				this.calculatedOverlapingStringLength[0] += this.matchingStringCurrent.length;
				isOverlaping = true; //
			}
			if (this.matchingStringCurrent.overlap(this.matchingStringElement, 1)) { //if the MatchingStrings overlap int Text 1
				this.calculatedOverlapingStringLength[1] += this.matchingStringCurrent.length;
				isOverlaping = true;
			}
			if (isOverlaping) { //if isOverlaping is true, this.matchingStringCurrent will be insertet in this.temp
				this.temp.add(this.matchingStringCurrent);
				this.calculatedOverlapingStringLengthUnique += this.matchingStringCurrent.length;
				isOverlaping = false; //reset isOverlaping
			}
			this.matchingStringCurrent = this.matchingStringCurrent.next; //go to next element
		}
		return;
	}

	/**
	 * Sets the pointers in MatchingStrings to
	 * delete every element in temp from and
	 * insert this.matchingStrinElement to the this.linlist	 *
	 */

	private void computeInsert() {
		//delet every element in this.temp
		while(!this.temp.isEmpty()) { //unbind every element in list this.temp
			this.matchingStringCurrent = this.temp.poll(); //get element from this.temp
			if(this.matchingStringCurrent == this.linlist) { //if this.matchingStringCurrent ist the head of the list
				this.linlist = this.matchingStringCurrent.next; //it needs special treatment
			}
			this.history += "### MatchingString geloescht ###\r\n" + this.matchingStringCurrent.toString();
			this.matchingStringCurrent.unbind(); //unbind element...
		}
		//insert this.matchingStringElement
		if(this.linlist != null) { //to avoid nullPointerExceptions
			this.matchingStringElement.next = this.linlist;
			this.linlist.prev = this.matchingStringElement;
		}
		this.linlist = this.matchingStringElement;
	}

	/**
	 * Calculates the sum of all MatchingStrings in this MatchingStringContainer
	 *
	 * @return the sum of all MatchingString in this MatchingStringContainer
	 */

	public int getMatchingStringSum() {
		int sum = 0;

		//for every elemnt in this.linlist...
		this.matchingStringCurrent 	= this.linlist;
		while(this.matchingStringCurrent != null) { //if element is not NULL
			sum += this.matchingStringCurrent.length; //add it...
			this.matchingStringCurrent = this.matchingStringCurrent.next; //go to next element
		}
		return sum;
	}

	public String toString() {
		String output = "";
		int count = 0;
		MatchingString temp = this.linlist;
		while(temp != null) {
			output += "### MatchingString " + count++ + " ###\r\n";
			output += temp.start[0] + " " + temp.start[1] + " " + temp.length + "\r\n";

			temp = temp.next;
		}

		//output += "\r\n### History output ###\r\n" + this.history;
		return output;
	}

	public String toString(String text0, String text1) {
		String output = "";
		int count = 0;
		MatchingString temp = this.linlist;
		while(temp != null) {
			output += "### MatchingString " + count++ + " ###\r\n";
			output += temp.start[0] + " " + temp.start[1] + " " + temp.length + "\r\n";
			output += "#Text1: \r\n";
			output += text0.substring(temp.start[0], temp.end[0]) + "\r\n";
			output += "#Text2: \r\n";
			output += text1.substring(temp.start[1], temp.end[1]) + "\r\n";

			temp = temp.next;
		}

		//output += "\r\n### History output ###\r\n" + this.history;
		return output;
	}

	public void resetPointer() {
		this.pointer = this.linlist;
	}

	public boolean hasNextMatchingString() {
		if(this.pointer != null) return true;
		return false;
	}

	public MatchingString nextMatchingString() {
		MatchingString temp = this.pointer;
		this.pointer = this.pointer.next;
		return temp;
	}

	public void sort(int i, boolean x) {
		MatchingString lastsort = this.linlist;
//		MatchingString prev = null;
		MatchingString akt = null;

		/*
		 * Ablauf Bubblesort
		 *
		 * mit erstem Element starten -> lastsort
		 * lastsort: das letzte sortierte Element merken (letztes bekanntes element mit aktuellem Element vergleich und eventuell Zeiger anpassen)
		 * wenn lastsort.next != null -> akt = lastsort.next
		 * akt an richtige stelle heraufblubbern
		 * - vergleichen
		 * - zeiger anpassen
		 * - wenn akt an erste stelle gehört (akt.prev == null) -> linlist anpassen
		 * wenn lastsort.next == akt -> lastsort = akt
		 *
		 */

		//Nach größeren Elementen suchen
		int count = 0;
		while(lastsort.next != null) {
			akt = lastsort.next;
			//heraufblubbern, bis es an der richtigen stelle ist
			//if(akt.prev != null) System.out.print(" ungleich null");
			//else System.out.print("GLEICH null");

			//if(!akt.after(akt.prev, i)) System.out.println("Blubbern...");
			//else System.out.print("was los?");

			while(akt.prev != null && !akt.after(akt.prev, i) && count < 5) { //true, akt hinter akt.prev liegt - also richtig
				count++;
				//System.out.println("blubbern");
				//es muss geblubbert werden...
				if(akt.prev.prev == null) { //wir sind am anfang der liste
					//startpunkt soll auf akt zeigen
					this.linlist = akt;
				}
				else {
					//nichts besonderes zu tun
				}


			}
			//sortiervorgang abgeschlossen
			//lastsort aktualisieren
			if(lastsort.next == akt) {
				lastsort = akt;
			}
		}
	}

	public void sort(int i) {
		MatchingString akt = this.linlist;
		MatchingString next;

		while(akt != null) {
			//System.out.println("akt: " + akt.start[0]);
			next = akt.next;
			while((akt.prev != null) && !akt.after(akt.prev, i)) {
				akt.moveUp();
			}
			if(akt.prev == null) {
				this.linlist = akt;
			}
			akt = next;

		}
		/*int count = 0;
		while(akt.prev != null && count++ < 5) {
			System.out.println("Schleife 2");
			System.out.println("### Beginn ###");
			System.out.println("davor: " + akt.prev.start[i]);
			if(akt.next != null) System.out.println("danach: " + akt.next.start[i]);
			System.out.println("### Ende ###");
			if(akt == akt.prev) System.out.println("RING!");
			akt.moveUp();
			if(akt.prev == null) {
				this.linlist = akt;
			}
		}
		*/

	}

	public void sorttest() {
		int i = 0;

		MatchingString akt = this.linlist;

		for(int y = 0; y < 2; y++) {
			akt = akt.next;
		}
		System.out.println(akt.start[i]);
		for(int z = 0; z < 1; z++) {
			akt.moveUp();
		}
	}

	public int analyze(int interval, int i) {
		return 0;
	}

	public java.util.Iterator<MatchingString> iterator() {
		return new java.util.Iterator<MatchingString>() {
			MatchingString pointer = MatchingStringContainer.this.linlist;

			public boolean hasNext() {
				if(this.pointer != null) return true;
				return false;
			}

			public void remove() {
//				nicht implementiert
			}

			public MatchingString next() {
				MatchingString temp = this.pointer;
				this.pointer = this.pointer.next;
				return temp;
			}


		};
	}

}