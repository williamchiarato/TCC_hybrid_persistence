package framework.core.impl;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import framework.core.ModelManager;
import framework.core.util.ModelUtils;
import framework.core.util.PropertiesReader;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class MongoModelManager implements ModelManager {

    protected MongoClient client;
    protected MongoDatabase db;

    private String serverIp;
    private String serverPort;

    private String databaseName;
    private String collectionName;

    private static final String MONGO_OBJECT_ID = "_id";
    private static final String SEQUENCES_COLLECTION_NAME = "sequences";
    private static final String NEXTVAL_ATTRIBUTE = "nextval";
    private static final String NEXTVAL_COLLECTION_ATTRIBUTE = "collection";

    protected void createClient() throws UnknownHostException {
        this.serverIp = PropertiesReader.getInstance().getProperty("persistence.mongodb.host");
        this.serverPort = PropertiesReader.getInstance().getProperty("persistence.mongodb.port");

        this.client = new MongoClient( this.serverIp , Integer.parseInt(this.serverPort) );
    }

    public void openDatabase() throws RuntimeException{
        try {
            this.createClient();
            this.databaseName = PropertiesReader.getInstance().getProperty("persistence.mongodb.database");
            this.db = this.client.getDatabase(this.databaseName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void closeDatabase(){
        if (this.client != null){
            this.client.close();
        }
    }

    private Integer getNextSequenceValue(String collection){
        MongoCollection<Document> dbCollection = this.db.getCollection(SEQUENCES_COLLECTION_NAME);
        Document seletor = new Document();
        seletor.append(NEXTVAL_COLLECTION_ATTRIBUTE, collection);
        Document result = dbCollection.find(seletor).first();

        Integer nextId = 1;
        if (result == null) {
            result = new Document().append(NEXTVAL_COLLECTION_ATTRIBUTE, collection).append(NEXTVAL_ATTRIBUTE, (nextId+1));
            dbCollection.insertOne(result);
        }
        else{
            nextId = result.getInteger(NEXTVAL_ATTRIBUTE);
            result.append(NEXTVAL_ATTRIBUTE, (nextId+1));
            dbCollection.findOneAndReplace(seletor, result);
        }
        return nextId;
    }

    @Override
    public void inserir(Object model) {
        this.openDatabase();

        try {
            String collection = ModelUtils.getInstance().getTableName(model);
            MongoCollection<Document> dbCollection = this.db.getCollection( collection );

            Document object = new Document();

            boolean incluirId = false;
            //se id é integer, tem que gerar uma sequence artificialmente
            if (ModelUtils.getInstance().isIdAnnotationInteger(model)) {
                ModelUtils.getInstance().setIdValue(model, this.getNextSequenceValue(collection));
                incluirId = true;
            }

            Map<String,Object> atributos = ModelUtils.getInstance().getFields(model, incluirId);
            for( String key: atributos.keySet() ){
                Object valor = atributos.get(key);

                //TRATAR AQUI tipos nao suportados nativamente pelo mongo
                if (valor != null && valor instanceof GregorianCalendar){
                    valor = ((GregorianCalendar)valor).getTime();
                }
                object.append(key, valor);
            }
            dbCollection.insertOne(object);

            //se for string, seta o id gerado pelo mongo
            if (!ModelUtils.getInstance().isIdAnnotationInteger(model)) {
                ModelUtils.getInstance().setIdValue( model, object.get(MONGO_OBJECT_ID).toString() );
            }

            this.closeDatabase();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void atualizar(Object model) {
        this.openDatabase();

        try {
            String collection = ModelUtils.getInstance().getTableName(model);
            MongoCollection<Document> dbCollection = this.db.getCollection( collection );

            UpdateResult result = null;
            Bson newObject = null;
            Bson query;

            //nunca pode utilizar o id dentro dos valores do objeto que serão atualizados
            Map<String,Object> atributos = ModelUtils.getInstance().getFields(model, false);

            boolean idString = true;
            if (ModelUtils.getInstance().isIdAnnotationInteger(model)) {
                idString = false;
            }

            if(idString == true) {
                query = eq("_id", new ObjectId(ModelUtils.getInstance().getObjectId(model)));
            } else {
                query = eq("id", ModelUtils.getInstance().getIdValue(model));
            }


            for( String key: atributos.keySet() ){
                Object valor = atributos.get(key);

                if (valor != null && valor instanceof GregorianCalendar){
                    valor = ((GregorianCalendar)valor).getTime();
                }

                if(valor != null) {
                    newObject = combine(set(key, valor));
                }

                result = dbCollection.updateOne(query, newObject);
            }

            try {
                System.out.println("Total de documentos editados: " + result.getModifiedCount());
            } catch (MongoException me) {
                System.err.println("Erro ao atualizar objeto: " + me);
            }

            this.closeDatabase();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remover(Object model) {
        this.openDatabase();

        try {
            String collection = ModelUtils.getInstance().getTableName(model);
            MongoCollection<Document> dbCollection = this.db.getCollection( collection );

            Document object = new Document();
            Bson query = null;
            boolean idString = true;

            if (ModelUtils.getInstance().isIdAnnotationInteger(model)) {
                idString = false;
            }

            if(idString == true) {
                query = eq("_id", new ObjectId(ModelUtils.getInstance().getObjectId(model)));
            } else {
                query = eq("id", ModelUtils.getInstance().getIdValue(model));
            }

            DeleteResult result = dbCollection.deleteOne(query);

            // Logica alternativa
            //DeleteResult result = dbCollection.deleteOne(eq("_id", new ObjectId("6167a1265cbd0519f51fe9cd")));

            if(result.getDeletedCount() > 0) {
                System.out.println("Contagem de documentos removidos: " + result.getDeletedCount());
                return;
            }

            this.closeDatabase();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void consultar(Object model, Object chavePrimaria) {
        this.openDatabase();

        try {
            String collection = ModelUtils.getInstance().getTableName(model);
            MongoCollection<Document> dbCollection = this.db.getCollection( collection );

            Document object = new Document();
            Bson query = null;
            boolean idString = true;

            if (ModelUtils.getInstance().isIdAnnotationInteger(model)) {
                idString = false;
            }

            if(idString == true) {
                query = eq("_id", new ObjectId(chavePrimaria.toString()));
            } else {
                query = eq("id", chavePrimaria);
            }

            object = dbCollection.find(query).first();

            if(object == null) {
                System.out.println("Nenhum resultado encontrado");
                return;
            }

            System.out.println("Documento encontrado: ");
            System.out.println(object);

            /* Lógica alternativa
            *   Document doc = dbCollection.find(query)
            *        .projection(object) // diz quais campos ele ira retornar
            *        .sort(Sorts.descending("imdb.rating"))
            *        .first();
            */

            /* Exemplo para pesquisar varios documentos
            * MongoCursor<Document> cursor = collection.find(lt("runtime", 15))
            *        .projection(projectionFields)
            *        .sort(Sorts.descending("title"))
            *        .iterator();
            *
            * if (doc == null) {
            *    System.out.println("No results found.");
            * } else {
            *    System.out.println(doc.toJson());
            * }
            */

            /* Codigo abaixo lista todos de uma collection
            * FindIterable<Document> iterDoc = dbCollection.find();
            * int i = 1;
            *
            * Iterator it = iterDoc.iterator();
            * while (it.hasNext()) {
            *   System.out.println(it.next());
            *   i++;
            * }
            */

            this.closeDatabase();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}