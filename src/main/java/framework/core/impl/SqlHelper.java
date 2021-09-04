package framework.core.impl;

import framework.core.util.ModelUtils;

import java.util.List;

public class SqlHelper {

    public String buildInsertSql( Object model ){
        StringBuilder sb = new StringBuilder( "INSERT INTO " );
        sb.append( ModelUtils.getInstance().getTableName(model) );
        sb.append(" (");

        List<String> colunas = ModelUtils.getInstance().getColumnNamesWithoutId(model);
        for ( String s: colunas ){
            sb.append(s); sb.append(",");
        }
        sb.deleteCharAt( sb.length()-1 );
        sb.append(") VALUES (");
        for ( int i = 0; i < colunas.size(); i++) {
            sb.append("?,");
        }
        sb.deleteCharAt( sb.length()-1 );
        sb.append(")");

        return sb.toString();
    }

    public String buildDeleteSql(Object model) {
    	StringBuilder sb = new StringBuilder("DELETE FROM ");
    	sb.append( ModelUtils.getInstance().getTableName(model) );
        sb.append(" WHERE ");

        List<String> colunas = ModelUtils.getInstance().getColumnNames(model);
        for ( int i = 0; i < colunas.size(); i++ ){
            if(i == colunas.size() - 1) {
                sb.append(colunas.get(i) + " = ? ");
            } else {
                sb.append(colunas.get(i) + " = ? AND ");
            }
        }
        //int id = ModelUtils.getInstance().getIdValue(model);
        //sb.append(" WHERE id = ");
        //sb.append(id);

    	return sb.toString();
    }
    
    public String buildSelectSql(Class entityClass, Object chavePrimaria) {
    	StringBuilder sb = new StringBuilder("SELECT * FROM ");
        //sb.append(entityClass);
        sb.append(" WHERE id = " + chavePrimaria);

    	return sb.toString();
    }
    
    public String buildUpdateSql(Object model) throws IllegalAccessException {
    	StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append( ModelUtils.getInstance().getTableName(model) );
        sb.append(" SET ");

        List<String> colunas = ModelUtils.getInstance().getColumnNamesWithoutId(model);
        for ( int i = 0; i < colunas.size(); i++ ){
            if(i == colunas.size() - 1) {
                sb.append(colunas.get(i) + " = ? ");
            } else {
                sb.append(colunas.get(i) + " = ?, ");
            }
        }

        int id = ModelUtils.getInstance().getIdValue(model);
        sb.append(" WHERE id = " + id);

    	return sb.toString();
    }
}
