package com.vmware.lambda.provider.scaler.k8s;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static com.vmware.lambda.provider.scaler.PropertyConstants.CONTAINER_VOLUME_MOUNT_PATH;
import static com.vmware.lambda.provider.scaler.PropertyConstants.IMAGE_PULL_SECRET;
import static com.vmware.lambda.provider.scaler.PropertyConstants.JOB_VOLUME_MOUNT_PATH;
import static com.vmware.lambda.provider.scaler.PropertyConstants.K8S_JOB_HEALTH_URL;
import static com.vmware.lambda.provider.scaler.PropertyConstants.K8S_MAX_NAME_LENGTH;
import static com.vmware.lambda.provider.scaler.PropertyConstants.LAMBDA_APP_NAME;
import static com.vmware.lambda.provider.scaler.PropertyConstants.LAMBDA_FUNCTION_NAME;
import static com.vmware.lambda.provider.scaler.PropertyConstants.LAMBDA_SVC_URI;
import static com.vmware.lambda.provider.scaler.PropertyConstants.VOLUME_MOUNT_NAME;
import static com.vmware.lambda.provider.scaler.PropertyConstants.WATCHER_PORT;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.HostPathVolumeSource;
import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.Job;
import io.fabric8.kubernetes.api.model.JobBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReference;
import io.fabric8.kubernetes.api.model.LocalObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.QuantityBuilder;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.JobOperationsImpl;
import io.fabric8.kubernetes.client.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vmware.lambda.provider.PropertyManager;
import com.vmware.lambda.provider.api.controller.LambdaServiceImpl;
import com.vmware.lambda.provider.api.dto.FunktionDto;
import com.vmware.lambda.provider.scaler.core.CorrectionPerformer;
import com.vmware.lambda.provider.scaler.core.States.Correction;


/**
 * Performs the correction by spinning a kubernetes job.
 * <p>
 * <pre>
 * Intended logic is, based on the correction, create a Job definition with
 *      a. completions count equalling the open queue size
 *      b. max parallelism equalling the max no of concurrent jobs
 * </pre>
 * <p>
 * Each job will have two containers. A watcher container and the actual lambda container.
 * <p>
 * Gotcha: The max. no of jobs getting executed at a point in time will be more than the configured limit (L) by (L-1)
 * for a short duration.
 */
@Component
public class KubeJobCorrectionPerformer implements CorrectionPerformer {

    @Autowired
    KubeUtil util;

    @Autowired
    private LambdaServiceImpl lambdaService;

    private KubernetesClient client;

    private SecureRandom secureRandom = new SecureRandom();

    private Logger log = LoggerFactory.getLogger(KubeJobCorrectionPerformer.class);

    @Override
    public Boolean performCorrectiveAction(Correction correction) {
        if (client == null) {
            client = util.newClient();
        }
        String qualifier = correction.getQualifier();
        String app = Util.appFromQualifier(qualifier);
        String function = Util.functionFromQualifier(qualifier);
        List<FunktionDto> funktions = lambdaService.listFunctions(app, function);

        FunktionDto match = funktions.stream()
                .filter(fn -> Objects.equals(fn.getName(), function))
                .filter(fn -> {
                    boolean enabled = true;
                    if (Boolean.TRUE.equals(fn.getDisable())) {
                        enabled = false;
//                        log.warn("Function is disabled. Hence not performing corrective action. Function details are {}", Utils.toJson(fn));
                    }
                    return enabled;
                })
                .findFirst().get();


        Job jobDefinition = createJobDefinition(correction, match, app);
        log.info("Job to be created is {}", jobDefinition);
        JobOperationsImpl jobOperations = new JobOperationsImpl(HttpClientUtils.createHttpClient(client.getConfiguration()),
                client.getConfiguration(), client.getApiVersion(), client.getNamespace(), null, true, null,
                null, false, -1, new TreeMap<>(), new TreeMap<>(), new TreeMap<>(), new TreeMap<>(), new TreeMap<>());
        Job job = jobOperations.create(jobDefinition);
        log.info("Created Job with props {} as {}", job.getAdditionalProperties(), job);
        return true;
    }

    private Job createJobDefinition(Correction correction, FunktionDto fn, String app) {
        JobBuilder jobBuilder = new JobBuilder()
                .withApiVersion("batch/v1");
        String name = app.toLowerCase() + fn.getName().toLowerCase();
        jobBuilder.withNewMetadata()
                .withName(getDynamicName(name))
                .addToLabels("lambdaApp", app)
                .addToLabels("lambdaFunction", fn.getName())
                .endMetadata();

        LocalObjectReference registrySecret = new LocalObjectReferenceBuilder()
                .withName(PropertyManager.getProperty(IMAGE_PULL_SECRET,"registrysecret"))
                .build();

        jobBuilder.withNewSpec()
                .withCompletions(util.getMaxCompletions(correction.getCompletions()))
                .withParallelism(correction.getParallelism())
                .withActiveDeadlineSeconds(util.getActiveDeadlineSec())
                .withNewTemplate()
                .withNewMetadata()
                .withName(name)
                .addToLabels("lambdaApp", app)
                .addToLabels("lambdaFunction", fn.getName())
                .addToLabels("owner", "lambda")
                .endMetadata()
                .withNewSpec()
                .withContainers(asList(newLambdaContainer(app, fn), newWatcherContainer(app, fn.getName())))
                .withRestartPolicy("OnFailure")
                .withImagePullSecrets(registrySecret)
                .withVolumes(getLogVolume())
                .endSpec()
                .endTemplate()
                .endSpec();

        return jobBuilder.build();
    }

    private List<Volume> getLogVolume() {
        HostPathVolumeSource hostPath = new HostPathVolumeSourceBuilder()
                .withPath(JOB_VOLUME_MOUNT_PATH)
                .build();
        Volume volume = new VolumeBuilder()
                .withName(VOLUME_MOUNT_NAME)
                .withHostPath(hostPath)
                .build();
        return singletonList(volume);
    }

    private String getDynamicName(String prefix) {
        String suffix = new BigInteger(63, secureRandom).toString(Character.MAX_RADIX).toLowerCase();
        String finalName = prefix + suffix;
        if (finalName.length() > K8S_MAX_NAME_LENGTH) {
            finalName = finalName.substring(0, K8S_MAX_NAME_LENGTH - 1);
        }
        return finalName;
    }

    Container newLambdaContainer(String appName, FunktionDto func) {
        Container container = new Container();
        container.setImage(func.getImage());
        container.setName("lambda");
        List<EnvVar> envVarList = getVars(appName, func.getName(), func);
        container.setEnv(envVarList);
        ResourceRequirements memory = new ResourceRequirementsBuilder()
                .addToLimits("memory", new QuantityBuilder().withAmount(func.getMemory() + "Mi").build())
                .build();
        VolumeMount volumeMount = new VolumeMountBuilder()
                .withName(VOLUME_MOUNT_NAME)
                .withMountPath(CONTAINER_VOLUME_MOUNT_PATH)
                .build();
        container.setVolumeMounts(singletonList(volumeMount));
        container.setResources(memory);
        return container;
    }

    private List<EnvVar> getVars(String appName, String functionName, FunktionDto func) {
        List<EnvVar> envVarList = new ArrayList<>();
        envVarList.add(new EnvVarBuilder().withName(LAMBDA_APP_NAME).withValue(appName).build());
        envVarList.add(new EnvVarBuilder().withName(LAMBDA_FUNCTION_NAME).withValue(functionName).build());
        envVarList.add(new EnvVarBuilder().withName(LAMBDA_SVC_URI).withValue(System.getenv(LAMBDA_SVC_URI)).build());
        if (func != null && func.getEnvs() != null) {
            Map<String, String> envs = new ObjectMapper().convertValue(func.getEnvs(), Map.class);
            envs.forEach((key, value) -> {
                envVarList.add(new EnvVarBuilder().withName(key).withValue(value).build());
            });
        }
        return envVarList;
    }

    Container newWatcherContainer(String appName, String fnName) {
        Container container = new Container();
        container.setImage(util.getWatcherImage());
        container.setName("watcher");
        ContainerPort port = new ContainerPortBuilder().withContainerPort(8080).build();
        container.setPorts(singletonList(port));
        List<EnvVar> envVarList = getVars(appName, fnName, null);
        container.setEnv(envVarList);

        Probe probe = new ProbeBuilder().withNewHttpGet()
                .withPath(K8S_JOB_HEALTH_URL)
                .withPort(WATCHER_PORT).and()
                .build();
        container.setLivenessProbe(probe);
        VolumeMount volumeMount = new VolumeMountBuilder()
                .withName(VOLUME_MOUNT_NAME)
                .withMountPath(CONTAINER_VOLUME_MOUNT_PATH)
                .build();
        container.setVolumeMounts(singletonList(volumeMount));
        return container;
    }
}
