package org.luapp.ldap;

import org.luapp.ldap.model.OuTreeNode;
import org.luapp.ldap.model.UserTreeNode;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.*;

/**
 * Created by lumeng on 2015/1/20.
 */
public class AdViewer extends JFrame {
    String ldapUrl;
    String baseDn;
    String domain;
    String user;
    String pwd;

    private String userFilter           //用于查询用户的过滤器，例如：(&(objectClass=user)(sAMAccountName=%s))
            = "(&(objectClass=user)(sAMAccountName=%s))";

    private String ouFilter             //用于查询ou的过滤器
            = "(&(objectClass=organizationalUnit))";

    private LdapContext context;

    private JTree tree;
    private JList list;
    private JPanel rootPanel;
    private JScrollPane treePanel;
    private JScrollPane listPanel;

    public AdViewer(String ldapUrl, String baseDn, String domain, String user, String pwd) {
        this.ldapUrl = ldapUrl;
        this.baseDn = baseDn;
        this.domain = domain;
        this.user = user;
        this.pwd = pwd;

        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("LDAP查看器");
        pack();
        setSize(800, 600);
        // 禁用最大最小化按钮
        setResizable(false);
        // setLocationRelativeTo在setSize后面  设置为null表示居中显示
        setLocationRelativeTo(null);
        // 树不可编辑
        tree.setEditable(false);

        init();
    }

    private void init() {

        try {
            context = open();
        } catch (NamingException e) {
            JOptionPane.showMessageDialog(null, "连接失败", "输入错误", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

        List<Map<String, String>> ouList = searchOu(baseDn);

        List<Map<String, String>> userList = searchUser(baseDn);

        if (ouList != null) {
            System.out.println("======" + ouList.size());
        }

        if (userList != null) {
            System.out.println("======" + userList.size());
        }

        initTree(baseDn, ouList, userList);
    }

    private void initTree(String rootDn, List<Map<String, String>> childOus, List<Map<String, String>> childUsers) {
        treePanel.setViewportView(null);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootDn);

        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        if (childOus != null) {
            for (Map<String, String> child : childOus) {
                root.add(new OuTreeNode("ou=" + child.get("ou"), child));
            }
        }

        if (childUsers != null) {
            for (Map<String, String> child : childUsers) {
                root.add(new UserTreeNode(child.get("name"), child));
            }
        }

        tree = new JTree(root);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) { //选中菜单节点的事件
                Object node = tree.getLastSelectedPathComponent();

                if (node instanceof OuTreeNode) {
                    Map<String, String> kvs = ((OuTreeNode) node).getOuData();
                    List<String> items = new ArrayList<String>(kvs.size());
                    if (kvs != null) {
                        for (Map.Entry<String, String> entry : kvs.entrySet()) {
                            items.add(entry.getKey() + ":" + entry.getValue());
                        }
                    }
                    list.setListData(items.toArray());
                } else if (node instanceof UserTreeNode) {
                    Map<String, String> kvs = ((UserTreeNode) node).getUserData();
                    List<String> items = new ArrayList<String>(kvs.size());
                    if (kvs != null) {
                        for (Map.Entry<String, String> entry : kvs.entrySet()) {
                            items.add(entry.getKey() + ":" + entry.getValue());
                        }
                    }
                    list.setListData(items.toArray());
                } else {
                    list.setListData(new Object[]{});
                }
            }
        });

        tree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("333333333333333333333333333");
//                    i((JTree)e.getSource()).getSelectionRows();
                    Object comp = tree.getLastSelectedPathComponent();
                    if (comp instanceof OuTreeNode) {
                        String dn = ((OuTreeNode) comp).getOuData().get("dn");

                        List<Map<String, String>> ouList = searchOu(dn);

                        List<Map<String, String>> userList = searchUser(dn);

                        if (ouList != null) {
                            System.out.println("======" + ouList.size());
                        }

                        if (userList != null) {
                            System.out.println("======" + userList.size());
                        }

                        insertTree((DefaultMutableTreeNode) comp, ouList, userList);

                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                System.out.println("2221111111111111");
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                System.out.println("3331111111111111");
            }
        });

        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                System.out.println("11111111111111");
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
//        tree.updateUI();

        treePanel.setViewportView(tree);
    }

    private void insertTree(DefaultMutableTreeNode node, List<Map<String, String>> childOus, List<Map<String, String>> childUsers) {
        if (childOus != null) {
            for (Map<String, String> child : childOus) {
                node.add(new OuTreeNode("ou=" + child.get("ou"), child));
            }
        }

        if (childUsers != null) {
            for (Map<String, String> child : childUsers) {
                node.add(new UserTreeNode(child.get("name"), child));
            }
        }

        tree.expandPath(new TreePath(node));

        tree.updateUI();
    }

    private List<Map<String, String>> searchUser(String searchBase) {
        return search(searchBase, String.format(userFilter, "*"), SearchControls.ONELEVEL_SCOPE);
    }

    private List<Map<String, String>> searchOu(String searchBase) {
        return search(searchBase, ouFilter, SearchControls.ONELEVEL_SCOPE);
    }

    private List<Map<String, String>> search(String searchBase, String searchFilter, int searchScope) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        int pageSize = 1000; // 1000 entries per page
        byte[] cookie = null;
        int total;
        try {
            context.setRequestControls(new Control[] { new PagedResultsControl(pageSize, Control.CRITICAL) });// 分页读取控制
            do {// 循环检索数据

                //搜索控制器
                SearchControls searchCtls = new SearchControls();
                //设置搜索范围
                searchCtls.setSearchScope(searchScope);
                //设置返回属性集
                String returnedAtts[] = { "ou", "userPrincipalName", "displayName", "name", "mail", "mobile", "objectGUID" };
                //              //设置返回属性集
                searchCtls.setReturningAttributes(returnedAtts);

                //根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
                NamingEnumeration results = context.search(searchBase, searchFilter, searchCtls);

                while (results != null && results.hasMoreElements()) {// 遍历结果集
                    SearchResult sr = (SearchResult) results.next();// 得到符合搜索条件的DN

                    int count = 0;

                    Attributes attrs = sr.getAttributes();// 得到符合条件的属性集
                    if (attrs != null) {

                        Map<String, String> userInfos = new HashMap<String, String>();
                        userInfos.put("dn", sr.getNameInNamespace());

                        //依次遍历每个属性
                        for (NamingEnumeration ne = attrs.getAll(); ne.hasMore();) {
                            Attribute attr = (Attribute) ne.next();

                            for (Enumeration vals = attr.getAll(); vals.hasMoreElements();) {
                                Object value = vals.nextElement();

                                    userInfos.put(attr.getID(), value.toString());

                            }
                        }

                        list.add(userInfos);
                    }
                }

                // Examine the paged results control response
                Control[] controls = context.getResponseControls();
                if (controls != null) {
                    for (int i = 0; i < controls.length; i++) {
                        if (controls[i] instanceof PagedResultsResponseControl) {
                            PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
                            total = prrc.getResultSize();
                            cookie = prrc.getCookie();
                        }
                    }
                }
                // Re-activate paged results
                context.setRequestControls(new Control[] { new PagedResultsControl(pageSize, cookie,
                        Control.CRITICAL) });
            } while (cookie != null);

        } catch (NamingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("总共:" + list.size() + "条信息.");
        return list;
    }

    private LdapContext open() throws NamingException {

        // Do nothing if there is a directory server connection already open
        if (context != null)
            return (context);

        try {

            // Ensure that we have a directory context available
            context = new InitialLdapContext(getDirectoryContextEnvironment(), null);

        } catch (Exception e) {
            // Try connecting to the alternate url.
            context = new InitialLdapContext(getDirectoryContextEnvironment(), null);

        } finally {

            // reset it in case the connection times out.
            // the primary may come back.

        }

        return (context);

    }

    /**
     * Create our directory context configuration.
     *
     * @return java.util.Hashtable the configuration for the directory context.
     */
    protected Hashtable<String, String> getDirectoryContextEnvironment() {
        Hashtable<String, String> hashEnv = new Hashtable<String, String>();

        user = user.indexOf(domain) > 0 ? user : user + domain;
        hashEnv.put(Context.SECURITY_AUTHENTICATION, "simple"); // LDAP访问安全级别
        hashEnv.put(Context.SECURITY_PRINCIPAL, user); // AD User
        hashEnv.put(Context.SECURITY_CREDENTIALS, pwd); // AD// Password
        hashEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); // LDAP工厂类
        hashEnv.put(Context.PROVIDER_URL, ldapUrl);
        hashEnv.put(Context.BATCHSIZE, "4100");

        return hashEnv;
    }
}
