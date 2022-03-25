package framework.core;

import framework.core.impl.MongoModelManager;
import framework.core.impl.PostgresModelManager;

public class ModelManagerFactory {

    public static final int POSTGRES = 1;
    public static final int MONGO = 2;

    public static ModelManager createModelManager(int type){

        if (type == POSTGRES){
            return new PostgresModelManager();
        }
        else if (type == MONGO){
            return new MongoModelManager();
        }
        throw new RuntimeException("Model Manager não implementado");
    }

}
