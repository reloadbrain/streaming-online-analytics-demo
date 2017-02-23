package com.radanalyticsio.camel;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBCollection;
import com.redhat.analytics.producer.KafkaMessenger;

import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhassan on 14/02/17.
 */
public class OrderService {
    private final Map<String, Order> Orders = new TreeMap<String, Order>();

    public OrderService() {
        Orders.put("123", new Order("Kitchen Table", 45.99, 1, 1));
        Orders.put("124", new Order("BookShelf", 34.99, 2, 1));
        Orders.put("225", new Order("Coffee Table", 9.99, 3, 1));
        Orders.put("135", new Order("Office Chair", 20.99, 3, 1));
    }

    public Order getOrder(String id) {

        DBCollection c = MongoDBService.connectFromEnv();
        JacksonDBCollection<Order, String> col = JacksonDBCollection.wrap(c, Order.class,
                String.class);
        Order rtnOrder= col.findOne(DBQuery.is("_id",id));
        return rtnOrder;
    }

    public List<Order> listOrders() {

        DBCollection c = MongoDBService.connectFromEnv();
        JacksonDBCollection<Order, String> col = JacksonDBCollection.wrap(c, Order.class,
                String.class);
        List<Order> orderList= col.find().limit(5).toArray();
        return orderList;

     }

    public Order createOrder(Order order) {
        //TODO: Place holder need to add mongodb persistence here..
        DBCollection c = MongoDBService.connectFromEnv();
        JacksonDBCollection<Order, String> coll = MongoDBService.persist(c, order);
        try {
            ObjectMapper mapper= new ObjectMapper();
            OrderEventMessage evt= new OrderEventMessage(OrderEvent.ADD_ORDER, order);
            String strOrder=mapper.writeValueAsString(evt);
            sendMsgFromEnv("topicA",strOrder);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return order;
    }

    private void sendMsgFromEnv(String topic, String msg) {
        String kafkaURL= "localhost:9092";
        try{
            String env_uri=System.getenv("KAFKA_URI");
            if(env_uri !=null )
                kafkaURL=env_uri;
        }catch(Exception ex){
            System.out.println("KAFKA_URI not set using default localhost");
        }
        System.out.println("Sending kafka message to:" + kafkaURL);
        KafkaMessenger messenger= new KafkaMessenger(kafkaURL);
        try {
            messenger.send(topic , msg).get();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }

}
}
