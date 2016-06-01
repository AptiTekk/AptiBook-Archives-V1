package com.AptiTekk.Agenda.core;

import com.AptiTekk.Agenda.core.entity.Asset;
import com.AptiTekk.Agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Calendar;

/**
 * Created by kevint on 6/1/2016.
 */
@RunWith(Arquillian.class)
public class ReservationServiceTest {

    @Inject
    ReservationService reservationService;

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Test
    public void verifyAvailableAssets() throws Exception {
        //ARRANGE

        Calendar calendar = Calendar.getInstance();


        Asset directlyAvailableAsset = new Asset();
        //set availability times and names

        Asset indirectlyAvailableAsset = new Asset();

        Asset notAvailableAsset = new Asset();

        Asset alreadyReservedAsset = new Asset();


        //ACT


    }
}
