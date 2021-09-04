package persistence;

import framework.core.impl.PostgresModelManager;
import model.Pessoa;
import org.junit.Test;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPostgresModelManager {

    @Test
    public void testa_insert_na_base(){
        Pessoa p = new Pessoa();
        p.setNome("William");
        p.setDataNascimento(Calendar.getInstance());

        PostgresModelManager manager = new PostgresModelManager();
        manager.inserir(p);

        assertThat(p.getId()).isNotNull().isGreaterThan(0);
    }
    
    @Test
    public void test_delete() {
        Pessoa p = new Pessoa();
        p.setId(1);
        p.setNome("William");
        p.setDataNascimento(Calendar.getInstance());
    	
    	PostgresModelManager manager = new PostgresModelManager();
        manager.remover(p);
    }
    
    @Test
    public void test_update() {
        Pessoa p = new Pessoa();
        p.setId(1);
        p.setNome("William editado");
        p.setDataNascimento(Calendar.getInstance());

        PostgresModelManager manager = new PostgresModelManager();
        manager.atualizar(p);
    }
    
    @Test
    public void test_select() {
        Pessoa p = new Pessoa();
        p.setId(1);

        PostgresModelManager manager = new PostgresModelManager();
        manager.consultar(p.getClass(), p.getId());
    }
}
