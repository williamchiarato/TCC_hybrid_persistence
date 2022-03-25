package framework.core.impl;

import framework.core.ModelManager;
import framework.core.util.ConnectionUtils;
import framework.core.util.ModelUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class PostgresModelManager implements ModelManager {

    @Override
    public void inserir(Object model) {

        Connection connection = ConnectionUtils.getInstance().getConnection();

        int id = -1;

        try {
            boolean isIdInteger = ModelUtils.getInstance().isIdAnnotationInteger(model);
            boolean includeId = !isIdInteger;

            //caso seja id em string, gera um uuid aqui
            if (!isIdInteger) {
                ModelUtils.getInstance().setIdValue(model, UUID.randomUUID().toString());
            }

            String sql = new SqlHelper().buildInsertSql(model, includeId);
            PreparedStatement ps = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );

            ModelUtils.getInstance().prepara(ps, ModelUtils.getInstance().getColumnValues( model, includeId ));
            ps.executeUpdate();

            //caso o id seja em integer ( gerado pelo banco com coluna serial (postgres) )
            if (isIdInteger) {
                ResultSet results = ps.getGeneratedKeys();
                if (results != null && results.next()) {
                    id = results.getInt(1);

                    //seta o id
                    ModelUtils.getInstance().setIdValue(model, id);
                }
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
            ModelUtils.getInstance().prepara(ps, ModelUtils.getInstance().getColumnValues( model, false ));
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

            ModelUtils.getInstance().prepara(ps, ModelUtils.getInstance().getColumnValues( model, false ));
            ps.executeUpdate();
            
    	} catch (Exception e ) {
    		e.printStackTrace();
    		
    	} finally {
    		ConnectionUtils.getInstance().closeSilenlty(connection);
    	}
    }

    @Override
    public void consultar(Object model, Object chavePrimaria) {

        Connection connection = ConnectionUtils.getInstance().getConnection();

        try {
            String sql = new SqlHelper().buildSelectSql(model, chavePrimaria);
            PreparedStatement ps = connection.prepareStatement(sql);

            System.out.println(sql);
            ps.execute();

        } catch (Exception e ) {
            e.printStackTrace();

        } finally {
            ConnectionUtils.getInstance().closeSilenlty(connection);
        }
    }

}
