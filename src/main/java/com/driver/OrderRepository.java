package com.driver;

import io.swagger.models.auth.In;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orders;
    private HashMap<String, DeliveryPartner> partners;
    private HashMap<String,String> orderDeliveryPartnerMap;
    private HashMap<String, List<Order>> partnerOrdersMap;

    public OrderRepository(){
        this.orders = new HashMap<String, Order>();
        this.partners = new HashMap<String, DeliveryPartner>();
        this.orderDeliveryPartnerMap = new HashMap<String,String>();
        this.partnerOrdersMap = new HashMap<String, List<Order>>();
    }

    public String addOrder(Order order){
        if (order==null) return "Order is null";
        if(!orders.containsKey(order.getId())){
            orders.put(order.getId(),order);
            return "New order added successfully";
        }
        return "Order already created";
    }

    public String addPartner(String partnerId){

        if(!partners.containsKey(partnerId)){
            partners.put(partnerId,new DeliveryPartner(partnerId));
            return "New delivery partner added successfully";
        }
        return "Partner already created";
    }

    public String addOrderPartnerPair(String orderId,String partnerId){

        //This is basically assigning that order to that partnerId
        DeliveryPartner partner = partners.get(partnerId);
        Order order = orders.get(orderId);
        partner.setNumberOfOrders(partner.getNumberOfOrders()+1);
        orderDeliveryPartnerMap.put(orderId,partnerId);

        if(!partnerOrdersMap.containsKey(partnerId)){
            List<Order> orders = new ArrayList<>();
            orders.add(order);
            partnerOrdersMap.put(partnerId,orders);
        }
        else{
            List<Order> orders = partnerOrdersMap.get(partnerId);
            orders.add(order);
            partnerOrdersMap.put(partnerId,orders);
        }

        return "New order-partner pair added successfully";
    }


    public Order getOrderById(String orderId){
        return orders.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partners.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId){
        return partners.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> partnerOrders = new ArrayList<>();
        for(Order order:partnerOrdersMap.get(partnerId)){
            partnerOrders.add(order.getId());
        }
        return partnerOrders;
    }

    public List<String> getAllOrders(){
        List<String> allOrders = new ArrayList<>();
        for(Map.Entry<String,Order> order: orders.entrySet()){
            allOrders.add(order.getKey());
        }
        return allOrders;
    }

    public Integer getCountOfUnassignedOrders(){

        return orders.size() - orderDeliveryPartnerMap.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer currTime = (Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3)) );
        Integer count =0;
        for (Map.Entry<String, List<Order>> partner : partnerOrdersMap.entrySet()){
            if(partner.getKey().equals(partnerId)){
                for(Order order:partner.getValue()){
                    if(order.getDeliveryTime()>currTime){
                        count++;
                    }
                }
                break;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        Integer time =0;
        for (Map.Entry<String, List<Order>> partner : partnerOrdersMap.entrySet()){
            if(partner.getKey().equals(partnerId)){
                for(Order order:partner.getValue()){
                    if(order.getDeliveryTime()>time){
                        time = order.getDeliveryTime();
                    }
                }
                break;
            }
        }
        Integer h = time/60;
        Integer m = time%60;
        String hourString = String.valueOf(h);
        String minString = String.valueOf(m);
        if(hourString.length()==1){
            hourString= "0"+hourString;
        }

        if(minString.length()==1){
            minString= "0"+minString;
        }
        return hourString+":"+minString;
    }

    public String deletePartnerById(String partnerId){
        List<Order> allOrder = partnerOrdersMap.get(partnerId);
        for(Order order: allOrder){
            orderDeliveryPartnerMap.remove(order.getId());
        }
        partners.remove(partnerId);
        partnerOrdersMap.remove(partnerId);
        return partnerId + " removed successfully";
    }

    public String deleteOrderById(String orderId){
        Order order = orders.get(orderId);
        for(Map.Entry<String,String> orderPartner : orderDeliveryPartnerMap.entrySet()){
            if(orderPartner.getKey().equals(orderId)){
                orderDeliveryPartnerMap.remove(orderId);
                String partnerId = orderPartner.getValue();
                DeliveryPartner partner = partners.get(partnerId);
                partner.setNumberOfOrders(partner.getNumberOfOrders()-1);
                List<Order> orderset = partnerOrdersMap.get(orderPartner.getValue());
                int index = orderset.indexOf(order);
                orderset.remove(index);
                partnerOrdersMap.put(orderPartner.getValue(),orderset);
                break;
        }
    }
        orders.remove(orderId);
        return orderId + " removed successfully";
    }


    }
