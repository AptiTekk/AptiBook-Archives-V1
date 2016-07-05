package com.aptitekk.agenda.core;

import com.aptitekk.agenda.core.services.AssetTypeService;
import com.aptitekk.agenda.core.services.UserGroupService;
import com.aptitekk.agenda.core.services.UserService;
import com.aptitekk.agenda.core.testingUtil.TestUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class StartupServiceTest {

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtils.createDeployment();
    }

    @Inject
    UserGroupService userGroupService;

    @Inject
    UserService userService;

    @Inject
    AssetTypeService assetTypeService;

    @Test
    public void testEntitiesAreCreatedOnStartup() throws Exception {

        assertNotNull("Root Group is Null!", userGroupService.findByName(UserGroupService.ROOT_GROUP_NAME));

        assertNotNull("Admin User is Null!", userService.findByName(UserService.ADMIN_USERNAME));

        assertNotNull("Rooms Asset Type is Null!", assetTypeService.findByName("Rooms"));
    }
}
