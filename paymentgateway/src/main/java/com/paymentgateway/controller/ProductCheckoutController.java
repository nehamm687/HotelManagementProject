package com.paymentgateway.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymentgateway.client.BookingClient;
import com.paymentgateway.dto.ProductRequest;
import com.paymentgateway.dto.StripeResponse;
import com.paymentgateway.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {


    private StripeService stripeService;
    
    @Autowired
    private BookingClient bookingClient;
    
    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public StripeResponse checkoutProducts(@RequestBody ProductRequest productRequest) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
     return stripeResponse;
    }
    
    @GetMapping("/success")
    public String handleSuccess(@RequestParam("session_id") String sessionId, @RequestParam("booking_id") long id) {
        Stripe.apiKey = System.getenv("STRIPE_API_KEY"); // Read from environment variable; // Replace with your actual secret key

        try {
            Session session = Session.retrieve(sessionId);
            String paymentStatus = session.getPaymentStatus();
            //database operation to update booking
            boolean result = bookingClient.updateBooking(id);
            if(result) {
            	//send email
            	
            }
            
            System.out.println(sessionId);

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                System.out.println("✅ Payment successful: true");
                return "Payment successful";
            } else {
                System.out.println("❌ Payment not completed: false");
                return "Payment not completed";
            }

        } catch (StripeException e) {
            e.printStackTrace();
            return "Stripe error occurred";
        }
    }


    @GetMapping("/cancel")
    public String handleCancel() {
        System.out.println("❌ Payment cancelled: false");
        return "Payment cancelled";
    }
}
