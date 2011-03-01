package com.wadsack.android.sheepshead.scorer;

import roboguice.config.AbstractAndroidModule;

/**
 * Author: Jeremy Wadsack
 */
public class ConfigurationModule extends AbstractAndroidModule{

    @Override
    protected void configure() {
        // Some stuff like this...
     /*
      * This tells Guice that whenever it sees a dependency on a TransactionLog,
      * it should satisfy the dependency using a DatabaseTransactionLog.
      */
//    bind(TransactionLog.class).to(DatabaseTransactionLog.class);

     /*
      * Similarly, this binding tells Guice that when CreditCardProcessor is used in
      * a dependency, that should be satisfied with a PaypalCreditCardProcessor.
      */
//    bind(CreditCardProcessor.class).to(PaypalCreditCardProcessor.class);
    }
}
