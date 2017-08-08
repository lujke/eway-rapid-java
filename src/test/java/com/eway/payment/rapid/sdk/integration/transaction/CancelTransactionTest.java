package com.eway.payment.rapid.sdk.integration.transaction;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eway.payment.rapid.sdk.InputModelFactory;
import com.eway.payment.rapid.sdk.RapidClient;
import com.eway.payment.rapid.sdk.beans.external.Address;
import com.eway.payment.rapid.sdk.beans.external.CardDetails;
import com.eway.payment.rapid.sdk.beans.external.Customer;
import com.eway.payment.rapid.sdk.beans.external.PaymentDetails;
import com.eway.payment.rapid.sdk.beans.external.PaymentMethod;
import com.eway.payment.rapid.sdk.beans.external.Refund;
import com.eway.payment.rapid.sdk.beans.external.Transaction;
import com.eway.payment.rapid.sdk.beans.internal.RefundDetails;
import com.eway.payment.rapid.sdk.integration.IntegrationTest;
import com.eway.payment.rapid.sdk.output.CreateTransactionResponse;
import com.eway.payment.rapid.sdk.output.RefundResponse;

public class CancelTransactionTest extends IntegrationTest {

    RapidClient client;
    Transaction t;
    Refund refund;

    @Before
    public void setup() {
        client = getSandboxClient();
        t = InputModelFactory.createTransaction();
        Customer c = InputModelFactory.initCustomer();
        Address a = InputModelFactory.initAddress();
        PaymentDetails p = InputModelFactory.initPaymentDetails();
        CardDetails cd = InputModelFactory.initCardDetails("12", "24");
        c.setCardDetails(cd);
        c.setAddress(a);
        t.setCustomer(c);
        t.setPaymentDetails(p);
        refund = new Refund();
        refund.setCustomer(c);

    }

    @Test
    public void testValidInput() {
        t.setCapture(false);
        CreateTransactionResponse res = client.create(PaymentMethod.Direct, t);
        RefundDetails rd = new RefundDetails();
        rd.setOriginalTransactionID(String.valueOf(res.getTransactionStatus().getTransactionID()));
        refund.setRefundDetails(rd);
        RefundResponse cancelRes = client.cancel(refund);
        Assert.assertTrue(cancelRes.getTransactionStatus().isStatus());
    }

    @Test
    public void testAgainstCapturedTransaction() {
        CreateTransactionResponse res = client.create(PaymentMethod.Direct, t);
        RefundDetails rd = new RefundDetails();
        rd.setOriginalTransactionID(String.valueOf(res.getTransactionStatus().getTransactionID()));
        refund.setRefundDetails(rd);
        RefundResponse cancelRes = client.cancel(refund);
        Assert.assertFalse(cancelRes.getTransactionStatus().isStatus());
        Assert.assertTrue(cancelRes.getErrors().contains("V6134"));
    }

    @Test
    public void testInvalidInput1() {
        RefundDetails rd = new RefundDetails();
        rd.setOriginalTransactionID("thistransactionneverexisted");
        refund.setRefundDetails(rd);
        RefundResponse cancelRes = client.cancel(refund);
        // I guess the sandbox coding is such that this just works!
        Assert.assertTrue(cancelRes.getErrors().contains("V6134"));
    }

    @Test
    public void testInvalidInput2() {
        CreateTransactionResponse res = client.create(PaymentMethod.Direct, t);
        t.setAuthTransactionID(res.getTransactionStatus().getTransactionID());
        CreateTransactionResponse authRes = client.create(PaymentMethod.Authorisation, t);
        RefundDetails rd = new RefundDetails();
        rd.setOriginalTransactionID(String.valueOf(res.getTransactionStatus().getTransactionID()));
        refund.setRefundDetails(rd);
        RefundResponse cancelRes = client.cancel(refund);
        Assert.assertTrue(!cancelRes.getTransactionStatus().isStatus());
    }

    @After
    public void tearDown() {

    }

}
