package org.luapp.ldap;

import org.luapp.ldap.model.OuTreeNode;
import org.luapp.ldap.model.UserTreeNode;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Created by lumeng on 2015/1/17.
 */
public class LdapViewer extends JFrame {
    private JPanel rootPanel;
    private JList list;
    private JTree tree;
    private JScrollPane treePane;
    private JScrollPane listPane;

    private String url;
    private String initDn;
    private String userName;
    private String pwd;
    private String regexUser;

    private DirContext context;

    /**
     * The JNDI context factory used to acquire our InitialContext.  By
     * default, assumes use of an LDAP server using the standard JNDI LDAP
     * provider.
     */
    protected String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    //用于查询ou的过滤器
    private String ouFilter = "(&(objectClass=organizationalUnit))";

    public LdapViewer(String url, String initDn, String userName, String pwd, String regexUser) {
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("LDAP导入设置");
        pack();
        setSize(800, 600);
        // 禁用最大最小化按钮
        setResizable(false);
        // setLocationRelativeTo在setSize后面  设置为null表示居中显示
        setLocationRelativeTo(null);
        // 树不可编辑
        tree.setEditable(false);

        this.url = url;
        this.initDn = initDn;
        this.userName = userName;
        this.pwd = pwd;
        this.regexUser = regexUser;

        init();
    }

    private void init() {
        try {
            context = open();
        } catch (NamingException e) {
            JOptionPane.showMessageDialog(null, "连接失败", "输入错误", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

        List<Map<String, String>> ouList = searchOu(initDn);

        List<Map<String, String>> userList = searchUser(initDn);

        if (ouList != null) {
            System.out.println("======" + ouList.size());
        }

        if (userList != null) {
            System.out.println("======" + userList.size());
        }

        initTree(initDn, ouList, userList);
    }

    private void initTree(String rootDn, List<Map<String, String>> childOus, List<Map<String, String>> childUsers) {
        treePane.setViewportView(null);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootDn);

        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        if (childOus != null) {
            for (Map<String, String> child : childOus) {
                root.add(new OuTreeNode("ou=" + child.get("ou"), child));
            }
        }

        if (childUsers != null) {
            for (Map<String, String> child : childUsers) {
                root.add(new UserTreeNode("uid=" + child.get("uid"), child));
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

                        insertTree((DefaultMutableTreeNode)comp,ouList,userList);

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

        treePane.setViewportView(tree);
    }

    private void insertTree(DefaultMutableTreeNode node, List<Map<String, String>> childOus, List<Map<String, String>> childUsers) {
        if (childOus != null) {
            for (Map<String, String> child : childOus) {
                node.add(new OuTreeNode("ou=" + child.get("ou"), child));
            }
        }

        if (childUsers != null) {
            for (Map<String, String> child : childUsers) {
                node.add(new UserTreeNode("uid=" + child.get("uid"), child));
            }
        }

        tree.expandPath(new TreePath(node));

        tree.updateUI();
    }

    private List<Map<String, String>> searchUser(String searchBase) {
        return search(searchBase, String.format(regexUser, "*"), SearchControls.ONELEVEL_SCOPE);
    }

    private List<Map<String, String>> searchOu(String searchBase) {
        return search(searchBase, ouFilter, SearchControls.ONELEVEL_SCOPE);
    }

    private List<Map<String, String>> search(String searchBase, String searchFilter, int searchScope) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            //搜索控制器
            SearchControls searchCtls = new SearchControls();
            //设置搜索范围
            searchCtls.setSearchScope(searchScope);
            //设置返回属性集
//            String returnedAtts[] = new String[5];
//            returnedAtts[0] = this.getGroupName();
//            returnedAtts[1] = this.getLoginId();
//            returnedAtts[2] = this.getUserName();
//            returnedAtts[3] = this.getMail();
//            returnedAtts[4] = this.getMobile();

//            searchCtls.setReturningAttributes(returnedAtts);
            searchCtls.setCountLimit(0);

            //根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
            NamingEnumeration answer = context.search(searchBase, searchFilter, searchCtls);

            while (answer.hasMoreElements()) {    //遍历搜索得到的结果集
                SearchResult sr = (SearchResult) answer.next();

                Attributes Attrs = sr.getAttributes();            //得到符合条件的属性集
                if (Attrs != null) {
                    Map<String, String> userInfos = new HashMap<String, String>();
                    String dn = sr.getNameInNamespace();
                    userInfos.put("dn", dn);
                    //依次遍历每个属性
                    for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore(); ) {
                        Attribute Attr = (Attribute) ne.next();

                        for (NamingEnumeration e = Attr.getAll(); e.hasMore(); ) {
                            String value = e.next().toString();
                            userInfos.put(Attr.getID().toString(), value);
                        }
                    }

                    list.add(userInfos);
                }
            }
        } catch (NamingException e) {
            System.out.println("搜索OpenLdap出错");
        }

        return list;
    }

    private DirContext open() throws NamingException {

        // Do nothing if there is a directory server connection already open
        if (context != null)
            return (context);

        try {

            // Ensure that we have a directory context available
            context = new InitialDirContext(getDirectoryContextEnvironment());

        } catch (Exception e) {
            // Try connecting to the alternate url.
            context = new InitialDirContext(getDirectoryContextEnvironment());

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
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, pwd);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put("com.sun.jndi.ldap.connect.timeout", "-1");

        return env;
    }

}
