/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2017 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.docdoku.api;

import com.docdoku.api.client.ApiClient;
import com.docdoku.api.client.ApiException;
import com.docdoku.api.models.*;
import com.docdoku.api.services.WorkspacesApi;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class PartUserGroupACLTest {

    private static final WorkspacesApi workspacesApi = new WorkspacesApi(TestConfig.REGULAR_USER_CLIENT);

    private static WorkspaceDTO workspace;
    private static UserGroupDTO group1 = new UserGroupDTO();
    private static UserGroupDTO group2 = new UserGroupDTO();
    private static ApiClient user1Client;
    private static ApiClient user2Client;
    private static ACLEntryDTO group1FullAccess = new ACLEntryDTO();
    private static ACLEntryDTO group2FullAccess = new ACLEntryDTO();
    private static ACLEntryDTO group1ReadOnly = new ACLEntryDTO();
    private static ACLEntryDTO group2ReadOnly = new ACLEntryDTO();
    private static ACLEntryDTO group2Forbidden = new ACLEntryDTO();
    private static ACLEntryDTO group1Forbidden = new ACLEntryDTO();
    private static ACLEntryDTO workspaceAdminFullAccess = new ACLEntryDTO();
    private static ACLEntryDTO workspaceAdminReadOnly = new ACLEntryDTO();
    private static ACLEntryDTO workspaceAdminForbidden = new ACLEntryDTO();

    @BeforeClass
    public static void initTestData() throws ApiException {

        workspace = TestUtils.createWorkspace(PartUserGroupACLTest.class.getName());

        AccountDTO account1 = TestUtils.createAccount();
        AccountDTO account2 = TestUtils.createAccount();

        group1.setId(TestUtils.randomString());
        group1.setWorkspaceId(workspace.getId());

        group2.setId(TestUtils.randomString());
        group2.setWorkspaceId(workspace.getId());

        workspacesApi.createGroup(workspace.getId(), group1);
        workspacesApi.createGroup(workspace.getId(), group2);

        UserDTO user1 = new UserDTO();
        user1.setLogin(account1.getLogin());
        UserDTO user2 = new UserDTO();
        user2.setLogin(account2.getLogin());

        user1Client = DocDokuPLMClientFactory.createJWTClient(TestConfig.URL, account1.getLogin(), TestConfig.PASSWORD);
        user2Client = DocDokuPLMClientFactory.createJWTClient(TestConfig.URL, account2.getLogin(), TestConfig.PASSWORD);

        workspacesApi.addUser(workspace.getId(), user1, group1.getId());
        workspacesApi.addUser(workspace.getId(), user2, group2.getId());

        group1FullAccess.setKey(group1.getId());
        group1FullAccess.setValue(ACLEntryDTO.ValueEnum.FULL_ACCESS);

        group2FullAccess.setKey(group2.getId());
        group2FullAccess.setValue(ACLEntryDTO.ValueEnum.FULL_ACCESS);

        group1ReadOnly.setKey(group1.getId());
        group1ReadOnly.setValue(ACLEntryDTO.ValueEnum.READ_ONLY);

        group2ReadOnly.setKey(group2.getId());
        group2ReadOnly.setValue(ACLEntryDTO.ValueEnum.READ_ONLY);

        group2Forbidden.setKey(group2.getId());
        group2Forbidden.setValue(ACLEntryDTO.ValueEnum.FORBIDDEN);

        group1Forbidden.setKey(group1.getId());
        group1Forbidden.setValue(ACLEntryDTO.ValueEnum.FORBIDDEN);

        group2Forbidden.setKey(group2.getId());
        group2Forbidden.setValue(ACLEntryDTO.ValueEnum.FORBIDDEN);

        workspaceAdminFullAccess.setKey(TestConfig.LOGIN);
        workspaceAdminFullAccess.setValue(ACLEntryDTO.ValueEnum.FULL_ACCESS);

        workspaceAdminReadOnly.setKey(TestConfig.LOGIN);
        workspaceAdminReadOnly.setValue(ACLEntryDTO.ValueEnum.READ_ONLY);

        workspaceAdminForbidden.setKey(TestConfig.LOGIN);
        workspaceAdminForbidden.setValue(ACLEntryDTO.ValueEnum.FORBIDDEN);
    }

    @Test
    public void testFullAccessGroupACL() throws ApiException {

        // Create parts full access for group1
        PartRevisionDTO part = TestUtils.createPart(workspace.getId(), null, Arrays.asList(group1FullAccess));

        // Allowed for user 1 which belongs to group 1
        TestUtils.assertUserCanRetrievePart(user1Client, part, true);
        // Not allowed for user 2 which does not belong to group 1
        TestUtils.assertUserCanRetrievePart(user2Client, part, false);

        // Allow for user 1 which belongs to group 1
        TestUtils.assertUserCanEditPart(user1Client, part, true);
        // Not allowed for user 2 which does not belong to group 1
        TestUtils.assertUserCanEditPart(user2Client, part, false);

        // Create parts full access for group2
        part = TestUtils.createPart(workspace.getId(), null, Arrays.asList(group2FullAccess));

        // Not allowed for user 1 which does not belong to group 2
        TestUtils.assertUserCanRetrievePart(user1Client, part, false);
        // Allowed for user 2 which belongs to group 2
        TestUtils.assertUserCanRetrievePart(user2Client, part, true);

        TestUtils.assertUserCanEditPart(user1Client, part, false);
        TestUtils.assertUserCanEditPart(user2Client, part, true);

        // Create parts full access for group1 & group2
        part = TestUtils.createPart(workspace.getId(), null, Arrays.asList(group1FullAccess, group2FullAccess));

        TestUtils.assertUserCanRetrievePart(user1Client, part, true);
        TestUtils.assertUserCanRetrievePart(user2Client, part, true);

        TestUtils.assertUserCanEditPart(user1Client, part, true);
        TestUtils.assertUserCanEditPart(user2Client, part, true);

    }

    @Test
    public void testForbiddenAccessGroupACL() throws ApiException {

        // Create parts forbidden for group1
        PartRevisionDTO part = TestUtils.createPart(workspace.getId(),
                null, Arrays.asList(group1Forbidden, group2FullAccess));

        TestUtils.assertUserCanRetrievePart(user1Client, part, false);
        TestUtils.assertUserCanRetrievePart(user2Client, part, true);

        TestUtils.assertUserCanEditPart(user1Client, part, false);
        TestUtils.assertUserCanEditPart(user2Client, part, true);

        // Create parts forbidden for group2
        part = TestUtils.createPart(workspace.getId(),
                null, Arrays.asList(group1FullAccess, group2Forbidden));

        TestUtils.assertUserCanRetrievePart(user1Client, part, true);
        TestUtils.assertUserCanRetrievePart(user2Client, part, false);

        TestUtils.assertUserCanEditPart(user1Client, part, true);
        TestUtils.assertUserCanEditPart(user2Client, part, false);

        // Create parts forbidden for group1 & group2
        part = TestUtils.createPart(workspace.getId(),
                null, Arrays.asList(group1Forbidden, group2Forbidden));

        TestUtils.assertUserCanRetrievePart(user1Client, part, false);
        TestUtils.assertUserCanRetrievePart(user2Client, part, false);

        TestUtils.assertUserCanEditPart(user1Client, part, false);
        TestUtils.assertUserCanEditPart(user2Client, part, false);
    }


    @Test
    public void testReadOnlyAccessGroupACL() throws ApiException {
        // Create parts read-only for group1
        PartRevisionDTO part = TestUtils.createPart(workspace.getId(),
                null, Arrays.asList(group1ReadOnly, group2FullAccess));

        TestUtils.assertUserCanRetrievePart(user1Client, part, true);
        TestUtils.assertUserCanRetrievePart(user2Client, part, true);

        TestUtils.assertUserCanEditPart(user1Client, part, false);
        TestUtils.assertUserCanEditPart(user2Client, part, true);

        // Create parts read-only for group2
        part =  TestUtils.createPart(workspace.getId(),
                null, Arrays.asList(group1ReadOnly, group2Forbidden));

        TestUtils.assertUserCanRetrievePart(user1Client, part, true);
        TestUtils.assertUserCanRetrievePart(user2Client, part, false);

        TestUtils.assertUserCanEditPart(user1Client, part, false);
        TestUtils.assertUserCanEditPart(user2Client, part, false);

        // Create parts read-only for group1 & group2
        part =  TestUtils.createPart(workspace.getId(),
                null, Arrays.asList(group1ReadOnly, group2ReadOnly));
        TestUtils.assertUserCanRetrievePart(user1Client, part, true);
        TestUtils.assertUserCanRetrievePart(user2Client, part, true);

        TestUtils.assertUserCanEditPart(user1Client, part, false);
        TestUtils.assertUserCanEditPart(user2Client, part, false);

    }

    @AfterClass
    public static void deleteWorkspace() throws ApiException {
        workspacesApi.deleteWorkspace(workspace.getId());
    }


}
