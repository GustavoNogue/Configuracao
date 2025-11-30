import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Se você salvou o arquivo de config como "config.txt" no diretório do projeto, o padrão funciona.
        // Caso queira testar com outro caminho, altere DEFAULT_CONFIG_PATH em AppConfig.java ou modifique o design.
        AppConfig cfg = AppConfig.getInstance();

        // Imprime todos os dados
        System.out.println("=== Conteúdo do arquivo de configuração ===");
        System.out.println(cfg.toString());

        // Imprime campos individualmente
        System.out.println("=== Acesso por getters ===");
        System.out.println("AppId: " + cfg.getAppId());
        System.out.println("UserName: " + cfg.getUserName());
        System.out.println("Language: " + cfg.getLanguage());
        System.out.println("Offline: " + cfg.isOffline());
        System.out.println("DLCName: " + cfg.getDlcName());
        System.out.println("ApplicationPath: " + cfg.getApplicationPath());

        // Acesso genérico por chave
        System.out.println("=== Acesso genérico ===");
        String signature = cfg.get("Signature");
        System.out.println("Signature: " + signature);

        // Iterar sobre todas as propriedades
        System.out.println("=== Iterando sobre todas as propriedades ===");
        for (Map.Entry<String,String> e : cfg.getAll().entrySet()) {
            System.out.printf("%s = %s%n", e.getKey(), e.getValue());
        }
    }
}
