package com.driver;

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
    private HashMap<Order,DeliveryPartner> orderDeliveryPartnerMap;
    private HashMap<DeliveryPartner, List<Order>> partnerOrdersMap;

    public OrderRepository(){
        this.orders = new HashMap<String, Order>();
        this.partners = new HashMap<String, DeliveryPartner>();
        this.orderDeliveryPartnerMap = new HashMap<Order,DeliveryPartner>();
        this.partnerOrdersMap = new HashMap<DeliveryPartner, List<Order>>();
    }

    public String addOrder(Order order){
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
        orderDeliveryPartnerMap.put(order,partner);

        if(!partnerOrdersMap.containsKey(partner)){
            List<Order> orders = new ArrayList<>();
            orders.add(order);
            partnerOrdersMap.put(partner,orders);
        }
        else{
            List<Order> orders = partnerOrdersMap.get(partner);
            orders.add(order);
            partnerOrdersMap.put(partner,orders);
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
        int count =0;
        for(Map.Entry<String,Order> order: orders.entrySet()){
            if(!orderDeliveryPartnerMap.containsKey(order.getValue())){
                count++;
            }
        }
        return count;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        int currTime = (Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3)) );
        int count =0;
        for (Map.Entry<DeliveryPartner, List<Order>> partner : partnerOrdersMap.entrySet()){
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
        int time =0;
        for (Map.Entry<DeliveryPartner, List<Order>> partner : partnerOrdersMap.entrySet()){
            if(partner.getKey().equals(partnerId)){
                for(Order order:partner.getValue()){
                    if(order.getDeliveryTime()>time){
                        time = order.getDeliveryTime();
                    }
                }
                break;
            }
        }
        int h = time/60;
        int m = time%60;
        String string = String.format("%02d",h)+":"+String.format("%02d",m);
        return string;
    }

    public String deletePartnerById(String partnerId){
        DeliveryPartner partner = partners.get(partnerId);
        for(Map.Entry<Order,DeliveryPartner> order : orderDeliveryPartnerMap.entrySet()){
            if(order.getValue()==partner){
                orderDeliveryPartnerMap.remove(order.getKey());
            }
        }
        partners.remove(partnerId);
        partnerOrdersMap.remove(partner);
        return partnerId + " removed successfully";
    }

    public String deleteOrderById(String orderId){
        Order order = orders.get(orderId);
        for(Map.Entry<Order,DeliveryPartner> orderPartner : orderDeliveryPartnerMap.entrySet()){
            if(orderPartner.getKey()==order){
                orderDeliveryPartnerMap.remove(order);
                for (Map.Entry<DeliveryPartner, List<Order>> partner : partnerOrdersMap.entrySet()){
                    if(partner.getKey()==orderPartner.getValue()){
                        int index = partner.getValue().indexOf(order);
                        partner.getValue().remove(index);
                        break;
                    }
                }
                break;
            }
        }
        orders.remove(orderId);
        return orderId + " removed successfully";
    }


    }
