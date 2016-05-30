import org.sql2o.Sql2oException;

public interface DatabaseObject {
	String queryName() throws Sql2oException, NoRecordFoundException;
	void updateName(String name) throws Sql2oException;
}
