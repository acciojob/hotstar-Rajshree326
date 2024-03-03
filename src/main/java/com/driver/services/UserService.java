package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).orElse(null);

        List<WebSeries> allWebSeries = webSeriesRepository.findAll();

        int count = 0;
        for(WebSeries webseries : allWebSeries){
            if(webseries.getAgeLimit() <= user.getAge()){
                if(isPossible(user , webseries)){
                    count++;
                }
            }
        }
        return count;
    }

    public Boolean isPossible(User user, WebSeries webSeries){
        if(user.getSubscription().getSubscriptionType() == SubscriptionType.ELITE){
            return true;
        }
        if(user.getSubscription().getSubscriptionType() == SubscriptionType.PRO){
            if(webSeries.getSubscriptionType() == SubscriptionType.ELITE)return false;
        }
        if(user.getSubscription().getSubscriptionType() == SubscriptionType.BASIC){
            if(webSeries.getSubscriptionType() == SubscriptionType.BASIC)return true;
            return false;
        }
        return true;
    }

}
