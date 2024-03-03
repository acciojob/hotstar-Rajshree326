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

        User user = userRepository.findById(subscriptionEntryDto.getUserId())
                .orElseThrow(()-> new IllegalArgumentException("User Not Found"));

        subscription.setUser(user);
        user.setSubscription(subscription);

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

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return savedSubscription.getTotalAmountPaid();
    }


    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Subscription currentSubscription = user.getSubscription();

        if (currentSubscription.getSubscriptionType() == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }

        //get the new SubscriptionType

        SubscriptionType newSubscriptionType ;

        switch (currentSubscription.getSubscriptionType()) {
            case BASIC:
                newSubscriptionType = SubscriptionType.PRO;
                break;
            case PRO:
                newSubscriptionType = SubscriptionType.ELITE;
                break;
            default:
                throw new IllegalArgumentException("Invalid subscription type");
        }

        //calculate the price paid in current subscription
        int baseCost;
        int costPerScreen;
        switch (currentSubscription.getSubscriptionType()) {
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
        int totalAmount = baseCost + costPerScreen * currentSubscription.getNoOfScreensSubscribed();


        //calculate the price paid in new subscription
        switch (newSubscriptionType) {
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
        int newTotalAmount = baseCost + costPerScreen * currentSubscription.getNoOfScreensSubscribed();

        //upgrade the current subscription
        currentSubscription.setSubscriptionType(newSubscriptionType);
        currentSubscription.setTotalAmountPaid(newTotalAmount);

        subscriptionRepository.save(currentSubscription);

        //return the price difference
        return newTotalAmount-totalAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        // Retrieve all subscriptions from the repository
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        // Sum up the total amounts of all subscriptions
        if(subscriptions.isEmpty()) return 0;
        return subscriptions.stream()
                .mapToInt(Subscription::getTotalAmountPaid)
                .sum();
    }

}
