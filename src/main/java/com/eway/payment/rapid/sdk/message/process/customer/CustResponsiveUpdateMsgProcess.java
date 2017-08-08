package com.eway.payment.rapid.sdk.message.process.customer;

import com.eway.payment.rapid.sdk.beans.external.Customer;
import com.eway.payment.rapid.sdk.beans.external.TransactionType;
import com.eway.payment.rapid.sdk.beans.internal.Payment;
import com.eway.payment.rapid.sdk.entities.*;
import com.eway.payment.rapid.sdk.exception.RapidSdkException;
import com.eway.payment.rapid.sdk.message.convert.BeanConverter;
import com.eway.payment.rapid.sdk.message.convert.CustomerToInternalCustomerConverter;
import com.eway.payment.rapid.sdk.message.convert.response.AccessCodeSharedToCreateCustConverter;
import com.eway.payment.rapid.sdk.message.process.AbstractMakeRequestMessageProcess;
import com.eway.payment.rapid.sdk.util.Constant;
import javax.ws.rs.client.WebTarget;

/**
 * Update customer with responsive shared method message process
 */
public class CustResponsiveUpdateMsgProcess extends AbstractMakeRequestMessageProcess<Customer, CreateCustomerResponse> {

    /**
     * @param resource The web resource to call Rapid API
     * @param requestPath Path of request URL. Used to make full web service URL
     */
    public CustResponsiveUpdateMsgProcess(WebTarget resource, String... requestPath) {
        super(resource, requestPath);
    }

    @Override
    protected Request createRequest(Customer input) throws RapidSdkException {
        CreateAccessCodeSharedRequest request = new CreateAccessCodeSharedRequest();
        CustomerToInternalCustomerConverter interCustConvert = new CustomerToInternalCustomerConverter();
        Payment payment = new Payment();
        payment.setTotalAmount(0);
        request.setPayment(payment);
        request.setCustomer(interCustConvert.doConvert(input));
        request.setMethod(Constant.UPDATE_TOKEN_CUSTOMER_METHOD);
        request.setTransactionType(TransactionType.Purchase.name());
        request.setRedirectUrl(input.getRedirectUrl());
        request.setCancelUrl(input.getCancelUrl());
        return request;
    }

    @Override
    protected CreateCustomerResponse makeResult(Response res) throws RapidSdkException {
        // Cast to implement response object
        CreateAccessCodeSharedResponse response = (CreateAccessCodeSharedResponse) res;
        BeanConverter<CreateAccessCodeSharedResponse, CreateCustomerResponse> converter = new AccessCodeSharedToCreateCustConverter();
        return converter.doConvert(response);
    }

    @Override
    protected Response sendRequest(Request req) throws RapidSdkException {
        return doPost(req, CreateAccessCodeSharedResponse.class);
    }

}
