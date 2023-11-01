package co.touchlab.squeaky.field.types;

import android.database.Cursor;
import co.touchlab.squeaky.field.FieldType;
import co.touchlab.squeaky.field.SqlType;

import java.sql.SQLException;

/**
 * Type that persists a Double object.
 *
 * @author graywatson
 */
public class DoubleObjectType extends BaseDataType
{

	private static final DoubleObjectType singleTon = new DoubleObjectType();

	public static DoubleObjectType getSingleton()
	{
		return singleTon;
	}

	private DoubleObjectType()
	{
		super(SqlType.DOUBLE, new Class<?>[]{Double.class});
	}

	protected DoubleObjectType(SqlType sqlType, Class<?>[] classes)
	{
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr)
	{
		return Double.parseDouble(defaultStr);
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, Cursor results, int columnPos) throws SQLException
	{
		return results.getDouble(columnPos);
	}

	@Override
	public boolean isEscapedValue()
	{
		return false;
	}
}
