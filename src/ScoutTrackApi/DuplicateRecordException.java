package ScoutTrackApi;

/**
 * Exception thrown when creation of a duplicate record is attempted.
 * @author Charlie Vorbach
 */
public class DuplicateRecordException extends Exception{
	private static final long serialVersionUID = 3472511973845673220L;
	private String recordType;

	/**
	 * Constructs exception
	 * @param recordType type of duplicate record
	 */
	public DuplicateRecordException(String recordType) {
		super("This " + recordType + " must be unique and is already present in the database.");
		this.recordType = recordType;
	}
	
	public String toString() {
		return "duplicate " + recordType;
	}
}
