package co.touchlab.squeaky.field.types;

import android.database.Cursor;
import co.touchlab.squeaky.field.FieldType;
import co.touchlab.squeaky.field.SqlType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Persists an Enum Java class as its ordinal integer value. You can also specify the {@link EnumStringType} as the
 * type.
 *
 * @author graywatson
 */
public class EnumIntegerType extends BaseEnumType
{

	private static final EnumIntegerType singleTon = new EnumIntegerType();

	public static EnumIntegerType getSingleton()
	{
		return singleTon;
	}

	private EnumIntegerType()
	{
		super(SqlType.INTEGER);
	}

	/**
	 * Here for others to subclass.
	 */
	protected EnumIntegerType(SqlType sqlType, Class<?>[] classes)
	{
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr)
	{
		return Integer.parseInt(defaultStr);
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, Cursor results, int columnPos) throws SQLException
	{
		return results.getInt(columnPos);
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException
	{
		if (fieldType == null)
		{
			return sqlArg;
		}
		// do this once
		Integer valInteger = (Integer) sqlArg;
		@SuppressWarnings("unchecked")
		Map<Integer, Enum<?>> enumIntMap = (Map<Integer, Enum<?>>) fieldType.getDataTypeConfigObj();
		if (enumIntMap == null)
		{
			return enumVal(fieldType, valInteger, null);
		}
		else
		{
			return enumVal(fieldType, valInteger, enumIntMap.get(valInteger));
		}
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object obj)
	{
		Enum<?> enumVal = (Enum<?>) obj;
		return (Integer) enumVal.ordinal();
	}

	@Override
	public boolean isEscapedValue()
	{
		return false;
	}

	@Override
	public Object makeConfigObject(FieldType fieldType) throws SQLException
	{
		Map<Integer, Enum<?>> enumIntMap = new HashMap<Integer, Enum<?>>();
		Enum<?>[] constants = (Enum<?>[]) fieldType.getFieldType().getEnumConstants();
		if (constants == null)
		{
			throw new SQLException("Field " + fieldType + " improperly configured as type " + this);
		}
		for (Enum<?> enumVal : constants)
		{
			enumIntMap.put(enumVal.ordinal(), enumVal);
		}
		return enumIntMap;
	}
}
