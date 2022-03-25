package persistence;

import framework.core.impl.SqlHelper;
import model.Pessoa;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSqlHelper {


    @Test
    public void testa_criacao_de_sql_de_insert(){
        Pessoa p = new Pessoa();
        String sql = new SqlHelper().buildInsertSql(p, true);

        System.out.println(sql);
        assertThat(sql).isEqualTo("INSERT INTO pessoa (nome,data_nascimento) VALUES (?,?)");
    }
    
    @Test
    public void testa_criacao_de_sql_de_delete() throws IllegalAccessException {
        Pessoa p = new Pessoa();
        String sql = new SqlHelper().buildDeleteSql(p);

        System.out.println(sql);
        assertThat(sql).isEqualTo("DELETE FROM pessoa WHERE id = ?");
    }
    
    @Test
    public void testa_criacao_de_sql_de_update(){
        //todo
    }

    
    @Test
    public void testa_criacao_de_sql_de_select(){
        //todo
    }


}
