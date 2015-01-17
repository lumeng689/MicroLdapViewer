package org.luapp.ldap.model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Map;

/**
 * Created by lumeng on 2015/1/17.
 */
public class UserTreeNode extends DefaultMutableTreeNode {

    private Map<String, String> userData;

    public Map<String, String> getUserData() {
        return userData;
    }

    public UserTreeNode(Object userObject, Map<String, String> userData) {
        super(userObject);
        this.userData = userData;
    }
}
