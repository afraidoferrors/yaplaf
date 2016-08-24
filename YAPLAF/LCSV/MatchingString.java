package YAPLAF.LCSV;
/**
 * A MatchingString represents an identical character String in two Strings.
 * E.g. the MatchingString(5, 2, 4) represents the "mann" within the Strings "hallomann" and "ohmann".
 *
 * @author PPR Group (Martin Weik)
 * @version 0.9.1 (prerelease, untested)
 *
 * changes 0.9.1
 * - overlap um = Zeichen ergänzt
 *
 *
 */

public class MatchingString {
	MatchingString next; //Next element in linear List
	MatchingString prev; //Previous element in linear list
	boolean active; //if false, next an prev do not refer to this object

	public int start[] = new int[2]; //
	public int end[] = new int[2];
	public int length;

	public static final int BEFORE = 1;
	public static final int OVERLAP = 2;
	public static final int AFTER = 3;

	public static final int ERROR = 0;

	/**
	 * Constructor for a MatchingString
	 *
	 * It computes the endpoints itself
	 *
	 * @param start1 The startpoint for this MatchingString in String 1
	 * @param start2 The startpoint for this MatchingString in String 2
	 * @param length The length of this MatchingString (in both Strings)
	 */

	public MatchingString(int start1, int start2, int length) {
		assert start1 >= 0: "start1 ist kleiner 0 (Grund liegt außerhalb des Konstruktors!!!)";
		this.start[0] = start1;

		assert start2 >= 0: "start2 ist kleiner 0 (Grund liegt außerhalb des Konstruktors!!!)";
		this.start[1] = start2;

		assert length > 0: "length ist nicht größer 0 (Grund liegt außerhalb des Konstruktors!!!)";
		this.length = length;

		this.end[0] = this.start[0] + this.length; //calculate end0
		this.end[1] = this.start[1] + this.length; //calculate end1

		// this object is not part of a linear list
		this.next = null;
		this.prev = null;

		this.active = true; //this object is set to active
	}

	/**
	 * Calculates if two MatchingStrings are overlaping in a specific String (0 or 1)
	 *
	 * @param rival the concurrent MatchingString
	 * @param i specifices the String (0 or 1) to check
	 * @return true if the two MatchingStrings are overlaping
	 */

	public boolean overlap(MatchingString rival, int i) {
		if(rival.end[i] > this.start[i] && rival.end[i] <= this.end[i]) {
			return true;
		}
		if(rival.start[i] >= this.start[i] && rival.start[i] < this.end[i]) {
			return true;
		}
		if(rival.start[i] <= this.start[i] && rival.end[i] >= this.end[i]) {
			return true;
		}
		return false;
	}

	/**
	 * calculates the position of the given MatchingString compared to this
	 *
	 * @param rival the concurrent MatchingString
	 * @param i specifices the String (0 or 1) to check
	 * @return the constants BEFORE, AFTER of OVERLAP are the return values
	 */

	public int position(MatchingString rival, int i) {
		if(rival.end[i] < this.start[i])
			return MatchingString.BEFORE; //rival is before this
		else if(rival.end[i] > this.start[i] && rival.end[i] < this.end[i])
			return MatchingString.OVERLAP;
		else if(rival.start[i] > this.start[i] && rival.start[i] < this.end[i])
			return MatchingString.OVERLAP;
		else if(rival.start[i] > this.end[i])
			return MatchingString.AFTER; //rival is after this
		return MatchingString.ERROR;
	}

	/**
	 * Unbinds itself from Linear List and sets active to false
	 */

	void unbind() {
		if(this.active) {
			if (this.prev != null) this.prev.next = this.next;
			if (this.next != null) this.next.prev = this.prev;
			this.active = false;
		}
	}

	public void print(int i) {
		System.out.println("Start: " + this.start[i] + " Ende: " + this.end[i]);
	}

	public String toString() {
		String output = this.start[0] + " " + this.start[1] + " " + this.length;
		return output;
	}
	public int getLength() {
		return this.length;
	}

	public boolean after(MatchingString temp, int i) {
		//System.out.print(" Vergleiche " + this.start[i] + " mit " + temp.start[i]);
		if(this.start[i] > temp.start[i]) {
			return true;
		}
		else {
			return false;
		}
	}

	public MatchingString clone() {
		return new MatchingString(this.start[0], this.start[1], this.length);
	}

	public boolean moveUp() {
		//richtung kopf der liste wandern

		/* mögliche Fälle, die beachtet werden müssen
		 *
		 * this == erstes element -> keine bewegung -> null
		 * this == 2. element -> wird 1. element -> this zurückgeben
		 * this == mittendrinn -> blubbern -> null
		 * this == vorletztes element -> blubbern -> null
		 * this == letztes element -> wird vorletztes element -> ???
		 */

		MatchingString prev = this.prev;
		if(prev.prev != null) prev.prev.next = this;
		if(this.next != null) this.next.prev = prev;
		prev.next = this.next;

		this.prev = prev.prev;
		this.next = prev;
		prev.prev = this;

		/*
		prev.next = this.next;
		this.next = prev;

		this.prev = prev.prev;
		prev.prev = this;
		*/



		return true;
	}

	public boolean moveDown() {
		return true;
	}
}
