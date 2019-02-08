package com.dyb.commonactivity.config;

//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
public class ESConfig {

//    @Bean
//    public TransportClient client() throws UnknownHostException {
//        // 9300是es的tcp服务端口
//        InetSocketTransportAddress node = new InetSocketTransportAddress(
//                InetAddress.getByName("192.168.190.129"),
//                9300);
//
//        // 设置es节点的配置信息
//        Settings settings = Settings.builder()
//                .put("cluster.name", "es")
//                .build();
//
//        // 实例化es的客户端对象
////        TransportClient client = new TransportClient(settings);
//        TransportClient client = new TransportClient(null);
//        client.addTransportAddress(node);
//
//        return client;
//    }
}
