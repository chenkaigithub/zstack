package org.zstack.test.kvm;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zstack.compute.host.HostGlobalConfig;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.cluster.ClusterInventory;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.network.l2.L2NetworkInventory;
import org.zstack.kvm.KVMHostFactory;
import org.zstack.simulator.kvm.KVMSimulatorConfig;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.WebBeanConstructor;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.concurrent.TimeUnit;

public class TestKvmAttachL2Network6 {
    static CLogger logger = Utils.getLogger(TestKvmAttachL2Network6.class);
    static Deployer deployer;
    static Api api;
    static ComponentLoader loader;
    static CloudBus bus;
    static DatabaseFacade dbf;
    static KVMHostFactory kvmFactory;
    static SessionInventory session;
    static KVMSimulatorConfig config;

    @BeforeClass
    public static void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/kvm/TestKvmAttachL2Network6.xml", con);
        deployer.addSpringConfig("Kvm.xml");
        deployer.addSpringConfig("KVMSimulator.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        kvmFactory = loader.getComponent(KVMHostFactory.class);
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        config = loader.getComponent(KVMSimulatorConfig.class);
        session = api.loginAsAdmin();
        HostGlobalConfig.PING_HOST_INTERVAL.updateValue(1);
    }

    @Test
    public void test() throws ApiSenderException, InterruptedException {
        L2NetworkInventory l2 = deployer.l2Networks.get("TestL2Network");
        ClusterInventory cluster = deployer.clusters.get("Cluster1");
        api.attachL2NetworkToCluster(l2.getUuid(), cluster.getUuid());
        Assert.assertEquals(1, config.createBridgeCmds.size());
        config.createBridgeCmds.clear();
        config.pingSuccess = false;
        TimeUnit.SECONDS.sleep(3);
        config.pingSuccess = true;
        TimeUnit.SECONDS.sleep(3);
        Assert.assertFalse(config.createBridgeCmds.isEmpty());
    }
}
