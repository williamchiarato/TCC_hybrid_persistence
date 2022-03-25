package persistence;

import framework.core.ModelManager;
import framework.core.ModelManagerFactory;
import model.CarroNovo;
import model.Pessoa;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMongoModelManager {

    private ModelManager manager;

    @Before
    public void setUp(){
        this.manager = ModelManagerFactory.createModelManager(ModelManagerFactory.MONGO);
    }

    @Test
    public void testa_insert_na_base(){
        Pessoa p = new Pessoa();
        p.setNome("William Chiarato Vieria");
        p.setDataNascimento(Calendar.getInstance());

        manager.inserir(p);

        assertThat(p.getId()).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testa_insert_na_base_id_em_string(){
        CarroNovo c = new CarroNovo();
        c.setNome("Lamborghini Murcielago");
        c.setDataCompra(Calendar.getInstance());

        manager.inserir(c);

        assertThat(c.getId()).isNotNull();
        System.out.println(c.getId());
    }
    
    @Test
    public void test_delete_id_string() {
        CarroNovo c = new CarroNovo();
        c.setId("622cded42be99b7ffabfdcad");

        manager.remover(c);
    }

    @Test
    public void test_delete_id_int() {
        Pessoa p = new Pessoa();
        p.setId(1);

        manager.remover(p);
    }
    
    @Test
    public void test_update_id_string() {
        CarroNovo c = new CarroNovo();
        c.setId("622e4cce9a5c4b4e08cf5912");
        c.setNome("Lamborghini Veneno");
        c.setDataCompra(Calendar.getInstance());

        manager.atualizar(c);
    }

    @Test
    public void test_update_id_int() {
        Pessoa p = new Pessoa();

        manager.atualizar(p);
    }

    @Test
    public void test_select_id_string() {
        CarroNovo c = new CarroNovo();
        c.setId("62376624865af908096b3b69");

        manager.consultar(c, c.getId());
    }

    @Test
    public void test_select_id_int() {
        Pessoa p = new Pessoa();
        p.setId(78670);

        manager.consultar(p, p.getId());
    }

    @Test
    public void test_insert_thousands() {
        long start = System.nanoTime();

        for(int i = 0; i < 100000; i++) {
            CarroNovo c = new CarroNovo();
            c.setNome("Carro 1 " + (i + 1));
            c.setDataCompra(Calendar.getInstance());

            manager.inserir(c);

            assertThat(c.getId()).isNotNull();
        }

        long elapsedTime = System.nanoTime() - start;
        System.out.println(elapsedTime);

        //7814162021 = 7 sec 815 ms (carro_novo) - 10.000 registros
        //9580079408 = 9 sec 581 ms (pessoa) - 10.000 registros

        //71727931432 = 1 min 12 sec (carro_novo) - 100.000 registros
        //84248797044 = 1 min 24 sec (pessoa) - 100.000 registros
    }
}
