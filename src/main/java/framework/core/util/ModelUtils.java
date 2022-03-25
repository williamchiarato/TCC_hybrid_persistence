package framework.core.util;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class ModelUtils {

    public static ModelUtils _INSTANCE = new ModelUtils();

    public static ModelUtils getInstance(){
        return _INSTANCE;
    }

    private ModelUtils(){};

    public String getTableName( Object o ) {
        Annotation a = o.getClass().getAnnotation(Table.class);

        if ( a == null ) {
            //annotation eh obrigatoria: lançar excecao
            return null;
        }
        else{
            Table table = (Table)a;
            return( table.name() );
        }
    }

    public List<String> getColumnNames(Object o, boolean includeId ) {
        Field[] campos = o.getClass().getDeclaredFields();
        List<String> colunas = new ArrayList<>();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) && !includeId ) {
                continue;
            }
            else if ( f.isAnnotationPresent( Column.class ) ) {
                Column coluna = f.getAnnotation( Column.class );
                colunas.add(  coluna.name() );
            }
        }
        return colunas;
    }

    public Map<String,Object> getFields(Object o, boolean includeId) throws IllegalAccessException {
        Field[] campos = o.getClass().getDeclaredFields();
        Map<String,Object> mapa = new HashMap<>();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) && !includeId ) {
                continue;
            }
            else if ( f.isAnnotationPresent( Column.class ) ) {
                Column coluna = f.getAnnotation( Column.class );
                f.setAccessible(true);
                mapa.put(  coluna.name(), f.get(o) );
            }
        }
        return mapa;
    }

    public List getColumnValues( Object o, boolean includeId ) throws IllegalAccessException {
        Field [] campos = o.getClass().getDeclaredFields();
        List atributos = new ArrayList();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) && !includeId ) {
                continue;
            }
            else if ( f.isAnnotationPresent( Column.class ) ) {
                f.setAccessible(true);
                atributos.add( f.get( o ) );
            }
        }
        return atributos;
    }

    public void prepara(PreparedStatement ps, List atributos ) throws SQLException {
        int cont = 1;
        for ( Object o: atributos ) {
            if ( o instanceof Integer ) {
                ps.setInt(cont++, (Integer)o );
            }
            else if ( o instanceof Double ) {
                ps.setDouble(cont++, (Double)o );
            }
            else if ( o instanceof String ) {
                ps.setString(cont++, (String)o );
            }
            else if ( o instanceof GregorianCalendar) {
                ps.setDate(cont++, new java.sql.Date ( ((GregorianCalendar)o ).getTimeInMillis() )  );
            }
            else if ( o instanceof java.util.Date ) {
                ps.setTimestamp(cont++, new java.sql.Timestamp ( ((java.util.Date)o ).getTime() )  );
            }
            else if ( o instanceof Character ) {
                ps.setString(cont++, o.toString() );
            }
            else if (o instanceof Boolean) {
                boolean v = ((Boolean)o).booleanValue();
                if (v)
                    ps.setInt(cont++, 1);
                else
                    ps.setInt(cont++, 0);
            }
            else if (o == null) {
                ps.setNull(cont++, java.sql.Types.INTEGER);
            }
            else if ( hasIdAnnotation(o) ) {
                try {
                    ps.setInt(cont++, getIdValue(o) );
                }
                catch ( Exception ex ) {
                    ex.printStackTrace();
                    throw new SQLException("Não foi possível pegar o Id");
                }
            }
            else {
                ps.setNull(cont++, java.sql.Types.INTEGER);
            }
        }
    }

    public boolean hasIdAnnotation(Object o ) {
        Field [] campos = o.getClass().getDeclaredFields();
        List atributos = new ArrayList();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) ) {
                return true;
            }
        }
        return false;
    }

    public Class getIdAnnotationType(Object o ) throws IllegalAccessException {
        Field [] campos = o.getClass().getDeclaredFields();
        List atributos = new ArrayList();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) ) {
                return f.getType();
            }
        }
        return null;
    }

    public boolean isIdAnnotationInteger(Object o) throws IllegalAccessException {
        Class idClass = this.getIdAnnotationType(o);
        return idClass.equals(Integer.class);
    }

    public int getIdValue( Object o ) throws IllegalAccessException {
        Field [] campos = o.getClass().getDeclaredFields();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) ) {
                f.setAccessible(true);
                return Integer.parseInt( f.get( o ).toString() );
            }
        }
        return -1;
    }

    public String getObjectId( Object o ) throws IllegalAccessException {
        Field [] campos = o.getClass().getDeclaredFields();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) ) {
                f.setAccessible(true);
                return f.get( o ).toString();
            }
        }
        return null;
    }

    public void setIdValue( Object model, Object id ) throws IllegalAccessException {
        Field [] campos = model.getClass().getDeclaredFields();
        for ( Field f : campos ) {
            if ( f.isAnnotationPresent( Id.class ) ) {
                f.setAccessible(true);
                f.set(model, id);
            }
        }
    }

}
