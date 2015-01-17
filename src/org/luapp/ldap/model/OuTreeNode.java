package org.luapp.ldap.model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Map;

/**
 * Created by lumeng on 2015/1/17.
 */
public class OuTreeNode extends DefaultMutableTreeNode {

    private Map<String,String> ouData;

    public Map<String, String> getOuData() {
        return ouData;
    }

    public OuTreeNode(Object userObject,Map<String,String> ouData) {
        super(userObject);
        this.ouData = ouData;
    }
}
