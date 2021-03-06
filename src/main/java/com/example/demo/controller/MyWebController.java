package com.example.demo.controller;
import com.mastercard.masterpass.merchant.CheckoutApi;
import com.mastercard.masterpass.merchant.MerchantInitializationApi;
import com.mastercard.masterpass.merchant.model.*;
import com.mastercard.sdk.core.exceptions.SDKErrorResponseException;
import com.mastercard.sdk.core.models.AccessTokenResponse;
import com.mastercard.sdk.core.models.RequestTokenResponse;
import com.mastercard.sdk.core.services.AccessTokenApi;
import com.mastercard.sdk.core.services.RequestTokenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
@Controller
public class MyWebController  extends BaseController{
    @Value("${masterpass.merchant.checkoutId}")
    String merchantCheckoutId;
    @RequestMapping("/start")
    public ModelAndView homePage(){
        ModelAndView modelAndView = new ModelAndView("home");
        System.out.println("start");
        return modelAndView;
    }
    @RequestMapping("/checkout")
 public String checkout(@RequestParam("mpstatus") String status, @RequestParam(value = "oauth_token", required = false) String oauthToken,
                    @RequestParam(value = "oauth_verifier", required = false) String oauthVerifier,
                    @RequestParam(value = "checkout_resource_url", required = false) String checkoutResourceUrl, HttpSession httpSession) {

        if (status.equals("success")) {

            String requestToken = httpSession.getAttribute("request_token").toString();
            System.out.println("Request token is " + requestToken);

            //Step 5: Get access token
            AccessTokenResponse accessTokenResponse = AccessTokenApi.create(oauthToken, oauthVerifier);
            String accessToken = accessTokenResponse.getOauthToken();


            int startIndex = checkoutResourceUrl.lastIndexOf('/') + 1;
            int endIndex = checkoutResourceUrl.indexOf('?') != -1 ?
                    checkoutResourceUrl.indexOf('?')
                    : checkoutResourceUrl.length();
            String checkoutId = checkoutResourceUrl.substring(startIndex, endIndex);


            Checkout checkout = CheckoutApi.show(checkoutId, accessToken);

            ShippingAddress shippingAddress = checkout.getShippingAddress();
            String transactionId = checkout.getTransactionId();

            Card card = checkout.getCard();
            String cardNum = card.getAccountNumber();


            httpSession.setAttribute("orderNumber", transactionId);
            httpSession.setAttribute("shippingAddress",shippingAddress);
        }



        return "redirect:/success";
    }

    @RequestMapping("/standardCheckout")
  public  ModelAndView standartCheckout(HttpSession httpSession){
        String callback_url = "http://localhost:8080/checkout";
        String request_token = null;
        ModelAndView modelAndView = new ModelAndView("standardCheckout");
        modelAndView.addObject("checkout_id", merchantCheckoutId);
        try{
            System.out.println("try work0");
            RequestTokenResponse requestTokenResponse = RequestTokenApi.create(callback_url);
            System.out.println("try work");
            request_token = requestTokenResponse.getOauthToken();
            System.out.println("try work");
            System.out.println("Request token is"+request_token);
            modelAndView.addObject("request_token", request_token);
            httpSession.setAttribute("request_token", request_token);

        }catch (SDKErrorResponseException e){
            System.out.println("Error"+ e.getMessage()+e.getErrorResponse());
        }
        try {


            MerchantInitializationRequest merchantInitializationRequest = new MerchantInitializationRequest()
                    .originUrl("https://localhost:8080/standardCheckout")
                    .oAuthToken(request_token);

            MerchantInitializationResponse merchantInitializationResponse = MerchantInitializationApi.create(merchantInitializationRequest);
            System.out.println("try work2");


        } catch (Exception e) {

            System.out.println("Error " + e.getMessage());
        }


        return modelAndView;
    }
    @RequestMapping("/success")
  public   ModelAndView success(HttpSession httpSession) {

        ModelAndView mv = new ModelAndView("success");
        mv.addObject("transactionId",httpSession.getAttribute("orderNumber"));

        List<String> shippingAddress = new ArrayList<>();

        ShippingAddress shippingAdd = (ShippingAddress) httpSession.getAttribute("shippingAddress");

        if(shippingAdd != null){

            if(shippingAdd.getLine1() != null){

                shippingAddress.add(shippingAdd.getLine1());

            }
            if(shippingAdd.getLine2() != null){

                shippingAddress.add(shippingAdd.getLine2());

            }
            if(shippingAdd.getLine3() != null){

                shippingAddress.add(shippingAdd.getLine3());

            }

            if(shippingAdd.getCity() != null){

                shippingAddress.add(shippingAdd.getCity());


            }

            if(shippingAdd.getCountry() != null){

                shippingAddress.add(shippingAdd.getCountry());
            }

            if(shippingAdd.getPostalCode()!=null){
                shippingAddress.add(shippingAdd.getPostalCode());
            }

        }


        mv.addObject("shippingAddress",shippingAddress);

        return mv;
    }
}

