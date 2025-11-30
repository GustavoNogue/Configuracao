import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton que carrega e fornece acesso aos dados de configuração.
 * Usa Initialization-on-demand holder idiom para thread-safety e inicialização lazy.
 */
public class AppConfig {
    // caminho padrão (pode ser alterado)
    private static final String DEFAULT_CONFIG_PATH = "config.txt";

    // Campos do arquivo (exemplos, você pode adicionar/remover conforme necessidade)
    private final String appId;
    private final String userName;
    private final String language;
    private final boolean offline;
    private final boolean autoDLC;
    private final String buildId;
    private final String dlcName;
    private final boolean updateDB;
    private final String signature;
    private final String windowInfo;
    private final String lvWindowInfo;
    private final String applicationPath;
    private final String workingDirectory;
    private final boolean waitForExit;
    private final boolean noOperation;

    // Mantém todas as chaves/valores lidos (preserva ordem de leitura)
    private final Map<String,String> allProperties;

    // Instancia singleton - holder idiom
    private AppConfig(Path configPath) {
        Properties props = loadProperties(configPath);
        // popular campos com parsing básico e valores default quando necessário
        this.appId = props.getProperty("AppId", "");
        this.userName = props.getProperty("UserName", "");
        this.language = props.getProperty("Language", "");
        this.offline = parseBooleanInt(props.getProperty("Offline", "0"));
        this.autoDLC = parseBooleanInt(props.getProperty("AutoDLC", "0"));
        this.buildId = props.getProperty("BuildId", "");
        this.dlcName = props.getProperty("DLCName", "");
        this.updateDB = parseBooleanInt(props.getProperty("UpdateDB", "0"));
        this.signature = props.getProperty("Signature", "");
        this.windowInfo = props.getProperty("WindowInfo", "");
        this.lvWindowInfo = props.getProperty("LVWindowInfo", "");
        this.applicationPath = props.getProperty("ApplicationPath", "");
        this.workingDirectory = props.getProperty("WorkingDirectory", "");
        this.waitForExit = parseBooleanInt(props.getProperty("WaitForExit", "0"));
        this.noOperation = parseBooleanInt(props.getProperty("NoOperation", "0"));

        // populate allProperties preserving order (Properties has no order; usamos LinkedHashMap para previsibilidade)
        Map<String,String> tmp = new LinkedHashMap<>();
        // Lista de chaves esperadas; caso queira incluir todas detectadas dinamicamente, use props.stringPropertyNames()
        for (String key : props.stringPropertyNames()) {
            tmp.put(key, props.getProperty(key));
        }
        this.allProperties = Collections.unmodifiableMap(tmp);
    }

    // Holder estático para lazy init com path padrão
    private static class Holder {
        private static final AppConfig INSTANCE = new AppConfig(Path.of(DEFAULT_CONFIG_PATH));
    }

    /**
     * Retorna instância singleton (usa o DEFAULT_CONFIG_PATH).
     */
    public static AppConfig getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Alternativa: criar uma instância inicial (usada apenas se desejar carregar de outro lugar
     * antes de getInstance ser chamado). Simples approach: chamar init(path) antes do first getInstance.
     */
    private static volatile boolean initializedViaPath = false;

    /**
     * Inicializa explicitamente o Singleton com caminho customizado.
     * Deve ser chamado antes de getInstance() se quiser um caminho diferente do padrão.
     */
    public static synchronized AppConfig initWithPath(Path configPath) {
        if (initializedViaPath) {
            throw new IllegalStateException("AppConfig já inicializado com caminho customizado.");
        }
        if (configPath == null) throw new IllegalArgumentException("configPath não pode ser null");
        // Criamos instância substituta no holder via reflexão? Simples: criar instância temporária e setar Holder.INSTANCE via hack não recomendado.
        // Abordagem prática: retornar uma nova instância (não recomendada para singleton estrito) ou instruir a usar o DEFAULT_CONFIG_PATH.
        // Para simplicidade pedagógica, lançamos UnsupportedOperationException para init custom no exemplo.
        throw new UnsupportedOperationException("initWithPath não implementado no exemplo—use DEFAULT_CONFIG_PATH ou modifique o código para suportar init custom.");
    }

    // Helper para carregar properties
    private Properties loadProperties(Path configPath) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configPath.toFile())) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Falha ao carregar arquivo de configuração: " + e.getMessage());
            // Em produção, trate melhor (throw exception ou fallback). Aqui continuamos com props vazias.
        }
        return props;
    }

    // parse int flag (0/1) para boolean
    private static boolean parseBooleanInt(String val) {
        if (val == null) return false;
        val = val.strip();
        return val.equals("1") || val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes");
    }

    // Getters para campos individuais
    public String getAppId() { return appId; }
    public String getUserName() { return userName; }
    public String getLanguage() { return language; }
    public boolean isOffline() { return offline; }
    public boolean isAutoDLC() { return autoDLC; }
    public String getBuildId() { return buildId; }
    public String getDlcName() { return dlcName; }
    public boolean isUpdateDB() { return updateDB; }
    public String getSignature() { return signature; }
    public String getWindowInfo() { return windowInfo; }
    public String getLvWindowInfo() { return lvWindowInfo; }
    public String getApplicationPath() { return applicationPath; }
    public String getWorkingDirectory() { return workingDirectory; }
    public boolean isWaitForExit() { return waitForExit; }
    public boolean isNoOperation() { return noOperation; }

    /**
     * Retorna o valor cru associado a uma chave (null se não existir).
     */
    public String get(String key) {
        return allProperties.get(key);
    }

    /**
     * Retorna todas as propriedades lidas (mapa imutável).
     */
    public Map<String,String> getAll() {
        return allProperties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AppConfig {").append(System.lineSeparator());
        allProperties.forEach((k,v) -> sb.append("  ").append(k).append(" = ").append(v).append(System.lineSeparator()));
        sb.append("}");
        return sb.toString();
    }

    // Opcional: método sincronizado para recarregar a config em tempo de execução (não usado pelo Holder)
    // Implementar conforme necessidade. Aqui só uma assinatura para referência.
    // public synchronized void reload() { ... }
}
