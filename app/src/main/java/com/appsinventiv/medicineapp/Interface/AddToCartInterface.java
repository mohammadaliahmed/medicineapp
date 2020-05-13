package com.appsinventiv.medicineapp.Interface;

import com.appsinventiv.medicineapp.Models.Product;

/**
 * Created by AliAh on 22/06/2018.
 */

public interface AddToCartInterface {
    public void addedToCart(Product product,int quantity,int position);
    public void deletedFromCart(Product product,int position);
    public void quantityUpdate(Product product,int quantity,int position);


}
