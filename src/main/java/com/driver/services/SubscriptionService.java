package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SubscriptionService {

    @Autowired
    public SubscriptionRepository subscriptionRepository;

    @Autowired
    public UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto)  {

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        int baseCost;
        int costPerScreen;

        switch (subscriptionEntryDto.getSubscriptionType()) {
            case BASIC:
                baseCost = 500;
                costPerScreen = 200;
                break;
            case PRO:
                baseCost = 800;
                costPerScreen = 250;
                break;
            case ELITE:
                baseCost = 1000;
                costPerScreen = 350;
                break;
            default:
                throw new IllegalArgumentException("Invalid subscription type");
        }
        int totalAmount = baseCost + costPerScreen * subscriptionEntryDto.getNoOfScreensRequired();
        subscription.setTotalAmountPaid(totalAmount);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);

        subscriptionRepository.save(subscription);

        return totalAmount;
    }


    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User u = userRepository.findById(userId).get();
        if(u.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        else if(u.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC)){
            u.getSubscription().setSubscriptionType(SubscriptionType.PRO);
            int nofs = u.getSubscription().getNoOfScreensSubscribed();
            int diff = 300+50*(nofs);
            u.getSubscription().setTotalAmountPaid(800+250*(nofs));
            userRepository.save(u);
            return diff;
        }
        else{
            u.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
            int nofs = u.getSubscription().getNoOfScreensSubscribed();
            int diff = 200+100*(nofs);
            u.getSubscription().setTotalAmountPaid(1000+350*(nofs));
            userRepository.save(u);
            return diff;
        }
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        // Retrieve all subscriptions from the repository
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        // Sum up the total amounts of all subscriptions

        int total = 0;
        for(Subscription s:subscriptions){
            total += s.getTotalAmountPaid();
        }

        return total;
    }

}
