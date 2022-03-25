package framework.core;

public interface ModelManager<T> {

    void inserir(Object model);

    void atualizar(Object model);

    void remover(Object model);

    void consultar(Object model, Object chavePrimaria);

}
