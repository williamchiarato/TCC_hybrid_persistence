package framework.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public static final String UF = "uf";

    //SINGLETON
    private static PropertiesReader _INSTANCE;

    public static PropertiesReader getInstance()  {
        if (_INSTANCE == null){
            _INSTANCE = new PropertiesReader();
        }
        return _INSTANCE;
    }


    //instancia
    private PropertiesReader(){
        this.readProperties();
    }

    private Properties properties;

    public Properties getProperties(){
        return this.properties;
    }

    public String getProperty(String key){
        return this.properties.getProperty(key);
    }

    public boolean getBooleanProperty(String key){
        String prop = this.properties.getProperty(key, "false");
        return Boolean.parseBoolean(prop);
    }

    public int getIntProperty(String key){
        String prop = this.properties.getProperty(key, "0");
        return Integer.parseInt(prop);
    }

    private void readProperties() {
        try {
            //busca o properties.dir (primeiro parametro do args) - se nao houver, usa o user.dir
            String dir = System.getProperty("properties.dir", System.getProperty("user.dir"));

            File f = new File ( dir + File.separator + "project.properties");
            if (f.exists()){
                properties = new Properties();
                properties.load(new FileInputStream(f));
            }
            else{
                //se nao achar, busca no resources
                InputStream is = this.getClass().getResourceAsStream("/project.properties");
                if (is != null){
                    properties = new Properties();
                    properties.load(is);
                }
                else {
                    throw new RuntimeException("Arquivo de propriedades não encontrado [" + f.getAbsolutePath() + "]");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo de propriedades: ["+e.getMessage()+"]", e);
        }
    }

}
