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
        p.setNome("William V.");
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
        p.setId(678675);

        manager.consultar(p, p.getId());
    }

    @Test
    public void test_insert_thousands() {
        long start = System.nanoTime();

        for(int i = 0; i < 100000; i++) {
            Pessoa p = new Pessoa();
            p.setNome("Usuario 2 " + (i + 1));
            p.setDataNascimento(Calendar.getInstance());

            manager.inserir(p);
            assertThat(p.getId()).isNotNull().isGreaterThan(0);

            //CarroNovo c = new CarroNovo();
            //c.setNome("Carro 1 " + (i + 1));
            //c.setDataCompra(Calendar.getInstance());

            //manager.inserir(c);

            //assertThat(c.getId()).isNotNull();
        }

        long elapsedTime = System.nanoTime() - start;
        System.out.println(elapsedTime);

        //72671604736 = 1 min 13 sec (carro_novo) - 10.000 registros
        //68717754062 = 1 min 9 sec (pessoa) - 10.000 registros

        //760611890786 = 12 min 41 sec (carro_novo) - 100.000 registros
        //790298509982 = 13 min 10 sec (pessoa) - 100.000 registros
    }
}
