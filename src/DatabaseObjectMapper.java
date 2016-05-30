import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sql2o.Sql2oException;

public abstract class DatabaseObjectMapper {
	private String[] UNIVERSAL_FIELDS = {"name"};
	
	protected List <String> getFields() {
		return new ArrayList<String> (Arrays.asList(UNIVERSAL_FIELDS));
	}
	
	protected boolean checkString(String str, int length) {
		if (str == null || str.length() > length) {
			return false;
		}
		return true;
	}	
	
	public abstract void validate() throws Sql2oException, NoRecordFoundException, InvalidJsonDataException, NoJsonToParseException;
}
