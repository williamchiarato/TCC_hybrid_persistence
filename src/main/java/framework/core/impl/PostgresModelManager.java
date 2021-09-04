package framework.core.impl;

import framework.core.ModelManager;
import framework.core.util.ConnectionUtils;
import framework.core.util.ModelUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class PostgresModelManager implements ModelManager {

    @Override
    public void inserir(Object model) {

        Connection connection = ConnectionUtils.getInstance().getConnection();

        int id = -1;

        try {
            String sql = new SqlHelper().buildInsertSql(model);
            PreparedStatement ps = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );

            ModelUtils.getInstance().prepara(ps, ModelUtils.getInstance().getColumnValuesWithoutId( model ));
            ps.executeUpdate();
            ResultSet results = ps.getGeneratedKeys();
            if ( results != null && results.next() ) {
                id = results.getInt(1);

                //seta o id
                ModelUtils.getInstance().setIdValue(model, id);
            }
        }
        catch ( Exception ex ) {
            ex.printStackTrace();
        }
        finally {
            ConnectionUtils.getInstance().closeSilenlty(connection);
        }
    }

    @Override
    public void atualizar(Object model) {

        Connection connection = ConnectionUtils.getInstance().getConnection();

        try {
            String sql = new SqlHelper().buildUpdateSql(model);
            PreparedStatement ps = connection.prepareStatement(sql);
            System.out.println(sql);
            ModelUtils.getInstance().prepara(ps, ModelUtils.getInstance().getColumnValuesWithoutId( model ));
            ps.executeUpdate();

        } catch (Exception e ) {
            e.printStackTrace();

        } finally {
            ConnectionUtils.getInstance().closeSilenlty(connection);
        }
    }

    @Override
    public void remover(Object model) {
    	
    	Connection connection = ConnectionUtils.getInstance().getConnection();
    	
    	try {
            String sql = new SqlHelper().buildDeleteSql(model);
            PreparedStatement ps = connection.prepareStatement(sql);

            ModelUtils.getInstance().prepara(ps, ModelUtils.getInstance().getColumnValues( model ));
            ps.executeUpdate();
            
    	} catch (Exception e ) {
    		e.printStackTrace();
    		
    	} finally {
    		ConnectionUtils.getInstance().closeSilenlty(connection);
    	}
    }

    @Override
    public void consultar(Class entityClass, Object chavePrimaria) {

        Connection connection = ConnectionUtils.getInstance().getConnection();

        try {
            String sql = new SqlHelper().buildSelectSql(entityClass, chavePrimaria);
            PreparedStatement ps = connection.prepareStatement(sql);

            System.out.println(sql);
            //ModelUtils.getInstance().getColumnValues(entityClass);
            //ps.executeUpdate();

        } catch (Exception e ) {
            e.printStackTrace();

        } finally {
            ConnectionUtils.getInstance().closeSilenlty(connection);
        }
    }

}
