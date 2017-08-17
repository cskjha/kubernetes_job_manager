package com.vmware.lambda.provider.scaler.k8s;

import static com.vmware.lambda.provider.PropertyManager.getProperty;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.concurrent.TimeUnit;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vmware.lambda.provider.PropertyManager;

@Component
public class KubeUtil {

    private static Logger log= LoggerFactory.getLogger(KubeUtil.class);

    private static final String URI = "kubernetes_url";
    private static final String NS = "kubernetes_namespace";
    private static final String USER = "kubernetes_username";
    private static final String PWD = "kubernetes_password";
    private static final String ACTIVE_DEADLINE_MIN = "JOB_ACTIVE_DEADLINE_MIN";
    private static final String DEF_ACTIVE_DEADLINE_MIN = "60" ;
    private static final String MAX_COMPLETIONS = "JOB_MAX_COMPLETIONS";
    private static final String DEF_MAX_COMPLETIONS = "10" ;

    public Config kubeConfig(boolean basicAuth) {
        ConfigBuilder configBuilder = new ConfigBuilder()
                .withMasterUrl(getProperty(URI))
                .withNamespace(getProperty(NS))
                .withTrustCerts(true)
                .withApiVersion("v1");
        if(basicAuth){
            basicAuth(configBuilder);
        }
        return configBuilder.build();
    }

    public long getActiveDeadlineSec(){
        String property = PropertyManager.getProperty(ACTIVE_DEADLINE_MIN, DEF_ACTIVE_DEADLINE_MIN);
        return TimeUnit.MINUTES.toSeconds(Integer.parseInt(property));
    }

    public int getMaxCompletions(int completions){
        String property = PropertyManager.getProperty(MAX_COMPLETIONS, DEF_MAX_COMPLETIONS);
        int maxLimit = Integer.parseInt(property);
        if(completions > maxLimit)
            return maxLimit;
        else
            return completions;
    }

    public KubernetesClient newClient(){
        return new DefaultKubernetesClient(kubeConfig(true));
    }

    public String getWatcherImage(){
        String watcher_image = System.getenv("watcher_image");
        if(watcher_image == null){
            watcher_image = getProperty("watcher_image");
        }
        return watcher_image;
    }

    private void basicAuth(ConfigBuilder builder){
        Decoder decoder = Base64.getDecoder();
        try {
            builder.withUsername(new String(decoder.decode(getProperty(USER))));
        } catch ( Exception e) {
            log.error("Unable to decode kubernetes_username property due to "+e.getMessage(), e);
            builder.withUsername(getProperty(USER));
        }
        try {
            builder.withPassword(new String(decoder.decode(getProperty(PWD))));
        } catch (Exception e) {
            log.error("Unable to decode kubernetes_password property due to "+e.getMessage(), e);
            builder.withPassword(getProperty(getProperty(PWD)));
        }
    }

}
