package persistence;

import framework.core.ModelManager;
import framework.core.ModelManagerFactory;
import model.CarroNovo;
import model.Pessoa;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPostgresModelManager {

    private ModelManager manager;

    @Before
    public void setUp(){
        this.manager = ModelManagerFactory.createModelManager(ModelManagerFactory.POSTGRES);
    }

    @Test
    public void testa_insert_na_base(){
        Pessoa p = new Pessoa();
        p.setNome("William");
        p.setDataNascimento(Calendar.getInstance());

        manager.inserir(p);
        assertThat(p.getId()).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testa_insert_na_base_id_em_string(){
        CarroNovo c = new CarroNovo();
        c.setNome("Lamborguini");
        c.setDataCompra(Calendar.getInstance());

        manager.inserir(c);

        assertThat(c.getId()).isNotNull();
        System.out.println(c.getId());
    }
    
    @Test
    public void test_delete() {
        Pessoa p = new Pessoa();
        p.setId(1);
        p.setNome("William");
        p.setDataNascimento(Calendar.getInstance());

        manager.remover(p);
    }
    
    @Test
    public void test_update() {
        Pessoa p = new Pessoa();
        p.setId(1);
        p.setNome("William editado");
        p.setDataNascimento(Calendar.getInstance());

        manager.atualizar(p);
    }
    
    @Test
    public void test_select() {
        Pessoa p = new Pessoa();
        p.setId(1);

        manager.consultar(p.getClass(), p.getId());
    }
}
